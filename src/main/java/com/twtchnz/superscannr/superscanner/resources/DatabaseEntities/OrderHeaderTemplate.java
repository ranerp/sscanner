package com.twtchnz.superscannr.superscanner.resources.DatabaseEntities;

import com.couchbase.lite.Database;
import com.twtchnz.superscannr.superscanner.utils.Utils;

import java.util.HashMap;
import java.util.Map;

public class OrderHeaderTemplate extends DatabaseObject {

    private static final Map<String, Object> initValues = new HashMap<String, Object>();
    static {
        initValues.put("title", Utils.ORDER_TITLE);
        initValues.put("company", Utils.ORDER_COMPANY);
        initValues.put("address", Utils.ORDER_ADDRESS);
        initValues.put("city", Utils.ORDER_CITY);
    }

    public OrderHeaderTemplate(Database db) {
        super(db, Utils.ORDER_HEADER_TEMPLATE_ID, initValues);
    }

    public OrderHeaderObject getOrderTemplate() {
        getLatestDocumentRevision();

        OrderHeaderObject orderHeaderObject = new OrderHeaderObject((String) doc.getProperty("title"),
                                                                          (String) doc.getProperty("company"),
                                                                          (String) doc.getProperty("address"),
                                                                          (String) doc.getProperty("city"));

        return orderHeaderObject;
    }

    public void setOrderTemplate(OrderHeaderObject orderHeaderObject) {
        Map<String, Object> newProperties = getLatestProperties();

        newProperties.put("title", orderHeaderObject.getTitle());
        newProperties.put("company", orderHeaderObject.getCompany());
        newProperties.put("address", orderHeaderObject.getAddress());
        newProperties.put("city", orderHeaderObject.getCity());

        save(newProperties);
    }


}
