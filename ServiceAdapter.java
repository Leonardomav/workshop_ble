package com.example.bledemo;


import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


public class ServiceAdapter extends ArrayAdapter<BluetoothGattService> {
    private static class ViewHolder {
        TextView txtUUID;
        TextView txtChars;
    }

    public ServiceAdapter(List<BluetoothGattService> data, Context context) {
        super(context, R.layout.service_item, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BluetoothGattService service = getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.service_item, parent, false);
            viewHolder.txtUUID = convertView.findViewById(R.id.uuid);
            viewHolder.txtChars = convertView.findViewById(R.id.number);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtUUID.setText(service.getUuid().toString());
        viewHolder.txtChars.setText(Integer.toString(service.getCharacteristics().size()));

        return convertView;
    }
}