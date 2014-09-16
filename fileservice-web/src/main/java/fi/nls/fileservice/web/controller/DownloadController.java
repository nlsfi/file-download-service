package fi.nls.fileservice.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class DownloadController extends BaseController {

    @RequestMapping(value = { "/lataus/**" })
    public String getIndex(HttpServletRequest request,
            HttpServletResponse response, Model model) throws IOException {
        return doFileRequest(request, response, model, true, true);
    }

    @RequestMapping(value = { "/tilauslataus/**" }, method = RequestMethod.HEAD)
    public void fileHeaders(HttpServletRequest request,
            HttpServletResponse response, Model model) throws IOException {
        doFileRequest(request, response, model, false, false);
    }

    @RequestMapping(value = { "/tilauslataus/**" }, method = RequestMethod.GET)
    public void downloadFile(HttpServletRequest request,
            HttpServletResponse response, Model model) throws IOException {
        doFileRequest(request, response, model, true, false);
    }

}
