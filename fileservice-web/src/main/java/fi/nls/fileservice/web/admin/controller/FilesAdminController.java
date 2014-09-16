package fi.nls.fileservice.web.admin.controller;

import java.io.IOException;
import java.security.AccessControlException;

import javax.inject.Inject;
import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import fi.nls.fileservice.files.DetachedNode;
import fi.nls.fileservice.files.FileService;
import fi.nls.fileservice.web.controller.BaseController;

@Controller
@RequestMapping("/hallinta")
public class FilesAdminController extends BaseController {

    @Inject
    private FileService fileService;

    @RequestMapping(value = "/tiedostot/**", method = RequestMethod.GET)
    public String showDirectoryListing(HttpServletRequest request, Model model) {
        String path = getPath(request);
        DetachedNode detachedNode = fileService.getNode(path);
        model.addAttribute("node", detachedNode);
        return "admin/files";
    }

    @RequestMapping(value = "/lataus/**", method = RequestMethod.GET)
    public void download(HttpServletRequest request,
            HttpServletResponse response) throws LoginException,
            RepositoryException, AccessControlException, IOException {
        super.doFileRequest(request, response, null, true, false);
    }

    @RequestMapping(value = "/tiedostot/**", method = RequestMethod.POST)
    public String setProperties(
            @ModelAttribute("node") DetachedNode detachedNode,
            BindingResult result, Model model, HttpServletRequest request)
            throws LoginException, RepositoryException {

        String path = getPath(request);

        if (result.hasErrors()) {
            return "admin/files";
        }

        fileService.saveProperties(path, detachedNode.getProperties());
        return "redirect:/hallinta/tiedostot" + path;
    }

    @RequestMapping(value = "/tiedostot/delete/**", method = RequestMethod.POST)
    public @ResponseBody
    String deleteItem(@RequestParam(value = "_method") String method,
            HttpServletRequest request) throws LoginException,
            RepositoryException {

        if ("delete".equals(method)) {
            String path = getPath(request);
            fileService.delete(path);
            return "OK";
        }

        throw new IllegalArgumentException("Unsupported _method: " + method);
    }

}
