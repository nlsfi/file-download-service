package fi.nls.fileservice.web.customer.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.jcr.LoginException;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import fi.nls.fileservice.common.DataAccessException;
import fi.nls.fileservice.common.NotFoundException;
import fi.nls.fileservice.dataset.Dataset;
import fi.nls.fileservice.dataset.DatasetFile;
import fi.nls.fileservice.dataset.DatasetService;
import fi.nls.fileservice.dataset.DatasetVersion;
import fi.nls.fileservice.dataset.jcr.DatasetFileRefreshnessComparator;
import fi.nls.fileservice.files.DatasetQueryParams;
import fi.nls.fileservice.files.DetachedNode;
import fi.nls.fileservice.files.FileService;
import fi.nls.fileservice.jcr.MetadataProperty;
import fi.nls.fileservice.order.MtpCustomer;
import fi.nls.fileservice.order.OpenDataOrder;
import fi.nls.fileservice.order.OrderService;
import fi.nls.fileservice.security.ExpiredOrNonexistingTokenException;
import fi.nls.fileservice.security.PermissionDeniedException;
import fi.nls.fileservice.web.api.controller.APIException;
import fi.nls.fileservice.web.common.APIResponse;
import fi.nls.fileservice.web.controller.BaseController;
import fi.nls.fileservice.web.feed.atom.builder.FeedMetadata;

@Controller
public class CustomerUIController extends BaseController {

    private static final Logger logger = LoggerFactory
            .getLogger(CustomerUIController.class);

    @Inject
    private OrderService orderService;

    @Inject
    private DatasetService datasetService;

    @Inject
    private FileService fileService;

    @Inject
    private FeedMetadata feedMetadata;

    @Inject
    private Environment env;

    @RequestMapping("/kartta")
    public String getMapUi() {
        return "map";
    }

