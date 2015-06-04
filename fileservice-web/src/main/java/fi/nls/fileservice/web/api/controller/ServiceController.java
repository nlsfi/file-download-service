package fi.nls.fileservice.web.api.controller;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;
import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fi.nls.fileservice.jcr.repository.MetadataUpdateService;
import fi.nls.fileservice.jcr.service.RepositoryService;

@RestController
@RequestMapping("/service")
public class ServiceController {

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private MetadataUpdateService updateService;

    @RequestMapping(value="/updatemetadata", method=RequestMethod.POST)
    public APIResponse updateMetadata(@RequestBody String body) {
        String[] paths = body.split("\\r?\\n");

        updateService.updateMetadataAsync(paths);
        return new APIResponse(HttpStatus.ACCEPTED.value(), "Queued " + paths.length
                + " file(s) for metadata update..\r\n");

    }

    @RequestMapping(value="/reindex", method=RequestMethod.POST)
    public APIResponse reindex(
            @RequestParam(required = false, value = "path") String path)
            throws LoginException, RepositoryException {
        repositoryService.reindex(path);

        String message = path == null ? "Reindexing all content.\r\n"
                : "Reindexing path: " + path + ".\r\n";
        return new APIResponse(HttpStatus.ACCEPTED.value(), message);
    }

    @RequestMapping(value="/exportmeta")
    public void exportMeta(HttpServletResponse response) throws LoginException,
            RepositoryException, IOException {
        response.setContentType("application/xml");
        repositoryService.exportRepository(response.getOutputStream());
    }

    @RequestMapping(value="/importmeta", method=RequestMethod.POST)
    public APIResponse importMeta(InputStream in)
            throws LoginException, RepositoryException, IOException {
        repositoryService.importRepository(in);
        return new APIResponse(200, "OK");
    }

    @RequestMapping(value="/backup", method=RequestMethod.POST, produces="application/json")
    public APIResponse backupRepository(
            @RequestParam(value="backupBaseDir") String backupBaseDir,
            @RequestParam(value="includeBinaries", required=false) boolean includeBinaries) {
        try {
            repositoryService.backupRepository(backupBaseDir, includeBinaries);
            return new APIResponse(200, "OK");
        } catch (Exception e) {
            throw new APIException(500, e.getMessage(), e);
        }
    }

    @RequestMapping(value="/restore", method=RequestMethod.POST, produces="application/json")
    public APIResponse backupRepository(
            @RequestParam(value="backupBaseDir") String backupBaseDir,
            @RequestParam(value="includeBinaries", required=false) boolean includeBinaries,
            @RequestParam(value="reindex", required=false) boolean reindexContentOnFinish) {
        try {
        repositoryService.restoreRepository(backupBaseDir, includeBinaries,
                reindexContentOnFinish);
            return new APIResponse(200, "OK");
        } catch (Exception e) {
            throw new APIException(500, e.getMessage(), e);
        }
    }
}
