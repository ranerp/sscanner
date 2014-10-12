package com.twtchnz.superscannr.superscanner.fragments;



import android.os.Bundle;
import android.app.Fragment;
import android.view.*;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.twtchnz.superscannr.superscanner.MainActivity;
import com.twtchnz.superscannr.superscanner.R;
import com.twtchnz.superscannr.superscanner.resources.DatabaseEntities.OrderHeaderObject;
import com.twtchnz.superscannr.superscanner.resources.ResourceManager;
import org.w3c.dom.Text;

public class OrderTemplateFragment extends Fragment {

    ResourceManager resourceManager;

    TextView titleTitle;
    TextView companyTitle;
    TextView addressTitle;
    TextView cityTitle;

    EditText titleView;
    EditText companyView;
    EditText addressView;
    EditText cityView;

    public OrderTemplateFragment(ResourceManager resourceManager) {
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
        return inflater.inflate(R.layout.fragment_order_template, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.order_template_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.order_template_save_button:
                onOrderTemplateSaveClicked();
                return true;
            case R.id.order_template_back_button:
                onOrderTemplateBackClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        titleTitle = (TextView) getView().findViewById(R.id.orderTemplateTitleTitle);
        companyTitle = (TextView) getView().findViewById(R.id.orderTemplateCompanyTitle);
        addressTitle = (TextView) getView().findViewById(R.id.orderTemplateAddressTitle);
        cityTitle = (TextView) getView().findViewById(R.id.orderTemplateCityTitle);


        titleView = (EditText) getView().findViewById(R.id.orderTemplateTitleView);
        titleView.setOnFocusChangeListener(new FocusChangeAlphaListener(titleTitle, titleView));

        companyView = (EditText) getView().findViewById(R.id.orderTemplateCompanyView);
        companyView.setOnFocusChangeListener(new FocusChangeAlphaListener(companyTitle, companyView));

        addressView = (EditText) getView().findViewById(R.id.orderTemplateAddressView);
        addressView.setOnFocusChangeListener(new FocusChangeAlphaListener(addressTitle, addressView));

        cityView = (EditText) getView().findViewById(R.id.orderTemplateCityView);
        cityView.setOnFocusChangeListener(new FocusChangeAlphaListener(cityTitle, cityView));

        OrderHeaderObject orderHeaderObject = resourceManager.getOrderHeaderTemplate();

        titleView.setText(orderHeaderObject.getTitle());
        companyView.setText(orderHeaderObject.getCompany());
        addressView.setText(orderHeaderObject.getAddress());
        cityView.setText(orderHeaderObject.getCity());
    }

    public void onOrderTemplateSaveClicked() {
        String title = titleView.getText().toString();
        String company = companyView.getText().toString();
        String address = addressView.getText().toString();
        String city = cityView.getText().toString();

        OrderHeaderObject orderHeaderObject = new OrderHeaderObject(title, company, address, city);

        resourceManager.setOrderHeaderTemplate(orderHeaderObject);

        Toast.makeText(getActivity(), R.string.order_template_save_message, Toast.LENGTH_SHORT).show();
    }

    public void onOrderTemplateBackClicked() {
        ((MainActivity) getActivity()).goToMainPage();
    }
}
