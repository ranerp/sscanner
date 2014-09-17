package com.twtchnz.superscannr.superscanner.resources;

import android.util.Log;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.twtchnz.superscannr.superscanner.utils.Utils;

import java.io.IOException;

public class DatabaseManager {

    private Manager manager;

    private Database db;

    public DatabaseManager(android.content.Context context) {
        this.manager = createManager(context);
        this.db = getDatabase(this.manager);
    }

    public boolean isManagerReady() {
        if(manager != null && db != null)
            return true;

        return false;
    }

    public Database getDatabase() {
        return db;
    }

    private Manager createManager(android.content.Context context) {
        Manager manager = null;

        try {
            manager = new Manager(new AndroidContext(context), Manager.DEFAULT_OPTIONS);
        } catch (IOException e) {
            Log.e(Utils.APP_TAG, Utils.DB_MANAGER_CREATE_ERROR);
        }

        return manager;
    }

    private Database getDatabase(Manager manager) {
        Database database = null;

        if (validateDbName(manager)) {
            try {
                database = manager.getDatabase(Utils.DB_NAME);
            } catch (CouchbaseLiteException e) {
                Log.e(Utils.APP_TAG, Utils.DB_GET_ERROR);
            }
        }

        return database;
    }

    private boolean validateDbName(Manager manager) {
        if (!manager.isValidDatabaseName(Utils.DB_NAME)) {
            Log.e(Utils.APP_TAG, Utils.DB_BAD_NAME);
            return false;
        }

        return true;
    }

}
