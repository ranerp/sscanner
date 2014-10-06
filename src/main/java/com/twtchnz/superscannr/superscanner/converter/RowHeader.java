package com.twtchnz.superscannr.superscanner.converter;

import com.hp.gagawa.java.elements.Text;

public class RowHeader {

    private int height;
    private Text text;

    public RowHeader(int height, Text text) {
        this.height = height;
        this.text = text;
    }

    public int getHeight() {
        return height;
    }

    public void setMaxHeight(int height) {
        this.height = Math.max(height, this.height);
    }

    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        this.text = text;
    }
}
