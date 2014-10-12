package com.twtchnz.superscannr.superscanner.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.twtchnz.superscannr.superscanner.R;
import com.twtchnz.superscannr.superscanner.fragments.ScanFragment;
import com.twtchnz.superscannr.superscanner.resources.DatabaseEntities.BarCodeObject;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

public class BarCodeObjectListAdapter extends ArrayAdapter<BarCodeObject> {

    public static class Holder {
        public BarCodeObject object;
        public Switch deleteSwitch;

        public ToggleButton orderNumberToggleButton;
        public ToggleButton materialCodeToggleButton;
        public ToggleButton salesOrderToggleButton;

        private ImageButton editButton;

        TextView scanRowTitle;
        TextView orderNumberView;

        public TextView materialCodeView;
        public TextView salesOrderView;
        public TextView quantityView;

    }

    private int layoutResourceId;

    public ArrayList<BarCodeObject> barCodeObjects;

    private ScanFragment scanFragment;

    private Set<String> deleteIdsSet;
    private Set<BarCodeObject> listDeleteRows;


    public BarCodeObjectListAdapter(Context context, ScanFragment scanFragment, int layoutResourceId, ArrayList<BarCodeObject> barCodeObjects) {
        super(context, layoutResourceId, barCodeObjects);

        this.barCodeObjects = barCodeObjects;
        this.layoutResourceId = layoutResourceId;
        this.scanFragment = scanFragment;

        this.deleteIdsSet = new TreeSet<String>();
        this.listDeleteRows = new TreeSet<BarCodeObject>();

    }

    public void addBarCodeObject(BarCodeObject barCodeObject) {
        barCodeObjects.add(barCodeObject);
        notifyDataSetChanged();
    }

    public ArrayList<BarCodeObject> getBarCodeObjects() {
        return barCodeObjects;
    }

    public void addAll(ArrayList<BarCodeObject> barCodeObjects) {
        this.barCodeObjects.clear();
        this.barCodeObjects.addAll(barCodeObjects);

        notifyDataSetChanged();
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
            convertView.setBackgroundColor(scanFragment.getResources().getColor(R.color.scan_second_row_color));
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        setupItem(holder, position);

        convertView.setTag(holder);

        return convertView;
    }

    private Holder initHolder(View convertView) {
        Holder holder = new Holder();

        holder.deleteSwitch = (Switch) convertView.findViewById(R.id.scanRowDeleteSwitch);

        holder.orderNumberToggleButton = (ToggleButton) convertView.findViewById(R.id.orderNumberToggle);
        holder.materialCodeToggleButton = (ToggleButton) convertView.findViewById(R.id.materialCodeToggle);
        holder.salesOrderToggleButton = (ToggleButton) convertView.findViewById(R.id.salesOrderToggle);

        holder.editButton = (ImageButton) convertView.findViewById(R.id.scanRowEditButton);

        holder.scanRowTitle = (TextView) convertView.findViewById(R.id.scanRowTitle);
        holder.materialCodeView = (TextView) convertView.findViewById(R.id.rowMaterialCodeView);
        holder.orderNumberView = (TextView) convertView.findViewById(R.id.rowOrderNumberView);
        holder.salesOrderView = (TextView) convertView.findViewById(R.id.rowSalesOrderView);
        holder.quantityView = (TextView) convertView.findViewById(R.id.rowQuantityView);

        return holder;
    }

    private void setupItem(Holder holder, int position) {
        holder.object = barCodeObjects.get(position);

        holder.orderNumberToggleButton.setTag(holder);
        holder.materialCodeToggleButton.setTag(holder);
        holder.salesOrderToggleButton.setTag(holder);
        holder.deleteSwitch.setTag(holder);
        holder.editButton.setTag(holder);

        holder.scanRowTitle.setText(scanFragment.getString(R.string.scan_row_title) + " " + (position + 1));
        holder.orderNumberView.setText(holder.object.getOrderNumber());
        holder.materialCodeView.setText(holder.object.getMaterialCode());
        holder.salesOrderView.setText(holder.object.getSalesOrder());
        holder.quantityView.setText(String.valueOf(holder.object.getCount()));
    }

    public void tryPushToDeleteSet(Switch deleteSwitch, String ID, BarCodeObject barCodeObject) {
        if (deleteSwitch.isChecked()) {
            deleteIdsSet.add(ID);
            listDeleteRows.add(barCodeObject);
        } else {
            deleteIdsSet.remove(ID);
            listDeleteRows.remove(barCodeObject);
        }
    }

    public boolean isDeleteSetEmpty() {
        return deleteIdsSet.isEmpty();
    }

    public Object[] getDeleteIds() {
        return deleteIdsSet.toArray();
    }

    public void removeRows() {
        for (BarCodeObject object : listDeleteRows) {
            barCodeObjects.remove(object);
        }

        deleteIdsSet.clear();
        listDeleteRows.clear();

        notifyDataSetChanged();
    }


}
