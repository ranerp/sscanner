package com.twtchnz.superscannr.superscanner.fragments;

import android.graphics.Point;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import com.itextpdf.text.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.joanzapata.pdfview.PDFView;
import com.twtchnz.superscannr.superscanner.R;
import com.twtchnz.superscannr.superscanner.resources.DatabaseEntities.*;
import com.twtchnz.superscannr.superscanner.resources.ResourceManager;
import com.twtchnz.superscannr.superscanner.utils.DateFormatter;
import com.twtchnz.superscannr.superscanner.utils.Utils;
import com.twtchnz.superscannr.superscanner.converter.XlsSheetToHtmlConverter;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class FileFragment extends Fragment implements com.joanzapata.pdfview.listener.OnPageChangeListener {

    private ResourceManager resourceManager;

    private TextView pdfFragmentTitle;
    private TextView pdfLocationTitle;
    private TextView pdfLocationView;

    private TextView xlsLocationTitle;
    private TextView xlsLocationView;

    private WebView xlsWebView;
    private Button viewXlsButton;
    private Button createXlsButton;
    private Button closeXlsButton;


    private Button viewPdfButton;
    private Button createPdfButton;
    private Button closePdfButton;

    private PDFView pdfView;

    private int columnCount = 5;

    private int pageNumber = 1;

    private float borderWidth = 0.5f;

    public FileFragment(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_file, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        pdfFragmentTitle = (TextView) getView().findViewById(R.id.pdfFragmentTitle);
        pdfLocationTitle = (TextView) getView().findViewById(R.id.pdfLocationTitle);
        pdfLocationView = (TextView) getView().findViewById(R.id.pdfLocationView);

        xlsLocationTitle = (TextView) getView().findViewById(R.id.xlsLocationTitle);
        xlsLocationView = (TextView) getView().findViewById(R.id.xlsLocationView);

        createXlsButton = (Button) getView().findViewById(R.id.createXlsButton);
        closeXlsButton = (Button) getView().findViewById(R.id.xlsCloseButton);
        viewXlsButton = (Button) getView().findViewById(R.id.viewXlsButton);

        createPdfButton = (Button) getView().findViewById(R.id.pdfCreateButton);
        closePdfButton = (Button) getView().findViewById(R.id.pdfCloseButton);
        viewPdfButton = (Button) getView().findViewById(R.id.viewPdfButton);

        pdfView = (PDFView) getView().findViewById(R.id.pdfView);
        pdfView.setVisibility(View.INVISIBLE);

        xlsWebView = (WebView) getView().findViewById(R.id.xlsWebView);
        xlsWebView.setVisibility(View.INVISIBLE);
        xlsWebView.getSettings().setBuiltInZoomControls(true);
        xlsWebView.getSettings().setDisplayZoomControls(true);
        xlsWebView.getSettings().setTextZoom(50);
        xlsWebView.getSettings().setUseWideViewPort(true);
        xlsWebView.getSettings().setJavaScriptEnabled(true);
        xlsWebView.setWebChromeClient(new WebChromeClient());

        closePdfButton.setEnabled(false);
        closePdfButton.setVisibility(View.INVISIBLE);

        closeXlsButton.setEnabled(false);
        closeXlsButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();

        initViews();
    }

    private void toggleViewButton(MainInfoObject mainInfoObject) {
        if(mainInfoObject.isPdfFileExists())
            viewPdfButton.setEnabled(true);
        else
            viewPdfButton.setEnabled(false);

        if(mainInfoObject.isXlsFileExists())
            viewXlsButton.setEnabled(true);
        else
            viewXlsButton.setEnabled(false);
    }

    private void initViews() {
        MainInfoObject mainInfoObject = resourceManager.getOrderInfo();
        toggleViewButton(mainInfoObject);
        setViews(mainInfoObject);
    }

    private void setViews(MainInfoObject mainInfoObject) {
        if(mainInfoObject.isPdfFileExists())
            pdfLocationView.setText(mainInfoObject.getPdfFilePath());
        else
            pdfLocationView.setText(getString(R.string.not_found));

        if(mainInfoObject.isXlsFileExists())
            xlsLocationView.setText(mainInfoObject.getXlsFilePath());
        else
            xlsLocationView.setText(getString(R.string.not_found));
    }

    private void createPDF() {
        try {
            Document document = resourceManager.startDocument();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
            Font regularFontBold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font regularFont  = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

            document.add(setHeader(titleFont, regularFont, regularFontBold));
            document.add(setTable(regularFont, regularFontBold));
            document.add(setFooter(regularFont, regularFontBold));

            resourceManager.closeDocument();

        } catch (DocumentException e) {
            Log.e(Utils.APP_TAG, e.getMessage());
        } catch (FileNotFoundException e) {
            Log.e(Utils.APP_TAG, e.getMessage());
        }
    }

    private PdfPTable setHeader(Font titleFont, Font regularFont, Font regularFontBold) throws DocumentException {
        PdfPTable table = new PdfPTable(columnCount);
        table.setWidthPercentage(90f);

        OrderHeaderObject orderHeaderObject = resourceManager.getActiveOrderHeader();
        MainInfoObject mainInfoObject = resourceManager.getOrderInfo();

        String date = DateFormatter.formatDate(Utils.DATE_FORMAT_DATABASE, mainInfoObject.getDate(), Utils.DATE_FORMAT_IN_FILE);

        PdfPCell titleCell = new PdfPCell(new Paragraph(orderHeaderObject.getTitle(), titleFont));
        titleCell.setColspan(columnCount);
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setPaddingBottom(50);
        titleCell.setPaddingTop(50);
        table.addCell(titleCell);

        PdfPCell companyCell = new PdfPCell(new Paragraph(orderHeaderObject.getCompany(), regularFontBold));
        companyCell.setColspan(3);
        companyCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(companyCell);

        PdfPCell dateTitleCell = new PdfPCell(new Paragraph("DATE:", regularFontBold));
        dateTitleCell.setBorder(Rectangle.NO_BORDER);
        dateTitleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(dateTitleCell);

        PdfPCell dateCell = new PdfPCell(new Paragraph(date, regularFont));
        dateCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(dateCell);

        PdfPCell addressCell = new PdfPCell(new Paragraph(orderHeaderObject.getAddress(), regularFont));
        addressCell.setColspan(columnCount);
        addressCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(addressCell);

        PdfPCell cityCell = new PdfPCell(new Paragraph(orderHeaderObject.getCity(), regularFont));
        cityCell.setColspan(3);
        cityCell.setBorder(Rectangle.BOTTOM);
        cityCell.setPaddingBottom(20);
        table.addCell(cityCell);

        PdfPCell fillerCell = new PdfPCell(new Paragraph());
        fillerCell.setColspan(2);
        fillerCell.setBorder(Rectangle.BOTTOM);
        fillerCell.setPaddingBottom(20);
        table.addCell(fillerCell);

        return table;
    }

    private PdfPTable setTable(Font regularFont, Font regularFontBold) throws  DocumentException {
        PdfPTable table = new PdfPTable(columnCount);
        table.setWidthPercentage(90f);
        table.setSpacingBefore(50f);
        table.setSpacingAfter(50f);

        float[] columnWidths = {1f, 3f, 3f, 3f, 1f};
        table.setWidths(columnWidths);

        BaseColor titleBackground = new BaseColor(122, 151, 40, 255);
        BaseColor secondRowBackground = new BaseColor(210, 210, 210, 255);
        BaseColor titleFontColor = new BaseColor(255, 255, 255, 255);

        Font whiteFont = new Font(regularFontBold.getFamily(), regularFontBold.getSize(), regularFontBold.getStyle(), titleFontColor);


        PdfPCell itemNumberTitle = new PdfPCell(new Paragraph("Item", whiteFont));
        itemNumberTitle.setPaddingTop(20);
        itemNumberTitle.setPaddingBottom(20);
        itemNumberTitle.setHorizontalAlignment(Element.ALIGN_CENTER);
        itemNumberTitle.setBackgroundColor(titleBackground);
        setCellWidths(itemNumberTitle, borderWidth, borderWidth, borderWidth, borderWidth);
        table.addCell(itemNumberTitle);

        PdfPCell orderNumberTitle = new PdfPCell(new Paragraph("Order number", whiteFont));
        orderNumberTitle.setPaddingTop(20);
        orderNumberTitle.setPaddingBottom(20);
        orderNumberTitle.setHorizontalAlignment(Element.ALIGN_CENTER);
        orderNumberTitle.setBackgroundColor(titleBackground);
        setCellWidths(orderNumberTitle, 0f, 0f, borderWidth, borderWidth);
        table.addCell(orderNumberTitle);

        PdfPCell materialCodeTitle = new PdfPCell(new Paragraph("Material code", whiteFont));
        materialCodeTitle.setPaddingTop(20);
        materialCodeTitle.setPaddingBottom(20);
        materialCodeTitle.setHorizontalAlignment(Element.ALIGN_CENTER);
        materialCodeTitle.setBackgroundColor(titleBackground);
        setCellWidths(materialCodeTitle, borderWidth, borderWidth, borderWidth, borderWidth);
        table.addCell(materialCodeTitle);

        PdfPCell salesOrderTitle = new PdfPCell(new Paragraph("Sales order", whiteFont));
        salesOrderTitle.setPaddingTop(20);
        salesOrderTitle.setPaddingBottom(20);
        salesOrderTitle.setHorizontalAlignment(Element.ALIGN_CENTER);
        salesOrderTitle.setBackgroundColor(titleBackground);
        setCellWidths(salesOrderTitle, 0f, 0f, borderWidth, borderWidth);
        table.addCell(salesOrderTitle);

        PdfPCell quantityTitle = new PdfPCell(new Paragraph("Qty.", whiteFont));
        quantityTitle.setPaddingTop(20);
        quantityTitle.setPaddingBottom(20);
        quantityTitle.setHorizontalAlignment(Element.ALIGN_CENTER);
        quantityTitle.setBackgroundColor(titleBackground);
        setCellWidths(quantityTitle, borderWidth, borderWidth, borderWidth, borderWidth);
        table.addCell(quantityTitle);

        ArrayList<BarCodeObject> barCodeObjects = resourceManager.getBarCodeObjects();

        float bottomBorderWidth;
        int i = 1;
        int itemCount = barCodeObjects.size();
        for (BarCodeObject object : barCodeObjects) {

            PdfPCell itemNumber = new PdfPCell(new Paragraph(String.valueOf(i), regularFont));
            itemNumber.setPadding(5f);
            itemNumber.setHorizontalAlignment(Element.ALIGN_CENTER);

            PdfPCell orderNumber = new PdfPCell(new Paragraph(String.valueOf(object.getOrderNumber()), regularFont));
            orderNumber.setPadding(5f);
            orderNumber.setHorizontalAlignment(Element.ALIGN_LEFT);

            PdfPCell materialCode = new PdfPCell(new Paragraph(String.valueOf(object.getMaterialCode()), regularFont));
            materialCode.setPadding(5f);
            materialCode.setHorizontalAlignment(Element.ALIGN_LEFT);

            PdfPCell salesOrder = new PdfPCell(new Paragraph(String.valueOf(object.getSalesOrder()), regularFont));
            salesOrder.setPadding(5f);
            salesOrder.setHorizontalAlignment(Element.ALIGN_LEFT);

            PdfPCell quantity = new PdfPCell(new Paragraph(String.valueOf(object.getCount()), regularFont));
            quantity.setPadding(5f);
            quantity.setHorizontalAlignment(Element.ALIGN_LEFT);

            if (i % 2 == 0) {
                itemNumber.setBackgroundColor(secondRowBackground);
                orderNumber.setBackgroundColor(secondRowBackground);
                materialCode.setBackgroundColor(secondRowBackground);
                salesOrder.setBackgroundColor(secondRowBackground);
                quantity.setBackgroundColor(secondRowBackground);
            }

            if(i == itemCount)
                bottomBorderWidth = borderWidth;
            else
                bottomBorderWidth = 0f;

            setCellWidths(itemNumber, borderWidth, borderWidth, 0f, bottomBorderWidth);
            setCellWidths(orderNumber, 0f, 0f, 0f, bottomBorderWidth);
            setCellWidths(materialCode, borderWidth, borderWidth, 0f, bottomBorderWidth);
            setCellWidths(salesOrder, 0f, 0f, 0f, bottomBorderWidth);
            setCellWidths(quantity, borderWidth, borderWidth, 0f, bottomBorderWidth);

            table.addCell(itemNumber);
            table.addCell(orderNumber);
            table.addCell(materialCode);
            table.addCell(salesOrder);
            table.addCell(quantity);

            i++;
        }

        return table;
    }

    private void setCellWidths(PdfPCell cell,float left, float right, float top, float bottom) {
        cell.setBorderColorLeft(BaseColor.BLACK);
        cell.setBorderColorRight(BaseColor.BLACK);
        cell.setBorderColorTop(BaseColor.BLACK);
        cell.setBorderColorBottom(BaseColor.BLACK);

        cell.setBorderWidthLeft(left);
        cell.setBorderWidthRight(right);
        cell.setBorderWidthTop(top);
        cell.setBorderWidthBottom(bottom);
    }

    public PdfPTable setFooter(Font regularFont, Font regularFontBold) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(90f);
        float[] columnWidths = {1f, 1f};
        table.setWidths(columnWidths);

        OrderFooterObject orderFooterObject = resourceManager.getActiveOrderFooter();

        PdfPCell scrapCopperTitle = new PdfPCell(new Paragraph(getString(R.string.scrap_copper), regularFontBold));
        scrapCopperTitle.setBorder(Rectangle.NO_BORDER);
        table.addCell(scrapCopperTitle);

        String scrapCopperString = orderFooterObject.getScrapCopper();
        if(scrapCopperString.isEmpty())
            scrapCopperString = "0";

        PdfPCell scrapCopper = new PdfPCell(new Paragraph(scrapCopperString + " kg", regularFont));
        scrapCopper.setBorder(Rectangle.NO_BORDER);
        table.addCell(scrapCopper);

        PdfPCell totalPalletsTitle = new PdfPCell(new Paragraph(getString(R.string.total_pallets), regularFontBold));
        totalPalletsTitle.setBorder(Rectangle.NO_BORDER);
        table.addCell(totalPalletsTitle);

        PdfPCell totalPallets = new PdfPCell(new Paragraph(orderFooterObject.getTotalPallets(), regularFont));
        totalPallets.setBorder(Rectangle.NO_BORDER);
        table.addCell(totalPallets);

        PdfPCell emptyReelsTitle = new PdfPCell(new Paragraph(getString(R.string.empty_reels), regularFontBold));
        emptyReelsTitle.setBorder(Rectangle.NO_BORDER);
        table.addCell(emptyReelsTitle);

        PdfPCell emptyReels = new PdfPCell(new Paragraph(orderFooterObject.getEmptyReels(), regularFont));
        emptyReels.setBorder(Rectangle.NO_BORDER);
        table.addCell(emptyReels);

        PdfPCell totalProductsTitle = new PdfPCell(new Paragraph(getString(R.string.total_products), regularFontBold));
        totalProductsTitle.setBorder(Rectangle.NO_BORDER);
        table.addCell(totalProductsTitle);

        PdfPCell totalProducts = new PdfPCell(new Paragraph(orderFooterObject.getTotalProducts(), regularFont));
        totalProducts.setBorder(Rectangle.NO_BORDER);
        table.addCell(totalProducts);



        return table;
    }

    private void createXLS() {
        MainInfoObject mainInfoObject = resourceManager.getOrderInfo();

        int rowAt = 0;

        try {
            HSSFWorkbook workbook = resourceManager.startWorkBook();

            HSSFSheet sheet = workbook.createSheet(mainInfoObject.getFileName());

            rowAt = setXlsHeader(mainInfoObject, workbook, sheet, rowAt);
            rowAt = setXlsTable(workbook, sheet, rowAt + 2);
            setXlsFooter(workbook, sheet, rowAt + 2);

            resourceManager.writeWorkBook(mainInfoObject);

        } catch (Exception e) {
            Log.e(Utils.APP_TAG, e.getMessage());
        }
    }

    private int setXlsHeader(MainInfoObject mainInfoObject, HSSFWorkbook workbook, HSSFSheet sheet, int startRow) {
        OrderHeaderObject orderHeaderObject = resourceManager.getActiveOrderHeader();
        Row row;
        Cell cell;
        int rowNum = startRow;

        HSSFFont titleFont = workbook.createFont();
        titleFont.setColor(HSSFColor.WHITE.index);
        titleFont.setBoldweight(org.apache.poi.ss.usermodel.Font.BOLDWEIGHT_BOLD);

        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setFillForegroundColor(HSSFColor.BLACK.index);
        titleStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        titleStyle.setAlignment(CellStyle.ALIGN_CENTER);
        titleStyle.setFont(titleFont);

        HSSFFont headerFont = workbook.createFont();
        headerFont.setBoldweight(org.apache.poi.ss.usermodel.Font.BOLDWEIGHT_BOLD);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setAlignment(CellStyle.ALIGN_CENTER);
        headerStyle.setFont(headerFont);

        CellStyle dateStyle = workbook.createCellStyle();
        CreationHelper creationHelper = workbook.getCreationHelper();
        dateStyle.setDataFormat(creationHelper.createDataFormat().getFormat("d-mmm-yy"));

        row = sheet.createRow(rowNum);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
        cell = row.createCell(0);
        cell.setCellValue(orderHeaderObject.getTitle());
        cell.setCellStyle(titleStyle);

        rowNum += 2;

        row = sheet.createRow(rowNum);
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 1));
        cell = row.createCell(0);
        cell.setCellValue(orderHeaderObject.getCompany());
        cell.setCellStyle(headerStyle);

        cell = row.createCell(5);
        cell.setCellValue(getString(R.string.date));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 6, 7));
        cell.setCellStyle(headerStyle);

        cell = row.createCell(6);
        String dateString = DateFormatter.formatDate(Utils.DATE_FORMAT_DATABASE, mainInfoObject.getDate(), Utils.DATE_FORMAT_IN_FILE);
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellValue(dateString);
        rowNum++;

        row = sheet.createRow(rowNum);
        cell = row.createCell(0);
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 1));
        cell.setCellValue(orderHeaderObject.getAddress());
        cell.setCellStyle(headerStyle);

        rowNum++;

        row = sheet.createRow(rowNum);
        cell = row.createCell(0);
        sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, 1));
        cell.setCellValue(orderHeaderObject.getCity());
        cell.setCellStyle(headerStyle);

        return rowNum;
    }

    private int setXlsTable(HSSFWorkbook workbook, HSSFSheet sheet, int startRow) {
        ArrayList<BarCodeObject> barCodeObjects = resourceManager.getBarCodeObjects();

        Row row;
        Cell cell;
        int rowNum = startRow;

        HSSFFont headerFont = workbook.createFont();
        headerFont.setBoldweight(org.apache.poi.ss.usermodel.Font.BOLDWEIGHT_BOLD);
        headerFont.setColor(HSSFColor.WHITE.index);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setAlignment(CellStyle.ALIGN_CENTER);
        headerStyle.setFillForegroundColor(HSSFColor.SEA_GREEN.index);
        headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        headerStyle.setFont(headerFont);

        HSSFFont itemNumFont = workbook.createFont();
        itemNumFont.setBoldweight(org.apache.poi.ss.usermodel.Font.BOLDWEIGHT_BOLD);

        CellStyle itemNumStyle = workbook.createCellStyle();
        itemNumStyle.setAlignment(CellStyle.ALIGN_CENTER);
        itemNumStyle.setFont(itemNumFont);

        CellStyle numberStyle = workbook.createCellStyle();
        numberStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));

        row = sheet.createRow(rowNum);

        cell = row.createCell(0);
        cell.setCellValue("Item");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(1);
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 1, 2));
        cell.setCellValue("Order number");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(3);
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 3, 4));
        cell.setCellValue("Material code");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(5);
        cell.setCellValue("Sales order");
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 5, 6));
        cell.setCellStyle(headerStyle);

        cell = row.createCell(7);
        cell.setCellValue("Qty.");
        cell.setCellStyle(headerStyle);

        rowNum++;

        int itemCount = 1;
        for (BarCodeObject barCodeObject : barCodeObjects) {
            row = sheet.createRow(rowNum);

            cell = row.createCell(0);
            cell.setCellValue(itemCount);
            cell.setCellStyle(itemNumStyle);

            cell = row.createCell(1);
            sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 1, 2));
            cell.setCellStyle(numberStyle);
            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
            cell.setCellValue(Double.parseDouble(barCodeObject.getOrderNumber()));

            cell = row.createCell(3);
            sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 3, 4));
            cell.setCellStyle(numberStyle);
            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
            cell.setCellValue(Double.parseDouble(barCodeObject.getMaterialCode()));

            cell = row.createCell(5);
            sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 5, 6));
            cell.setCellStyle(numberStyle);
            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
            cell.setCellValue(Double.parseDouble(barCodeObject.getSalesOrder()));

            cell = row.createCell(7);
            cell.setCellStyle(numberStyle);
            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
            cell.setCellValue(barCodeObject.getCount());

            rowNum++;
            itemCount++;
        }

        return rowNum;
    }

    private int setXlsFooter(HSSFWorkbook workbook, HSSFSheet sheet, int startRow) {
        OrderFooterObject orderFooterObject = resourceManager.getActiveOrderFooter();

        int rowNum = startRow;
        Row row;
        Cell cell;

        HSSFFont headerFont = workbook.createFont();
        headerFont.setBoldweight(org.apache.poi.ss.usermodel.Font.BOLDWEIGHT_BOLD);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);

        CellStyle noDecimalStyle = workbook.createCellStyle();
        noDecimalStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));

        CellStyle decimalStyle = workbook.createCellStyle();
        decimalStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));

        row = sheet.createRow(rowNum);

        cell = row.createCell(0);
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 2));
        cell.setCellValue(getString(R.string.scrap_copper));
        cell.setCellStyle(headerStyle);

        cell = row.createCell(3);
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 3, 4));
        cell.setCellStyle(decimalStyle);
        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
        cell.setCellValue(Double.parseDouble(orderFooterObject.getScrapCopper()));

        cell = row.createCell(5);
        cell.setCellValue("kg");

        rowNum++;

        row = sheet.createRow(rowNum);

        cell = row.createCell(0);
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 2));
        cell.setCellValue(getString(R.string.empty_reels));
        cell.setCellStyle(headerStyle);

        cell = row.createCell(3);
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 3, 4));
        cell.setCellStyle(noDecimalStyle);
        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
        cell.setCellValue(Double.parseDouble(orderFooterObject.getEmptyReels()));

        rowNum++;

        row = sheet.createRow(rowNum);

        cell = row.createCell(0);
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 2));
        cell.setCellValue(getString(R.string.total_pallets));
        cell.setCellStyle(headerStyle);

        cell = row.createCell(3);
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 3, 4));
        cell.setCellStyle(noDecimalStyle);
        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
        cell.setCellValue(Double.parseDouble(orderFooterObject.getTotalPallets()));

        rowNum++;

        row = sheet.createRow(rowNum);

        cell = row.createCell(0);
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 2));
        cell.setCellValue(getString(R.string.total_products));
        cell.setCellStyle(headerStyle);

        cell = row.createCell(3);
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 3, 4));
        cell.setCellStyle(noDecimalStyle);
        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
        cell.setCellValue(Double.parseDouble(orderFooterObject.getTotalProducts()));

        rowNum++;

        return rowNum;
    }

    private void fillWebView() {
        MainInfoObject mainInfoObject = resourceManager.getOrderInfo();
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        try {
            HSSFWorkbook workbook = resourceManager.getWorkBook(mainInfoObject.getXlsFilePath());
            HSSFSheet sheet = workbook.getSheet(mainInfoObject.getFileName());

            XlsSheetToHtmlConverter converter = new XlsSheetToHtmlConverter(workbook, sheet, size.x, size.y);

            String html = converter.write();
            Log.d(Utils.APP_TAG, html);
            xlsWebView.loadDataWithBaseURL(Utils.WEBVIEW_ASSETS_LOCATION, html, "text/html", "UTF-8", "");

        } catch (IOException e) {
            Log.e(Utils.APP_TAG, e.getMessage());
        }

    }

    public void onXlsCreateClicked(View view) {
        createXLS();
        initViews();
    }

    public void onXlsViewClicked(View view) {
        xlsWebView.setVisibility(View.VISIBLE);

        pdfLocationTitle.setVisibility(View.INVISIBLE);
        pdfLocationView.setVisibility(View.INVISIBLE);
        pdfFragmentTitle.setVisibility(View.INVISIBLE);

        xlsLocationTitle.setVisibility(View.INVISIBLE);
        xlsLocationView.setVisibility(View.INVISIBLE);

        createPdfButton.setVisibility(View.INVISIBLE);
        createPdfButton.setEnabled(false);

        viewPdfButton.setVisibility(View.INVISIBLE);
        viewPdfButton.setEnabled(false);

        viewXlsButton.setVisibility(View.INVISIBLE);
        viewXlsButton.setEnabled(false);

        createXlsButton.setVisibility(View.INVISIBLE);
        createXlsButton.setEnabled(false);

        closeXlsButton.setVisibility(View.VISIBLE);
        closeXlsButton.setEnabled(true);

        fillWebView();
    }

    public void onXlsCloseClicked(View view) {
        xlsWebView.setVisibility(View.INVISIBLE);
        pdfLocationTitle.setVisibility(View.VISIBLE);
        pdfLocationView.setVisibility(View.VISIBLE);
        pdfFragmentTitle.setVisibility(View.VISIBLE);

        xlsLocationTitle.setVisibility(View.VISIBLE);
        xlsLocationView.setVisibility(View.VISIBLE);

        createPdfButton.setVisibility(View.VISIBLE);
        createPdfButton.setEnabled(true);

        viewPdfButton.setVisibility(View.VISIBLE);
        viewPdfButton.setEnabled(true);

        viewXlsButton.setVisibility(View.VISIBLE);
        viewXlsButton.setEnabled(true);

        createXlsButton.setVisibility(View.VISIBLE);
        createXlsButton.setEnabled(true);

        closeXlsButton.setVisibility(View.INVISIBLE);
        closeXlsButton.setEnabled(false);

    }

    public void onPdfCreateClicked(View view) {
        createPDF();
        initViews();
    }

    public void onPdfViewClicked(View view) {
        pdfView.setVisibility(View.VISIBLE);
        pdfLocationTitle.setVisibility(View.INVISIBLE);
        pdfLocationView.setVisibility(View.INVISIBLE);
        pdfFragmentTitle.setVisibility(View.INVISIBLE);

        xlsLocationTitle.setVisibility(View.INVISIBLE);
        xlsLocationView.setVisibility(View.INVISIBLE);

        createPdfButton.setVisibility(View.INVISIBLE);
        createPdfButton.setEnabled(false);

        viewPdfButton.setVisibility(View.INVISIBLE);
        viewPdfButton.setEnabled(false);

        viewXlsButton.setVisibility(View.INVISIBLE);
        viewXlsButton.setEnabled(false);

        createXlsButton.setVisibility(View.INVISIBLE);
        createXlsButton.setEnabled(false);

        closePdfButton.setVisibility(View.VISIBLE);
        closePdfButton.setEnabled(true);

        PDFView.Configurator configurator = pdfView.fromFile(new File(resourceManager.getOrderInfo().getPdfFilePath()));
        configurator.defaultPage(1);
        configurator.showMinimap(false);
        configurator.enableSwipe(true);
        configurator.onPageChange(this);
        configurator.load();
    }

    public void onPdfCloseClicked(View view) {
        pdfView.setVisibility(View.INVISIBLE);
        pdfLocationTitle.setVisibility(View.VISIBLE);
        pdfLocationView.setVisibility(View.VISIBLE);
        pdfFragmentTitle.setVisibility(View.VISIBLE);

        xlsLocationTitle.setVisibility(View.VISIBLE);
        xlsLocationView.setVisibility(View.VISIBLE);

        createPdfButton.setVisibility(View.VISIBLE);
        createPdfButton.setEnabled(true);

        viewPdfButton.setVisibility(View.VISIBLE);
        viewPdfButton.setEnabled(true);

        viewXlsButton.setVisibility(View.VISIBLE);
        viewXlsButton.setEnabled(true);

        createXlsButton.setVisibility(View.VISIBLE);
        createXlsButton.setEnabled(true);

        closePdfButton.setVisibility(View.INVISIBLE);
        closePdfButton.setEnabled(false);

        pdfView.recycle();
    }

    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
    }
}
