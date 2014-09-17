package com.twtchnz.superscannr.superscanner.resources;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.twtchnz.superscannr.superscanner.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class FileManager {

    private File pdfStorageDir;

    private File appDir;

    private Document document;

    private String fileExtension = ".pdf";

    public FileManager(Context context) {
        if(isExternalStorageWritable())
            pdfStorageDir = getStorageDir();
        else
            pdfStorageDir = context.getDir(Utils.PDF_INT_DIR_NAME, Context.MODE_PRIVATE);

        appDir = context.getFilesDir();

    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();

        if(Environment.MEDIA_MOUNTED.equals(state))
            return true;
        else
            return false;

    }

    private File getStorageDir() {
        File file = new File(Environment.getExternalStorageDirectory() + "/" + Utils.PDF_EXT_DIR_NAME);

        if (!file.exists()) {
            if(!file.mkdirs())
                Log.e(Utils.APP_TAG, Utils.STORAGE_DIR_ERR);
        }

        return file;
    }

    public String getFilePath(String fileName) {
        return getPdfStorageDir() + "/" + fileName + fileExtension;
    }

    public boolean doesFileExist(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    public File getPdfStorageDir() { return pdfStorageDir; }

    public String getFileExtension() { return fileExtension; }

    public File getAppDir() { return appDir; }

    public Uri getFileUri(String filePath) {
        Uri uri = Uri.fromFile(new File(filePath));

        return uri;
    }

    public Document startDocument(String filePath) throws DocumentException, FileNotFoundException {
        document = new Document();

        File writePath = new File(filePath);

        PdfWriter.getInstance(document, new FileOutputStream(writePath));
        document.open();

        return document;
    }

    public void closeDocument() throws DocumentException, FileNotFoundException {
        document.close();
        document = null;
    }
}
