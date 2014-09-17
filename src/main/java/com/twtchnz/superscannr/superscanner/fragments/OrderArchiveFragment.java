package com.twtchnz.superscannr.superscanner.fragments;



import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ListView;
import com.twtchnz.superscannr.superscanner.R;
import com.twtchnz.superscannr.superscanner.adapters.OrdersInfoListAdapter;
import com.twtchnz.superscannr.superscanner.resources.DatabaseEntities.OrderInfoObject;
import com.twtchnz.superscannr.superscanner.resources.ResourceManager;
import com.twtchnz.superscannr.superscanner.utils.Utils;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

public class OrderArchiveFragment extends Fragment {

    ResourceManager resourceManager;

    private ListView orderArchiveView;
    private Button deleteButton;

    private OrdersInfoListAdapter ordersInfoListAdapter;
    private ArrayList<OrderInfoObject> orderInfoObjects;

    public OrderArchiveFragment(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_archive, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        orderArchiveView = (ListView) getView().findViewById(R.id.orderArchiveListView);
        deleteButton = (Button) getView().findViewById(R.id.orderArchiveDeleteButton);
        deleteButton.setEnabled(false);

        orderInfoObjects = resourceManager.getOrderInfoObjects();
        ordersInfoListAdapter = new OrdersInfoListAdapter(getActivity(), R.layout.order_archive_row, orderInfoObjects);

        orderArchiveView.setAdapter(ordersInfoListAdapter);
    }

    public void onOrderArchiveRowSwitchClicked(View view) {
        OrdersInfoListAdapter.Holder holder = (OrdersInfoListAdapter.Holder) view.getTag();

        ordersInfoListAdapter.tryPushToDeleteSet(holder.deleteSwitch, holder.object.getID(), holder.object);

        if(ordersInfoListAdapter.isDeleteSetEmpty())
            deleteButton.setEnabled(false);
        else
            deleteButton.setEnabled(true);

    }

    public void onOrderArchiveDeleteClicked(View view) {
        Object[] deleteIds = ordersInfoListAdapter.getDeleteIds();

        if(deleteIds.length > 0)
            resourceManager.deleteOrders(ordersInfoListAdapter.getDeleteIds());

        ordersInfoListAdapter.removeRows();
        deleteButton.setEnabled(false);
    }

    public void onOrderArchiveActivateClicked(View view) {
        OrdersInfoListAdapter.Holder holder = (OrdersInfoListAdapter.Holder) view.getTag();

        resourceManager.activateOrder(holder.idView.getText().toString());
    }

}
