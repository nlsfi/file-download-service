package fi.nls.fileservice.web.feed.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import fi.nls.fileservice.common.NotFoundException;
import fi.nls.fileservice.dataset.Dataset;
import fi.nls.fileservice.dataset.DatasetService;
import fi.nls.fileservice.dataset.DatasetVersion;
import fi.nls.fileservice.dataset.crs.CrsDefinition;
import fi.nls.fileservice.files.DatasetQueryParams;
import fi.nls.fileservice.files.FileService;
import fi.nls.fileservice.jcr.MetadataProperty;
import fi.nls.fileservice.security.PermissionDeniedException;
import fi.nls.fileservice.web.common.LinkBuilder;
import fi.nls.fileservice.web.common.UriComponentsLinkBuilder;
import fi.nls.fileservice.web.feed.atom.Feed;
import fi.nls.fileservice.web.feed.atom.builder.AtomRequestContext;
import fi.nls.fileservice.web.feed.atom.builder.DatasetFeedBuilder;
import fi.nls.fileservice.web.feed.atom.builder.DownloadServiceFeedBuilder;
import fi.nls.fileservice.web.feed.atom.builder.FeedMetadata;
import fi.nls.fileservice.web.feed.atom.builder.InspireAtomFeedBuilder;
import fi.nls.fileservice.web.feed.atom.builder.MtpFeedBuilder;
import fi.nls.fileservice.web.feed.atom.builder.MtpServiceFeedBuilder;

@Controller
@RequestMapping("/feed")
public class AtomFeedController {

    private static final Logger logger = LoggerFactory
            .getLogger(AtomFeedController.class);

    @Inject
    private DatasetService datasetService;

    @Inject
    private FileService fileService;

    @Inject
    private FeedMetadata feedMetadata;

    @Inject
    private MessageSource messageSource;

    @Inject
    private Environment env;

    @Resource(name = "mimeTypeMappings")
    private Map<String, String> mimeTypeMappings;

    @Resource(name = "reverseMimeTypeMappings")
    private Map<String, String> reverseMimeTypeMappings;

    @Resource(name = "crsMappings")
    private Map<String, String> crsMappings;

    @Resource(name = "crsDefinitions")
    private Map<String, CrsDefinition> crsDefinitions;

