#include "DataSource.h"
#include <iostream>
#include <fstream>
using namespace std;

#include <cstdlib>

#define BUF_SIZE 256

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

//	for (vector<ActivityData*>::iterator iter = m_vecActivity.begin();
//				iter != m_vecActivity.end(); ++iter) {
//		ActivityData* pData = *iter;
//		if (pData) {
//			delete pData;
//		}
//	}

	m_vecChunk.clear();
//	m_vecActivity.clear();

	return 0;
}

int DataSource::GetMaxActivityValue(const char* pszFile)
{
//	ActivityData* pData = NULL;
//
//	for (vector<ActivityData*>::iterator iter = m_vecActivity.begin();
//			iter != m_vecActivity.end(); ++iter) {
//		pData = *iter;
//		// already loaded
//		if (!strcmp(pData->strFile.c_str(), pszFile)) {
//			break;
//		}
//	}
//	int nMaxValue = pData ? pData->nMaxValue : -1;
//
//	return nMaxValue;
	return 0;
}

vector<AccelSensorData>* DataSource::LoadActivityData(const char* pszFile)
{
	void ParseLine(char* line, AccelSensorData& rASD);

	AccelSensorData asd;
	char line[BUF_SIZE];
	ifstream fin(pszFile);

	m_nMaxAccelAverage = INT_MIN;

	fin.getline(line, BUF_SIZE); // skip the first line
	while (fin.getline(line, BUF_SIZE)) {
		ParseLine(line, asd);
		m_vecASD.push_back(asd);

		if (asd.nIntAccelAverage > m_nMaxAccelAverage) {
			m_nMaxAccelAverage = asd.nIntAccelAverage;
		}
	}

	int i = m_vecASD.size();

	return &m_vecASD;
}

void ParseLine(char* line, AccelSensorData& rASD)
{
	char* pString = strtok(line, " ,.:");
	int label = 0;
	while (pString) {
		switch (label++) {
		case 0: // date
			// skip
			break;
		case 1: // hour
			rASD.nHour = atoi(pString);
			break;
		case 2: // minute
			rASD.nMinute = atoi(pString);
			break;
		case 3: // second
			rASD.nSecond = atoi(pString);
			break;
		case 4:
			rASD.nMilliSecond = atoi(pString);
			break;
		case 5:
			rASD.nIntAccelAverage = atoi(pString);
			break;
		case 6:
			rASD.nIntAccelSamples = atoi(pString);
			break;
		}
		pString = strtok (NULL, " ,.:");
	}
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
