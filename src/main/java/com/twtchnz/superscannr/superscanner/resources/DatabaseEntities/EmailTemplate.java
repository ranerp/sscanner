package com.twtchnz.superscannr.superscanner.resources.DatabaseEntities;

import android.util.Log;
import com.couchbase.lite.Database;
import com.twtchnz.superscannr.superscanner.utils.Utils;

import java.util.HashMap;
import java.util.Map;

public class EmailTemplate extends DatabaseObject {

    private static final Map<String, Object> initValues = new HashMap<String, Object>();
    static {
        initValues.put("recipient", "recipient@example.com");
        initValues.put("from", "your@email.com");
        initValues.put("subject", "subject of email");
        initValues.put("body", "body of email");
    }

    public EmailTemplate(Database db) {
        super(db, Utils.EMAIL_TEMPLATE_ID, initValues);
    }

    public void setEmailTemplate(EmailTemplateObject emailTemplateObject) {
        Map<String, Object> newProperties = getLatestProperties();

        newProperties.put("recipient", emailTemplateObject.getRecipient());
        newProperties.put("from", emailTemplateObject.getFrom());
        newProperties.put("subject", emailTemplateObject.getSubject());
        newProperties.put("body", emailTemplateObject.getBody());

        save(newProperties);
    }

    public EmailTemplateObject getEmailTemplate() {
        getLatestDocumentRevision();

        EmailTemplateObject emailTemplateObject = new EmailTemplateObject((String) doc.getProperty("recipient"),
                                                                          (String) doc.getProperty("from"),
                                                                          (String) doc.getProperty("subject"),
                                                                          (String) doc.getProperty("body"));

        return emailTemplateObject;
    }
}
