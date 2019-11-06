package com.example.bledemo;


import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class ServiceListFragment extends Fragment {
    private static final String TAG = "ServiceList";
    private MainActivity ma;
    private BluetoothGatt gatt;
    private ServiceAdapter adapter;
    private List<BluetoothGattService> serviceList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ma = (MainActivity) getActivity();

        if (ma.device == null) {
            Toast.makeText(ma, "No BLE device selected", Toast.LENGTH_SHORT).show();
            ma.getSupportFragmentManager().popBackStackImmediate();
        }

        gatt = ma.device.connectGatt(ma, false, mGattCallback);
        Toast.makeText(ma, "Reading services", Toast.LENGTH_SHORT).show();

        serviceList = new ArrayList<>();
        adapter = new ServiceAdapter(serviceList,ma);
        ListView lv = ma.findViewById(R.id.listViewDetails);
        lv.setAdapter(adapter);
        lv.setClickable(true);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                ma.service = serviceList.get(position).getUuid();

                FragmentTransaction ft = ma.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, new CharacteristicListFragment());
                ft.addToBackStack(null);
                ft.commit();
            }
        });
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {

                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                boolean start = gatt.discoverServices();
                Log.i(TAG, "Attempting to start service discovery:" + start);
            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "Disconnected from GATT server.");
                ma.getSupportFragmentManager().popBackStackImmediate();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.i(TAG, "Discovered Services.");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                serviceList.clear();
                serviceList.addAll(gatt.getServices());

                ma.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
            else {
                Log.i(TAG, "onServicesDiscovered received: " + status);
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        gatt.close();
    }
}
