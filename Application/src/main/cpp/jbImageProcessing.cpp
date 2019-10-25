//
// Created by cvrob on 10/24/2019.
//

#include <jni.h>
#include <android/log.h>
#include <android/bitmap.h>

#include "jbImageProcessing.h"
#include <string>

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, "JBImageProcessing", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "JBImageProcessing", __VA_ARGS__)

extern "C" {

    JNIEXPORT jstring
    JNICALL Java_com_ntnuerc_android_halloweencamera_JNILink_jbTest(
            JNIEnv *pEnv, jobject pThis, jint srcWidth, jint srcHeight,
            jobject srcPlaneY, jobject srcPlaneU, jobject srcPlaneV) {

        LOGD("Running cpp code");

        return pEnv->NewStringUTF("Ok");
    }
}