/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class cn_shorr_serialport_SerialPort */

#ifndef _Included_cn_shorr_serialport_SerialPort
#define _Included_cn_shorr_serialport_SerialPort
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     cn_shorr_serialport_SerialPort
 * Method:    open
 * Signature: (Ljava/lang/String;II)Ljava/io/FileDescriptor;
 */
JNIEXPORT jobject JNICALL Java_cn_shorr_serialport_SerialPort_open
  (JNIEnv *, jclass, jstring, jint, jint);

/*
 * Class:     cn_shorr_serialport_SerialPort
 * Method:    close
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_cn_shorr_serialport_SerialPort_close
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif