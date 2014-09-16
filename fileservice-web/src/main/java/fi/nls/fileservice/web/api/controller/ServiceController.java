package fi.nls.fileservice.web.api.controller;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;
import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import fi.nls.fileservice.jcr.repository.MetadataUpdateService;
import fi.nls.fileservice.jcr.service.RepositoryService;

@Controller
@RequestMapping("/service")
public class ServiceController {

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private MetadataUpdateService updateService;

    protected ResponseEntity<String> getHttpResponse(String message,
            HttpStatus status) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "text/plain");
        ResponseEntity<String> entity = new ResponseEntity<String>(message,
                headers, status);
        return entity;
    }

    @RequestMapping(value = "/updatemetadata", method = RequestMethod.POST)
    public ResponseEntity<String> updateMetadata(@RequestBody String body) {
        String[] paths = body.split("\\r?\\n");

        updateService.updateMetadataAsync(paths);
        return getHttpResponse("Queued " + paths.length
                + " file(s) for metadata update..\r\n", HttpStatus.ACCEPTED);

    }

    @RequestMapping(value = "/reindex", method = RequestMethod.POST)
    public ResponseEntity<String> reindex(
            @RequestParam(required = false, value = "path") String path)
            throws LoginException, RepositoryException {
        repositoryService.reindex(path);

        String message = path == null ? "Reindexing all content.\r\n"
                : "Reindexing path: " + path + ".\r\n";
        return getHttpResponse(message, HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/exportmeta")
    public void exportMeta(HttpServletResponse response) throws LoginException,
            RepositoryException, IOException {
        response.setContentType("application/xml");
        repositoryService.exportRepository(response.getOutputStream());
    }

    @RequestMapping(value = "/importmeta", method = RequestMethod.POST)
    public ResponseEntity<String> importMeta(InputStream in)
            throws LoginException, RepositoryException, IOException {
        repositoryService.importRepository(in);
        return getHttpResponse("OK.\r\n", HttpStatus.OK);
    }

}
