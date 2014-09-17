package com.twtchnz.superscannr.superscanner.fragments;


import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.twtchnz.superscannr.superscanner.R;
import com.twtchnz.superscannr.superscanner.adapters.BarCodeObjectListAdapter;
import com.twtchnz.superscannr.superscanner.resources.DatabaseEntities.BarCodeObject;
import com.twtchnz.superscannr.superscanner.resources.ResourceManager;
import com.twtchnz.superscannr.superscanner.utils.Utils;

import java.util.ArrayList;
import java.util.UUID;

public class ScanFragment extends Fragment {


    private BarCodeObjectListAdapter barCodeObjectListAdapter;

    private ListView scanObjectsListView;

    private ResourceManager resourceManager;

    private Button deleteButton;
    private Button scanButton;

    private ScanStates scanState;
    private BarCodeObject activeScanObject;
    private ToggleButton lastToggleButton;


    public ScanFragment(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_second_menu, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        scanObjectsListView = (ListView) getView().findViewById(R.id.scanObjectsListView);

        scanState = ScanStates.NEW_ORDER;
        scanButton = (Button) getView().findViewById(R.id.scanButton);

        deleteButton = (Button) getView().findViewById(R.id.scanDeleteButton);
        deleteButton.setEnabled(false);

        barCodeObjectListAdapter = new BarCodeObjectListAdapter(getActivity(), this, R.layout.scan_object_row, new ArrayList<BarCodeObject>());
        scanObjectsListView.setAdapter(barCodeObjectListAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        barCodeObjectListAdapter.addAll(resourceManager.getBarCodeObjects());
    }

    public void onScanDeleteClicked(View view) {
        resourceManager.deleteBarCodeObjects(barCodeObjectListAdapter.getDeleteIds());
        barCodeObjectListAdapter.removeRows();

        deleteButton.setEnabled(false);
    }

    public void onItemEditClicked(View view) {
        BarCodeObjectListAdapter.Holder holder = (BarCodeObjectListAdapter.Holder) view.getTag();

        ItemEditDialogFragment dialogFragment = new ItemEditDialogFragment(this, resourceManager, holder.object);
        dialogFragment.show(getFragmentManager(), getString(R.string.item_edit_dialog));


    }

    public void onScanClick(View view) {
        prepareForScan(ScanStates.NEW_ORDER, null, null);
        initScan();
    }

    public void onOrderNumberToggleClicked(View view) {
        BarCodeObjectListAdapter.Holder holder = (BarCodeObjectListAdapter.Holder) view.getTag();

        prepareForScan(ScanStates.ORDER_NUMBER, holder.orderNumberToggleButton, holder.object);
        initScan();
    }

    public void onMaterialCodeToggleClicked(View view) {
        BarCodeObjectListAdapter.Holder holder = (BarCodeObjectListAdapter.Holder) view.getTag();

        prepareForScan(ScanStates.MATERIAL_CODE, holder.materialCodeToggleButton, holder.object);
        initScan();
    }

    public void onSalesOrderToggleClicked(View view) {
        BarCodeObjectListAdapter.Holder holder = (BarCodeObjectListAdapter.Holder) view.getTag();

        prepareForScan(ScanStates.SALES_ORDER, holder.salesOrderToggleButton, holder.object);
        initScan();
    }

    private void prepareForScan(ScanStates scanState, ToggleButton toggleButton, BarCodeObject barCodeObject) {
        cleanScanState();
        this.scanState = scanState;

        if (toggleButton != null) {
            toggleButton.setChecked(true);
            lastToggleButton = toggleButton;
        }


        if(barCodeObject != null)
            activeScanObject = barCodeObject;
    }

    private void initScan() {
        IntentIntegrator scanIntegration = new IntentIntegrator(this);
        scanIntegration.addExtra("SAVE_HISTORY", false);
        scanIntegration.initiateScan();
    }

    private void cleanScanState() {
        if (lastToggleButton != null) {
            lastToggleButton.setChecked(false);

            lastToggleButton = null;
        }

        scanState = ScanStates.NEW_ORDER;
    }

    public void onScanRowSwitchClicked(View view) {
        BarCodeObjectListAdapter.Holder holder = (BarCodeObjectListAdapter.Holder) view.getTag();

        barCodeObjectListAdapter.tryPushToDeleteSet(holder.deleteSwitch, holder.object.getID(), holder.object);

        if(barCodeObjectListAdapter.isDeleteSetEmpty())
            deleteButton.setEnabled(false);
        else
            deleteButton.setEnabled(true);
    }

    public void onItemChanged(BarCodeObject barCodeObject) {
        resourceManager.putBarCodeObject(barCodeObject);
        barCodeObjectListAdapter.notifyDataSetChanged();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (scanningResult != null && scanningResult.getFormatName() != null && scanningResult.getContents() != null) {

            if(scanState == ScanStates.NEW_ORDER)
                createBarCodeObject(scanningResult.getFormatName(), scanningResult.getContents());
            else
                updateBarCodeObject(scanningResult.getContents());

        } else {
            Toast.makeText(getActivity().getApplicationContext(), R.string.error_getting_barcode, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateBarCodeObject(String code) {
        if (scanState == ScanStates.ORDER_NUMBER) {

            if(isOrderNumberExists(code, activeScanObject))
                Toast.makeText(getActivity(), getString(R.string.order_number_exists), Toast.LENGTH_SHORT).show();
            else
                activeScanObject.setOrderNumber(code);

        } else if (scanState == ScanStates.MATERIAL_CODE)
            activeScanObject.setMaterialCode(code);
        else if (scanState == ScanStates.SALES_ORDER)
            activeScanObject.setSalesOrder(code);

        resourceManager.putBarCodeObject(activeScanObject);
        barCodeObjectListAdapter.notifyDataSetChanged();

    }

    private boolean isOrderNumberExists(String code, BarCodeObject object) {
        for (BarCodeObject barCodeObject : barCodeObjectListAdapter.getBarCodeObjects()) {
            if(!barCodeObject.equals(object) && barCodeObject.getOrderNumber().equals(code))
                return true;
        }

        return false;
    }

    private void createBarCodeObject(String format, String orderNumber) {
        boolean makeNew = true;

        for (BarCodeObject barCodeObject : barCodeObjectListAdapter.getBarCodeObjects()) {
            if (barCodeObject.getOrderNumber().equals(orderNumber)) {
                barCodeObject.countUp();
                onItemChanged(barCodeObject);
                makeNew = false;
                break;
            }
        }

        if (makeNew) {
            BarCodeObject newObject = new BarCodeObject(UUID.randomUUID().toString(), orderNumber, "", "", format, 1);
            barCodeObjectListAdapter.addBarCodeObject(newObject);
            onItemChanged(newObject);
        }
    }

}