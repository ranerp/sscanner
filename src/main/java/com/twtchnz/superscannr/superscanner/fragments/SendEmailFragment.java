package com.twtchnz.superscannr.superscanner.fragments;



import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.*;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.twtchnz.superscannr.superscanner.R;
import com.twtchnz.superscannr.superscanner.adapters.AttachmentListAdapter;
import com.twtchnz.superscannr.superscanner.resources.DatabaseEntities.AttachmentObject;
import com.twtchnz.superscannr.superscanner.resources.DatabaseEntities.EmailTemplateObject;
import com.twtchnz.superscannr.superscanner.resources.DatabaseEntities.FileTypes;
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
    private ListView attachmentsView;

    private MenuItem sendEmailButton;
    private Menu menu;

    private MainInfoObject mainInfoObject;
    private EmailTemplateObject emailTemplateObject;

    private AttachmentListAdapter attachmentListAdapter;

    public SendEmailFragment(ResourceManager resourceManager) {

        this.resourceManager = resourceManager;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_send_email, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.send_email, menu);
        this.menu = menu;
        this.sendEmailButton = this.menu.findItem(R.id.send_email_button);
        setEmailButton();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send_email_button:
                onEmailSendClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mainInfoObject = resourceManager.getOrderInfo();

        recipientView = (EditText) getView().findViewById(R.id.emailSendRecipientView);
        fromView = (EditText) getView().findViewById(R.id.emailSendFromView);
        subjectView = (EditText) getView().findViewById(R.id.emailSendSubjectView);
        bodyView = (EditText) getView().findViewById(R.id.emailSendBodyView);
        attachmentsView = (ListView) getView().findViewById(R.id.emailSendAttachmentView);

        ArrayList<AttachmentObject> attachmentObjects = new ArrayList<AttachmentObject>();

        if(mainInfoObject.isPdfFileExists())
            attachmentObjects.add(new AttachmentObject(mainInfoObject.getPdfFilePath(), FileTypes.PDF));
        if(mainInfoObject.isXlsFileExists())
            attachmentObjects.add(new AttachmentObject(mainInfoObject.getXlsFilePath(), FileTypes.XLS));

        attachmentListAdapter = new AttachmentListAdapter(getActivity(), R.layout.attachment_object_row, attachmentObjects);
        attachmentsView.setAdapter(attachmentListAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        setViews();
    }

    public void setViews() {
        emailTemplateObject = resourceManager.getEmailTemplate();
        mainInfoObject = resourceManager.getOrderInfo();

        recipientView.setText(emailTemplateObject.getRecipient());
        fromView.setText(emailTemplateObject.getFrom());
        subjectView.setText(emailTemplateObject.getSubject());
        bodyView.setText(emailTemplateObject.getBody());

        setEmailButton();
    }

    private void setEmailButton() {
        if (sendEmailButton != null && mainInfoObject != null) {
            if (resourceManager.isFileExists(mainInfoObject.getXlsFilePath())) {
                sendEmailButton.setIcon(R.drawable.ic_email);
                sendEmailButton.setEnabled(true);
            } else {
                sendEmailButton.setEnabled(false);
                sendEmailButton.setIcon(R.drawable.ic_email_not_active);
            }
        }
    }

    public void onEmailSendClicked() {
        setUpEmail();
    }

    private void setUpEmail() {
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

        getActivity().startActivity(emailIntent);
    }

}
