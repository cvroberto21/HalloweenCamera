package com.ntnuerc.android.halloweencamera

import java.nio.ByteBuffer


class JNILink {
    private val TAG = "JNILink"

    init {
        System.loadLibrary( "jbImageProcessing" )
    }

    external fun jbTest(srcWidth: Int, srcHeight: Int, srcBufY: ByteBuffer, srcBufU: ByteBuffer, srcBufV: ByteBuffer ): String
}