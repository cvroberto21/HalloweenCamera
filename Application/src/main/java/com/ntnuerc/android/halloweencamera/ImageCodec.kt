package com.ntnuerc.android.halloweencamera

import android.media.Image

abstract class ImageCodec {
    abstract fun encode( src: Image )

    abstract fun decode( src: Image )
}