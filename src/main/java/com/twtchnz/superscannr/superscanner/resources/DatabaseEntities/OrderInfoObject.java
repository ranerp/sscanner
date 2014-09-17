package com.twtchnz.superscannr.superscanner.resources.DatabaseEntities;

import android.util.Log;
import com.twtchnz.superscannr.superscanner.utils.Utils;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OrderInfoObject implements Comparable, Serializable {

    String ID;
    String name;
    String date;
    int index;

    public OrderInfoObject(String ID, String name, String date, int index) {
        this.ID = ID;
        this.name = name;
        this.date = date;
        this.index = index;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getIndex() { return index; }


    @Override
    public int compareTo(Object another) {
        if(this == another) return 0;
        if(!(another instanceof  OrderInfoObject)) return -1;

        return  ((OrderInfoObject) another).getIndex() - index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderInfoObject)) return false;

        OrderInfoObject that = (OrderInfoObject) o;

        if (index != that.getIndex()) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }
}