    @InitBinder
    public void InitBinder(HttpServletRequest request,
            ServletRequestDataBinder binder) {
        // http://blogs.reucon.com/srt/spring-mvc-null-or-an-empty-string-8336/
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @RequestMapping(value = { "/mtp/{datasetName}/{datasetVersionName}",
            "/protectedmtp/{datasetName}/{datasetVersionName}" }, produces = "application/atom+xml; charset=utf-8")
    public ResponseEntity<Feed> mtpDatasetFeed(
            @PathVariable(value = "datasetName") String datasetName,
            @PathVariable(value = "datasetVersionName") String datasetVersionName,
            @RequestParam(value = "crs", required = false) String crs,
            @RequestParam(value = "format", required = false) String format,
            @RequestParam(value = "updated", required = false) String updatedAfter,
            @RequestParam(value = "api_key", required = false) String apiKey,
            @RequestParam(value = "offset", required = false) Integer start,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "lang", required = false) String lang,
            UriComponentsBuilder uriComponentsBuilder, Locale locale) {

        DatasetVersion datasetVersion = datasetService.getDatasetVersion(
                datasetName, datasetVersionName);
        if (datasetVersion != null && datasetVersion.getDataset().isPublished()) {

            Map<String, String> params = new HashMap<String, String>();
            if (apiKey != null) {
                params.put("api_key", apiKey);
            }
            UriComponentsLinkBuilder linkBuilder = new UriComponentsLinkBuilder(
                    uriComponentsBuilder.build(), params, "tilauslataus");

            int maxEntriesPerPage = env.getProperty(
                    "feed_max_entries_per_page", Integer.class);

            if (limit == null || limit < 0 || limit > maxEntriesPerPage) {
                limit = maxEntriesPerPage;
            }

            uriComponentsBuilder.pathSegment("feed", "mtp", datasetName,
                    datasetVersionName);

            if (apiKey != null) {
                uriComponentsBuilder.queryParam("api_key", apiKey);
            }

            if (updatedAfter != null) {
                uriComponentsBuilder.queryParam("updated", updatedAfter);
            }
            if (format != null) {
                uriComponentsBuilder.queryParam("format", format);
            }
            if (crs != null) {
                uriComponentsBuilder.queryParam("crs", crs);
            }
            if (limit != null) {
                uriComponentsBuilder.queryParam("limit", limit);
            }
            if (lang != null) {
                uriComponentsBuilder.queryParam("lang", locale.getLanguage());
            }

            DatasetQueryParams queryParams = new DatasetQueryParams();
            queryParams.setDataset(datasetVersion.getDataset().getName());
            queryParams.setDatasetVersion(datasetVersionName);

            if (crs != null && crsMappings.containsKey(crs)) {
                queryParams.setCrs(crsMappings.get(crs));
            } else {
                queryParams.setCrs(crs);
            }

            if (format != null && mimeTypeMappings.containsKey(format)) {
                queryParams.setDistributionFormat(mimeTypeMappings.get(format));
            } else {
                queryParams.setDistributionFormat(format);
            }

            queryParams.setUpdated(updatedAfter);
            if (start != null) {
                queryParams.setOffset(start);
            }
            queryParams.setOrderBy(MetadataProperty.NLS_FILECHANGED);

            queryParams.setLimit(limit);

            AtomRequestContext context = new AtomRequestContext();
            context.setUriComponents(uriComponentsBuilder.build());
            context.setLocale(locale);
            context.setMessageSource(messageSource);

            MtpFeedBuilder builder = new MtpFeedBuilder(datasetVersion,
                    fileService, queryParams, context, linkBuilder, limit);
            Feed feed = builder.getFeed();

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add("Content-Type",
                    "application/atom+xml; charset=utf-8");
            return new ResponseEntity<Feed>(feed, responseHeaders,
                    HttpStatus.OK);
        }

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type",
                "application/atom+xml; charset=utf-8");
        return new ResponseEntity<Feed>(new Feed(), responseHeaders,
                HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = { "/mtp", "/protectedmtp" }, produces = "application/atom+xml; charset=utf-8")
    public ResponseEntity<Feed> mtpDownloadServiceFeed(
            @RequestParam(value = "api_key", required = false) String apiKey,
            @RequestParam(value = "lang", required = false) String lang,
            UriComponentsBuilder uriComponentsBuilder, Locale locale) {

        Map<String, String> params = new HashMap<String, String>();
        if (apiKey != null) {
            params.put("api_key", apiKey);
        }
        if (lang != null) {
            params.put("lang", locale.getLanguage());
        }

        UriComponentsLinkBuilder linkBuilder = new UriComponentsLinkBuilder(
                uriComponentsBuilder.build(), params, "feed", "mtp");

        uriComponentsBuilder.pathSegment("feed", "mtp");

        MtpServiceFeedBuilder feedBuilder = new MtpServiceFeedBuilder();
        feedBuilder.setReverseMimeTypeMappings(reverseMimeTypeMappings);

        return buildDownloadServiceFeed(feedBuilder, linkBuilder,
                uriComponentsBuilder, locale);
    }

    @RequestMapping(value = "/inspire", produces = "application/atom+xml; charset=utf-8")
    public ResponseEntity<Feed> inspireDownloadServiceFeed(
            @RequestParam(value = "lang", required = false) String lang,
            UriComponentsBuilder uriComponentsBuilder, Locale locale) {

        Map<String, String> params = new HashMap<String, String>();
        if (lang != null) {
            params.put("lang", locale.getLanguage());
        }

        UriComponentsLinkBuilder linkBuilder = new UriComponentsLinkBuilder(
                uriComponentsBuilder.build(), params, "feed", "inspire");

        uriComponentsBuilder.pathSegment("feed", "inspire");

        DownloadServiceFeedBuilder feedBuilder = new DownloadServiceFeedBuilder();

        return buildDownloadServiceFeed(feedBuilder, linkBuilder,
                uriComponentsBuilder, locale);
    }

