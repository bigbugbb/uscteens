/*
 * DataSourceJNI.cpp
 *
 *  Created on: Jan 26, 2013
 *      Author: bigbug
 */

#include <jni.h>
#include <assert.h>
#include "DataSource.h"

//#define DEBUG

#ifndef NULL
#define NULL	0
#endif

#define LOG_TAG "data_src"

#ifdef DEBUG
	#include <android/log.h>
	#define D(x...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, x)
#else
	#define D(...)  do {} while (0)
#endif

const char* CLASS_NAME = "edu/neu/android/mhealth/uscteensver1/data/DataSource";

static JavaVM *gJavaVM;
static DataSource* gDataSrc = DataSource::GetInstance();

JNIEnv* JNI_GetEnv()
{
    JNIEnv* env;
//    int status = gJavaVM->GetEnv((void **) &env, JNI_VERSION_1_4);
//    if(status < 0) {
	int status = gJavaVM->AttachCurrentThread(&env, NULL);
//		if(status < 0) {
//			return NULL;
//		}
//    }
    return env;
}

jint Create(JNIEnv * env, jobject thiz)
{
	jint nResult = gDataSrc->Create();

	return nResult;
}

jint Destroy(JNIEnv * env, jobject thiz)
{
	jint nResult = gDataSrc->Destroy();

	return nResult;
}

jintArray LoadActivityData(JNIEnv* env, jobject thiz, jstring path)
{
	jboolean bCopy;
//	JNIEnv* env = JNI_GetEnv();
	const char* pszPath = env->GetStringUTFChars(path, &bCopy);
	vector<jint>* pVecData = gDataSrc->LoadActivityData(pszPath);
	env->ReleaseStringUTFChars(path, pszPath);

	jsize size = pVecData->size();
	jintArray result = env->NewIntArray(size);
	if (result == NULL) {
		return NULL; /* out of memory error thrown */
	}
	// move from the temp structure to the java structure
	env->SetIntArrayRegion(result, 0, size, static_cast<jint*>(&(*pVecData)[0]));

	return result;
}

jint UnloadActivityData(JNIEnv * env, jobject thiz, jstring path)
{
	jboolean bCopy;
//	JNIEnv* env = JNI_GetEnv();
	const char* pszPath = env->GetStringUTFChars(path, &bCopy);
	jint result = gDataSrc->UnloadActivityData(pszPath);
	env->ReleaseStringUTFChars(path, pszPath);
	return result;
}

jintArray LoadChunkData(JNIEnv* env, jobject thiz, jstring path)
{
	jboolean bCopy;
//	JNIEnv* env = JNI_GetEnv();
	const char* pszPath = env->GetStringUTFChars(path, &bCopy);
	vector<int>* pVecData = gDataSrc->LoadChunkData(pszPath);
	env->ReleaseStringUTFChars(path, pszPath);

	jsize size = pVecData->size();
	jintArray result = env->NewIntArray(size);
	if (result == NULL) {
		return NULL; /* out of memory error thrown */
	}
	// move from the temp structure to the java structure
	env->SetIntArrayRegion(result, 0, size, static_cast<jint*>(&(*pVecData)[0]));

	return result;
}

jint UnloadChunkData(JNIEnv * env, jobject thiz, jstring path)
{
	jboolean bCopy;
	const char* pszPath = env->GetStringUTFChars(path, &bCopy);
	jint result = gDataSrc->UnloadChunkData(pszPath);
	env->ReleaseStringUTFChars(path, pszPath);
	return result;
}

static JNINativeMethod methods[] = {
	{"create", "()I", (void*)Create },
	{"destroy", "()I", (void*)Destroy },
	{"loadActivityData", "(Ljava/lang/String;)[I", (void*)LoadActivityData },
	{"unloadActivityData", "(Ljava/lang/String;)I", (void*)UnloadActivityData },
//	{"loadChunkData", "(Ljava/lang/String;)[I", (void*)LoadChunkData },
//	{"unloadChunkData", "(Ljava/lang/String;)I", (void*)UnloadChunkData },
};

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved)
{
	JNIEnv *env;

	gJavaVM = vm;
	if (vm->GetEnv((void **)&env, JNI_VERSION_1_4) != JNI_OK) {
		return JNI_ERR;
	}
	jclass clazz = env->FindClass(CLASS_NAME);
	if (clazz == NULL) {
		D("Can't find the class");
	    return JNI_ERR;
	}
	D("OnLoad ok");
	env->RegisterNatives(clazz, methods, sizeof(methods) / sizeof(JNINativeMethod));

	return JNI_VERSION_1_4;
}
