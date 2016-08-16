LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_CPPFLAGS += -std=c++0x -frtti -fexceptions
LOCAL_MODULE := FakeSecurity
LOCAL_SRC_FILES := main.cc
LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog

include $(BUILD_SHARED_LIBRARY)
