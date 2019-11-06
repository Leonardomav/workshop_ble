package com.example.bledemo;


import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class CharacteristicListFragment extends Fragment implements WriteDialogFragment.WriteDialogListener  {
    private static final String TAG = "CharacteristicList";
    private MainActivity ma;
    private BluetoothGatt gatt;
    private CharacteristicAdapter adapter;
    private List<BluetoothGattCharacteristic> characteristicsList;
    private BluetoothGattCharacteristic current_characteristic;

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
        Toast.makeText(ma, "Reading characteristics", Toast.LENGTH_SHORT).show();

        characteristicsList = new ArrayList<>();
        adapter = new CharacteristicAdapter(characteristicsList, ma);
        ListView lv = ma.findViewById(R.id.listViewDetails);
        lv.setAdapter(adapter);
        lv.setClickable(true);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                current_characteristic = characteristicsList.get(position);
                int properties = current_characteristic.getProperties();
                if (hasWriteProperty(properties)) {
                    Log.i(TAG, "clicked on write");
                    // write value
                    openDialog();
                }
                else if (hasReadProperty(properties)){
                    Log.i(TAG, "clicked on read");
                    gatt.readCharacteristic(current_characteristic);
                }
                else if (hasNotifyProperty(properties)){
                    Log.i(TAG, "clicked on notify");
                    gatt.setCharacteristicNotification(current_characteristic, true);
                    BluetoothGattDescriptor descriptor = current_characteristic.getDescriptor(
                            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    gatt.writeDescriptor(descriptor);
                }
            }
        });

    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {

                Log.i(TAG, "Connected to GATT server.");
                gatt.discoverServices();
            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "Disconnected from GATT server.");
                ma.getSupportFragmentManager().popBackStackImmediate();
            }
        }
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            Log.i(TAG, "characteristic read");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "characteristic read success");

                ma.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });

            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            Log.i(TAG, "characteristic changed");

            ma.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            Log.i(TAG, "characteristic written");

            ma.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });

        }

        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.i(TAG, "Discovered Services.");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                characteristicsList.clear();
                characteristicsList.addAll(gatt.getService(ma.service).getCharacteristics());
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

    public void openDialog() {
        WriteDialogFragment exampleDialog = new WriteDialogFragment(this);
        exampleDialog.show(ma.getSupportFragmentManager(), null);
    }

    @Override
    public void applyText(String message) {
        current_characteristic.setValue(message);
        gatt.writeCharacteristic(current_characteristic);
    }



    static boolean hasReadProperty(int property) {
        return (property & BluetoothGattCharacteristic.PROPERTY_READ) != 0;
    }

    static boolean hasWriteProperty(int property) {
        return (property & BluetoothGattCharacteristic.PROPERTY_WRITE) != 0;
    }

    static boolean hasNotifyProperty(int property) {
        return (property & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0;
    }
    @Override
    public void onPause() {
        super.onPause();
        gatt.close();
    }
}
