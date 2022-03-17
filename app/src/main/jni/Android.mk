   LOCAL_PATH := $(call my-dir)


   include $(CLEAR_VARS)
   LOCAL_MODULE    := socketclientserverlib
   LOCAL_SRC_FILES := socket_client_server.c
   ##使用log模块
   LOCAL_LDLIBS := -llog

   include $(BUILD_SHARED_LIBRARY)