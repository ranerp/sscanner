package com.twtchnz.superscannr.superscanner.fragments;



import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import com.twtchnz.superscannr.superscanner.R;
import com.twtchnz.superscannr.superscanner.resources.DatabaseEntities.MainInfoObject;
import com.twtchnz.superscannr.superscanner.resources.ResourceManager;
import com.twtchnz.superscannr.superscanner.utils.Utils;

import java.util.Map;

public class FirstMenuFragment extends Fragment {

    Button deleteOrderButton;
    Switch deleteOrderSwitch;

    TextView activeOrderView;
    TextView dateCreatedView;

    ResourceManager resourceManager;

    public FirstMenuFragment(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first_menu, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activeOrderView = (TextView) getView().findViewById(R.id.activeOrderView);
        dateCreatedView = (TextView) getView().findViewById(R.id.dateCreatedView);

        deleteOrderButton = (Button) getView().findViewById(R.id.deleteOrderButton);
        deleteOrderSwitch = (Switch) getView().findViewById(R.id.deleteOrderSwitch);

        setViews();

        deleteOrderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onSwitchClick(buttonView);
            }
        });

    }

    private void setViews() {
        MainInfoObject mainInfoObject = resourceManager.getOrderInfo();

        activeOrderView.setText(mainInfoObject.getName());
        dateCreatedView.setText(mainInfoObject.getDate());

        deleteOrderSwitch.setChecked(false);
        deleteOrderButton.setEnabled(false);

        if(resourceManager.isDummyOrder())
            deleteOrderSwitch.setEnabled(false);
        else
            deleteOrderSwitch.setEnabled(true);
    }

    public void onSwitchClick(View view) {
        if(deleteOrderSwitch.isChecked())
            deleteOrderButton.setEnabled(true);
        else
            deleteOrderButton.setEnabled(false);
    }

    public void onNewOrderClicked(View view) {
        setViews();
    }

    public void onDeleteOrderClicked(View view) {
        String[] array = { resourceManager.getOrderUUID() };
        resourceManager.deleteOrders(array);
        setViews();
    }

}
