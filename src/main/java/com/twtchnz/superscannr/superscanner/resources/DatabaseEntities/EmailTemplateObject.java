package com.twtchnz.superscannr.superscanner.resources.DatabaseEntities;

public class EmailTemplateObject {

    private String recipient;
    private String from;
    private String subject;
    private String body;

    public EmailTemplateObject(String recipient, String from, String subject, String body) {
        this.recipient = recipient;
        this.from = from;
        this.subject = subject;
        this.body = body;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
