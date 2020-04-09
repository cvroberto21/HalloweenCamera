package com.ntnuerc.android.halloweencamera

import android.graphics.ImageFormat
import android.media.Image
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface

class JPEGImageReader(width: Int, height: Int, format: Int = ImageFormat.JPEG, maxImages: Int = 5) {

    private val imageReader: ImageReader
    private val TAG = "JPEGImageReader"
    private val listener = ImageReader.OnImageAvailableListener {
        val image = it.acquireLatestImage()
        if (image != null ) {
            val fmt = it.imageFormat
            Log.d(TAG, "Image format" + fmt)
            val planes = image.planes

            Log.d(TAG, "Acquired image height" + image.height + ", width" + image.width + " planes")
            Log.d(TAG, "Acquired image " + planes[0].pixelStride + "," + planes[0].rowStride + " planes")
//            Log.d(TAG, "Acquired image " + planes[1].pixelStride + "," + planes[1].rowStride + " planes")
//            Log.d(TAG, "Acquired image " + planes[2].pixelStride + "," + planes[2].rowStride + " planes")

            processImage( image )
            Log.d(TAG, "Processed image " )

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
    init {
        imageReader = ImageReader.newInstance(width, height, format, maxImages)

        val thread = HandlerThread("RGBImageReader")
        thread.start()
        val handler = Handler(thread.looper)

        backgroundHandler = handler
        backgroundThread = thread
        imageReader.setOnImageAvailableListener( listener, backgroundHandler )
    }

    fun getSurface(): Surface {
        return imageReader.surface
    }

    lateinit private var videoService : VideoClientRunnerThread

    fun setVideoService( vt : VideoClientRunnerThread ) {
        videoService = vt
    }

    fun processImage(src: Image) {
        require( src.getFormat() == ImageFormat.JPEG) {
            "src must have format JPEG."
        }

        val jpegData = imageToByteArray( src )
        Log.d(TAG, "jpeg image size " + jpegData?.size )

        if ( jpegData != null ) {
            videoService.write( jpegData )
        }
    }

    fun imageToByteArray(image: Image): ByteArray? {
        var data: ByteArray? = null
        if (image.format == ImageFormat.JPEG) {
            val planes = image.planes
            val buffer = planes[0].buffer
            data = ByteArray(buffer.capacity())
            buffer.get(data)
        }
        return data
    }

}