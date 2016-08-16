package com.github.thecjw.qqmailbypass;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.activity_launcher)
public class LauncherActivity extends ActionBarActivity {
  private final static String TAG = LauncherActivity.class.getSimpleName();
  private final static String tencent_mail_package_name = "com.tencent.androidqqmail";

  @AfterViews
  void init_view() {
    Log.d(TAG, "QQmail lockscreen bypass, by TheCjw");
  }

  @Click
  void doEnterComposeMailActivity() {
    (new AccessQQMailTask("com.tencent.qqmail.LaunchComposeMail")).execute();
    finish();
  }

  @Click
  void doEnterComposeNoteActivity() {
    (new AccessQQMailTask("com.tencent.qqmail.LaunchComposeNote")).execute();
    finish();
  }

  @Click
  void doEnterLauncherActivity() {
    (new AccessQQMailTask("com.tencent.qqmail.LaucherActivity")).execute();
    finish();
  }

  @Click
  void doEnterFtnUploadActivity() {
    (new AccessQQMailTask("com.tencent.qqmail.LaunchFtnUpload")).execute();
    finish();
  }

  @Click
  void doEnterCalendarActivity() {
    (new AccessQQMailTask("com.tencent.qqmail.calendar.fragment.CalendarFragmentActivity")).execute();
    finish();
  }

  private class AccessQQMailTask extends AsyncTask<Void, Void, Void> {

    private String target_activity_name;

    public AccessQQMailTask(String activity_name) {
      super();
      target_activity_name = activity_name;
    }

    @Override
    protected void onPreExecute() {
    }

    void start_activity() {
      Intent intent = new Intent();
      intent.setClassName(tencent_mail_package_name, target_activity_name);
      startActivity(intent);
    }

    @Override
    protected Void doInBackground(Void... params) {
      try {
        start_activity();
        Thread.sleep(500);
        start_activity();
      } catch (Exception e) {
        Log.e(TAG, e.getMessage());
      }

      return null;
    }

    @Override
    protected void onPostExecute(Void result) {
    }
  }
}
