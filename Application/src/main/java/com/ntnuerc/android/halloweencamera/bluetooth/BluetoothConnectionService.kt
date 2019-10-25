package com.ntnuerc.android.halloweencamera.bluetooth


import android.bluetooth.BluetoothDevice
import android.content.Context

import com.ntnuerc.android.halloweencamera.bluetooth.request.*


class BluetoothConnectionService(val context: Context ) {
    var eventListener : IBluetoothEventListener = EmptyBluetoothEventListener()
    val enableRequest = EnableRequest(context, eventListener)
    val pairRequest = PairRequest(context, eventListener)
    val audioConnectionRequest = AudioConnectionRequest(context, eventListener)
    var discoverRequest = DiscoverRequest(context, eventListener)
    var connectionRequest = ConnectionRequest(context, eventListener)

    fun setBluetoothEventListener(listener: IBluetoothEventListener) {
        eventListener = listener

        discoverRequest = DiscoverRequest( context, eventListener )
        connectionRequest = ConnectionRequest( context, eventListener )
    }

    fun enableBluetoothAdapter() {
        enableRequest.enableluetooth()
    }

    fun disableBluetoothAdapter() {
        enableRequest.disableBluetooth()
    }

    fun discoverDevices() {
        discoverRequest.discover()
    }

    fun pairDevice(device : BluetoothDevice) {
        pairRequest.pair(device)
    }

    fun connectDevice(device: BluetoothDevice) {
        connectionRequest.connect(device)
    }

    fun connectDevice(name: String) {
        for (dev in discoverRequest.discoveredDevices ) {
            if ( dev.name == name ) {
                connectDevice(dev)
                break
            }
        }
    }

    fun connectPairedDevice(name: String) {
        connectionRequest.connectPairedDevice( name )
    }

    fun stopConnectDevice() {
        connectionRequest.stopConnect()
    }

    fun connectAudioDevice(device : BluetoothDevice) {
        audioConnectionRequest.connect(device)
    }

    fun cleanUp() {
        enableRequest.cleanup()
        discoverRequest.cleanup()
        pairRequest.cleanup()
    }
}