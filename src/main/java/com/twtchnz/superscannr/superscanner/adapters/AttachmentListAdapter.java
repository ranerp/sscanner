package com.twtchnz.superscannr.superscanner.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import com.twtchnz.superscannr.superscanner.R;
import com.twtchnz.superscannr.superscanner.resources.DatabaseEntities.AttachmentObject;

import java.util.ArrayList;

public class AttachmentListAdapter extends ArrayAdapter<AttachmentObject> {

    public static class Holder {
        public AttachmentObject object;

        public ImageButton removeButton;
        public TextView pathView;
    }

    private int layoutResourceId;
    private ArrayList<AttachmentObject> attachmentObjects;

    public AttachmentListAdapter(Context context, int layoutResourceId, ArrayList<AttachmentObject> attachmentObjects) {
        super(context, layoutResourceId, attachmentObjects);

        this.layoutResourceId = layoutResourceId;
        this.attachmentObjects = attachmentObjects;
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

        setupItem(holder, position);
        convertView.setTag(holder);

        return convertView;
    }

    private Holder initHolder(View convertView) {
        Holder holder = new Holder();
        holder.removeButton = (ImageButton) convertView.findViewById(R.id.attachmentRemoveButton);
        holder.pathView = (TextView) convertView.findViewById(R.id.attachmentPathView);

        return holder;
    }

    private void setupItem(Holder holder, int position) {
        holder.object = attachmentObjects.get(position);

        holder.pathView.setText(holder.object.getPath());

        holder.removeButton.setTag(holder);

    }

}
