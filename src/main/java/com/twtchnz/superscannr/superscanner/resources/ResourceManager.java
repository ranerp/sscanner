package com.twtchnz.superscannr.superscanner.resources;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.twtchnz.superscannr.superscanner.resources.DatabaseEntities.*;
import com.twtchnz.superscannr.superscanner.utils.Utils;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ResourceManager {

    private FileManager fileManager;
    private DatabaseManager databaseManager;

    private OrderHeaderTemplate orderHeaderTemplate;
    private EmailTemplate emailTemplate;

    private OrdersInfo ordersInfo;
    private Order order;

    public ResourceManager(Context context) {
        this.fileManager = new FileManager(context);
        this.databaseManager = new DatabaseManager(context);
    }

    public boolean initTemplate() {
        if(!databaseManager.isManagerReady())
            return false;

        orderHeaderTemplate = new OrderHeaderTemplate(databaseManager.getDatabase());
        emailTemplate = new EmailTemplate(databaseManager.getDatabase());

        return true;
    }

    public boolean init() {
        if(!databaseManager.isManagerReady())
            return false;

        orderHeaderTemplate = new OrderHeaderTemplate(databaseManager.getDatabase());
        emailTemplate = new EmailTemplate(databaseManager.getDatabase());

        ordersInfo = new OrdersInfo(databaseManager.getDatabase());

        if (ordersInfo.isActiveOrder()) {
            order = new Order(databaseManager.getDatabase(), ordersInfo.getActiveOrderId());
            resolveFileExist();
        } else {
            order = new Order(databaseManager.getDatabase(), Utils.ORDER_DUMMY_ID);
        }

        return true;
    }

    public void activateNewOrder() {
        String id = UUID.randomUUID().toString();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = GregorianCalendar.getInstance();

        Date dateNow = calendar.getTime();
        String date = dateFormat.format(dateNow);

        int index = ordersInfo.getNewIndex();

        String name = Utils.ORDER_NAME + index;
        String fileName = Utils.ORDER_PDF_FILE_NAME + index;
        String filePath = fileManager.getFilePath(fileName);
        boolean fileExists = false;

        MainInfoObject mainInfoObject = new MainInfoObject(date, fileName, name, filePath, fileExists);

        order = new Order(databaseManager.getDatabase(), id);
        order.init(orderHeaderTemplate.getOrderTemplate(), mainInfoObject);

        ordersInfo.addAndActivateOrder(id, name, date, index);
    }

    public void setNotActive() {
        ordersInfo.setNotActive();
        setToDummyOrder();
    }

    private void setToDummyOrder() {
        order = new Order(databaseManager.getDatabase(), Utils.ORDER_DUMMY_ID);
    }

    public boolean isDummyOrder() {
        return order.isDummy();
    }

    public void deleteOrders(Object[] array) {
        boolean setNotActive = ordersInfo.deleteOrders(array);
        if (setNotActive) {
            setNotActive();
        }

        for (Object item : array) {
            Order order = new Order(databaseManager.getDatabase(), item.toString());
            order.purge();
        }
    }

    public void activateOrder(String orderId) {
        ordersInfo.activateOrder(orderId);

        order = new Order(databaseManager.getDatabase(), orderId);
        resolveFileExist();
    }

    private void resolveFileExist() {
        MainInfoObject mainInfoObject = order.getMainInfo();
        boolean fileExists = fileManager.doesFileExist(mainInfoObject.getFilePath());

        if (!fileExists && mainInfoObject.isFileExists())
            order.setFilePath("", false);
    }

    public boolean isFileExists(String filePath) {
        return fileManager.doesFileExist(filePath);
    }

    public Uri getFileUri(String filePath) {
        return fileManager.getFileUri(filePath);
    }

    public MainInfoObject getOrderInfo() { return order.getMainInfo(); }

    public String getOrderUUID() { return order.getUUID(); }

    public ArrayList<BarCodeObject> getBarCodeObjects() { return order.getBarCodeObjects(); }

    public void putBarCodeObject(BarCodeObject barCodeObject) { order.putBarCodeObject(barCodeObject); }

    public void putBarCodeObjects(ArrayList<BarCodeObject> barCodeObjects) { order.putBarCodeObjects(barCodeObjects); }

    public void deleteBarCodeObjects(Object[] deleteIds) { order.deleteBarCodeObjects(deleteIds); }

    public void setEmailTemplate(EmailTemplateObject emailTemplateObject) { emailTemplate.setEmailTemplate(emailTemplateObject); }

    public EmailTemplateObject getEmailTemplate() { return emailTemplate.getEmailTemplate(); }

    public OrderHeaderObject getOrderHeaderTemplate() { return orderHeaderTemplate.getOrderTemplate(); }

    public void setOrderHeaderTemplate(OrderHeaderObject orderHeaderObject) { orderHeaderTemplate.setOrderTemplate(orderHeaderObject); }

    public OrderHeaderObject getActiveOrderHeader() { return order.getHeader(); }

    public void setActiveOrderHeader(OrderHeaderObject orderHeaderObject) {  order.setHeader(orderHeaderObject); }

    public OrderFooterObject getActiveOrderFooter() { return order.getFooter(); }

    public void setActiveOrderFooter(OrderFooterObject orderFooterObject) {  order.setFooter(orderFooterObject); }

    public ArrayList<OrderInfoObject> getOrderInfoObjects() { return ordersInfo.getOrderIds(); }

    public Document startDocument() throws DocumentException, FileNotFoundException {
        MainInfoObject mainInfoObject = order.getMainInfo();
        String filePath = fileManager.getFilePath(mainInfoObject.getFileName());
        order.setFilePath(filePath, true);

        return fileManager.startDocument(filePath);
    }

    public void closeDocument() throws DocumentException, FileNotFoundException{
        fileManager.closeDocument();
        resolveFileExist();
    }

}
