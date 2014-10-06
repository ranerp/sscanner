package com.twtchnz.superscannr.superscanner.converter;

import com.hp.gagawa.java.Document;
import com.hp.gagawa.java.DocumentType;
import com.hp.gagawa.java.Node;
import com.hp.gagawa.java.elements.*;
import com.twtchnz.superscannr.superscanner.utils.ExcelUnitToPixel;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;

import static org.apache.poi.ss.usermodel.CellStyle.*;

import java.util.*;
import java.util.Map;

public class XlsSheetToHtmlConverter {

    private HSSFWorkbook workbook;
    private HSSFSheet sheet;

    private Div mainContainer;
    private Div tableContainer;

    private int firstColumn = 0;
    private int lastColumn = 0;
    private int firstRow = 0;
    private int lastRow = 0;

    private int defaultColumnWidth = 50;
    private int defaultRowHeight = 20;

    private int topFillerWidth = defaultRowHeight;
    private int topFillerHeight = defaultRowHeight;

    private int viewPortWidth = 0;
    private int viewPortHeight = 0;

    private static final String MAIN_CONTAINER = "main_container";
    private static final String TABLE_CONTAINER = "table_container";
    private static final String DEFAULT_TABLE = "default_table";
    private static final String ROW_HEADER = "row_header";
    private static final String COLUMN_HEADER = "column_header";
    private static final String FILLER_COLOR = "filler_color";
    private static final String CENTER_HEADER_CLASS = "center_header";

    private RowHeader[] rowHeaders;
    private ColumnHeader[] columnHeaders;
    private Table table;

    private ArrayList<MergedRegion> mergedRegions;

    private static final Map<Short, String> ALIGN = mapFor(ALIGN_GENERAL, "left", ALIGN_LEFT, "left",
            ALIGN_CENTER, "center", ALIGN_RIGHT, "right", ALIGN_FILL, "left",
            ALIGN_JUSTIFY, "justify", ALIGN_CENTER_SELECTION, "center");

    private static final Map<Short, String> VERTICAL_ALIGN = mapFor(
            VERTICAL_BOTTOM, "bottom", VERTICAL_CENTER, "middle", VERTICAL_TOP,
            "top");

    private static final Map<Short, String> BORDER = mapFor(BORDER_DASH_DOT,
            "1px dashed", BORDER_DASH_DOT_DOT, "1px dashed", BORDER_DASHED,
            "1px dashed", BORDER_DOTTED, "1px dotted", BORDER_DOUBLE,
            "3px double", BORDER_HAIR, "1px double", BORDER_MEDIUM, "2px solid",
            BORDER_MEDIUM_DASH_DOT, "dashed 2pt", BORDER_MEDIUM_DASH_DOT_DOT,
            "2px dashed", BORDER_MEDIUM_DASHED, "2px dashed", BORDER_NONE,
            "none", BORDER_SLANTED_DASH_DOT, "2px dashed", BORDER_THICK,
            "3px solid", BORDER_THIN, "1px double");

    @SuppressWarnings({"unchecked"})
    private static <K, V> Map<K, V> mapFor(java.lang.Object... mapping) {
        Map<K, V> map = new HashMap<K, V>();
        for (int i = 0; i < mapping.length; i += 2) {
            map.put((K) mapping[i], (V) mapping[i + 1]);
        }
        return map;
    }

    public XlsSheetToHtmlConverter(HSSFWorkbook workbook, HSSFSheet sheet, int viewPortWidth, int viewPortHeight) {
        this.workbook = workbook;
        this.sheet = sheet;
        this.viewPortWidth = viewPortWidth;
        this.viewPortHeight = viewPortHeight;

        setBounds();
    }

