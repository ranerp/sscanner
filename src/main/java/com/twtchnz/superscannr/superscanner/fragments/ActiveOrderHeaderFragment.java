package com.twtchnz.superscannr.superscanner.fragments;



import android.os.Bundle;
import android.app.Fragment;
import android.view.*;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.twtchnz.superscannr.superscanner.R;
import com.twtchnz.superscannr.superscanner.resources.DatabaseEntities.OrderHeaderObject;
import com.twtchnz.superscannr.superscanner.resources.ResourceManager;

public class ActiveOrderHeaderFragment extends Fragment {

    ResourceManager resourceManager;

    TextView titleTitle;
    TextView companyTitle;
    TextView addressTitle;
    TextView cityTitle;

    EditText titleView;
    EditText companyView;
    EditText addressView;
    EditText cityView;

    public ActiveOrderHeaderFragment(ResourceManager resourceManager) {
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
        return inflater.inflate(R.layout.fragment_active_order_header, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.active_order_header, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.active_order_header_save_button:
                onActiveOrderHeaderSaveClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        titleTitle = (TextView) getView().findViewById(R.id.activeOrderHeaderTitleTitle);
        companyTitle = (TextView) getView().findViewById(R.id.activeOrderHeaderCompanyTitle);
        addressTitle = (TextView) getView().findViewById(R.id.activeOrderHeaderAddressTitle);
        cityTitle = (TextView) getView().findViewById(R.id.activeOrderHeaderCityTitle);

        titleView = (EditText) getView().findViewById(R.id.activeOrderHeaderTitleView);
        titleView.setOnFocusChangeListener(new FocusChangeAlphaListener(titleTitle, titleView));

        companyView = (EditText) getView().findViewById(R.id.activeOrderHeaderCompanyView);
        companyView.setOnFocusChangeListener(new FocusChangeAlphaListener(companyTitle, companyView));

        addressView = (EditText) getView().findViewById(R.id.activeOrderHeaderAddressView);
        addressView.setOnFocusChangeListener(new FocusChangeAlphaListener(addressTitle, addressView));

        cityView = (EditText) getView().findViewById(R.id.activeOrderHeaderCityView);
        cityView.setOnFocusChangeListener(new FocusChangeAlphaListener(cityTitle, cityView));

    }

    @Override
    public void onResume() {
        super.onResume();

        setViews();
    }

    public void setViews() {
        OrderHeaderObject orderHeaderObject = resourceManager.getActiveOrderHeader();

        titleView.setText(orderHeaderObject.getTitle());
        companyView.setText(orderHeaderObject.getCompany());
        addressView.setText(orderHeaderObject.getAddress());
        cityView.setText(orderHeaderObject.getCity());
    }

    public void onActiveOrderHeaderSaveClicked() {
        String title = titleView.getText().toString();
        String company = companyView.getText().toString();
        String address = addressView.getText().toString();
        String city = cityView.getText().toString();

        OrderHeaderObject orderHeaderObject = new OrderHeaderObject(title, company, address, city);

        resourceManager.setActiveOrderHeader(orderHeaderObject);

        Toast.makeText(getActivity(), R.string.active_order_header_save_message, Toast.LENGTH_SHORT).show();
    }


}
