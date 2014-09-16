package fi.nls.fileservice.web.admin.controller;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import fi.nls.fileservice.common.DataAccessException;
import fi.nls.fileservice.security.service.UserService;
import fi.nls.fileservice.security.web.UserDetails;
import fi.nls.fileservice.system.Status;
import fi.nls.fileservice.system.StatusChecker;
import fi.nls.fileservice.web.controller.BaseController;

@Controller
@RequestMapping("/hallinta")
public class AdminController extends BaseController {

    @Inject
    private UserService permissionsService;

    @Inject
    private StatusChecker statusChecker;

    @RequestMapping(value = { "", "/" })
    public String index() {
        return "redirect:/hallinta/tuotteet";
    }

    @RequestMapping(value = { "/oikeudet" }, method = RequestMethod.GET)
    public String permissions(
            @RequestParam(value = "name", required = false) String name,
            Model model) throws IOException {

        if (name != null && !"".equals(name)) {

            try {
                UserDetails permissions = permissionsService
                        .getUserDetails(name);

                if (permissions == null) {
                    model.addAttribute("errorMessage", "Käyttäjää " + name
                            + " ei löydy");
                } else {
                    model.addAttribute("userInformation",
                            permissions.getUserAttributes());
                    model.addAttribute("permissions", permissions);
                }

            } catch (DataAccessException e) {
                model.addAttribute("errorMessage",
                        "Käyttäjän tietoja hakiessa tapahtui odottamaton virhetilanne.");
            }
        }

        return "admin/permissions";
    }

    @RequestMapping(value = "/oikeudet", method = RequestMethod.POST)
    public String storeAcl(@Valid UserDetails permissions,
            BindingResult result, Model model) throws IOException {

        if (result.hasErrors()) {
            return "admin/permissions";
        }

        permissionsService.savePermissions(permissions);

        return "redirect:/hallinta/oikeudet";
    }

    @RequestMapping(value = "/status")
    public String status(HttpServletResponse response, Model model) {
        Status status = statusChecker.getStatus();

        if (!status.isStatusOk()) {
  //         response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }

        model.addAttribute("status", status);

        return "admin/status";
    }

}