    @RequestMapping(value = "/kartta/tuotteet", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public @ResponseBody
    List<Dataset> getTuotteetAsJson(UriComponentsBuilder uriCompBuilder,
            Locale locale) {

        String lang = locale.getLanguage();

        List<Dataset> datasets = datasetService.getPublishedDatasets();
        for (Dataset dataset : datasets) {
            dataset.setTitle(dataset.getTranslatedTitles().get(lang));

            if (dataset.getFileIdentifier() != null) {
                UriTemplate template = new UriTemplate(
                        feedMetadata.getMetadataHTMLUri());
                dataset.setMetadataUrl(template.expand(
                        dataset.getFileIdentifier(), lang).toString());
            }

            List<DatasetVersion> versions = dataset.getVersions();
            for (DatasetVersion dsv : versions) {
                dsv.setTitle(dsv.getTranslatedTitles().get(lang));

                UriComponentsBuilder copyBuilder = UriComponentsBuilder
                        .fromUriString(uriCompBuilder.build().toUriString());
                // uriCompBuilder.path("/kartta/tuotteet/{datasetName}/{datasetVersionName}");
                // dsv.setUri(uriCompBuilder.build().expand(dataset.getName(),
                // dsv.getName()).toUriString()); //TODO doesn't work?
                copyBuilder.pathSegment(new String[] { "kartta", "tuotteet",
                        dataset.getName(), dsv.getName() });
                dsv.setUri(copyBuilder.build().toUriString());

            }
        }

        return datasets;
    }

    @RequestMapping(value = "/kartta/tuotteet/{datasetName}/{datasetVersionName}", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public @ResponseBody
    List<DatasetFile> getDatasetFiles(
            @PathVariable(value = "datasetName") String datasetName,
            @PathVariable(value = "datasetVersionName") String datasetVersion,
            @RequestParam(value = "gridIds", required = false) String[] gridIds,
            @RequestParam(value = "crs", required = false) String crs,
            @RequestParam(value = "format", required = false) String format,
            UriComponentsBuilder uriCompBuilder, Locale locale)
            throws APIException {

        List<DatasetFile> df = new ArrayList<DatasetFile>();

        try {

            // query dataset first to see if the user has access rights
            // and the dataset is published
            DatasetVersion version = datasetService.getDatasetVersion(
                    datasetName, datasetVersion);
            if (version != null && version.getDataset().isPublished()) {

                DatasetQueryParams dqp = new DatasetQueryParams();
                dqp.setDataset(datasetName);
                dqp.setDatasetVersion(datasetVersion);
                dqp.setGridIds(gridIds);
                dqp.setCrs(crs);
                dqp.setDistributionFormat(format);
                dqp.setOrderBy(MetadataProperty.NLS_FILECHANGED);

                // query dataset files
                List<DetachedNode> nodes = fileService.queryNodes(dqp,
                        new DatasetFileRefreshnessComparator());
                for (DetachedNode fileNode : nodes) {

                    DatasetFile file = new DatasetFile();
                    file.setPath(fileNode.getPath());
                    file.setLength(fileNode.getLength());
                    file.setLastModified(fileNode.getLastModified());

                    if (fileNode.hasProperty(MetadataProperty.NLS_GRIDCELL)) {
                        file.setGridCell(fileNode.getProperty(
                                MetadataProperty.NLS_GRIDCELL).getValue());
                    }

                    if (fileNode.hasProperty(MetadataProperty.NLS_CRS)) {
                        file.setCrs(fileNode.getProperty(
                                MetadataProperty.NLS_CRS).getValue());
                    }

                    if (fileNode
                            .hasProperty(MetadataProperty.GMD_DISTRIBUTIONFORMAT)) {
                        List<String> values = fileNode.getProperty(
                                MetadataProperty.GMD_DISTRIBUTIONFORMAT)
                                .getValues();
                        // files have only one format
                        if (values.size() > 0) {
                            file.setFormat(values.get(0));
                        }
                    }
                    df.add(file);
                }
            }

        } catch (NotFoundException pnfe) {
            throw new APIException(404, "Not found", pnfe);
        } catch (PermissionDeniedException pde) {
            throw new APIException(403, "Not authorized", pde);
        } catch (DataAccessException dae) {
            throw new APIException(500, "Internal error", dae);
        }
        return df;
    }

    @RequestMapping(value = "/mtp/tilaus", method = RequestMethod.GET)
    public String getMtpOrderForm(Model model) {
        model.addAttribute("customer", new MtpCustomer());
        return "apikey_order";
    }

    @RequestMapping(value = "/mtp/tilaus_ok", method = RequestMethod.GET)
    public String getMtpOrderConfirmation() {
        return "apikey_order_confirmation";
    }

    @RequestMapping(value = "/mtp/tilaus", method = RequestMethod.POST)
    public String saveMtpOrder(
            @ModelAttribute("customer") @Valid MtpCustomer customer,
            BindingResult result, Model model, Locale locale) {
        
        if (!result.hasErrors()) {
            try {
                orderService.saveMtpOrder(customer, locale);
                return "redirect:/mtp/tilaus_ok";
            } catch (Exception e) {
                logger.error("Error processing order", e);
                model.addAttribute("error_msg", "error_processing_mtp_order");
            }
        }
        return "apikey_order";
     
    }

    @RequestMapping(value = "/tilaus/{tokenVariable}", method = RequestMethod.GET)
    public String openDataOrder(
            @PathVariable("tokenVariable") String tokenVariable, Model model)
            throws LoginException, PathNotFoundException, RepositoryException {

        Collection<Dataset> datasets = orderService
                .getOpenDataOrder(tokenVariable);

        model.addAttribute("datasets", datasets);
        return "opendata_order";

    }

    @RequestMapping(value = "/tilaus", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public @ResponseBody
    APIResponse saveOpenDataOrder(@Valid @RequestBody OpenDataOrder order,
            UriComponentsBuilder downloadLinkBuilder, Locale locale) {

        // FIXME must check this manually, because JSR-303 validation
        // doesn't support validation of primitive lists (List<String>)
        if (!order.isValid()) {
            throw new APIException(400, "Invalid order request");
        }

        if (order.getFiles().size() > env.getProperty(
                "opendata.order.max.files", Integer.class, 1000)) {
            throw new APIException(400, "Too many files: "
                    + order.getFiles().size());
        }

        downloadLinkBuilder.path("/tilaus/{token}");
        downloadLinkBuilder.queryParam("lang", locale.getLanguage());

        orderService.saveOpenDataOrder(order, downloadLinkBuilder, locale);

        return new APIResponse("OK");
    }

    @ExceptionHandler(APIException.class)
    public void handleAPIException(APIException throwable,
            HttpServletResponse response) throws IOException {
        logger.warn("API error: " + throwable.getMessage(), throwable);

        response.setStatus(throwable.getErrorCode());

        ObjectMapper mapper = new ObjectMapper();
        APIResponse jsonResponse = new APIResponse(throwable.getMessage());
        OutputStream out = response.getOutputStream();
        response.setContentType("application/json; charset=utf-8");
        mapper.writeValue(out, jsonResponse);
    }

    @ExceptionHandler(ExpiredOrNonexistingTokenException.class)
    public void handleRepositoryException(
            ExpiredOrNonexistingTokenException throwable,
            HttpServletResponse response) throws IOException {
        logger.warn("Expired or non existing token: " + throwable.getMessage());
        response.sendError(HttpServletResponse.SC_FORBIDDEN,
                throwable.getMessage());
    }

}
