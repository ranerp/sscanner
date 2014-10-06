package com.twtchnz.superscannr.superscanner.resources.DatabaseEntities;

import android.util.Log;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.twtchnz.superscannr.superscanner.utils.Utils;

import java.util.*;

public class Order extends DatabaseObject {

    private String UUID;

    private static final Map<String, Object> initValues = new HashMap<String, Object>();
    private static final Map<String, Object> header = new HashMap<String, Object>();
    private static final Map<String, Object> footer = new HashMap<String, Object>();
    static {
        initValues.put("name", "No active order");
        initValues.put("file_name", "");
        initValues.put("pdf_file_path", "");
        initValues.put("xls_file_path", "");
        initValues.put("pdf_file_exists", false);
        initValues.put("xls_file_exists", false);
        initValues.put("date", "");
        initValues.put("index", "");

        header.put("title", "");
        header.put("company", "");
        header.put("address", "");
        header.put("city", "");
        initValues.put("header", header);

        initValues.put("rows", new HashMap<String, Map<String, Object>>());

        footer.put("scrap_copper", "");
        footer.put("empty_reels", "");
        footer.put("total_pallets", "");
        footer.put("total_products", "");
        initValues.put("footer", footer);
    }

    public Order(Database db, String UUID) {
        super(db, UUID, initValues);

        this.UUID = UUID;
    }

    public String getUUID() { return UUID; }

    public boolean isDummy() {
        if(UUID == Utils.ORDER_DUMMY_ID)
            return true;
        else
            return false;
    }

    public void init(OrderHeaderObject orderHeaderObject, MainInfoObject mainInfoObject) {
        Map<String, Object> newProperties = getLatestProperties();

        Map<String, Object> header = new HashMap<String, Object>();
        header.put("title", orderHeaderObject.getTitle());
        header.put("company", orderHeaderObject.getCompany());
        header.put("address", orderHeaderObject.getAddress());
        header.put("city", orderHeaderObject.getCity());
        newProperties.put("header", header);

        newProperties.put("index", mainInfoObject.getIndex());
        newProperties.put("date", mainInfoObject.getDate());
        newProperties.put("name", mainInfoObject.getName());
        newProperties.put("file_name", mainInfoObject.getFileName());
        newProperties.put("pdf_file_exists", mainInfoObject.isPdfFileExists());
        newProperties.put("pdf_file_path", mainInfoObject.getPdfFilePath());
        newProperties.put("xls_file_exists", mainInfoObject.isXlsFileExists());
        newProperties.put("xls_file_path", mainInfoObject.getXlsFilePath());

        save(newProperties);
    }

    public MainInfoObject getMainInfo() {
        getLatestDocumentRevision();

        return new MainInfoObject( (String) doc.getProperty("index"),
                (String) doc.getProperty("date"),
                (String) doc.getProperty("file_name"),
                (String) doc.getProperty("name"),
                (String) doc.getProperty("pdf_file_path"),
                (String) doc.getProperty("xls_file_path"),
                (Boolean) doc.getProperty("pdf_file_exists"),
                (Boolean) doc.getProperty("xls_file_exists"));
    }

    public void setPdfFilePath(String filePath, boolean fileExists) {
        Map<String, Object> newProperties = getLatestProperties();

        newProperties.put("pdf_file_path", filePath);
        newProperties.put("pdf_file_exists", fileExists);

        save(newProperties);
    }

    public void setXlsFilePath(String filePath, boolean fileExists) {
        Map<String, Object> newProperties = getLatestProperties();

        newProperties.put("xls_file_path", filePath);
        newProperties.put("xls_file_exists", fileExists);

        save(newProperties);
    }

    public OrderHeaderObject getHeader() {
        getLatestDocumentRevision();

        @SuppressWarnings("unchecked")
        Map<String, Object> properties = (Map) doc.getProperty("header");

        return new OrderHeaderObject((String) properties.get("title"),
                (String) properties.get("company"),
                (String) properties.get("address"),
                (String) properties.get("city"));
    }

    public void setHeader(OrderHeaderObject orderHeaderObject) {
        Map<String, Object> newProperties = getLatestProperties();

        Map<String, Object> newHeader = new HashMap<String, Object>();
        newHeader.put("title", orderHeaderObject.getTitle());
        newHeader.put("company", orderHeaderObject.getCompany());
        newHeader.put("address", orderHeaderObject.getAddress());
        newHeader.put("city", orderHeaderObject.getCity());

        newProperties.put("header", newHeader);

        save(newProperties);
    }

