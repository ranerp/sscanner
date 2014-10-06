package com.twtchnz.superscannr.superscanner.converter;

public class MergedRegion {

    private int fromRow;
    private int toRow;
    private int fromColumn;
    private int toColumn;

    private int rowSpan;
    private int colSpan;

    private boolean isUsed;

    public MergedRegion(int fromRow, int toRow, int fromColumn, int toColumn) {
        this.fromRow = fromRow;
        this.toRow = toRow;
        this.fromColumn = fromColumn;
        this.toColumn = toColumn;
        this.isUsed = false;

        this.rowSpan = this.toRow - this.fromRow + 1;
        this.colSpan = this.toColumn - this.fromColumn + 1;
    }

    public boolean isInRegion(int row, int column) {
        if(fromRow <= row && row <= toRow &&  fromColumn <= column && column <= toColumn)
            return true;

        return false;
    }

    public void setUsed() { this.isUsed = true; }

    public boolean isUsed() { return this.isUsed; }

    public int getRowSpan() {
        return rowSpan;
    }

    public int getColSpan() {
        return colSpan;
    }
}
