package com.example.bledemo;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;



public class ScanFragment extends Fragment {
    private static final String TAG = "Scan";
    private DeviceAdapter adapter;
    private MainActivity ma;
    private boolean isScanning = false;
    private ArrayList<BluetoothDevice> devicesList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scan, container, false);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ma = (MainActivity) getActivity();
        ma.findViewById(R.id.scanButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ma.ba.isEnabled()) {
                    Toast.makeText(ma, "Bluetooth is disabled. Enabling", Toast.LENGTH_LONG).show();
                    ma.ba.enable();
                }
                if (!isScanning) startScan();
            }
        });

        adapter = new DeviceAdapter(devicesList, ma);
        ListView lv = ma.findViewById(R.id.listViewScan);
        lv.setAdapter(adapter);
        lv.setClickable(true);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                stopScan();
                ma.device = devicesList.get(position);

                FragmentTransaction ft = ma.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, new ServiceListFragment());
                ft.addToBackStack(null);
                ft.commit();
            }
        });
    }

    private void startScan(){
        Log.i(TAG, "Starting BLE Scan");
        devicesList.clear();
        adapter.notifyDataSetChanged();
        isScanning = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScan();
            }
        }, 5000);
        Toast.makeText(ma, "Scanning...", Toast.LENGTH_SHORT).show();
        ma.ba.startLeScan(leCallback);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopScan();
    }

    private void stopScan() {
        if (!isScanning) return;
        isScanning = false;
        ma.ba.stopLeScan(leCallback);
        Toast.makeText(ma, "Scan ended", Toast.LENGTH_SHORT).show();
    }

    private BluetoothAdapter.LeScanCallback leCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.i(TAG, "Found a device");
            ma.runOnUiThread(new Runnable()  {
                @Override
                public void run() {
                    if (devicesList.indexOf(device) == -1){
                        devicesList.add(device);
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }
    };

}
