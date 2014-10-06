package com.twtchnz.superscannr.superscanner.fragments;



import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.twtchnz.superscannr.superscanner.R;
import com.twtchnz.superscannr.superscanner.resources.DatabaseEntities.EmailTemplateObject;
import com.twtchnz.superscannr.superscanner.resources.DatabaseEntities.MainInfoObject;
import com.twtchnz.superscannr.superscanner.resources.ResourceManager;
import com.twtchnz.superscannr.superscanner.utils.Utils;

import java.util.ArrayList;

public class SendEmailFragment extends Fragment {

    private ResourceManager resourceManager;

    private EditText recipientView;
    private EditText fromView;
    private EditText subjectView;
    private EditText bodyView;
    private TextView attachmentsView;

    private Button sendEmailButton;

    public SendEmailFragment(ResourceManager resourceManager) {

        this.resourceManager = resourceManager;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_send_email, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recipientView = (EditText) getView().findViewById(R.id.emailSendRecipientView);
        fromView = (EditText) getView().findViewById(R.id.emailSendFromView);
        subjectView = (EditText) getView().findViewById(R.id.emailSendSubjectView);
        bodyView = (EditText) getView().findViewById(R.id.emailSendBodyView);
        attachmentsView = (TextView) getView().findViewById(R.id.emailSendAttachmentView);
        sendEmailButton = (Button) getView().findViewById(R.id.emailSendButton);
    }

    @Override
    public void onResume() {
        super.onResume();

        setViews();
    }

    public void setViews() {
        EmailTemplateObject emailTemplateObject = resourceManager.getEmailTemplate();
        MainInfoObject mainInfoObject = resourceManager.getOrderInfo();

        recipientView.setText(emailTemplateObject.getRecipient());
        fromView.setText(emailTemplateObject.getFrom());
        subjectView.setText(emailTemplateObject.getSubject());
        bodyView.setText(emailTemplateObject.getBody());

        String attachments  = getString(R.string.no_attachments_message);

        if(mainInfoObject.isPdfFileExists())
            attachments = mainInfoObject.getPdfFilePath();
        if(mainInfoObject.isXlsFileExists())
            attachments += ", " + mainInfoObject.getXlsFilePath();

        attachmentsView.setText(attachments);

        if(resourceManager.isFileExists(mainInfoObject.getXlsFilePath()))
            sendEmailButton.setEnabled(true);
        else
            sendEmailButton.setEnabled(false);
    }

    public Intent onEmailSendClicked(View view) {
        return setUpEmail();
    }

    private Intent setUpEmail() {
        MainInfoObject mainInfoObject = resourceManager.getOrderInfo();

        Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("application/pdf");

        String[] TO = {recipientView.getText().toString()};
        String[] CC = {fromView.getText().toString()};

        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subjectView.getText().toString());
        emailIntent.putExtra(Intent.EXTRA_TEXT, bodyView.getText().toString());

        ArrayList<Uri> uris = new ArrayList<Uri>();
        uris.add(resourceManager.getFileUri(mainInfoObject.getXlsFilePath()));

        if(mainInfoObject.isPdfFileExists())
            uris.add(resourceManager.getFileUri(mainInfoObject.getPdfFilePath()));

        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

        return emailIntent;
    }

}
