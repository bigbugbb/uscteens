#ifndef _DATA_SOURCE_H
#define _DATA_SOURCE_H

#include "BaseClasses.h"

#include <map>
using std::map;

#include <vector>
using std::vector;

#include <string>
using std::string;

struct AccelSensorData
{
	int nHour;
	int nMinute;
	int nSecond;
	int nMilliSecond;
	int nTimeInSec;
	int nIntAccelAverage;
	int nIntAccelSamples;
};

class DataSource : public CBaseObject
{
public:
	int  Create();
	int  Destroy();
	int  GetMaxActivityValue(const char* pszFile);
	vector<AccelSensorData>* LoadActivityData(const char* pszFile);
	int  UnloadActivityData(const char* pszFile);
	vector<int>* LoadChunkData(const char* pszFile);
	int  UnloadChunkData(const char* pszFile);

	static DataSource* GetInstance();

protected:
	DataSource();
	virtual ~DataSource();

	int	m_nMaxAccelAverage;

	struct ChunkData {
		string		strFile;
		vector<int> vecData;
	};

	vector<ChunkData*>    m_vecChunk;
	vector<AccelSensorData> m_vecASD;
};

#endif
