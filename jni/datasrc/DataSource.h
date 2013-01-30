#ifndef _DATA_SOURCE_H
#define _DATA_SOURCE_H

#include <map>
using std::map;

#include <vector>
using std::vector;

#include <string>
using std::string;

class DataSource
{
public:
	int  Create();
	int  Destroy();
	vector<int>* LoadActivityData(const char* pszFile);
	int  UnloadActivityData(const char* pszFile);
	vector<int>* LoadChunkData(const char* pszFile);
	int  UnloadChunkData(const char* pszFile);

	static DataSource* GetInstance();

protected:
	DataSource();
	virtual ~DataSource();

	struct ActivityData {
		string 		strFile;
		vector<int> vecData;
	};

	struct ChunkData {
		string		strFile;
		vector<int> vecData;
	};

	vector<ChunkData*>    m_vecChunk;
	vector<ActivityData*> m_vecActivity;
};

#endif
