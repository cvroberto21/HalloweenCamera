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

    static inline int clamp( int min, int val, int max );

    JNIEXPORT jstring
    JNICALL Java_com_ntnuerc_android_halloweencamera_JNILink_jbProcessImage(
            JNIEnv *pEnv, jobject pThis, jint srcWidth, jint srcHeight,
            jobject srcPlaneY, jobject srcPlaneU, jobject srcPlaneV,
            jobject outBuffer) {

        uint8_t * yP = reinterpret_cast<uint8_t *>( pEnv->GetDirectBufferAddress( srcPlaneY ) );
        uint8_t * uP = reinterpret_cast<uint8_t *>( pEnv->GetDirectBufferAddress( srcPlaneU ) );
        uint8_t * vP = reinterpret_cast<uint8_t *>( pEnv->GetDirectBufferAddress( srcPlaneV ) );

        LOGD("Buffers Y:%p U:%p V:%p FB:%p", yP, uP, vP, outBuffer );
        uint8_t * oBuff = reinterpret_cast<uint8_t *>( pEnv->GetDirectBufferAddress( outBuffer ) );

        LOGD("Running cpp code %dx%d", srcWidth, srcHeight );
        LOGD("Buffers Y:%p U:%p V:%p FB:%p", yP, uP, vP, oBuff );

        int32_t * outP = reinterpret_cast<int32_t *>( oBuff );
        for (int row = 0; row < srcHeight; row++ ) {
            for ( int col = 0; col < srcWidth; col++ ) {
                int y = *yP++;
                int u = *uP; uP += 2;
                int v = *vP; vP += 2;

                y = clamp(16, y, 255);

                int a = 0x00;
                int r = static_cast<int>(1.164f * (y - 16) + 1.596f * (v - 128));
                int g = static_cast<int>(1.164f * (y - 16) - 0.813f * (v - 128) - 0.391f * (u - 128));
                int b = static_cast<int>(1.164f * (y - 16) + 2.018f * (u - 128));

                r = clamp(0, r, 255 );
                g = clamp(0, g, 255 );
                b = clamp(0, b, 255 );

                int32_t pixel = (a << 24) | (r << 16) | (g << 8) | (b << 0);

                *outP++ = pixel;
            }
        }
        return pEnv->NewStringUTF("Ok RGB scrambled");
    }

    static inline int clamp( int min, int val, int max )  {
        if ( val < min ) {
            val = min;
        } else if ( max < val ) {
            val = max;
        }
        return val;
    }
}