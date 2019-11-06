package com.example.bledemo;


import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import static com.example.bledemo.CharacteristicListFragment.hasReadProperty;
import static com.example.bledemo.CharacteristicListFragment.hasWriteProperty;
import static com.example.bledemo.CharacteristicListFragment.hasNotifyProperty;

public class CharacteristicAdapter extends ArrayAdapter<BluetoothGattCharacteristic>{
    private static class ViewHolder {
        TextView txtUUID;
        TextView txtProperties;
        TextView txtData;
    }

    public CharacteristicAdapter(List<BluetoothGattCharacteristic> data, Context context) {
        super(context, R.layout.characteristic_item, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BluetoothGattCharacteristic characteristic = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.characteristic_item, parent, false);
            viewHolder.txtUUID = convertView.findViewById(R.id.uuid);
            viewHolder.txtProperties = convertView.findViewById(R.id.properties);
            viewHolder.txtData = convertView.findViewById(R.id.data);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtUUID.setText(characteristic.getUuid().toString());
        viewHolder.txtProperties.setText(propertiesText(characteristic));
        viewHolder.txtData.setText(dataToString(characteristic.getValue()));
        return convertView;
    }

    private static String dataToString(byte[] data) {
        if(data==null) return "No data found";
        final StringBuilder sb = new StringBuilder(data.length);

        for(byte byteChar : data) {
            sb.append(String.format("%02X ", byteChar));
        }
        return String.format("%s\nRaw Code: [ %s]", new String(data), sb.toString());
    }

    private String propertiesText(BluetoothGattCharacteristic c){
        int properties = c.getProperties();
        StringBuilder sb = new StringBuilder();
        if (hasReadProperty(properties)) {
            sb.append("R");
        }

        if (hasWriteProperty(properties)) {
            sb.append("W");
        }

        if (hasNotifyProperty(properties)) {
            sb.append("N");
        }
        return sb.toString();
    }
}