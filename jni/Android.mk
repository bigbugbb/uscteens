LOCAL_PATH := $(call my-dir)

LOCAL_CFLAGS += -O3

include $(CLEAR_VARS)
include $(call all-makefiles-under, $(LOCAL_PATH))