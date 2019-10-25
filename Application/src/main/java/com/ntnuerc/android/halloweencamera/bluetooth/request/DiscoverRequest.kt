package com.ntnuerc.android.halloweencamera.bluetooth.request

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.ntnuerc.android.halloweencamera.bluetooth.IBluetoothEventListener
import java.util.*

class DiscoverRequest(private val context : Context, private val eventListener: IBluetoothEventListener) : IBluetoothRequest  {

    val discoveredDevices:MutableList<BluetoothDevice> = mutableListOf()
    private val bluetoothAdapter : BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    private val discoverReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val act = intent.action
            Log.d("SPP", "discover action " + act )
            if ( intent.action == BluetoothDevice.ACTION_FOUND ) {
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                addDiscoveredDevice(device!!)
            }
            else if ( intent.action == BluetoothAdapter.ACTION_DISCOVERY_FINISHED ) {
                eventListener.onDiscovered()
            }
        }
    }

    fun discover() {
        registerReceiver()

        if (bluetoothAdapter.isDiscovering) {
            bluetoothAdapter.cancelDiscovery()
        }
//
//            val dIntent =  Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
//            dIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
//            startActivity(dIntent);
//
        bluetoothAdapter.startDiscovery()
        eventListener.onDiscovering()
    }

    fun cancelDiscovery() {
        if (bluetoothAdapter.isDiscovering) {
            bluetoothAdapter.cancelDiscovery()
        }
    }
    private fun registerReceiver() {
        context.registerReceiver( discoverReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND ))
        context.registerReceiver( discoverReceiver, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED ) )
    }

    private fun addDiscoveredDevice(device: BluetoothDevice) {
        if (device.bondState != BluetoothDevice.BOND_BONDED)
            return

        for (dev in discoveredDevices) {
            if (dev.address.equals(device.address))
                return
        }

        discoveredDevices.add(device)
    }

    override fun cleanup() {
        context.unregisterReceiver(discoverReceiver)
    }
}