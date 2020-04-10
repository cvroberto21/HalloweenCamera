//
// Created by cvrob on 10/24/2019.
//

#include <jni.h>
#include <android/log.h>
#include <android/bitmap.h>

#include "jbImageProcessing.h"
#include <string>
#include <cinttypes>
#include <memory>

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, "JBImageProcessing", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "JBImageProcessing", __VA_ARGS__)

extern "C" {
    JNIEXPORT jstring
    JNICALL Java_com_ntnuerc_android_halloweencamera_JNILink_jbProcessImage(
            JNIEnv *pEnv, jobject pThis, jint srcWidth, jint srcHeight,
            jobject srcPlaneY, jobject srcPlaneU, jobject srcPlaneV,
            jobject outBuffer ) {

        uint8_t * buf = reinterpret_cast<uint8_t *>( pEnv->GetDirectBufferAddress( srcPlaneY ) );
        LOGD("Running cpp code %dx%d", srcWidth, srcHeight );

        for (int y = 0; y < srcHeight; y++ ) {
            for ( int x = 0; x < srcWidth; x++ ) {
                if ( buf[y * srcWidth + x] < 0 ) {
                    buf[y * srcWidth + x] = 0;
                }
            }
        }

        return pEnv->NewStringUTF("Ok RGB scrambled");
    }
}