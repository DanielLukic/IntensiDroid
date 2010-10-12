#ifndef MODPLUG_H
#define MODPLUG_H

#ifdef __cplusplus
extern "C" {
#endif

#include <jni.h>

JNIEXPORT jlong JNICALL Java_net_intensicode_droid_audio_ModplugModuleEngine_load
  ( JNIEnv * env, jobject thiz, jbyteArray aModuleData, jint aModuleDataSizeInBytes, jint aSampleRate, jboolean aLoopingFlag );

JNIEXPORT void JNICALL Java_net_intensicode_droid_audio_ModplugModuleEngine_setLoop
  ( JNIEnv * env, jobject thiz, jlong aModuleData, jboolean aLoopingFlag );

JNIEXPORT void JNICALL Java_net_intensicode_droid_audio_ModplugModuleEngine_unload
  (JNIEnv *, jobject, jlong);

JNIEXPORT jint JNICALL Java_net_intensicode_droid_audio_ModplugModuleEngine_getSoundData
  (JNIEnv *, jobject, jlong, jbyteArray, jint);

JNIEXPORT void JNICALL Java_net_intensicode_droid_audio_ModplugModuleEngine_restart
  (JNIEnv *, jobject, jlong);

#ifdef __cplusplus
}
#endif

#endif
