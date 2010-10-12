#include <stdlib.h>
#include <stdio.h>

#include <string.h>
#include <ctype.h>

#include "jni.h"
#include "modplug/modplug.h"

#define MOD_TYPE_NONE		0x00
#define MOD_TYPE_MOD		0x01
#define MOD_TYPE_S3M		0x02
#define MOD_TYPE_XM			0x04
#define MOD_TYPE_MED		0x08
#define MOD_TYPE_MTM		0x10
#define MOD_TYPE_IT			0x20
#define MOD_TYPE_669		0x40
#define MOD_TYPE_ULT		0x80
#define MOD_TYPE_STM		0x100

struct ModInfo {
	ModPlugFile *mod;
	const char *modType;
	char author[128];
	char mod_name[128];
	int mod_length;
};


JNIEXPORT jlong JNICALL Java_net_intensicode_droid_audio_ModplugModuleEngine_load
(JNIEnv *env, jobject obj, jbyteArray bArray, jint size, jint sample_rate, jboolean loop)
{
	jbyte *ptr = env->GetByteArrayElements(bArray, NULL);

	ModPlug_Settings settings;
	ModPlug_GetSettings(&settings);
	settings.mChannels = 2;
	settings.mFrequency = sample_rate;
	settings.mBits = 16;
	settings.mLoopCount = loop ? -1 : 0;

	ModPlug_SetSettings(&settings);

	ModPlugFile *mod = ModPlug_Load(ptr, size);
	ModInfo *info = NULL;

	if(mod)
	{
		info = new ModInfo();
		info->mod = mod;
		strcpy(info->mod_name, ModPlug_GetName(mod));
		info->mod_length = ModPlug_GetLength(mod);
		info->modType = "ModPlug";
		*info->author = 0;

		int t = ModPlug_GetModuleType(mod);
		switch(t) {
		case MOD_TYPE_MOD:
			info->modType = "MOD";
			break;
		case MOD_TYPE_S3M:
			info->modType = "S3M";
			break;
		case MOD_TYPE_XM:
			info->modType = "XM";
			break;
		case MOD_TYPE_IT:
			info->modType = "IT";
			break;
		case MOD_TYPE_STM:
			info->modType = "STM";
			break;
		}


		settings.mResamplingMode = MODPLUG_RESAMPLE_LINEAR;
		settings.mFlags = MODPLUG_ENABLE_OVERSAMPLING;

		if(t == 1) settings.mResamplingMode = MODPLUG_RESAMPLE_NEAREST;

		ModPlug_SetSettings(&settings);

	}

	env->ReleaseByteArrayElements(bArray, ptr, 0);
	return (long)info;
}

JNIEXPORT void JNICALL Java_net_intensicode_droid_audio_ModplugModuleEngine_setLoop
(JNIEnv * env, jobject obj, jlong song, jboolean loop)
{
	ModInfo *info = (ModInfo*)song;
	if(info->mod) ModPlug_SetLoop(info->mod, loop);
}

JNIEXPORT void JNICALL Java_net_intensicode_droid_audio_ModplugModuleEngine_unload
(JNIEnv *env, jobject obj, jlong song)
{
	ModInfo *info = (ModInfo*)song;
	if(info->mod) ModPlug_Unload(info->mod);
	delete info;
	info = NULL;
}


JNIEXPORT jint JNICALL Java_net_intensicode_droid_audio_ModplugModuleEngine_getSoundData
(JNIEnv *env, jobject obj, jlong song, jbyteArray bArray, int size)
{
	ModInfo *info = (ModInfo*)song;
	jbyte *ptr = (jbyte*)env->GetByteArrayElements(bArray, NULL);
	int rc = ModPlug_Read(info->mod, (void*)ptr, size);
	env->ReleaseByteArrayElements(bArray, ptr, 0);
	return rc;
}

JNIEXPORT void JNICALL Java_net_intensicode_droid_audio_ModplugModuleEngine_restart
(JNIEnv *env, jobject obj, jlong song)
{
	ModInfo *info = (ModInfo*)song;
	if(info->mod) ModPlug_Seek(info->mod, 0);
}
