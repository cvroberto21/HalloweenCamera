package com.ntnuerc.android.halloweencamera

import android.app.Activity
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.TextView
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import kotlinx.android.synthetic.main.fragment_camera2_video.*

class VideoClientRunnerThread( val logView: TextView ) {

    companion object {
        private const val TAG = "VideoClientRunner"
        // Defines several constants used when transmitting messages between the
        // service and the UI.
        const val MESSAGE_READ: Int = 0
        const val MESSAGE_WRITE: Int = 1
        const val MESSAGE_TOAST: Int = 2
// ... (Add other message types here as needed.)
    }

    lateinit private var connectThread : ConnectedThread
    lateinit private var handler : Handler

    fun connect( socket : BluetoothSocket, handler : Handler ) {
        connectThread = ConnectedThread( context, socket, handler, logView )
        this.handler = handler
        connectThread.start()
    }

    fun disconnect( ) {
        connectThread.cancel()
    }

    fun write(bytes: ByteArray) {
        connectThread.write( bytes )
    }

    private inner class ConnectedThread( private val context: Context,
                                         private val mmSocket: BluetoothSocket,
                                         private val handler : Handler,
                                         private val logView : TextView) : Thread() {

        private val mmInStream: InputStream = mmSocket.inputStream
        private val mmOutStream: OutputStream = mmSocket.outputStream
        private val mmBuffer: ByteArray = ByteArray(1024) // mmBuffer store for the stream

        override fun run() {
            val act: Activity = context as Activity
            act.runOnUiThread(Runnable {
                logView.append( "Connected *** thread started\n" )
            })

            var numBytes: Int // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                // Read from the InputStream.
                numBytes = try {
                    mmInStream.read(mmBuffer)
                } catch (e: IOException) {
                    Log.d(TAG, "Input stream was disconnected", e)
                    break
                }

                // Send the obtained bytes to the UI activity.
//                val readMsg = handler.obtainMessage(
//                        MESSAGE_READ, numBytes, -1,
//                        mmBuffer)
//                readMsg.sendToTarget()
            }
        }

        // Call this from the main activity to send data to the remote device.
        fun write(bytes: ByteArray) {
            Log.d( TAG, "Write bleutooth data ")
            for (b in bytes ) {
                Log.i("myactivity", String.format("0x%20x", b))
            }

            var s = StringBuilder()
            s.append( "data:" )
            for (b in bytes ) {
                s.append( String.format(" 0x%20x", b) )
            }
            s.append("\n")
            val act: Activity = context as Activity
            act.runOnUiThread(Runnable {
                logView.append( "Connected thread write data\n" )
                logView.setText( s.toString() )
            })

            try {
                mmOutStream.write(bytes)
            } catch (e: IOException) {
                Log.e(TAG, "Error occurred when sending data", e)

                // Send a failure message back to the activity.
                val writeErrorMsg = handler.obtainMessage(MESSAGE_TOAST)
                val bundle = Bundle().apply {
                    putString("toast", "Couldn't send data to the other device")
                }
                writeErrorMsg.data = bundle
                handler.sendMessage(writeErrorMsg)
                return
            }

            // Share the sent message with the UI activity.
            val writtenMsg = handler.obtainMessage(
                    MESSAGE_WRITE, -1, -1, mmBuffer)
            writtenMsg.sendToTarget()
        }

        // Call this method from the main activity to shut down the connection.
        fun cancel() {
            try {
                mmSocket.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the connect socket", e)
            }
        }
    }
}