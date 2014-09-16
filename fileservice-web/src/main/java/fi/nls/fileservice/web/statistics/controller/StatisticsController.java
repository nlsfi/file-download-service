package fi.nls.fileservice.web.statistics.controller;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;

import fi.nls.fileservice.statistics.DatasetStatistics;
import fi.nls.fileservice.statistics.ServiceOrders;
import fi.nls.fileservice.statistics.StatisticsService;
import fi.nls.fileservice.statistics.web.ExcelStatisticView;
import fi.nls.fileservice.web.controller.BaseController;

@Controller
public class StatisticsController extends BaseController {

    @Inject
    private StatisticsService statisticsService;

    @InitBinder
    public void binder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                try {
                    setValue(new SimpleDateFormat("dd.MM.yyyy").parse(value));
                } catch (ParseException e) {
                    setValue(null);
                }
            }

            @Override
            public String getAsText() {
                return new SimpleDateFormat("dd.MM.yyyy")
                        .format((Date) getValue());
            }

        });
    }

    @RequestMapping("/tilastot")
    public String statistics(Locale locale, Model model) {
        List<DatasetStatistics> stats = statisticsService.getTotalStats(locale);
        ServiceOrders serviceOrders = statisticsService.getOrderCount();
        model.addAttribute("statistics", stats);
        model.addAttribute("orders", serviceOrders.getOrderCount());
        return "statistics";
    }

    /*
     * @RequestMapping("/tilastot/raportti.xlsx") public ModelAndView
     * statisticsReport(@RequestParam(value="from",required=false) Date from,
     * 
     * @RequestParam(value="to",required=false) Date to, Locale locale,
     * ModelAndView model) {
     * 
     * PivotStatistics stats = statisticsService.getDailyStatistics(from, to,
     * locale); model.addObject("statistics", stats);
     * 
     * List<DailyOrders> dailyOrders = statisticsService.getDailyOrders();
     * model.addObject("dailyOrders", dailyOrders); model.addObject("filename",
     * getFilenameWithDate("tilasto", new Date(), "xlsx")); model.setView(new
     * ExcelDailyStatisticView());
     * 
     * return model; }
     */

    @RequestMapping("/tilastot/kaikki.xlsx")
    public View statisticsExcel(Locale locale, Model model) {
        List<DatasetStatistics> stats = statisticsService.getTotalStats(locale,
                false);
        model.addAttribute("statistics", stats);

        ServiceOrders serviceOrders = statisticsService.getOrderCount();
        model.addAttribute("orders", serviceOrders.getOrderCount());
        model.addAttribute("filename",
                getFilenameWithDate("tilasto", new Date(), "xlsx"));
        return new ExcelStatisticView();
    }

    private static String getFilenameWithDate(String basename, Date date,
            String suffix) {
        StringBuilder builder = new StringBuilder(basename);
        builder.append("-");
        builder.append(new SimpleDateFormat("yyyyMMddHHmmss").format(date));
        builder.append(".");
        builder.append(suffix);
        return builder.toString();
    }

}
