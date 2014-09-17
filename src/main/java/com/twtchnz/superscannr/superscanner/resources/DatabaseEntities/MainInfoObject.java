package com.twtchnz.superscannr.superscanner.resources.DatabaseEntities;

public class MainInfoObject {

    String date;
    String fileName;
    String filePath;
    String name;
    boolean fileExists;

    public MainInfoObject(String date, String fileName, String name, String filePath, boolean fileExists) {
        this.date = date;
        this.fileName = fileName;
        this.name = name;
        this.filePath = filePath;
        this.fileExists = fileExists;
    }

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

    public String getFilePath() { return filePath; }

    public void setFilePath(String filePath) { this.filePath = filePath; }

    public boolean isFileExists() { return fileExists; }

    public void setFileExists(boolean fileExists) { this.fileExists = fileExists; }
}
