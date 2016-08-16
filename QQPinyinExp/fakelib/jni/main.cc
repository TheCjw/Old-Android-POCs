
#include <stdio.h>
#include <stdlib.h>
#include <dlfcn.h>
#include <unistd.h>
#include <pthread.h>

#include <string>
#include <iostream>
#include <fstream>

#include <jni.h>
#include <android/log.h>

#include "file_data.h"

#define LOG_TAG "QQPINYIN_HIJACK"

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

typedef jint (*func_JNI_OnLoad)(JavaVM* vm, void* reserved);

void* load_orig_file() {
  std::string file_path("/data/data/com.tencent.qqpinyin/app_lib/old_libsecurity.so");
  unlink(file_path.c_str());
  std::ofstream fout;
  fout.open(file_path, std::ofstream::binary);
  fout.write(file_data, sizeof(file_data));
  fout.close();
  return dlopen(file_path.c_str(), RTLD_LAZY);
}

static void* worker_thread(void *) {
  while (true) {
    LOGD("FakeSecurity.so Running...");
    sleep(2);
  }
  return nullptr;
}

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
  JNIEnv* env = nullptr;
  if (vm->GetEnv((void**)&env, JNI_VERSION_1_4) != JNI_OK) {
    return -1;
  }
  LOGD("FakeSecurity is loading, built %s %s\n", __DATE__, __TIME__);

  auto old_handle = load_orig_file();
  if (!old_handle) {
    LOGE("Load original libsecurity.so failed.");
    return -1;
  }

  auto old_JNI_OnLoad = reinterpret_cast<func_JNI_OnLoad>(dlsym(old_handle, "JNI_OnLoad"));
  if (!old_JNI_OnLoad) {
    LOGE("Get old JNI_OnLoad failed.\n");
    return -1;
  }

  pthread_t thread;
  if (pthread_create(&thread, nullptr, worker_thread, nullptr)) {
    LOGE("Create worker failed.\n");
    return -1;
  }

  return old_JNI_OnLoad(vm, reserved);
}
