package com.twtchnz.superscannr.superscanner.fragments;



import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.*;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.twtchnz.superscannr.superscanner.R;
import com.twtchnz.superscannr.superscanner.resources.DatabaseEntities.OrderFooterObject;
import com.twtchnz.superscannr.superscanner.resources.ResourceManager;
import com.twtchnz.superscannr.superscanner.utils.Utils;

public class ActiveOrderFooterFragment extends Fragment {

    ResourceManager resourceManager;

    TextView scrapCopperTitle;
    TextView emptyReelsTitle;
    TextView totalPalletsTitle;
    TextView totalProductsTitle;

    EditText scrapCopperView;
    EditText emptyReelsView;
    EditText totalPalletsView;
    EditText totalProductsView;

    public ActiveOrderFooterFragment(ResourceManager resourceManager) {
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
        return inflater.inflate(R.layout.fragment_active_order_footer, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.active_order_footer, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.active_order_footer_save_button:
                onFooterSaveClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        scrapCopperTitle = (TextView) getView().findViewById(R.id.footerCopperTitle);
        emptyReelsTitle = (TextView) getView().findViewById(R.id.footerEmptyReelsTitle);
        totalPalletsTitle = (TextView) getView().findViewById(R.id.footerTotalPalletsTitle);
        totalProductsTitle = (TextView) getView().findViewById(R.id.footerTotalProductsTitle);

        scrapCopperView = (EditText) getView().findViewById(R.id.footerScraCopperView);
        scrapCopperView.setOnFocusChangeListener(new FocusChangeAlphaListener(scrapCopperTitle, scrapCopperView));

        emptyReelsView = (EditText) getView().findViewById(R.id.footerEmptyReelsView);
        emptyReelsView.setOnFocusChangeListener(new FocusChangeAlphaListener(emptyReelsTitle, emptyReelsView));

        totalPalletsView = (EditText) getView().findViewById(R.id.footerTotalPalletsView);
        totalPalletsView.setOnFocusChangeListener(new FocusChangeAlphaListener(totalPalletsTitle, totalPalletsView));

        totalProductsView = (EditText) getView().findViewById(R.id.footerTotalProductsView);
        totalProductsView.setOnFocusChangeListener(new FocusChangeAlphaListener(totalProductsTitle, totalProductsView));
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

    public void onFooterSaveClicked() {
        String scrapCopper = scrapCopperView.getText().toString();
        String emptyReels = emptyReelsView.getText().toString();
        String totalPallets = totalPalletsView.getText().toString();
        String totalProducts = totalProductsView.getText().toString();

        OrderFooterObject orderFooterObject = new OrderFooterObject(scrapCopper, emptyReels, totalPallets, totalProducts);

        resourceManager.setActiveOrderFooter(orderFooterObject);

        Toast.makeText(getActivity(), R.string.active_order_footer_save_message, Toast.LENGTH_SHORT).show();
    }


}
