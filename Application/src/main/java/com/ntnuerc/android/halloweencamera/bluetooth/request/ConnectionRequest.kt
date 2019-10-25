package com.ntnuerc.android.halloweencamera.bluetooth.request

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import com.ntnuerc.android.halloweencamera.bluetooth.IBluetoothEventListener
import java.io.IOException
import java.util.*

private const val ACP_BASE_UUID = "00000000-0000-1000-8000-00805F9B34FB"
private const val SPP_BASE_UUID = "00001101-0000-1000-8000-00805f9b34fb"

class ConnectionRequest(private val context : Context, private val eventListener: IBluetoothEventListener) : IBluetoothRequest {
    private var connectionThread : ConnectionThread? = null
    private val bluetoothAdapter : BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    fun connect(device: BluetoothDevice) {

        // bluetoothAdapter.cancelDiscovery() Donce in thread

        eventListener.onConnecting()
        connectionThread = ConnectionThread(device)
        { isSuccess -> eventListener.onConnected(isSuccess)}
        connectionThread?.start()
    }

    fun stopConnect() {
        if (connectionThread != null)
            connectionThread?.cancel()
    }

    override fun cleanup() {
        stopConnect()
    }

    fun connectPairedDevice( name: String ) {

        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
        pairedDevices?.forEach { device ->
            val deviceName = device.name
            // val deviceHardwareAddress = device.address // MAC address

            Log.d("SPP", "Paired device " + deviceName )
            if ( deviceName == name ) {
                connect( device )
            }
        }
    }

    inner class ConnectionThread(private val device : BluetoothDevice,
                                   private val onComplete: (isSuccess : Boolean) -> Unit) : Thread() {

        //private var bluetoothAdapter : BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        private var bluetoothSocket : BluetoothSocket? = createSocket()

        private fun createSocket() : BluetoothSocket? {
            var socket : BluetoothSocket? = null

            try {
//                val uuid = if (device.uuids.size > 0)
//                                device.uuids[0].uuid
//                            else
//                                UUID.fromString(ACP_BASE_UUID)
                val uuid = UUID.fromString(SPP_BASE_UUID)
                socket = device.createRfcommSocketToServiceRecord(uuid)
            }
            catch (e : IOException) {

            }

            return socket
        }

        override fun run() {
            super.run()

            bluetoothAdapter.cancelDiscovery()
            var isSuccess = false

            try {
                if (bluetoothSocket != null) {
                    bluetoothSocket?.connect()
                    isSuccess = true
                }

            }
            catch (e: Exception) {
                Log.i( "SPP", "Connection failed")
            }

            onComplete(isSuccess)
        }

        fun cancel() {
            if (bluetoothSocket != null)
                bluetoothSocket?.close()
        }
    }
}