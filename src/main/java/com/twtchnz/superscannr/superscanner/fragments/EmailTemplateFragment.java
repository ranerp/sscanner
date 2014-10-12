package com.twtchnz.superscannr.superscanner.fragments;



import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.*;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.twtchnz.superscannr.superscanner.MainActivity;
import com.twtchnz.superscannr.superscanner.R;
import com.twtchnz.superscannr.superscanner.resources.DatabaseEntities.EmailTemplateObject;
import com.twtchnz.superscannr.superscanner.resources.ResourceManager;
import com.twtchnz.superscannr.superscanner.utils.Utils;

public class EmailTemplateFragment extends Fragment {

    ResourceManager resourceManager;

    private TextView recipientTitle;
    private TextView fromTitle;
    private TextView subjectTitle;
    private TextView bodyTitle;


    private EditText recipientView;
    private EditText fromView;
    private EditText subjectView;
    private EditText bodyView;

    public EmailTemplateFragment(ResourceManager resourceManager) {
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
        return inflater.inflate(R.layout.fragment_email_template, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.email_template_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.email_template_save_button:
                onEmailTemplateSaveClicked();
                return true;
            case R.id.email_template_back_button:
                onEmailTemplateBackClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recipientTitle = (TextView) getView().findViewById(R.id.emailTemplateRecipientTitle);
        fromTitle = (TextView) getView().findViewById(R.id.emailTemplateFromTitle);
        subjectTitle = (TextView) getView().findViewById(R.id.emailTemplateSubjectTitle);
        bodyTitle = (TextView) getView().findViewById(R.id.emailTemplateBodyTitle);


        recipientView = (EditText) getView().findViewById(R.id.emailTemplateRecipientView);
        recipientView.setOnFocusChangeListener(new FocusChangeAlphaListener(recipientTitle, recipientView));

        fromView = (EditText) getView().findViewById(R.id.emailTemplateFromView);
        fromView.setOnFocusChangeListener(new FocusChangeAlphaListener(fromTitle, fromView));

        subjectView = (EditText) getView().findViewById(R.id.emailTemplateSubjectView);
        subjectView.setOnFocusChangeListener(new FocusChangeAlphaListener(subjectTitle, subjectView));

        bodyView = (EditText) getView().findViewById(R.id.emailTemplateBodyView);
        bodyView.setOnFocusChangeListener(new FocusChangeAlphaListener(bodyTitle, bodyView));


        EmailTemplateObject emailTemplateObject = resourceManager.getEmailTemplate();

        recipientView.setText(emailTemplateObject.getRecipient());
        fromView.setText(emailTemplateObject.getFrom());
        subjectView.setText(emailTemplateObject.getSubject());
        bodyView.setText(emailTemplateObject.getBody());
    }

    public void onEmailTemplateSaveClicked() {
        String recipient = recipientView.getText().toString();
        String from = fromView.getText().toString();
        String subject = subjectView.getText().toString();
        String body = bodyView.getText().toString();

        EmailTemplateObject emailTemplateObject = new EmailTemplateObject(recipient, from, subject, body);

        resourceManager.setEmailTemplate(emailTemplateObject);

        Toast.makeText(getActivity(), R.string.email_template_save_message, Toast.LENGTH_SHORT).show();
    }

    public void onEmailTemplateBackClicked() {
        ((MainActivity) getActivity()).goToMainPage();
    }

}
