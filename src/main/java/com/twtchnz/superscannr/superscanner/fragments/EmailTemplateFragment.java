package com.twtchnz.superscannr.superscanner.fragments;



import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import com.twtchnz.superscannr.superscanner.R;
import com.twtchnz.superscannr.superscanner.resources.DatabaseEntities.EmailTemplateObject;
import com.twtchnz.superscannr.superscanner.resources.ResourceManager;
import com.twtchnz.superscannr.superscanner.utils.Utils;

public class EmailTemplateFragment extends Fragment {

    ResourceManager resourceManager;

    private EditText recipientView;
    private EditText fromView;
    private EditText subjectView;
    private EditText bodyView;

    public EmailTemplateFragment(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_email_template, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recipientView = (EditText) getView().findViewById(R.id.emailTemplateRecipientView);
        fromView = (EditText) getView().findViewById(R.id.emailTemplateFromView);
        subjectView = (EditText) getView().findViewById(R.id.emailTemplateSubjectView);
        bodyView = (EditText) getView().findViewById(R.id.emailTemplateBodyView);

        EmailTemplateObject emailTemplateObject = resourceManager.getEmailTemplate();

        recipientView.setText(emailTemplateObject.getRecipient());
        fromView.setText(emailTemplateObject.getFrom());
        subjectView.setText(emailTemplateObject.getSubject());
        bodyView.setText(emailTemplateObject.getBody());
    }

    public void onEmailTemplateSaveClicked(View view) {
        String recipient = recipientView.getText().toString();
        String from = fromView.getText().toString();
        String subject = subjectView.getText().toString();
        String body = bodyView.getText().toString();

        EmailTemplateObject emailTemplateObject = new EmailTemplateObject(recipient, from, subject, body);

        resourceManager.setEmailTemplate(emailTemplateObject);
    }


}
