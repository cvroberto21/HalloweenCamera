package com.ntnuerc.android.halloweencamera

import android.graphics.ImageFormat
import android.media.Image
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface
import java.nio.ByteBuffer
import java.nio.IntBuffer

//class RGBImageReader(width: Int, height: Int, format: Int = ImageFormat.YUV_420_888, maxImages: Int = 5) {
class RGBImageReader(width: Int, height: Int, format: Int = ImageFormat.YUV_420_888, maxImages: Int = 5) {
    private val TAG = "RGBImageReader"
    val jni  = JNILink()

    private val imageReader: ImageReader
    private val listener = ImageReader.OnImageAvailableListener {
        val image = it.acquireLatestImage()
        if (image != null ) {
            val fmt = it.imageFormat
            Log.d(TAG, "Image format" + fmt)
            val planes = image.planes

            Log.d(TAG, "Acquired image " + planes.size )
            val result = processImage( image )
            Log.d(TAG, "Processed image " + result )

            image.close()
        }
    }

    private val backgroundThread: Thread
    private val backgroundHandler : Handler

    private val lock : Any = Any()

    private val framebuffer: FrameBuffer

    init {
        Log.d(TAG, "Creating image reader of format $format")
        imageReader = ImageReader.newInstance(width, height, format, maxImages)

        val thread = HandlerThread("RGBImageReader")
        thread.start()
        val handler = Handler(thread.looper)

        framebuffer = FrameBuffer( width, height, FrameBuffer.Type.ARGB8888 )
        backgroundHandler = handler
        backgroundThread = thread

        imageReader.setOnImageAvailableListener( listener, backgroundHandler )
    }

    fun getSurface(): Surface {
        return imageReader.surface
    }

    fun processImage(src: Image): String {
        require( src.getFormat() == ImageFormat.YUV_420_888 ) {
            "src must have format YUV_420_888."
        }

        val planes = src.planes
        // Spec guarantees that planes[0] is luma and has pixel stride of 1.
        // It also guarantees that planes[1] and planes[2] have the same row and
        // pixel stride.
        require(!(planes[1].pixelStride != 1 && planes[1].pixelStride != 2)) {
            "src chroma plane must have a pixel stride of 1 or 2: got " + planes[1].pixelStride
        }

        Log.d( TAG, "out ${framebuffer.buff} isDirect: ${framebuffer.buffer} ")
        Log.d( TAG, "input planes ${planes[0].pixelStride} ${planes[1].pixelStride}  ${planes[2].pixelStride} ${framebuffer.buff}  ")
        @kotlin.ExperimentalUnsignedTypes
        val ret = jni.jbProcessImage( src.width, src.height, planes[0].buffer, planes[1].buffer, planes[2].buffer, framebuffer.buff )

        for( yi: Int in framebuffer.height / 2 - 3 .. framebuffer.height / 2 + 3  ) {
            var s = StringBuilder()
            s.append( "pixels[ ${framebuffer.width/2 - 4 }, $yi]:" )
            for( xi: Int in framebuffer.width / 2 - 3 .. framebuffer.width / 2 + 3 ) {
                s.append( String.format(" 0x%08x", framebuffer.getPixel(xi, yi)) )
            }
            Log.d( TAG, s.toString() )
        }

        videoService?.write( framebuffer.buffer )

        return ret
    }

    private var videoService : VideoClientRunnerThread? = null

    fun setVideoService( vt : VideoClientRunnerThread ) {
        videoService = vt
    }
}