    private void setBounds() {
        lastRow = Math.round(viewPortHeight / defaultRowHeight);
        lastRow = Math.max(lastRow, sheet.getLastRowNum() + 1);
        lastColumn = Math.round(viewPortWidth / defaultColumnWidth);

        Iterator<Row> iterator = sheet.rowIterator();
        firstColumn = (iterator.hasNext() ? Integer.MAX_VALUE : 0);

        while (iterator.hasNext()) {
            Row row = iterator.next();
            short firstCell = row.getFirstCellNum();

            if (firstCell >= 0) {
                firstColumn = Math.min(firstColumn, firstCell);
                lastColumn = Math.max(lastColumn, row.getLastCellNum());
            }
        }
    }

    public String write() {
        Document document = new Document(DocumentType.XHTMLTransitional);
        mergedRegions = new ArrayList<MergedRegion>();

        Link link = new Link();
        link.setRel("stylesheet");
        link.setHref("css/xls_style.css");
        document.head.appendChild(link);

        Script scriptJquery = new Script("text/javascript");
        scriptJquery.setSrc("js/jquery-2.1.1.min.js");
        document.head.appendChild(scriptJquery);

        Script scriptMain = new Script("text/javascript");
        scriptMain.setSrc("js/scroll.js");
        document.head.appendChild(scriptMain);

        mainContainer = new Div();
        mainContainer.setCSSClass(MAIN_CONTAINER);

        tableContainer = new Div();
        tableContainer.setCSSClass(TABLE_CONTAINER);

        table = new Table();
        addClass(table, DEFAULT_TABLE);

        rowHeaders = new RowHeader[lastRow];
        columnHeaders = new ColumnHeader[lastColumn];

        fillHeaders();
        fillMergedRegionArray();
        calculateDimensions();

        fillTable();
        fillDocument();

        Meta metaWidth = new Meta("width=" + calculateViewPortWidth());
        metaWidth.setName("viewport");
        document.head.appendChild(metaWidth);


        document.body.appendChild(mainContainer);
        return document.write();
    }

    private void fillHeaders() {
        for (int i = 0; i < rowHeaders.length; i++)
            rowHeaders[i] = new RowHeader(defaultRowHeight, new Text(i + 1));

        for (int i = 0; i < columnHeaders.length; i++)
            columnHeaders[i] = new ColumnHeader(defaultColumnWidth, new Text(getTHString(i)));
    }

