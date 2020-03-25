package com.ntnuerc.android.halloweencamera

import java.nio.ByteBuffer


class JNILink {
    private val TAG = "JNILink"

    init {
        System.loadLibrary( "jbImageProcessing" )
    }

    external fun jbProcessImage(srcWidth: Int, srcHeight: Int, srcBufY: ByteBuffer, srcBufU: ByteBuffer, srcBufV: ByteBuffer ): String
}