    protected ResponseEntity<Feed> buildDownloadServiceFeed(
            DownloadServiceFeedBuilder feedBuilder, LinkBuilder linkBuilder,
            UriComponentsBuilder uriComponentsBuilder, Locale locale) {

        AtomRequestContext context = new AtomRequestContext();
        context.setUriComponents(uriComponentsBuilder.build());
        context.setLocale(locale);
        context.setMessageSource(messageSource);

        List<Dataset> datasets = datasetService.getPublishedDatasets();

        feedBuilder.setFeedMetadata(feedMetadata);
        feedBuilder.setAtomRequestContext(context);
        feedBuilder.setDatasets(datasets);
        feedBuilder.setLinkBuilder(linkBuilder);
        feedBuilder.setCrsDefinitions(crsDefinitions);

        Feed feed = feedBuilder.getFeed();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type",
                "application/atom+xml; charset=utf-8");
        return new ResponseEntity<Feed>(feed, responseHeaders, HttpStatus.OK);

    }

    @RequestMapping(value = "/inspire/{dataset}/{datasetVersion}", produces = "application/atom+xml; charset=utf-8")
    public ResponseEntity<Feed> inspireDatasetFeed(
            @PathVariable("dataset") String datasetName,
            @PathVariable("datasetVersion") String datasetVersionName,
            UriComponentsBuilder uriBuilder, Locale locale) {

        DatasetVersion datasetVersion = datasetService.getDatasetVersion(
                datasetName, datasetVersionName);
        if (datasetVersion != null && datasetVersion.getDataset().isPublished()) {

            AtomRequestContext context = new AtomRequestContext();
            context.setUriComponents(uriBuilder.build());
            context.setLocale(locale);
            context.setMessageSource(messageSource);

            InspireAtomFeedBuilder bb = new DatasetFeedBuilder(feedMetadata,
                    context, datasetVersion, null, fileService, crsDefinitions);
            Feed feed = bb.getFeed();

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add("Content-Type",
                    "application/atom+xml; charset=utf-8");
            return new ResponseEntity<Feed>(feed, responseHeaders,
                    HttpStatus.OK);
        }

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type",
                "application/atom+xml; charset=utf-8");
        return new ResponseEntity<Feed>(new Feed(), responseHeaders,
                HttpStatus.NOT_FOUND);

    }

    /*
     * @RequestMapping(value="/inspire/opensearch") public
     * ResponseEntity<OpenSearchDescription>
     * inspireOpenSearchDescription(UriComponentsBuilder uriComponentsBuilder,
     * Locale locale) {
     * 
     * OpenSearchDescription desc = new OpenSearchDescription();
     * desc.setShortName("Maanmittauslaitoksen INSPIRE tiedostopalvelu");
     * desc.setDescription
     * ("Maanmittauslaitoksen INSPIRE tiedostopalvelun OpenSearch-haku");
     * 
     * Url self = new Url(); self.setRel("self");
     * self.setTemplate(uriComponentsBuilder.build().toUriString());
     * 
     * Url genericQuery = new Url(); genericQuery.setRel("results");
     * genericQuery.setType("application/atom+xml");
     * genericQuery.setTemplate(uriComponentsBuilder.queryParam("q",
     * "{searchTerms}").build().toUriString());
     * desc.getUrls().add(genericQuery);
     * 
     * desc.getUrls().add(self); desc.setContact("myynti@maanmittauslaitos.fi");
     * 
     * String[] supportedLanguages = feedMetadata.getLanguages(); for (String
     * language : supportedLanguages) { desc.getLanguages().add(language); }
     * 
     * HttpHeaders responseHeaders = new HttpHeaders();
     * responseHeaders.add("Content-Type",
     * "application/opensearchdescription+xml"); return new
     * ResponseEntity<OpenSearchDescription>(desc,responseHeaders,
     * HttpStatus.OK); }
     */

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

}
