/*
 * DataSourceJNI.cpp
 *
 *  Created on: Jan 26, 2013
 *      Author: bigbug
 */

#include <jni.h>
#include <assert.h>
#include "DataSource.h"

//#define _DEBUG_MODE

#ifndef NULL
#define NULL	0
#endif

#define LOG_TAG "data_src"

#ifdef _DEBUG_MODE
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

jint Create(JNIEnv * env, jclass clazz)
{
	jint nResult = gDataSrc->Create();

	return nResult;
}

jint Destroy(JNIEnv * env, jclass clazz)
{
	jint nResult = gDataSrc->Destroy();

	return nResult;
}

jint GetMaxActivityValue(JNIEnv* env, jclass clazz, jstring path)
{
	jboolean bCopy;
	const char* pszPath = env->GetStringUTFChars(path, &bCopy);
	jint nMaxValue = gDataSrc->GetMaxActivityValue(pszPath);
	env->ReleaseStringUTFChars(path, pszPath);

	return nMaxValue;
}

jint LoadHourlyAccelSensorData(JNIEnv* env, jclass clazz, jstring path)
{
	jboolean bCopy;
	const char* pszPath = env->GetStringUTFChars(path, &bCopy);
	vector<AccelSensorData>& vecData = gDataSrc->LoadActivityData(pszPath);
	env->ReleaseStringUTFChars(path, pszPath);

	// get DataSource class
	jclass dsClass = env->FindClass("edu/neu/android/mhealth/uscteensver1/data/DataSource");
	// get onGetAccelData method
	jmethodID mid = env->GetStaticMethodID(dsClass, "onAddAccelData", "(IIIIIII)V");
//	// get AccelData class
//	jclass adClass = env->FindClass("edu/neu/android/mhealth/uscteensver1/data/AccelData");
//	// get AccelData constructor
//	jmethodID constructor = env->GetMethodID(adClass, "<init>", "(IIIIIII)V");
	// fill each AccelData and send it back by onAddAccelData
	int size = vecData.size();
	for (int i = 0; i < size; ++i) {
		if (mid) {
			env->CallStaticVoidMethod(clazz, mid,
					vecData[i].nHour, vecData[i].nMinute, vecData[i].nSecond, vecData[i].nMilliSecond,
					vecData[i].nTimeInSec, vecData[i].nIntAccelAverage, vecData[i].nIntAccelSamples);
		} else {
			D("mid is null");
		}
	}

	return 0;
}

jintArray JNICALL CreateDailyRawChunkData(JNIEnv* env, jclass clazz, jint start, jint stop, jintArray sensorData)
{
	jboolean bCopy;
	jint* pSensorData = env->GetIntArrayElements(sensorData, &bCopy);
	jint size = env->GetArrayLength(sensorData);
	vector<int>& vecChunkPos = gDataSrc->CreateDailyRawChunkData(start, stop, pSensorData, size);
	env->ReleaseIntArrayElements(sensorData, pSensorData, 0);

	size = vecChunkPos.size();
	jintArray result = env->NewIntArray(size);
	// move from the temp structure to the java structure
	env->SetIntArrayRegion(result, 0, size, static_cast<jint*>(&vecChunkPos[0]));

	return result;
}

jint UnloadActivityData(JNIEnv * env, jclass clazz, jstring path)
{
	jboolean bCopy;
//	JNIEnv* env = JNI_GetEnv();
	const char* pszPath = env->GetStringUTFChars(path, &bCopy);
	jint result = gDataSrc->UnloadActivityData(pszPath);
	env->ReleaseStringUTFChars(path, pszPath);
	return result;
}

jintArray LoadChunkData(JNIEnv* env, jclass clazz, jstring path)
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

jint UnloadChunkData(JNIEnv * env, jclass clazz, jstring path)
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
	{"loadHourlyAccelSensorData", "(Ljava/lang/String;)I", (void*)LoadHourlyAccelSensorData },
	{"createDailyRawChunkData", "(II[I)[I", (void*)CreateDailyRawChunkData },
	{"unloadActivityData", "(Ljava/lang/String;)I", (void*)UnloadActivityData },
	{"getMaxActivityValue", "(Ljava/lang/String;)I", (void*)GetMaxActivityValue },
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
