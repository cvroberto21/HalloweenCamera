package com.ntnuerc.android.halloweencamera

class FrameBuffer( val width: Int, val height: Int, val type : Type ) {
    enum class Type {
        ARGB8888
    }

    private val TAG = "JBFB"

    val buffer: ByteArray
    val bytesPerLine: Int
    val bytesPerPixel : Int

    init {
        bytesPerLine = width * 4
        bytesPerPixel = 4

        buffer = ByteArray( width * height * bytesPerPixel  )
    }

    fun getPixel( x : Int, y : Int ) : Byte {
        return buffer[ y * bytesPerLine + x * bytesPerPixel ]
    }

    fun setPixel( x: Int, y: Int, value: Byte ) {
        buffer[ y * bytesPerLine + x * bytesPerPixel ] = value
    }
}