package com.twtchnz.superscannr.superscanner.fragments;



import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import com.twtchnz.superscannr.superscanner.R;
import com.twtchnz.superscannr.superscanner.resources.DatabaseEntities.OrderFooterObject;
import com.twtchnz.superscannr.superscanner.resources.ResourceManager;
import com.twtchnz.superscannr.superscanner.utils.Utils;

public class ActiveOrderFooterFragment extends Fragment {

    ResourceManager resourceManager;

    EditText scrapCopperView;
    EditText emptyReelsView;
    EditText totalPalletsView;
    EditText totalProductsView;

    public ActiveOrderFooterFragment(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_active_order_footer, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        scrapCopperView = (EditText) getView().findViewById(R.id.footerScraCopperView);
        emptyReelsView = (EditText) getView().findViewById(R.id.footerEmptyReelsView);
        totalPalletsView = (EditText) getView().findViewById(R.id.footerTotalPalletsView);
        totalProductsView = (EditText) getView().findViewById(R.id.footerTotalProductsView);
    }

    @Override
    public void onResume() {
        super.onResume();

        setViews();
    }

    public void setViews() {
        OrderFooterObject orderFooterObject = resourceManager.getActiveOrderFooter();

        scrapCopperView.setText(orderFooterObject.getScrapCopper());
        emptyReelsView.setText(orderFooterObject.getEmptyReels());
        totalPalletsView.setText(orderFooterObject.getTotalPallets());
        totalProductsView.setText(orderFooterObject.getTotalProducts());
    }

    public void onFooterSaveClicked(View view) {
        String scrapCopper = scrapCopperView.getText().toString();
        String emptyReels = emptyReelsView.getText().toString();
        String totalPallets = totalPalletsView.getText().toString();
        String totalProducts = totalProductsView.getText().toString();

        OrderFooterObject orderFooterObject = new OrderFooterObject(scrapCopper, emptyReels, totalPallets, totalProducts);

        resourceManager.setActiveOrderFooter(orderFooterObject);
    }


}
