package com.example.bledemo;


import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class DeviceAdapter extends ArrayAdapter<BluetoothDevice>{
    private static class ViewHolder {
        TextView txtName;
        TextView txtAddress;
    }

    public DeviceAdapter(List<BluetoothDevice> data, Context context) {
        super(context, R.layout.device_item, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BluetoothDevice device = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.device_item, parent, false);
            viewHolder.txtName = convertView.findViewById(R.id.name);
            viewHolder.txtAddress = convertView.findViewById(R.id.address);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtName.setText(device.getName());
        viewHolder.txtAddress.setText(device.getAddress());
        return convertView;
    }
}