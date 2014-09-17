package com.twtchnz.superscannr.superscanner.resources.DatabaseEntities;

import java.io.Serializable;

public class BarCodeObject implements Comparable, Serializable {

    private String ID;

    private String format;

    private String orderNumber;
    private String materialCode;
    private String salesOrder;

    private int count;

    public BarCodeObject(String ID, String orderNumber, String materialCode, String salesOrder, String format, int count) {
        this.ID = ID;
        this.orderNumber = orderNumber;
        this.salesOrder = salesOrder;
        this.format = format;
        this.materialCode = materialCode;
        this.count = count;

    }

    public String getID() { return ID; }
    
    public void setFormat(String format) { this.format = format; }

    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public void setSalesOrder(String salesOrder) { this.salesOrder = salesOrder; }
    
    public String getFormat() { return format; }

    public String getSalesOrder() {  return salesOrder; }

    public String getOrderNumber() { return orderNumber; }

    public String getMaterialCode() { return materialCode; }

    public int getCount() { return count; }

    public void countUp() { count++; }

    public void countDown() { count--; }

    public void setMaterialCode(String materialCode) { this.materialCode = materialCode; }

    public void setCount(int count) { this.count = count; }
    
    @Override
    public int compareTo(Object o) {
        if(!(o instanceof BarCodeObject))
            return -1;

        return ID.compareTo( ((BarCodeObject) o).getID() );
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof BarCodeObject))
            return false;

        return ID.equals( ((BarCodeObject) o).getID() );
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }

    
}
