package com.twtchnz.superscannr.superscanner.resources.DatabaseEntities;

import android.util.Log;
import com.couchbase.lite.Database;
import com.twtchnz.superscannr.superscanner.resources.DatabaseEntities.DatabaseObject;
import com.twtchnz.superscannr.superscanner.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrdersInfo extends DatabaseObject {

    private static final String ID = Utils.ORDER_ACTIVE_ID;

    private static final Map<String, Object> initValues = new HashMap<String, Object>();
    static {
        initValues.put("active_order_id", "-1");
        initValues.put("is_active", false);
        initValues.put("next_name_id", 1);
        initValues.put("order_ids", new HashMap<String, Map<String, Object>>());
    }

   public OrdersInfo(Database db) {
        super(db, ID, initValues);
    }

    public int getNewIndex() {
        Map<String, Object> newProperties = getLatestProperties();

        int id = (Integer) newProperties.get("next_name_id");
        int nextId = id + 1;

        newProperties.put("next_name_id", nextId);
        save(newProperties);

        return id;
    }

   public boolean isActiveOrder() {
       getLatestDocumentRevision();

       return (Boolean) doc.getProperty("is_active");
   }

   public String getActiveOrderId() {
        getLatestDocumentRevision();

        return (String) doc.getProperty("active_order_id");
    }

    @SuppressWarnings("unchecked")
   public ArrayList<OrderInfoObject> getOrderIds() {
       getLatestDocumentRevision();

       ArrayList<OrderInfoObject> list = new ArrayList<OrderInfoObject>();

       Map<String, Object> orders = (Map) doc.getProperty("order_ids");


       for (Map.Entry<String, Object> entry : orders.entrySet()) {
           Map<String, Object> order = (Map) entry.getValue();

           String name = (String) order.get("name");
           String id = entry.getKey();
           String date = (String) order.get("date");
           int index = (Integer) order.get("index");

           list.add(new OrderInfoObject(id, name, date, index));
       }

       return list;
   }

   public void activateOrder(String orderId) {
       Map<String, Object> newProperties = getLatestProperties();
       newProperties.put("is_active", true);
       newProperties.put("active_order_id", orderId);

       save(newProperties);
   }

   public void addAndActivateOrder(String orderId, String name, String date, int index) {
       Map<String, Object> newProperties = getLatestProperties();
       newProperties.put("is_active", true);
       newProperties.put("active_order_id", orderId);

       @SuppressWarnings("unchecked")
       Map<String, Object> orderIds = (Map) newProperties.get("order_ids");
       newProperties.put("order_ids", pushOrderId(orderIds, orderId, name, date, index));

       save(newProperties);
   }

   public void setNotActive() {
       Map<String, Object> newProperties = getLatestProperties();
       newProperties.put("is_active", false);
       newProperties.put("active_order_id", "-1");
       save(newProperties);
   }

    private Map<String, Object> pushOrderId(Map<String, Object> properties, String orderId, String name, String date, int index) {
        Map<String, Object> orderIds = properties;

        Map<String, Object> values = new HashMap<String, Object>();
        values.put("name", name);
        values.put("date", date);
        values.put("index", index);

        orderIds.put(orderId, values);

        return orderIds;
    }

    public boolean deleteOrders(Object[] array) {
        Map<String, Object> newProperties = getLatestProperties();
        boolean setNotActive = false;
        String activeOrderId = newProperties.get("active_order_id").toString();

        @SuppressWarnings("unchecked")
        Map<String, Object> orderIds = ((Map<String, Object>) newProperties.get("order_ids"));
        for (Object item : array) {
            if(activeOrderId.equals(item.toString()))
                setNotActive = true;

            orderIds.remove(item.toString());
        }

        newProperties.put("order_ids", orderIds);
        save(newProperties);

        return setNotActive;
    }

}