    private void fillMergedRegionArray() {

        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress cellRangeAddress = sheet.getMergedRegion(i);
            mergedRegions.add(new MergedRegion(cellRangeAddress.getFirstRow(), cellRangeAddress.getLastRow(),
                                cellRangeAddress.getFirstColumn(), cellRangeAddress.getLastColumn()));
        }

    }

    private String getTHString(int column) {
        int dividend = column + 1;
        int modulo;
        int alphabetLength = 'Z' - 'A' + 1;
        StringBuilder stringBuilder = new StringBuilder();

        do {
            modulo = (dividend - 1) % alphabetLength;
            stringBuilder.insert(0, (char)('A' + modulo));
            dividend = (dividend - modulo) / alphabetLength;
        } while (dividend > 0);

        return stringBuilder.toString();
    }

    private void calculateDimensions() {
        for (int i = 0; i < lastRow; i++) {
            Row row = sheet.getRow(i);

            if (row != null) {
                rowHeaders[i].setMaxHeight(ExcelUnitToPixel.heightUnits2Pixel(row.getHeight()));

                for (int j = 0; j < lastColumn; j++) {
                    Cell cell = row.getCell(j);

                    if (cell != null)
                        columnHeaders[j].setMaxWidth(ExcelUnitToPixel.widthUnits2Pixel((short) sheet.getColumnWidth(j)));
                }
            }
        }
    }

    private int calculateViewPortWidth() {
        int width = topFillerWidth;
        for (int i = 0; i < columnHeaders.length; i++) {
            width += columnHeaders[i].getWidth();
        }

        return width;
    }

    private void fillTable() {
        Thead thead = new Thead();
        Tr theadTr = new Tr();

        Th fillerTh = new Th();
        addClass(fillerTh, FILLER_COLOR);
        addStyle(fillerTh, "width", defaultRowHeight + "px");
        addStyle(fillerTh, "height", defaultRowHeight + "px");
        theadTr.appendChild(fillerTh);

        for (ColumnHeader columnHeader : columnHeaders) {
            Th th = new Th();
            addClass(th, CENTER_HEADER_CLASS);
            addClass(th, COLUMN_HEADER);
            addStyle(th, "width", columnHeader.getWidth() + "px");
            theadTr.appendChild(th.appendChild(columnHeader.getText()));
        }

        thead.appendChild(theadTr);

        Tbody tbody = new Tbody();
        for (int rowIndex = firstRow; rowIndex < lastRow; rowIndex++) {
            Row row = sheet.getRow(rowIndex);

            Tr tr = new Tr();

            Th th = new Th();
            addStyle(th, "height", rowHeaders[rowIndex].getHeight() + "px");
            addClass(th, CENTER_HEADER_CLASS);
            addClass(th, ROW_HEADER);
            th.appendChild(rowHeaders[rowIndex].getText());
            tr.appendChild(th);

            for (int columnIndex = firstColumn; columnIndex < lastColumn; columnIndex++) {
                Cell cell = null;
                MergedRegion mergedRegion = getMergedRegion(rowIndex, columnIndex);


                Td td = new Td();
                td.setColspan(String.valueOf(mergedRegion.getColSpan()));
                td.setRowspan(String.valueOf(mergedRegion.getRowSpan()));

                if(row != null)
                    cell = row.getCell(columnIndex);

                if (cell != null && !mergedRegion.isUsed()) {

                    CellStyle cellStyle = cell.getCellStyle();

                    P p = getParagraph(cell, cellStyle);

                    setCellStyle(td, cellStyle);
                    setFontStyle(p, cellStyle);
                    setTextAlign(td, cellStyle);

                    td.appendChild(p);
                    tr.appendChild(td);

                    mergedRegion.setUsed();
                } else if(!mergedRegion.isUsed()) {
                    tr.appendChild(td);
                }
            }

            tbody.appendChild(tr);
        }

        table.appendChild(thead);
        table.appendChild(tbody);
        tableContainer.appendChild(table);
    }

    private String getRGB(HSSFColor color) {

        short[] triplet = color.getTriplet();

        String rgb = "rgb(";
        for (int i = 0; i < 3; i++) {
            rgb += triplet[i];

            if(i != 2)
                rgb += ",";
        }
        rgb += ")";

        return rgb;
    }

    private MergedRegion getMergedRegion(int row, int column) {
        for (MergedRegion mergedRegion : mergedRegions) {
            if(mergedRegion.isInRegion(row, column))
                return mergedRegion;
        }

        return new MergedRegion(row, row, column, column);
    }

    private void fillDocument() {
        mainContainer.appendChild(tableContainer);
    }

    private void setCellStyle(Node node, CellStyle cellStyle) {
        short leftBorder = cellStyle.getBorderLeft();
        short rightBorder = cellStyle.getBorderRight();
        short topBorder = cellStyle.getBorderTop();
        short bottomBorder = cellStyle.getBorderBottom();

        HSSFColor bgColor = HSSFColor.getIndexHash().get((int) cellStyle.getFillForegroundColor());
        if (bgColor != null) {
            addStyle(node, "background-color", getRGB(bgColor));
        }

        if (leftBorder != BORDER_NONE) {
            String style = BORDER.get(leftBorder);
            HSSFColor leftColor = HSSFColor.getMutableIndexHash().get((int) cellStyle.getLeftBorderColor());

            if(leftColor != null)
                style += " " + getRGB(leftColor);

            style += " !important";
            addStyle(node, "border-left", style);
        } else if (bgColor != null) {
            addStyle(node, "border-left", "0 double white !important");
        }

        if (rightBorder != BORDER_NONE) {
            String style = BORDER.get(rightBorder);

            HSSFColor rightColor = HSSFColor.getMutableIndexHash().get((int) cellStyle.getRightBorderColor());
            if(rightColor != null)
                style += " " + getRGB(rightColor);

            style += " !important";
            addStyle(node, "border-right", style);
        } else if (bgColor != null) {
            addStyle(node, "border-right", "0 double white !important");
        }

        if (topBorder != BORDER_NONE) {
            String style = BORDER.get(topBorder);

            HSSFColor topColor = HSSFColor.getMutableIndexHash().get((int) cellStyle.getTopBorderColor());
            if(topColor != null)
                style += " " + getRGB(topColor);

            style += " !important";
            addStyle(node, "border-top", style);
        } else if (bgColor != null) {
            addStyle(node, "border-top", "0 double white !important");
        }

        if (bottomBorder != BORDER_NONE) {
            String style = BORDER.get(bottomBorder);

            HSSFColor bottomColor = HSSFColor.getMutableIndexHash().get((int) cellStyle.getBottomBorderColor());
            if(bottomColor != null)
                style += " " + getRGB(bottomColor);

            style += " !important";
            addStyle(node, "border-bottom", style);
        } else if (bgColor != null) {
            addStyle(node, "border-bottom", "0 double white !important");
        }
    }

    private void setTextAlign(Td td, CellStyle style) {
        addStyle(td, "text-align", style.getAlignment(), ALIGN);
        addStyle(td, "vertical-align", style.getVerticalAlignment(), VERTICAL_ALIGN);
    }

    private void setFontStyle(P p, CellStyle style) {
        Font font = workbook.getFontAt(style.getFontIndex());

        if(font.getBoldweight() > HSSFFont.BOLDWEIGHT_NORMAL)
            addStyle(p, "font-weight", "bold");
        if(font.getItalic())
            addStyle(p, "font-style", "italic");

        int fontHeight = font.getFontHeightInPoints();
        if(fontHeight == 9)
            fontHeight = 10;

        addStyle(p, "font-size", fontHeight + "pt");

        HSSFColor color = HSSFColor.getIndexHash().get((int) font.getColor());
        if (color != null)
            addStyle(p, "color", getRGB(color));

    }

    private P getParagraph(Cell cell, CellStyle cellStyle) {
        P p = new P();
        DataFormatter dataFormatter = new DataFormatter();
        switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_BLANK:
                p.appendChild(new Text(""));
                break;
            case HSSFCell.CELL_TYPE_NUMERIC:
                if(cellStyle.getAlignment() == ALIGN_GENERAL)
                    cellStyle.setAlignment(ALIGN_RIGHT);

                p.appendChild(new Text(dataFormatter.formatCellValue(cell)));
                break;
            case HSSFCell.CELL_TYPE_STRING:
                p.appendChild(new Text(cell.getStringCellValue()));
                break;
            case HSSFCell.CELL_TYPE_FORMULA:
                FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
                formulaEvaluator.evaluateInCell(cell);

                if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC && cellStyle.getAlignment() == ALIGN_GENERAL)
                    cellStyle.setAlignment(ALIGN_RIGHT);

                p.appendChild(new Text(dataFormatter.formatCellValue(cell)));
                break;
            case HSSFCell.CELL_TYPE_BOOLEAN:
                p.appendChild(new Text(String.valueOf(cell.getBooleanCellValue())));
                break;
            default:
                p.appendChild(new Text(cell.toString()));
                break;
        }

        return p;
    }

    private <K> void addStyle(Node node, String attribute, K key, Map<K, String> map) {
        String style;
        style = node.getAttribute("style");
        if(style == null)
            style = "";

        String value = map.get(key);
        if(value != null)
            style += attribute + ": " + value + ";";

        node.setAttribute("style", style);
    }

    private void addStyle(Node node, String attribute, String value) {
        String style = node.getAttribute("style");
        if(style == null)
            style = "";

        style += attribute + ": " + value + ";";
        node.setAttribute("style", style);
    }

    private void addClass(Node Node, String aClass) {
        String classes = Node.getAttribute("class");
        if(classes == null)
            classes = "";

        classes += aClass + " ";

        Node.setAttribute("class", classes);
    }
}
