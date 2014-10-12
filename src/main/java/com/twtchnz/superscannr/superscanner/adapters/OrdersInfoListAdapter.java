package com.twtchnz.superscannr.superscanner.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.twtchnz.superscannr.superscanner.R;
import com.twtchnz.superscannr.superscanner.resources.DatabaseEntities.OrderInfoObject;
import com.twtchnz.superscannr.superscanner.utils.DateFormatter;
import com.twtchnz.superscannr.superscanner.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class OrdersInfoListAdapter extends ArrayAdapter<OrderInfoObject> {

    public static class Holder {
        public OrderInfoObject object;
        public int objectPosition;

        public TextView idView;
        public TextView nameView;
        public TextView dateView;
        public Switch deleteSwitch;
        public ImageButton activeButton;
    }

    private int layoutResourceId;

    private Set<String> deleteIdsSet;
    private Set<OrderInfoObject> listDeleteRows;

    public ArrayList<OrderInfoObject> orderInfoObjects;

    public OrdersInfoListAdapter(Context context, int layoutResourceId, ArrayList<OrderInfoObject> orderInfoObjects) {
        super(context, layoutResourceId, orderInfoObjects);

        this.orderInfoObjects = orderInfoObjects;
        this.layoutResourceId = layoutResourceId;
        this.deleteIdsSet = new TreeSet<String>();
        this.listDeleteRows = new TreeSet<OrderInfoObject>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);

            holder = initHolder(convertView);

        } else {
            holder = (Holder) convertView.getTag();
        }

        if ((position + 1) % 2 == 0) {
            convertView.setBackgroundColor(getContext().getResources().getColor(R.color.scan_second_row_color));
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        Collections.sort(orderInfoObjects);

        setupItem(holder, position);
        convertView.setTag(holder);

        return convertView;
    }

    private Holder initHolder(View convertView) {
        Holder holder = new Holder();
        holder.idView = (TextView) convertView.findViewById(R.id.orderArchiveIdView);
        holder.nameView = (TextView) convertView.findViewById(R.id.orderArchiveNameView);
        holder.dateView = (TextView) convertView.findViewById(R.id.orderArchiveDateView);
        holder.deleteSwitch = (Switch) convertView.findViewById(R.id.orderArchiveDeleteSwitch);
        holder.activeButton = (ImageButton) convertView.findViewById(R.id.orderArchiveActivateButton);

        return holder;
    }

    private void setupItem(Holder holder, int position) {
        holder.object = orderInfoObjects.get(position);

        holder.objectPosition = position;
        holder.idView.setText(holder.object.getID());
        holder.nameView.setText(holder.object.getName());
        holder.dateView.setText(DateFormatter.formatDate(Utils.DATE_FORMAT_DATABASE, holder.object.getDate(), Utils.DATE_FORMAT_IN_APP));

        holder.deleteSwitch.setTag(holder);
        holder.activeButton.setTag(holder);

        if(deleteIdsSet.contains(holder.object.getID()))
            holder.deleteSwitch.setChecked(true);
        else
            holder.deleteSwitch.setChecked(false);
    }

    public void tryPushToDeleteSet(Switch deleteSwitch, String ID, OrderInfoObject orderInfoObject) {
        if (deleteSwitch.isChecked()) {
            deleteIdsSet.add(ID);
            listDeleteRows.add(orderInfoObject);
        } else {
            deleteIdsSet.remove(ID);
            listDeleteRows.remove(orderInfoObject);
        }
    }

    public boolean isDeleteSetEmpty() {
        return deleteIdsSet.isEmpty();
    }

    public Object[] getDeleteIds() {
        return deleteIdsSet.toArray();
    }

    public void removeRows() {
        for (OrderInfoObject object : listDeleteRows) {
            orderInfoObjects.remove(object);
        }

        deleteIdsSet.clear();
        listDeleteRows.clear();

        notifyDataSetChanged();
    }
}
