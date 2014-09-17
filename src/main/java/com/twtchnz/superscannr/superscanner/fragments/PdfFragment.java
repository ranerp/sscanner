package com.twtchnz.superscannr.superscanner.fragments;



import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.joanzapata.pdfview.PDFView;
import com.twtchnz.superscannr.superscanner.R;
import com.twtchnz.superscannr.superscanner.resources.DatabaseEntities.BarCodeObject;
import com.twtchnz.superscannr.superscanner.resources.DatabaseEntities.MainInfoObject;
import com.twtchnz.superscannr.superscanner.resources.DatabaseEntities.OrderFooterObject;
import com.twtchnz.superscannr.superscanner.resources.DatabaseEntities.OrderHeaderObject;
import com.twtchnz.superscannr.superscanner.resources.ResourceManager;
import com.twtchnz.superscannr.superscanner.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class PdfFragment extends Fragment implements com.joanzapata.pdfview.listener.OnPageChangeListener {

    private ResourceManager resourceManager;

    private TextView pdfFragmentTitle;
    private TextView pdfLocationTitle;
    private TextView pdfLocationView;


    private Button viewPdfButton;
    private Button createPdfButton;
    private Button closePdfButton;

    private PDFView pdfView;

    private int columnCount = 5;

    private int pageNumber = 1;

    private float borderWidth = 0.5f;

    public PdfFragment(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pdf, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        pdfFragmentTitle = (TextView) getView().findViewById(R.id.pdfFragmentTitle);
        pdfLocationTitle = (TextView) getView().findViewById(R.id.pdfLocationTitle);
        pdfLocationView = (TextView) getView().findViewById(R.id.pdfLocationView);

        createPdfButton = (Button) getView().findViewById(R.id.pdfCreateButton);
        closePdfButton = (Button) getView().findViewById(R.id.pdfCloseButton);
        viewPdfButton = (Button) getView().findViewById(R.id.viewPdfButton);

        pdfView = (PDFView) getView().findViewById(R.id.pdfView);
        pdfView.setVisibility(View.INVISIBLE);

        closePdfButton.setEnabled(false);
        closePdfButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();

        initViews();
    }

    private void toggleViewButton(MainInfoObject mainInfoObject) {
        if(mainInfoObject.isFileExists())
            viewPdfButton.setEnabled(true);
        else
            viewPdfButton.setEnabled(false);
    }

    private void initViews() {
        MainInfoObject mainInfoObject = resourceManager.getOrderInfo();
        toggleViewButton(mainInfoObject);
        setViews(mainInfoObject);
    }

    private void setViews(MainInfoObject mainInfoObject) {
        if(mainInfoObject.isFileExists())
            pdfLocationView.setText(mainInfoObject.getFilePath());
        else
            pdfLocationView.setText(R.string.not_found);
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

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Calendar calendar = GregorianCalendar.getInstance();

        Date dateNow = calendar.getTime();
        String date = dateFormat.format(dateNow);

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

        PdfPCell orderNumberTitle = new PdfPCell(new Paragraph("Order Number", whiteFont));
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

    public void onPdfCreateClicked(View view) {
        createPDF();
        initViews();
    }

    public void onPdfViewClicked(View view) {
        pdfView.setVisibility(View.VISIBLE);
        pdfLocationTitle.setVisibility(View.INVISIBLE);
        pdfLocationView.setVisibility(View.INVISIBLE);
        pdfFragmentTitle.setVisibility(View.INVISIBLE);

        createPdfButton.setVisibility(View.INVISIBLE);
        createPdfButton.setEnabled(false);

        viewPdfButton.setVisibility(View.INVISIBLE);
        viewPdfButton.setEnabled(false);

        closePdfButton.setVisibility(View.VISIBLE);
        closePdfButton.setEnabled(true);

        PDFView.Configurator configurator = pdfView.fromFile(new File(resourceManager.getOrderInfo().getFilePath()));
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

        createPdfButton.setVisibility(View.VISIBLE);
        createPdfButton.setEnabled(true);

        viewPdfButton.setVisibility(View.VISIBLE);
        viewPdfButton.setEnabled(true);

        closePdfButton.setVisibility(View.INVISIBLE);
        closePdfButton.setEnabled(false);

        pdfView.recycle();
    }

    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
    }
}
