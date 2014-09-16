package fi.nls.fileservice.statistics.web;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import fi.nls.fileservice.statistics.DatasetStatistics;

public class ExcelStatisticView extends AbstractXLSXView {

    @SuppressWarnings("unchecked")
    @Override
    protected void buildExcelDocument(Map<String, Object> model,
            XSSFWorkbook workbook, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        List<DatasetStatistics> statistics = (List<DatasetStatistics>) model
                .get("statistics");

        XSSFSheet sheet = workbook
                .createSheet("Tiedostopalvelun lataustilasto");
        XSSFRow header = sheet.createRow(0);

        XSSFCellStyle headerStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        headerStyle.setBorderBottom((short) 1);
        // header.setRowStyle(headerStyle); doesn't work correctly

        header.createCell(0).setCellValue("Tuote");
        header.createCell(1).setCellValue("Tuoteversio");
        header.createCell(2).setCellValue("Tiedostot");
        header.createCell(3).setCellValue("Koko (tavuja)");
        // header.getCell(0).setCellStyle(headerStyle);
        // header.getCell(1).setCellStyle(headerStyle);
        // header.getCell(2).setCellStyle(headerStyle);
        // header.getCell(3).setCellStyle(headerStyle);

        int rowNum = 1;
        for (DatasetStatistics stat : statistics) {
            XSSFRow row = sheet.createRow(rowNum++);

            if (stat.getDatasetTitle() != null) {
                row.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(
                        stat.getDatasetTitle());
            }
            if (stat.getDatasetVersionTitle() != null) {
                row.createCell(1, Cell.CELL_TYPE_STRING).setCellValue(
                        stat.getDatasetVersionTitle());
            }

            row.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(
                    stat.getTotalDownloads());
            row.createCell(3, Cell.CELL_TYPE_NUMERIC).setCellValue(
                    stat.getTotalBytesTransferred());

        }

        XSSFRow totalRow = sheet.createRow(rowNum);
        totalRow.createCell(0, Cell.CELL_TYPE_STRING).setCellValue("Yhteensä");
        totalRow.createCell(2, Cell.CELL_TYPE_FORMULA).setCellFormula(
                "SUM(C2:C" + rowNum + ")");
        totalRow.createCell(3, Cell.CELL_TYPE_FORMULA).setCellFormula(
                "SUM(D2:D" + rowNum + ")");

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);

        Map<String, Integer> serviceOrders = (Map<String, Integer>) model
                .get("orders");
        if (serviceOrders != null) {

            XSSFSheet sheet2 = workbook
                    .createSheet("Tiedostopalvelun tilaukset");
            XSSFRow header2 = sheet2.createRow(0);

            header2.createCell(0).setCellValue("Tyyppi");
            header2.createCell(1).setCellValue("Tilauksia");
            header2.getCell(0).setCellStyle(headerStyle);
            header2.getCell(1).setCellStyle(headerStyle);

            rowNum = 1;
            for (Entry<String, Integer> entry : serviceOrders.entrySet()) {
                XSSFRow row = sheet2.createRow(rowNum++);
                row.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(
                        entry.getKey());
                row.createCell(1, Cell.CELL_TYPE_NUMERIC).setCellValue(
                        entry.getValue());
            }

            sheet2.autoSizeColumn(0);
            sheet2.autoSizeColumn(1);
        }

    }

}
