package org.stalexman.timetable;

import android.content.Intent;
import android.graphics.Path;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.InvalidPropertiesFormatException;

public class WebViewActivity extends AppCompatActivity {
    private WebView webView;
    private String LOG = "DEV WebViewActivity";
    private String group;
    private String faculty;
    private String startingActivity;
    private FileOutputStream fos;
    private FileInputStream fis;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG, "onCreate() started");
        super.onCreate(savedInstanceState);
        startingActivity = getIntent().getStringExtra("startingActivity");
        setContentView(R.layout.webview_layout);
        webView = (WebView)findViewById(R.id.activityWebView);
        faculty = getIntent().getStringExtra("faculty");
        group = getIntent().getStringExtra("group");
        if (startingActivity.equals("MainActivity")){
            webView.loadUrl("file:///data/data/org.stalexman.timetable/files/new_timetable.html");
        } else {
            webView.loadUrl("file:///data/data/org.stalexman.timetable/files/schedules/" + faculty + "_" + group + ".html");
        }
        Log.i(LOG, "onCreate() completed");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(LOG, "onCreateOptionsMenu() started");
        if (startingActivity.equals("MainActivity")) {
            getMenuInflater().inflate(R.menu.webview_menu, menu);
            Log.i(LOG, "onCreateOptionsMenu() completed");
            return true;
        } else if (startingActivity.equals("ListActivity")) {
            getMenuInflater().inflate(R.menu.webactivity_from_listactivity_menu, menu);
            Log.i(LOG, "onCreateOptionsMenu() completed");
            return true;

        } else if (startingActivity.equals("onMySchedule")) {
            getMenuInflater().inflate(R.menu.webactivity_from_listactivity_menu, menu);
            Log.i(LOG, "onCreateOptionsMenu() completed");
            return true;
        }
        return false;
    }

        @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save_webactivity:
                // Saving current schedule
                try{
                    fis = new FileInputStream(getFilesDir() + File.separator + "new_timetable.html");
                    File first = getFilesDir();
                    File second = new File(first, "schedules");
                    File path = new File(second,faculty + "_" + group + ".html");
                    fos = new FileOutputStream(path);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        fos.write(buffer, 0, length);
                    }
                    Toast.makeText(this, "Сохранено", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(this, "Ошибка копирования", Toast.LENGTH_SHORT).show();
                } finally {
                    try {
                        if (!(fis == null)) {fis.close();}
                        if (!(fos == null)) {fos.close();}
                    } catch (IOException e){
                        Toast.makeText(this, "Файл поврежден", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.action_settings_webactivity:
                // intent to SettingsActivity
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_delete_timetable:
                // intent to SettingsActivity
                File first = getFilesDir();
                File second = new File(first, "schedules");
                File path = new File(second,faculty + "_" + group + ".html");
                if (path.delete()){
                    Toast.makeText(this, "Расписание удалено", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Ошибка удаления", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                Toast.makeText(this, "Неизвестный пункт меню", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
