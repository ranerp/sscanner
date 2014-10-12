package com.twtchnz.superscannr.superscanner.resources.DatabaseEntities;

public enum FileTypes {
    PDF(0), XLS(1);

    private int type;

    private FileTypes(int type) { this.type = type; }

    public int getType() { return type; }
}
