#ifndef _BASECLASSES_H
#define _BASECLASSES_H

#include "Config.h"

#ifdef _DEBUG_MODE
    #include <cassert>
    #ifdef _ANDROID
        #include <android/log.h>
    #endif
#endif

class CBaseObject
{
#ifdef _DEBUG_MODE    
    #ifdef _ANDROID
		#define LOG_LABEL "datasrc"
        #define Log(format, args) \
        { \
            __android_log_print(ANDROID_LOG_INFO, LOG_LABEL, format, ##args);\
        }
    #else
        #ifdef _LOG_TO_FILE
            #define Log(format, args...) \
            { \
                extern std::string strPathLog; \
                FILE* fp = fopen(strPathLog.c_str(), "a+"); \
                fprintf(fp, format, ##args); \
                fclose(fp); \
            }
        #else
            #define Log(format, args) \
            { \
                printf(format, ##args); \
            }
        #endif
    #endif \

    #define AssertValid(condition) \
    { \
        assert(condition); \
    }    
#else
    #define Log(format, ...)
    #define AssertValid(bCondition)
#endif
};


#endif
