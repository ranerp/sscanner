package com.twtchnz.superscannr.superscanner.fragments;



import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import com.twtchnz.superscannr.superscanner.R;
import com.twtchnz.superscannr.superscanner.resources.DatabaseEntities.OrderHeaderObject;
import com.twtchnz.superscannr.superscanner.resources.ResourceManager;

public class ActiveOrderHeaderFragment extends Fragment {

    ResourceManager resourceManager;

    EditText titleView;
    EditText companyView;
    EditText addressView;
    EditText cityView;

    public ActiveOrderHeaderFragment(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_active_order_header, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        titleView = (EditText) getView().findViewById(R.id.activeOrderHeaderTitleView);
        companyView = (EditText) getView().findViewById(R.id.activeOrderHeaderCompanyView);
        addressView = (EditText) getView().findViewById(R.id.activeOrderHeaderAddressView);
        cityView = (EditText) getView().findViewById(R.id.activeOrderHeaderCityView);

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

    public void onActiveOrderHeaderSaveClicked(View view) {
        String title = titleView.getText().toString();
        String company = companyView.getText().toString();
        String address = addressView.getText().toString();
        String city = cityView.getText().toString();

        OrderHeaderObject orderHeaderObject = new OrderHeaderObject(title, company, address, city);

        resourceManager.setActiveOrderHeader(orderHeaderObject);
    }


}
