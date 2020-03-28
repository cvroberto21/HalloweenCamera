package com.ntnuerc.android.halloweencamera

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.Handler
import android.util.Log
import android.widget.TextView
import java.io.IOException
import java.util.*

class VideoClientConnectThread(private val context: Context, private val device: BluetoothDevice, private val handler: Handler, private val logView: TextView ) {
    private val TAG = "JBVidCon"
    //private val SPP_UUID = "00001101-0000-1000-8000-00805f9b34fb"
    private val VIDEO_SERVER_UUID = "00001101-0000-1000-8000-00805f9b34ff"

    val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var connectThread : ConnectThread? = null

    init {
        connectThread = ConnectThread(context, device, handler, logView )
        connectThread?.start()
    }

    public var videoClientRunnerThread : VideoClientRunnerThread? = null

    fun getRunner() : VideoClientRunnerThread? {
        return videoClientRunnerThread
    }

    fun cancel() {
        connectThread?.cancel()
    }
    private inner class ConnectThread( private val context: Context, private val device: BluetoothDevice, private val handler: Handler, private val logView: TextView) : Thread() {

        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord( UUID.fromString(VIDEO_SERVER_UUID) )
        }

        public override fun run() {
            Log.d(TAG, "Connection thread started")

            val act: Activity = context as Activity
            act.runOnUiThread(Runnable {
                logView.append( "Connection thread started\n" )
            })

            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter?.cancelDiscovery()

            mmSocket?.let { socket ->
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.

                var success = false

                try {
                    socket.connect()
                    success = true
                } catch ( e : IOException ) {
                    Log.e(TAG, "Connection failed")
                }

                Log.d(TAG, "Connection success $success")

                val act: Activity = context as Activity
                act.runOnUiThread(Runnable {
                    logView.append( "Connection success $success\n" )
                })

                if ( success ) {
                    videoClientRunnerThread = VideoClientRunnerThread( context, logView )

                    // The connection attempt succeeded. Perform work associated with
                    // the connection in a separate thread.
                    videoClientRunnerThread?.connect(socket, handler)
                }
            }
        }

        // Closes the client socket and causes the thread to finish.
        fun cancel() {
            videoClientRunnerThread?.disconnect()
            videoClientRunnerThread = null
            try {
                mmSocket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the client socket", e)
            }
        }
    }
}