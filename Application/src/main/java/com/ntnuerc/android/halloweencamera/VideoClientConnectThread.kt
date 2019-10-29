package com.ntnuerc.android.halloweencamera

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.util.Log
import java.io.IOException
import java.util.*

class VideoClientConnectThread(private val device: BluetoothDevice, private val handler: Handler ) {
    private val TAG = "VideoConnectThread"
    private val SPP_UUID = "00001101-0000-1000-8000-00805f9b34fb"

    val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    init {
        val connectThread = ConnectThread( device, handler )
        connectThread.start()
    }

    public var videoClientRunnerThread : VideoClientRunnerThread? = null

    fun getRunner() : VideoClientRunnerThread? {
        return videoClientRunnerThread
    }

    private inner class ConnectThread(device: BluetoothDevice, var handler : Handler) : Thread() {

        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord( UUID.fromString(SPP_UUID) )
        }


        public override fun run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter?.cancelDiscovery()

            mmSocket?.let { socket ->
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                socket.connect()
                videoClientRunnerThread = VideoClientRunnerThread( )

                // The connection attempt succeeded. Perform work associated with
                // the connection in a separate thread.
                videoClientRunnerThread?.connect( socket, handler )
            }
        }

        // Closes the client socket and causes the thread to finish.
        fun cancel() {
            try {
                videoClientRunnerThread = null
                mmSocket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the client socket", e)
            }
        }
    }
}