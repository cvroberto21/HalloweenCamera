package com.ntnuerc.android.halloweencamera

import android.graphics.ImageFormat
import android.media.Image
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface

class RGBImageReader {
    val jni  = JNILink()

    private val imageReader: ImageReader
    private val TAG = "RGBImageReader"
    private val listener = ImageReader.OnImageAvailableListener {
        val image = it.acquireLatestImage()
        if (image != null ) {
            val format = it.imageFormat
            Log.d(TAG, "Image format" + format)
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

//    private fun makeHandler( looper: Looper): Handler {
//        return object:Handler( looper ) {
//
//        }
//    }


    @JvmOverloads
    constructor(width: Int, height: Int, format: Int = ImageFormat.YUV_420_888, maxImages: Int = 5) {
        imageReader = ImageReader.newInstance(width, height, format, maxImages)

        val thread = HandlerThread("RGBImageReader")
        thread.start()
        val handler = Handler(thread.looper)


        backgroundHandler = handler
        backgroundThread = thread

        imageReader.setOnImageAvailableListener(listener, backgroundHandler)
    }

    fun getSurface(): Surface {
        return imageReader.surface
    }

    fun processImage(src: Image): String {
        require( src.getFormat() == ImageFormat.YUV_420_888) {
            "src must have format YUV_420_888."
        }

        val planes = src.planes
        // Spec guarantees that planes[0] is luma and has pixel stride of 1.
        // It also guarantees that planes[1] and planes[2] have the same row and
        // pixel stride.
        require(!(planes[1].pixelStride != 1 && planes[1].pixelStride != 2)) {
            "src chroma plane must have a pixel stride of 1 or 2: got " + planes[1].pixelStride
        }


        return jni.jbTest( src.width, src.height, planes[0].buffer, planes[1].buffer, planes[2].buffer )
    }

}