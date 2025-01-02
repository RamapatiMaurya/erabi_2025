package com.school.erabi.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.AdapterView.OnItemClickListener
import androidx.core.app.ActivityCompat
import com.school.erabi.R


class DeviceListActivity : Activity() {
    private lateinit var bluetoothManager: BluetoothManager
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mPairedDevicesArrayAdapter: ArrayAdapter<String>? = null

    @SuppressLint("MissingPermission")
    private val mDeviceClickListener = OnItemClickListener { mAdapterView, mView, mPosition, mLong ->
        try {


            if (UserSlipActivity.checkPermission(this@DeviceListActivity)) {
                mBluetoothAdapter!!.cancelDiscovery()
                val mDeviceInfo = (mView as TextView).text.toString()
                val mDeviceAddress = mDeviceInfo.substring(mDeviceInfo.length - 17)
                Log.v(TAG, "Device_Address $mDeviceAddress")

                val mBundle = Bundle()
                mBundle.putString("DeviceAddress", mDeviceAddress)
                val mBackIntent = Intent()
                mBackIntent.putExtras(mBundle)
                setResult(RESULT_OK, mBackIntent)
                finish()
            }
        } catch (ex: Exception) {

        }
    }

    override fun onCreate(mSavedInstanceState: Bundle?) {
        super.onCreate(mSavedInstanceState)
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS)
        setContentView(R.layout.activity_device_list)
        bluetoothManager = this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
     //   setResult(RESULT_CANCELED)
        mPairedDevicesArrayAdapter = ArrayAdapter(this, R.layout.device_name)

        val mPairedListView = findViewById(R.id.paired_devices) as ListView
        mPairedListView.adapter = mPairedDevicesArrayAdapter
        mPairedListView.onItemClickListener = mDeviceClickListener

        if(UserSlipActivity.checkPermission(this@DeviceListActivity)) {
            mBluetoothAdapter = bluetoothManager.adapter
            val mPairedDevices = mBluetoothAdapter!!.bondedDevices

            if (mPairedDevices.size > 0) {
                (findViewById(R.id.title_paired_devices) as TextView).visibility = View.VISIBLE
                for (mDevice in mPairedDevices) {
                    mPairedDevicesArrayAdapter!!.add(mDevice.name + "\n" + mDevice.address)
                }
            } else {
                val mNoDevices =
                    "None Paired"//getResources().getText(R.string.none_paired).toString();
                mPairedDevicesArrayAdapter!!.add(mNoDevices)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mBluetoothAdapter != null) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            mBluetoothAdapter!!.cancelDiscovery()
        }
    }

    companion object {
        protected val TAG = "TAG"
    }

}