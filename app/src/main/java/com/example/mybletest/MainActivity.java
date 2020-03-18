package com.example.mybletest;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MyBleTest";
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS = 0;
    private Button btn0, btn1;
    public BluetoothAdapter mBluetoothAdapter;
    private ArrayList bluetoothDeviceArrayList = new ArrayList();
    private boolean mScanning = false;
    private long SCAN_PERIOD = 1000;
    private Handler mHandler = new Handler();

    List<ScanFilter> filters = Collections.singletonList(new ScanFilter.Builder().build());

    private final static int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        openble();
    }


    private void initView() {
        btn0 = (Button) findViewById(R.id.button0);
        btn1 = (Button) findViewById(R.id.button1);
        btn0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "click btn0!");
                scanLeDevice(true);
            }
        });
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "click btn1!");
                scanLeDevice(false);
            }
        });

    }

    private void openble() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CAMERA,}, PERMISSIONS_REQUEST_CODE_ACCESS);
        }
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(this.getBaseContext().BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

    }

    final BluetoothAdapter.LeScanCallback callback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            bluetoothDeviceArrayList.add(device);
            Log.d(TAG, "run: scanning... addr: " + device.getAddress());
        }

        public void onScanResult(int callbackType, final ScanResult result) {
            Log.d(TAG, "onScanResult ...");
        }
    };

    android.bluetooth.le.ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.d(TAG, " onScanResult... add: " + result.getDevice().getAddress());
            super.onScanResult(callbackType, result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            return super.equals(obj);
        }

        @NonNull
        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString();
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
        }
    };


    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(r, 2000);
        } else {
            mBluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
            mHandler.removeCallbacks(r);
        }
    }

    Runnable r = new Runnable() {
        @Override
        public void run() {
            //do something
            //每隔1s循环执行run方法
            Log.d(TAG, "run ~~~ ");
            mHandler.postDelayed(this, 2000);
            mBluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
            mBluetoothAdapter.getBluetoothLeScanner().startScan(filters, buildScanSettings(), scanCallback);
        }
    };

    //设置蓝牙扫描过滤器集合
    private List<ScanFilter> scanFilterList;
    //设置蓝牙扫描过滤器
    private ScanFilter.Builder scanFilterBuilder;
    //设置蓝牙扫描设置
    private ScanSettings.Builder scanSettingBuilder;

    private List<ScanFilter> buildScanFilters() {
        scanFilterList = new ArrayList<>();
        // 通过服务 uuid 过滤自己要连接的设备   过滤器搜索GATT服务UUID
        scanFilterBuilder = new ScanFilter.Builder();
        ParcelUuid parcelUuidMask = ParcelUuid.fromString("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF");
        ParcelUuid parcelUuid = ParcelUuid.fromString("0000ff07-0000-1000-8000-00805f9b34fb");
        scanFilterBuilder.setServiceUuid(parcelUuid, parcelUuidMask);
        scanFilterList.add(scanFilterBuilder.build());
        return scanFilterList;
    }


    private ScanSettings buildScanSettings() {
        scanSettingBuilder = new ScanSettings.Builder();
        //设置蓝牙LE扫描的扫描模式。
        //使用最高占空比进行扫描。建议只在应用程序处于此模式时使用此模式在前台运行
        scanSettingBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
        //设置蓝牙LE扫描滤波器硬件匹配的匹配模式
        //在主动模式下，即使信号强度较弱，hw也会更快地确定匹配.在一段时间内很少有目击/匹配。
        scanSettingBuilder.setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE);
        //设置蓝牙LE扫描的回调类型
        //为每一个匹配过滤条件的蓝牙广告触发一个回调。如果没有过滤器是活动的，所有的广告包被报告
        scanSettingBuilder.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES);
        return scanSettingBuilder.build();
    }


}
