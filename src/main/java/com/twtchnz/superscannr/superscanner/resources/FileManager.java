package com.twtchnz.superscannr.superscanner.resources;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.twtchnz.superscannr.superscanner.utils.Utils;
import org.apache.poi.hpsf.Util;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.*;
import java.util.logging.Logger;

public class FileManager {

    private File fileStorageDir;

    private File appDir;

    private Document document;

    private HSSFWorkbook workbook;

    private String pdfExtension = ".pdf";

    private String xlsExtension = ".xls";

    public FileManager(Context context) {
        if(isExternalStorageWritable())
            fileStorageDir = getStorageDir();
        else
            fileStorageDir = context.getDir(Utils.PDF_INT_DIR_NAME, Context.MODE_PRIVATE);

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

    public String getPdfFilePath(String fileName) {
        return getFileStorageDir().getPath() + "/" + fileName + getPdfExtension();
    }

    public String getXlsFilePath(String fileName) {
        return getFileStorageDir().getPath() + "/" + fileName + getXlsExtension();
    }

    public boolean doesFileExist(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    public File getFileStorageDir() { return fileStorageDir; }

    public String getPdfExtension() { return pdfExtension; }

    public String getXlsExtension() { return xlsExtension; }

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

    public HSSFWorkbook startWorkBook() {
        workbook = new HSSFWorkbook();

        return workbook;
    }

    public void writeWorkBook(String filePath) throws Exception {
        FileOutputStream out = new FileOutputStream(filePath);
        workbook.write(out);
        out.close();
    }

    public HSSFWorkbook getWorkBook(String filePath) throws IOException {
        File file = new File(filePath);
        FileInputStream inputStream = new FileInputStream(file);
        POIFSFileSystem fileSystem = new POIFSFileSystem(inputStream);

        HSSFWorkbook workbook = new HSSFWorkbook(fileSystem);

        return workbook;
    }
}
