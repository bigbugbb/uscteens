#include "DataSource.h"
#include <iostream>
#include <fstream>
using namespace std;

#include <cstdlib>


DataSource* DataSource::GetInstance()
{
	static DataSource sDataSrc;
	return &sDataSrc;
}

DataSource::DataSource()
{

}

DataSource::~DataSource()
{

}

int DataSource::Create()
{
	return 0;
}

int DataSource::Destroy()
{
	for (vector<ChunkData*>::iterator iter = m_vecChunk.begin();
					iter != m_vecChunk.end(); ++iter) {
		ChunkData* pData = *iter;
		if (pData) {
			delete pData;
		}
	}

	for (vector<ActivityData*>::iterator iter = m_vecActivity.begin();
				iter != m_vecActivity.end(); ++iter) {
		ActivityData* pData = *iter;
		if (pData) {
			delete pData;
		}
	}

	m_vecChunk.clear();
	m_vecActivity.clear();

	return 0;
}

vector<int>* DataSource::LoadActivityData(const char* pszFile)
{
	for (vector<ActivityData*>::iterator iter = m_vecActivity.begin();
			iter != m_vecActivity.end(); ++iter) {
		ActivityData* pData = *iter;
		// already loaded
		if (!strcmp(pData->strFile.c_str(), pszFile)) {
			return &pData->vecData;
		}
	}

	ActivityData* pData = new ActivityData(); // ignore the check here
	FILE* fp = fopen(pszFile, "r");
	if (fp == NULL) {
		return NULL;
	}
	float num;
	while (!feof(fp)) {
		fscanf(fp, "%d", &num);
		pData->vecData.push_back(num);
	}
	m_vecActivity.push_back(pData);

	return &pData->vecData;
}

int DataSource::UnloadActivityData(const char* pszFile)
{
	return 0;
}

vector<int>* DataSource::LoadChunkData(const char* pszFile)
{
//	for (vector<ActivityData*>::iterator iter = m_vecChunk.begin();
//			iter != m_vecChunk.end(); ++iter) {
//		ChunkData* pData = *iter;
//		// already loaded
//		if (!strcmp(pData->strFile.c_str(), pszFile)) {
//			return &pData->vecData;
//		}
//	}
//
//	ChunkData* pData = new ChunkData(); // ignore the check here
//	FILE* fp = fopen(pszFile, "r");
//	if (fp == NULL) {
//		return NULL;
//	}
//	int num;
//	while (!feof(fp)) {
//		fscanf(fp, "%f", &num);
//		pData->vecData.push_back(num);
//	}
//	m_vecChunk.push_back(pData);
//
//	return &pData->vecData;
	return NULL;
}

int DataSource::UnloadChunkData(const char* pszFile)
{
	return 0;
}
