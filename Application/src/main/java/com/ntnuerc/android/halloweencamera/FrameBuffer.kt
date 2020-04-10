package com.ntnuerc.android.halloweencamera

class FrameBuffer( val width: Int, val height: Int ) {
    private val buffer: IntArray

    init {
        buffer = IntArray( width * height )
    }

    fun at( x : Int, y : Int ) : Int {
        return buffer[y*width + x ]
    }

    fun getPixel( x: Int, y: Int, value: Int ) {
        buffer[y * width + x ] = value
    }
}