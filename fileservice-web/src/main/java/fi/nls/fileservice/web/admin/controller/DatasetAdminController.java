package fi.nls.fileservice.web.admin.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import fi.nls.fileservice.dataset.Dataset;
import fi.nls.fileservice.dataset.DatasetService;
import fi.nls.fileservice.dataset.DatasetVersion;
import fi.nls.fileservice.dataset.Licence;
import fi.nls.fileservice.dataset.crs.CrsDefinition;
import fi.nls.fileservice.util.JSONUtils;
import fi.nls.fileservice.web.controller.BaseController;

@Controller
@RequestMapping("/hallinta")
public class DatasetAdminController extends BaseController {

    @Inject
    private DatasetService datasetService;

    @Resource(name="distributionFormats")
    private Map<String, String> distributionFormats;
    
    @Resource(name="crsDefinitions")
    private Map<String, CrsDefinition> crsDefinitions;

    @RequestMapping(value="/tuotteet", method=RequestMethod.GET)
    public String datasetList(HttpServletRequest request, Model model) {
        List<Dataset> datasets = datasetService.getAllDatasets();

        // sort alphabetically by title
        Collections.sort(datasets, new Comparator<Dataset>() {

            @Override
            public int compare(Dataset d1, Dataset d2) {
                String d1Title = d1.getTranslatedTitles().get("fi");
                String d2Title = d2.getTranslatedTitles().get("fi");
                if (d1Title != null && d2Title != null) {
                    return d1Title.compareTo(d2Title);
                }

                return 0;
            }

        });

        model.addAttribute("datasets", datasets); // auto gen would give JCRNodeDatasetList
        return "admin/datasets";
    }

    @RequestMapping(value="/tuotteet/lisaa", method=RequestMethod.GET)
    public String newDataset(Model model) {
        model.addAttribute("dataset", new Dataset());
        return "admin/dataset_edit";
    }

    @RequestMapping(value="/tuotteet/{tuote}", method=RequestMethod.GET)
    public String editDataset(@PathVariable(value="tuote") String datasetID,
            Model model) {
        Dataset dataset = datasetService.getDatasetById(datasetID);
        model.addAttribute("dataset", dataset);
        model.addAttribute("licenses", Licence.values());
        return "admin/dataset_edit";
    }

    @RequestMapping(value="/tuotteet/", method=RequestMethod.POST)
    public String saveDataset(@ModelAttribute("dataset") @Valid Dataset dataset,
            BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "admin/dataset_edit";
        }

        datasetService.saveDataset(dataset);
        return "redirect:/hallinta/tuotteet/" + dataset.getName();
    }

    @RequestMapping(value="/tuotteet/delete/{tuote}", method=RequestMethod.POST)
    public @ResponseBody
    String deleteDataset(@RequestParam(value = "_method") String method,
            @PathVariable("tuote") String datasetID) {
        datasetService.deleteDataset(datasetID);
        return "OK";
    }

    @RequestMapping(value="/tuotteet/{tuote}/lisaa", method=RequestMethod.GET)
    public String newDatasetVersion(
            @PathVariable(value = "tuote") String datasetID, Model model) {
        Dataset dataset = datasetService.getDatasetById(datasetID);
        model.addAttribute("dataset", dataset);
        model.addAttribute("datasetName", datasetID);
        model.addAttribute("datasetVersion", new DatasetVersion());
        model.addAttribute("distributionFormats", distributionFormats);
        model.addAttribute("crsDefinitionsJSON", JSONUtils.object2JSON(crsDefinitions));
        return "admin/dataset_version_edit";
    }

    @RequestMapping(value="/tuotteet/{tuote}/{tuoteversio}", method=RequestMethod.GET)
    public String editDatasetVersion(
            @PathVariable(value = "tuote") String dataset,
            @PathVariable(value = "tuoteversio") String datasetVersionID, Model model) {
        DatasetVersion datasetVersion = datasetService.getDatasetVersion(dataset, datasetVersionID);
        model.addAttribute("datasetName", dataset);
        model.addAttribute("dataset", datasetVersion.getDataset());
        model.addAttribute("datasetVersion", datasetVersion);
        model.addAttribute("distributionFormats", distributionFormats);
        model.addAttribute("crsDefinitionsJSON", JSONUtils.object2JSON(crsDefinitions));
        return "admin/dataset_version_edit";
    }

    @RequestMapping(value="/tuotteet/{tuote}/versio", method=RequestMethod.POST)
    public String saveDatasetVersion(
            @PathVariable(value = "tuote") String datasetID,
            @Valid DatasetVersion datasetVersion, BindingResult result, Model model) {
        if (result.hasErrors()) {
            Dataset dataset = datasetService.getDatasetById(datasetID);
            model.addAttribute("dataset", dataset);
            model.addAttribute("datasetName", datasetID);
            model.addAttribute("distributionFormats", distributionFormats);
            model.addAttribute("crsDefinitionsJSON", JSONUtils.object2JSON(crsDefinitions));
            return "admin/dataset_version_edit";
        }
        datasetService.saveDatasetVersion(datasetID, datasetVersion);
        return "redirect:/hallinta/tuotteet/{tuote}";
    }

    @RequestMapping(value="/tuotteet/delete/{tuote}/{tuoteversio}", method=RequestMethod.POST)
    public @ResponseBody
    String deleteDatasetVersion(@RequestParam(value = "_method") String method,
            @PathVariable("tuote") String datasetID,
            @PathVariable("tuoteversio") String datasetVersionID) {
        datasetService.deleteDatasetVersion(datasetID, datasetVersionID);
        return "OK";
    }

}
