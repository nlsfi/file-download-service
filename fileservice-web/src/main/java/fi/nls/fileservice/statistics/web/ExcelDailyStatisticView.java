package fi.nls.fileservice.statistics.web;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import fi.nls.fileservice.statistics.DailyOrders;
import fi.nls.fileservice.statistics.DatasetStatistics;
import fi.nls.fileservice.statistics.PivotStatistics;

public class ExcelDailyStatisticView extends AbstractXLSXView {

    @Override
    protected void buildExcelDocument(Map<String, Object> model,
            XSSFWorkbook workbook, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        String[] alpha26 = new String[] { "A", "B", "C", "D", "E", "F", "G",
                "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S",
                "T", "U", "V", "W", "X", "Y", "Z", "AA", "BB", "CC", "DD", "EE", "FF", "GG", "HH", "II", "JJ", "KK", "LL", "MM", "NN",
                "OO","PP","QQ","RR","SS","TT", "UU","VV","WW","XX","YY","ZZ"};

        PivotStatistics pivotStats = (PivotStatistics) model.get("statistics");

        XSSFSheet sheet = workbook.createSheet("Tiedostot");
        XSSFRow header = sheet.createRow(0);

        XSSFSheet sheetLength = workbook.createSheet("Tiedonsiirto");
        XSSFRow headerLength = sheetLength.createRow(0);

        XSSFCellStyle headerStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        headerStyle.setBorderBottom((short) 1);
        // header.setRowStyle(headerStyle); doesn't work correctly

        String[] headers = pivotStats.getDatasetTitles();
        header.createCell(0).setCellValue("Päivä");
        // header.getCell(0).setCellStyle(headerStyle);
        headerLength.createCell(0).setCellValue("Päivä");
        // headerLength.getCell(0).setCellStyle(headerStyle);
        for (int i = 1; i < headers.length; i++) {
            Cell cell = header.createCell(i);
            // cell.setCellStyle(headerStyle);
            cell.setCellValue(headers[i]);
            cell = headerLength.createCell(i);
            // cell.setCellStyle(headerStyle);
            cell.setCellValue(headers[i]);
        }

        header.createCell(headers.length).setCellValue("Yhteensä");
        headerLength.createCell(headers.length).setCellValue("Yhteensä");

        int rowNum = 1;
        for (int i = 0; i < pivotStats.getDailyStats().size(); i++) {
            XSSFRow rowFiles = sheet.createRow(rowNum);
            XSSFRow rowLength = sheetLength.createRow(rowNum);
            rowNum++;

            Date day = pivotStats.getDays().get(i);
            rowFiles.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(day);
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("m/d/yy"));
            rowFiles.getCell(0).setCellStyle(cellStyle);

            rowLength.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(day);
            rowLength.getCell(0).setCellStyle(cellStyle);

            List<DatasetStatistics> dailyStats = pivotStats.getDailyStats().get(i);

            for (int j = 0; j < dailyStats.size(); j++) {
                DatasetStatistics stat = dailyStats.get(j);
                if (stat != null) {
                    rowFiles.createCell(j + 1, Cell.CELL_TYPE_NUMERIC).setCellValue(stat.getTotalDownloads());
                    rowLength.createCell(j + 1, Cell.CELL_TYPE_NUMERIC).setCellValue(stat.getTotalBytesTransferred());
                }
            }

            String formula = "SUM(B" + rowNum + ":" + alpha26[headers.length - 1] + rowNum + ")";
            rowFiles.createCell(headers.length, Cell.CELL_TYPE_FORMULA).setCellFormula(formula);
            rowLength.createCell(headers.length, Cell.CELL_TYPE_FORMULA).setCellFormula(formula);

        }

        XSSFRow totalRow = sheet.createRow(rowNum);
        XSSFRow totalLengthRow = sheetLength.createRow(rowNum);

        // sheet.autoSizeColumn(0);
        // sheet.setColumnWidth(0, 10);
        // sheetLength.autoSizeColumn(0);
        // sheetLength.setColumnWidth(0, 100);

        totalRow.createCell(0, Cell.CELL_TYPE_STRING).setCellValue("Yhteensä");
        totalLengthRow.createCell(0, Cell.CELL_TYPE_STRING).setCellValue("Yhteensä");

        for (int i = 1; i <= headers.length; i++) {
            totalRow.createCell(i, Cell.CELL_TYPE_FORMULA).setCellFormula(
                    "SUM(" + alpha26[i] + "2:" + alpha26[i]
                            + totalRow.getRowNum() + ")");
            totalLengthRow.createCell(i, Cell.CELL_TYPE_FORMULA)
                    .setCellFormula(
                            "SUM(" + alpha26[i] + "2:" + alpha26[i]
                                    + totalLengthRow.getRowNum() + ")");

            sheet.autoSizeColumn(i);
            sheetLength.autoSizeColumn(i);
        }

        // daily orders, sheet 3
        @SuppressWarnings("unchecked")
        List<DailyOrders> dailyOrders = (List<DailyOrders>) model.get("dailyOrders");
        XSSFSheet ordersSheet = workbook.createSheet("Tilaukset");
        XSSFRow ordersHeader = ordersSheet.createRow(0);
        ordersHeader.createCell(0).setCellValue("Päivä");
        ordersHeader.createCell(1).setCellValue("Tilauksia");

        int rowNumber = 1;
        for (DailyOrders order : dailyOrders) {
            XSSFRow row = ordersSheet.createRow(rowNumber++);

            Date day = order.getDay();
            row.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(day);
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("m/d/yy"));
            row.getCell(0).setCellStyle(cellStyle);
            row.createCell(1, Cell.CELL_TYPE_NUMERIC).setCellValue(order.getCount());
        }

        XSSFRow totalOrdersRow = ordersSheet.createRow(rowNumber);
        totalOrdersRow.createCell(0, Cell.CELL_TYPE_STRING).setCellValue("Yhteensä");
        totalOrdersRow.createCell(1, Cell.CELL_TYPE_FORMULA).setCellFormula("SUM(B2:B" + Integer.toString(rowNumber) + ")");

    }

}
