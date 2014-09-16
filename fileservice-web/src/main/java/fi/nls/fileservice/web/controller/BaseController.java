package fi.nls.fileservice.web.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessControlException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.inject.Inject;
import javax.jcr.AccessDeniedException;
import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.HandlerMapping;

import fi.nls.fileservice.common.DataAccessException;
import fi.nls.fileservice.common.NotFoundException;
import fi.nls.fileservice.files.DetachedNode;
import fi.nls.fileservice.files.PropertiesFilter;
import fi.nls.fileservice.jcr.MetadataProperty;
import fi.nls.fileservice.security.AuthorizationContextHolder;
import fi.nls.fileservice.security.PermissionDeniedException;
import fi.nls.fileservice.statistics.DownloadStatistic;
import fi.nls.fileservice.statistics.StatisticsService;
import fi.nls.fileservice.web.NavigationMenu;
import fi.nls.fileservice.web.Page;

public abstract class BaseController {

    @Autowired(required = false)
    private StatisticsService statisticsService;

    @Inject
    private Repository repository;

    @Autowired(required = false)
    private PropertiesFilter propertiesFilter;

    protected static final Logger logger = LoggerFactory
            .getLogger(BaseController.class);

    @InitBinder
    public void initBinder(HttpServletRequest request,
            ServletRequestDataBinder binder) {
        // http://blogs.reucon.com/srt/spring-mvc-null-or-an-empty-string-8336/
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    protected String doFileRequest(HttpServletRequest request,
            HttpServletResponse response, Model model, boolean isGetRequest,
            boolean allowDirectoryListing) throws IOException {

        String path = getPath(request);

        Session session = null;
        try {
            session = repository.login(AuthorizationContextHolder
                    .getCredentials());
            Node node = session.getNode(path);
            if (node.isNodeType(NodeType.NT_FILE)) {
                serveFile(node, request, response, isGetRequest);
                return null;
            } else {
                if (allowDirectoryListing) {
                    DetachedNode detachedNode = new DetachedNode(node);

                    if (propertiesFilter != null) {
                        // filter out private properties
                        propertiesFilter.filter(detachedNode);
                    }

                    model.addAttribute("credentials",
                            AuthorizationContextHolder.getCredentials());
                    model.addAttribute("node", detachedNode);

                    return "listing";
                } else {
                    throw new PermissionDeniedException(
                            "Directory listing not allowed for " + path);
                }
            }

        } catch (LoginException le) {
            throw new PermissionDeniedException(le);
        } catch (AccessControlException ace) {
            throw new PermissionDeniedException(ace);
        } catch (AccessDeniedException ade) {
            throw new PermissionDeniedException(ade);
        } catch (PathNotFoundException e) {
            throw new NotFoundException(e);
        } catch (RepositoryException re) {
            throw new DataAccessException(re);
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }

    public void serveFile(Node fileNode, HttpServletRequest request,
            HttpServletResponse response, boolean isGet)
            throws PathNotFoundException, AccessControlException,
            RepositoryException, IOException {

        Node content = fileNode.getNode(Node.JCR_CONTENT);

        if (content.hasProperty(Property.JCR_LAST_MODIFIED)) {
            Calendar calendar = content.getProperty(Property.JCR_LAST_MODIFIED)
                    .getDate();

            SimpleDateFormat format = new SimpleDateFormat(
                    "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
            format.setTimeZone(TimeZone.getTimeZone("GMT"));
            response.setHeader("Last-Modified",
                    format.format(calendar.getTime()));
        }

        response.setContentType(content.getProperty(Property.JCR_MIMETYPE)
                .getString());

        // don't use response.setContentLength(int length) because of integer
        // overflow for files over 2GB
        long length = content.getProperty(Property.JCR_DATA).getLength();
        response.setHeader("Content-Length", Long.toString(content.getProperty(
                Property.JCR_DATA).getLength()));
        response.setHeader("Content-Disposition", "attachment; filename="
                + fileNode.getName());

        if (isGet) {

            InputStream in = content.getProperty(Property.JCR_DATA).getBinary()
                    .getStream();
            try {
                OutputStream out = response.getOutputStream();
                int nBytes;
                byte[] buf = new byte[8192];
                while ((nBytes = in.read(buf)) > 0) {
                    out.write(buf, 0, nBytes);
                }

                if (statisticsService != null) {
                    registerAudit(fileNode, length, request);
                }

            } finally {
                if (in != null) {
                    in.close();
                }
            }
        }
    }

    protected void registerAudit(Node node, long length,
            HttpServletRequest request) throws RepositoryException {

        DownloadStatistic audit = new DownloadStatistic();
        audit.setTimestamp(System.currentTimeMillis());
        audit.setLength(length);
        audit.setRemoteIP(request.getRemoteAddr());
        audit.setUid(node.getSession().getUserID());
        audit.setPath(node.getPath());

        String serviceId = (String) request
                .getAttribute("fi.nls.lapa.serviceid");
        audit.setServiceId(serviceId);

        if (node.hasProperty(MetadataProperty.NLS_DATASET)) {
            audit.setDataset(node.getProperty(MetadataProperty.NLS_DATASET)
                    .getString());
        }
        if (node.hasProperty(MetadataProperty.NLS_DATASETVERSION)) {
            audit.setDatasetVersion(node.getProperty(
                    MetadataProperty.NLS_DATASETVERSION).getString());
        }
        if (node.hasProperty(MetadataProperty.GMD_DISTRIBUTIONFORMAT)) {
            audit.setFormat(node.getProperty(
                    MetadataProperty.GMD_DISTRIBUTIONFORMAT).getString());
        }
        if (node.hasProperty(MetadataProperty.NLS_CRS)) {
            audit.setCrs(node.getProperty(MetadataProperty.NLS_CRS).getString());
        }
        statisticsService.saveAudit(audit);
    }

    /**
     * This method hacks around the fact, that Spring MVC cannot extract
     * complete PATH from URL (/foo/bar..). (compared to JAX_RS: {path:.*}).
     * This will quite possibly break in future Spring versions..
     * 
     * @param request
     * @return file path
     */
    protected String getPath(HttpServletRequest request) {
        String path = (String) request
                .getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        int indx = path.indexOf("**");
        path = path.substring(0, indx);

        String path2 = (String) request
                .getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        path2 = path2.substring(path.length() - 1);
        if ("".equals(path2)) {
            path2 = "/";
        }
        return path2;
    }

    @ModelAttribute("nav")
    public NavigationMenu getNavigationMenu() {
        NavigationMenu menu = new NavigationMenu();

        Page tuotteet = new Page();
        tuotteet.setPath("/hallinta/tuotteet");
        tuotteet.setName("Tuotteet");

        Page tiedostot = new Page();
        tiedostot.setPath("/hallinta/tiedostot");
        tiedostot.setName("Tiedostot");

        Page oikeudet = new Page();
        oikeudet.setPath("/hallinta/oikeudet");
        oikeudet.setName("Oikeudet");

        menu.getPages().add(tuotteet);
        menu.getPages().add(tiedostot);
        menu.getPages().add(oikeudet);

        return menu;
    }

    @ExceptionHandler(NotFoundException.class)
    public void handlerPathNotFoundException(NotFoundException throwable,
            HttpServletResponse response) throws IOException {
        logger.warn("Resource not found ", throwable.getMessage());
        response.sendError(HttpServletResponse.SC_NOT_FOUND,
                throwable.getMessage());
    }

    @ExceptionHandler(PermissionDeniedException.class)
    public void handleSecurityException(PermissionDeniedException throwable,
            HttpServletResponse response) throws IOException {
        logger.warn("Access denied", throwable.getMessage());
        response.sendError(HttpServletResponse.SC_FORBIDDEN,
                throwable.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    public void handleDataAccessException(DataAccessException throwable,
            HttpServletResponse response) throws IOException {
        logger.warn("Error processing request", throwable);
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                throwable.getMessage());
    }

    @ExceptionHandler(RepositoryException.class)
    public void handleRepositoryException(RepositoryException throwable,
            HttpServletResponse response) throws IOException {
        logger.warn("Error processing request", throwable);
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                throwable.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public void handleIllegalArgumentException(
            IllegalArgumentException throwable, HttpServletResponse response)
            throws IOException {
        logger.warn("Error processing request", throwable);
        response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                throwable.getMessage());
    }

}
