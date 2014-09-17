package com.twtchnz.superscannr.superscanner.resources.DatabaseEntities;

import android.util.Log;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.twtchnz.superscannr.superscanner.utils.Utils;

import java.util.*;

public abstract class DatabaseObject {

    protected String ID;

    protected Database db;

    protected Document doc;


    public DatabaseObject(Database db, String ID) {
        this(db, ID, null);
    }

    public DatabaseObject(Database db, String ID, Map<String, Object> initiationProperties) {
        this.db = db;
        this.ID = ID;
        this.doc = this.db.getExistingDocument(this.ID);

        if (initiationProperties != null && this.doc == null) {
            this.doc = this.db.getDocument(this.ID);
            save(initiationProperties);
        } else if (this.doc == null) {
            this.doc = this.db.getDocument(this.ID);
            save(doc.getProperties());
        }

    }

    protected void getLatestDocumentRevision() {
        doc = db.getDocument(ID);
    }

    protected Map<String, Object> getLatestProperties() {
        doc = db.getDocument(ID);

        Map<String, Object> copy = new HashMap<String, Object>();
        copy.putAll(doc.getProperties());

        return copy;
    }

    protected void save(Map<String, Object> newProperties) {
        try {
            doc.putProperties(newProperties);
        } catch (Exception e) {
            Log.e(Utils.APP_TAG, Utils.DB_UPDATE_ERROR);
        }
    }
}
