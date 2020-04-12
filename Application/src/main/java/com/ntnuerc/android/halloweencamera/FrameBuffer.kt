package com.ntnuerc.android.halloweencamera

import java.nio.ByteBuffer

class FrameBuffer( val width: Int, val height: Int, val type : Type ) {
    enum class Type {
        ARGB8888
    }

    private val TAG = "JBFB"

    @kotlin.ExperimentalUnsignedTypes
    //val buffer: UByteArray
    val buff: ByteBuffer
    val buffer: ByteArray
    val bytesPerLine: Int
    val bytesPerPixel : Int

    init {
        bytesPerLine = width * 4
        bytesPerPixel = 4

        buff = ByteBuffer.allocateDirect( width * height * bytesPerPixel )
        buffer = buff.array()
    }

    fun coordToOffset( x: Int, y: Int ): Int {
        return y * bytesPerLine + x * bytesPerPixel
    }

    fun getPixel( x : Int, y : Int ) : Int {
        val off = coordToOffset( x, y )
        val a : Int = buffer[off + 3 ].toInt() and 0xff
        val r : Int = buffer[off + 2 ].toInt() and 0xff
        val g : Int = buffer[off + 1 ].toInt() and 0xff
        val b : Int = buffer[off + 0 ].toInt() and 0xff

        return (a shl 24) or (r shl 16) or (g shl 8) or ( b shl 0 )
    }

    fun setPixel( x: Int, y: Int, value: Int ) {
        val a : Byte = ( ( value ushr 24 ) and 0xff ).toByte()
        val r : Byte = ( ( value ushr 16 ) and 0xff ).toByte()
        val g : Byte = ( ( value ushr 8 ) and 0xff ).toByte()
        val b : Byte = ( ( value ushr 0 ) and 0xff ).toByte()

        val off = coordToOffset( x, y )
        buffer[off + 0 ] = a
        buffer[off + 1 ] = r
        buffer[off + 2 ] = g
        buffer[off + 3 ] = b
    }
}