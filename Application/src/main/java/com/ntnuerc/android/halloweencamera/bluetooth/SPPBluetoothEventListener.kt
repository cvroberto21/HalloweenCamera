package com.ntnuerc.android.halloweencamera.bluetooth

import android.util.Log


class SPPBluetoothEventListener( private val server: BluetoothConnectionService ) : IBluetoothEventListener {
    private val TAG = "SPP"
    private val peerName = "JB_Canary"

    override fun onDisconnecting() {

    }

    override fun onDisconnected() {

    }

    override fun onConnected(isSuccess: Boolean) {
        Log.d(TAG, "Connection established " + isSuccess )
    }

    override fun onPairing() {

    }

    override fun onConnecting() {
        Log.d(TAG, "Bluetooth connecting ... " )
    }

    override fun onDiscovering() {
        Log.d(TAG, "Bluetooth discovering ... " )
    }

    override fun onDiscovered() {
        Log.d(TAG, "Bluetooth discovery finished" )
        Log.d(TAG, "Found devices " + server.discoverRequest.discoveredDevices.size + " devices" )

        server.connectPairedDevice( peerName )
    }

    override fun onPaired() {

    }

    override fun onEnable() {

    }
}