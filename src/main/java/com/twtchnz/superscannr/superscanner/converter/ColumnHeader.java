package com.twtchnz.superscannr.superscanner.converter;

import com.hp.gagawa.java.elements.Text;

public class ColumnHeader {

    private int width;
    private Text text;

    public ColumnHeader(int width, Text text) {
        this.width = width;
        this.text = text;
    }

    public int getWidth() {
        return width;
    }

    public void setMaxWidth(int width) {
        this.width = Math.max(width, this.width);
    }

    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        this.text = text;
    }
}
