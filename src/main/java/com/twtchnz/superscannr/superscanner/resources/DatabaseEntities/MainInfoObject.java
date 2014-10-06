package com.twtchnz.superscannr.superscanner.resources.DatabaseEntities;

public class MainInfoObject {

    String index;

    String date;
    String fileName;
    String pdfFilePath;
    String xlsFilePath;
    String name;
    boolean pdfFileExists;
    boolean xlsFileExists;

    public MainInfoObject(String index, String date, String fileName, String name, String pdfFilePath, String xlsFilePath, boolean pdfFileExists, boolean xlsFileExists) {
        this.index = index;
        this.date = date;
        this.fileName = fileName;
        this.name = name;
        this.pdfFilePath = pdfFilePath;
        this.xlsFilePath = xlsFilePath;
        this.pdfFileExists = pdfFileExists;
        this.xlsFileExists = xlsFileExists;
    }

    public String getIndex() { return index; }

    public String getDate() {
        return date;
    }

    public String getFileName() {
        return fileName;
    }

    public String getName() {
        return name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPdfFilePath() { return pdfFilePath; }

    public void setPdfFilePath(String pdfFilePath) { this.pdfFilePath = pdfFilePath; }

    public String getXlsFilePath() { return xlsFilePath; }

    public void setXlsFilePath(String xlsFilePath) { this.xlsFilePath = xlsFilePath; }

    public boolean isPdfFileExists() { return pdfFileExists; }

    public void setPdfFileExists(boolean pdfFileExists) { this.pdfFileExists = pdfFileExists; }

    public boolean isXlsFileExists() { return xlsFileExists; }

    public void setXlsFileExists(boolean xlsFileExists) { this.xlsFileExists = xlsFileExists; }
}