    public OrderFooterObject getFooter() {
        getLatestDocumentRevision();

        @SuppressWarnings("unchecked")
        Map<String, Object> properties = (Map) doc.getProperty("footer");

        return new OrderFooterObject((String) properties.get("scrap_copper"),
                (String) properties.get("empty_reels"),
                (String) properties.get("total_pallets"),
                String.valueOf(properties.get("total_products")));
    }

    public void setFooter(OrderFooterObject orderFooterObject) {
        Map<String, Object> newProperties = getLatestProperties();

        Map<String, Object> newFooter = new HashMap<String, Object>();
        newFooter.put("scrap_copper", orderFooterObject.getScrapCopper());
        newFooter.put("empty_reels", orderFooterObject.getEmptyReels());
        newFooter.put("total_pallets", orderFooterObject.getTotalPallets());
        newFooter.put("total_products", orderFooterObject.getTotalProducts());

        newProperties.put("footer", newFooter);

        save(newProperties);
    }

    public void putBarCodeObject(BarCodeObject barCodeObject) {
        Map<String, Object> newProperties = getLatestProperties();

        @SuppressWarnings("unchecked")
        Map<String, Object> rows = (Map) newProperties.get("rows");

        rows.put(barCodeObject.getID(), getRow(barCodeObject));
        newProperties.put("rows", rows);

        setTotalProducts(newProperties, rows);

        save(newProperties);
    }

    private Map<String, Object> getRow(BarCodeObject barCodeObject) {
        Map<String, Object> row = new HashMap<String, Object>();

        row.put("id", barCodeObject.getID());
        row.put("order_number", barCodeObject.getOrderNumber());
        row.put("material_number", barCodeObject.getMaterialCode());
        row.put("sales_order", barCodeObject.getSalesOrder());
        row.put("format", barCodeObject.getFormat());
        row.put("count", barCodeObject.getCount());

        return row;
    }

    @SuppressWarnings("unchecked")
    private void setTotalProducts(Map<String, Object> newProperties, Map<String, Object> rows) {
        int count = 0;
        for (Map.Entry<String, Object> entry : rows.entrySet()) {
            Map<String, Object> brObject = (Map) entry.getValue();
            count += (Integer) brObject.get("count");
        }

        Map<String, Object> newFooter = (Map) newProperties.get("footer");
        newFooter.put("total_products", count);
        newProperties.put("footer", newFooter);
    }

    public void putBarCodeObjects(ArrayList<BarCodeObject> barCodeObjects) {
        Map<String, Object> newProperties = getLatestProperties();

        Map<String, Object> rows = new HashMap<String, Object>();
        for (BarCodeObject barCodeObject : barCodeObjects) {
            rows.put(barCodeObject.getID(), getRow(barCodeObject));
        }

        newProperties.put("rows", rows);

        setTotalProducts(newProperties, rows);

        save(newProperties);
    }

    @SuppressWarnings("unchecked")
    public ArrayList<BarCodeObject> getBarCodeObjects() {
        getLatestDocumentRevision();

        ArrayList<BarCodeObject> list = new ArrayList<BarCodeObject>();

        Map<String, Object> rows = (Map) doc.getProperty("rows");

        for (Map.Entry<String, Object> entry : rows.entrySet()) {
            Map<String, Object> row = (Map) entry.getValue();

            String ID = (String )row.get("id");
            String salesOrder = (String) row.get("sales_order");
            String orderNumber = (String) row.get("order_number");
            String materialCode = (String) row.get("material_number");
            String format = (String) row.get("format");
            int count = (Integer) row.get("count");

            BarCodeObject br = new BarCodeObject(ID, orderNumber, materialCode, salesOrder, format, count);

            list.add(br);
        }

        return list;
    }

    public void deleteBarCodeObjects(Object[] deleteIds) {
        Map<String, Object> newProperties = getLatestProperties();

        @SuppressWarnings("unchecked")
        Map<String, Object> rows = (Map) newProperties.get("rows");

        for (Object item : deleteIds) {
            rows.remove(item.toString());
        }

        newProperties.put("rows", rows);

        setTotalProducts(newProperties, rows);

        save(newProperties);
    }

    public boolean  purge() {
        boolean isRemoved = false;
        if (UUID != Utils.ORDER_DUMMY_ID) {
            getLatestDocumentRevision();

            try {
                doc.purge();
                isRemoved = true;
            } catch (CouchbaseLiteException e) {
                Log.e(Utils.APP_TAG, Utils.DB_DOC_REMOVE_ERROR);
                isRemoved = false;
            }
        }

        return isRemoved;
    }
}
