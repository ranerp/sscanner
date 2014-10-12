package com.twtchnz.superscannr.superscanner.fragments;



import android.opengl.Visibility;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.*;

import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.twtchnz.superscannr.superscanner.MainActivity;
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

    private MenuItem deleteButton;
    private Menu menu;

    private OrdersInfoListAdapter ordersInfoListAdapter;
    private ArrayList<OrderInfoObject> orderInfoObjects;

    public OrderArchiveFragment(ResourceManager resourceManager) {
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
        return inflater.inflate(R.layout.fragment_order_archive, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.order_archive_menu, menu);
        this.menu = menu;
        this.deleteButton = this.menu.findItem(R.id.order_archive_delete_button);
        this.deleteButton.setEnabled(false);
        this.deleteButton.setIcon(R.drawable.ic_delete_not_active);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.order_archive_delete_button:
                onOrderArchiveDeleteClicked();
                return true;
            case R.id.order_archive_back_button:
                onOrderArchiveBackClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        orderArchiveView = (ListView) getView().findViewById(R.id.orderArchiveListView);

        orderInfoObjects = resourceManager.getOrderInfoObjects();
        ordersInfoListAdapter = new OrdersInfoListAdapter(getActivity(), R.layout.order_archive_row, orderInfoObjects);

        orderArchiveView.setAdapter(ordersInfoListAdapter);
    }

    public void onOrderArchiveRowSwitchClicked(View view) {
        OrdersInfoListAdapter.Holder holder = (OrdersInfoListAdapter.Holder) view.getTag();

        ordersInfoListAdapter.tryPushToDeleteSet(holder.deleteSwitch, holder.object.getID(), holder.object);

        if (deleteButton != null) {
            if (ordersInfoListAdapter.isDeleteSetEmpty()) {
                deleteButton.setEnabled(false);
                deleteButton.setIcon(R.drawable.ic_delete_not_active);
            } else {
                deleteButton.setEnabled(true);
                deleteButton.setIcon(R.drawable.ic_delete);
            }
        }

    }

    public void onOrderArchiveDeleteClicked() {
        Object[] deleteIds = ordersInfoListAdapter.getDeleteIds();

        if(deleteIds.length > 0)
            resourceManager.deleteOrders(ordersInfoListAdapter.getDeleteIds());

        ordersInfoListAdapter.removeRows();

        if (deleteButton != null) {
            deleteButton.setEnabled(false);
            deleteButton.setIcon(R.drawable.ic_delete_not_active);
        }

        Toast.makeText(getActivity(), R.string.orders_delete_message, Toast.LENGTH_SHORT).show();

        ((MainActivity) getActivity()).setPagerAdapterScrollLimit();
    }

    public void onOrderArchiveBackClicked() {
        ((MainActivity) getActivity()).goToMainPage();
    }

    public void onOrderArchiveActivateClicked(View view) {
        OrdersInfoListAdapter.Holder holder = (OrdersInfoListAdapter.Holder) view.getTag();

        resourceManager.activateOrder(holder.idView.getText().toString());
    }

}
