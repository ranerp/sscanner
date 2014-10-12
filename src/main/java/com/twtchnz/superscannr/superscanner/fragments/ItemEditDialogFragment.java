package com.twtchnz.superscannr.superscanner.fragments;

import android.app.DialogFragment;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.twtchnz.superscannr.superscanner.R;
import com.twtchnz.superscannr.superscanner.resources.DatabaseEntities.BarCodeObject;
import com.twtchnz.superscannr.superscanner.resources.ResourceManager;

public class ItemEditDialogFragment extends DialogFragment {

    private ScanFragment scanFragment;
    private ResourceManager resourceManager;
    private BarCodeObject barCodeObject;

    private TextView orderNumberView;
    private EditText materialCodeView;
    private EditText salesOrderView;
    private EditText quantityView;

    private ImageButton saveButton;
    private ImageButton cancelButton;

    public ItemEditDialogFragment(ScanFragment scanFragment, ResourceManager resourceManager, BarCodeObject barCodeObject) {
        this.scanFragment = scanFragment;
        this.resourceManager = resourceManager;
        this.barCodeObject = barCodeObject;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_item_edit_dialog, container, false);

        getDialog().setTitle(R.string.item_edit_dialog);

        orderNumberView = (TextView) rootView.findViewById(R.id.itemEditOrderNumberView);
        materialCodeView = (EditText) rootView.findViewById(R.id.itemEditMaterialCodeView);
        salesOrderView = (EditText) rootView.findViewById(R.id.itemEditSalesOrderView);
        quantityView = (EditText) rootView.findViewById(R.id.itemEditQuantityView);

        saveButton = (ImageButton) rootView.findViewById(R.id.itemEditSaveButton);
        cancelButton = (ImageButton) rootView.findViewById(R.id.itemEditCancelButton);

        orderNumberView.setText(barCodeObject.getOrderNumber());
        materialCodeView.setText(barCodeObject.getMaterialCode());
        salesOrderView.setText(barCodeObject.getSalesOrder());
        quantityView.setText(String.valueOf(barCodeObject.getCount()));

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barCodeObject.setMaterialCode(materialCodeView.getText().toString());
                barCodeObject.setSalesOrder(salesOrderView.getText().toString());
                barCodeObject.setCount(Integer.parseInt(quantityView.getText().toString()));
                scanFragment.onItemChanged(barCodeObject);

                dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        return rootView;
    }

}
