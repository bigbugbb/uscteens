#include "DataSource.h"
#include <iostream>
#include <fstream>
using namespace std;

#include <cstdlib>

#define BUF_SIZE 256
#define DATA_COUNT_FOR_DAY_CROSSING	30 // 20 is enough, but for safety, set it 30

const static int NO_SENSOR_DATA = -1;

// threshold for chunking generation
const static int CHUNKING_MIN_MEAN_AVG         = 350;
const static int CHUNKING_MIN_MEAN_AVG_DIFF    = 350;
const static int CHUNKING_MAX_MEAN_AVG_DIFF    = 800;
const static int CHUNKING_MEAN_AVG_DISTANCE    = 60;
const static int CHUNKING_MIN_SENSITIVITY      = 400;
const static int CHUNKING_MAX_SENSITIVITY      = 999;
const static int CHUNKING_MIN_DISTANCE 		   = 120;

DataSource* DataSource::GetInstance()
{
	static DataSource sDataSrc;
	return &sDataSrc;
}

DataSource::DataSource()
{
	m_nMaxAccelAverage = 0;
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

vector<AccelSensorData>& DataSource::LoadActivityData(const char* pszFile)
{
	bool ParseLineForSensorData(char* pszLine, AccelSensorData& rASD, int nCount);

	AccelSensorData asd;
	char szLine[BUF_SIZE];
	ifstream fin(pszFile);

	m_vecASD.clear();
	m_nMaxAccelAverage = INT_MIN;

	int count = 0;
	fin.getline(szLine, BUF_SIZE); // skip the first line
	while (fin.getline(szLine, BUF_SIZE)) {
		if (ParseLineForSensorData(szLine, asd, count++)) {
			m_vecASD.push_back(asd);
		}

		if (asd.nIntAccelAverage > m_nMaxAccelAverage) {
			m_nMaxAccelAverage = asd.nIntAccelAverage;
		}
	}

	return m_vecASD;
}

vector<LabelData>& DataSource::LoadDailyLabelData(const char* pszFile)
{
	bool ParseLineForLabelData(char* pszLine, LabelData& data);

	LabelData data;
	char szLine[BUF_SIZE];
	ifstream fin(pszFile);

	m_vecLabel.clear();
	fin.getline(szLine, BUF_SIZE); // skip the first line
	while (fin.getline(szLine, BUF_SIZE)) {
		if (ParseLineForLabelData(szLine, data)) {
			m_vecLabel.push_back(data);
		}
	}

	return m_vecLabel;
}

vector<int>& DataSource::CreateDailyRawChunkData(int nStart, int nStop, int* pSensorData, int nSize)
{
	m_vecChunkPos.clear();
	if (nSize < CHUNKING_MIN_DISTANCE) {
		return m_vecChunkPos;
	}
	// convolution to the accelerometer data
	int* pConvolution = new int[nSize];
	memcpy(pConvolution, pSensorData, sizeof(int) * nSize);
	for (int i = 1; i < nSize - 1; ++i) { // [-1 0 1]
		pConvolution[i] = pSensorData[i + 1] - pSensorData[i - 1];
	}

	// calculate mean average of CHUNKING_MIN_DISTANCE points' of data to the left of current position
	int sum = 0;
	int* pMeanAverageL = new int[nSize];
	memset(pMeanAverageL, 0, sizeof(int) * CHUNKING_MEAN_AVG_DISTANCE);
	for (int i = 0; i < CHUNKING_MEAN_AVG_DISTANCE; ++i) {
		sum += pSensorData[i];
	}
	for (int i = CHUNKING_MEAN_AVG_DISTANCE; i < nSize; ++i) {
		pMeanAverageL[i] = sum / CHUNKING_MEAN_AVG_DISTANCE;
		sum += pSensorData[i];
		sum -= pSensorData[i - CHUNKING_MEAN_AVG_DISTANCE];
	}

	// calculate mean average of CHUNKING_MIN_DISTANCE points' of data to the right of current position
	sum = 0;
	int* pMeanAverageR = new int[nSize];
	memset(pMeanAverageR + nSize - CHUNKING_MEAN_AVG_DISTANCE, 0, sizeof(int) * CHUNKING_MEAN_AVG_DISTANCE);
	for (int i = nSize - 1; i >= nSize - CHUNKING_MEAN_AVG_DISTANCE; --i) {
		sum += pSensorData[i];
	}
	for (int i = nSize - CHUNKING_MEAN_AVG_DISTANCE - 1; i >= 0; --i) {
		pMeanAverageR[i] = sum / CHUNKING_MEAN_AVG_DISTANCE;
		sum += pSensorData[i];
		sum -= pSensorData[i + CHUNKING_MEAN_AVG_DISTANCE];
	}

	// figure out the possible chunking positions
	int nPrev = nStart;
	m_vecChunkPos.push_back(nStart);
	for (int i = nStart + 1; i < nSize - CHUNKING_MIN_DISTANCE; ++i) {
		if (i - nPrev < CHUNKING_MIN_DISTANCE) {
			continue;
		}
		// chunking for no data area
		if (pSensorData[i] == NO_SENSOR_DATA) {
			if (pSensorData[i - 1] != NO_SENSOR_DATA || pSensorData[i + 1] != NO_SENSOR_DATA) {
				m_vecChunkPos.push_back(i);
				nPrev = i;
				continue;
			}
		}
		// chunking for data change area
		if (pConvolution[i] > CHUNKING_MIN_SENSITIVITY &&
				pSensorData[i] > CHUNKING_MIN_SENSITIVITY &&
				pSensorData[i] < CHUNKING_MAX_SENSITIVITY) {
			if (abs(pMeanAverageL[i] - pMeanAverageR[i]) > CHUNKING_MIN_MEAN_AVG_DIFF) {
				m_vecChunkPos.push_back(i);
				nPrev = i;
				continue;
			}
		}
		// chunking for separated sensor data with huge value
		if (pSensorData[i] > CHUNKING_MAX_SENSITIVITY) {
			int nNext = i + CHUNKING_MIN_DISTANCE;
			if (pMeanAverageL[i] < CHUNKING_MIN_MEAN_AVG && pMeanAverageR[nNext] < CHUNKING_MIN_MEAN_AVG) {
				m_vecChunkPos.push_back(i);
			} else {
				continue;
			}
			if (nNext < nSize - CHUNKING_MIN_DISTANCE) {
				m_vecChunkPos.push_back(nNext);
				nPrev = nNext;
			} else {
				nPrev = i;
			}
		}
	}
	m_vecChunkPos.push_back(nStop);

	delete[] pConvolution;
	delete[] pMeanAverageL;
	delete[] pMeanAverageR;

	return m_vecChunkPos;
}

bool ParseLineForLabelData(char* pszLine, LabelData& data)
{
	char* pString = strtok(pszLine, " ,.:");

	int label = 0;
	while (pString) {
		switch (label++) {
		case 0: // date
			// skip
			break;
		case 1: // hour
			data.nHour = atoi(pString);
			break;
		case 2: // minute
			data.nMinute = atoi(pString);
			break;
		case 3: // second
			data.nSecond = atoi(pString);
			break;
		case 4:
			data.strText = pString;
			break;
		}
		pString = strtok (NULL, " ,.:");
	}
	data.strText[data.strText.size() - 1] = '\0';
	data.nTimeInSec = data.nHour * 3600 + data.nMinute * 60 + data.nSecond;

	return true;
}

bool ParseLineForSensorData(char* pszLine, AccelSensorData& rASD, int nCount)
{
	char* pString = strtok(pszLine, " ,.:");
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
	rASD.nTimeInSec = rASD.nHour * 3600 + rASD.nMinute * 60 + rASD.nSecond;
	if (nCount < DATA_COUNT_FOR_DAY_CROSSING) { ////////////////////////////////////////////////////
		if (rASD.nHour == 23 && rASD.nMinute == 59) { // the case for daily crossing
			return false;
		}
	}

	return true;
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
