package com.twtchnz.superscannr.superscanner.resources.DatabaseEntities;

public class AttachmentObject {

    private String path;
    private FileTypes fileType;

    public AttachmentObject(String path, FileTypes fileType) {
        this.path = path;
        this.fileType = fileType;
    }

    public String getPath() {
        return path;
    }

    public FileTypes getFileType() {
        return fileType;
    }
}
