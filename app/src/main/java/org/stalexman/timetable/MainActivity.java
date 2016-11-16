package org.stalexman.timetable;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.stalexman.timetable.classes.MyRequest;
import org.stalexman.timetable.fragments.FatherFragment;
import org.stalexman.timetable.fragments.FgFragment;
import org.stalexman.timetable.fragments.WebViewFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences mSettings;
    public static final String APP_SETTINGS = "mySettings";
    public static final String APP_SETTINGS_FACULTY = "mFaculty";
    public static final String APP_SETTINGS_GROUP = "mGroup";

    private String LOG = "DEV MainActivity";

    private String lastButtonPressed = "onNewRequest";
    private String myGroup;
    private String myFaculty;
    private FatherFragment currentFragment;
    private FileOutputStream fos;
    private OutputStreamWriter osw;
    private Button myShedule;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG, "onCreate() started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        mSettings = getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
        myShedule = (Button)findViewById(R.id.mySchedule);
        //it needs at first start of app
        File folder = new File(getFilesDir(), "schedules");
        if (!folder.exists()){
            folder.mkdirs();
        }

        //===========================================================
        // if Activity recreates, we download old data for fragment
        if (savedInstanceState != null){
            if (savedInstanceState.getString("fragment").equals("FgFragment")){
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                currentFragment = new FgFragment();
                Log.i(LOG, "onCreate() Bundle group = " + savedInstanceState.getBundle("bundle").getString("group"));
                currentFragment.setArguments(savedInstanceState.getBundle("bundle"));
                //    ((FgFragment)currentFragment).setFacultyId(savedInstanceState.getInt("facultyId"));
                Log.i(LOG, "onCreate() Fragment changing starts");
                ft.add(R.id.changebleFG, currentFragment);
                Log.i(LOG, "onCreate() Fragment changing ends");
                ft.commit();
                Log.i(LOG, "onCreate() completed");
                return;
            } else if (savedInstanceState.getString("fragment").equals("WebViewFragment")){
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                currentFragment = new WebViewFragment();
                currentFragment.setFaculty(savedInstanceState.getString("faculty"));
                currentFragment.setGroup(savedInstanceState.getString("group"));
                ft.replace(R.id.changebleFG, currentFragment);
                ft.commit();
                Log.i(LOG, "onCreate() completed");
                return;
            }
        }


            // If myFaculty and myGroup are setted up,...
        if ((mSettings.contains(APP_SETTINGS_GROUP) && mSettings.contains(APP_SETTINGS_FACULTY)) && (mSettings.getString(APP_SETTINGS_FACULTY, null) != null) && (mSettings.getString(APP_SETTINGS_GROUP, null) != null)) {
                myFaculty = mSettings.getString(APP_SETTINGS_FACULTY, null);
                myGroup = mSettings.getString(APP_SETTINGS_GROUP, null);
                // ...search file with the schedule...
                String searchedFile = myFaculty + "_" + myGroup + ".html";
                File first = getFilesDir();
                File second = new File(first, "schedules");
                String [] fileNames = second.list();
                for (int i = 0; i < fileNames.length; i++) {
                    if (fileNames[i].equals(searchedFile)) {
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        // ...and open it in WebView
                        currentFragment = new WebViewFragment();
                        currentFragment.setFaculty(myFaculty);
                        currentFragment.setGroup(myGroup);
                        ft.add(R.id.changebleFG, currentFragment);
                        ft.commit();
                        Log.i(LOG, "onCreate() completed");
                        return;
                    }
                }
            }
            // myFaculty and myGroup are not setted up, create FgFragment
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            currentFragment = new FgFragment();
            ft.add(R.id.changebleFG, currentFragment);
            ft.commit();
        Log.i(LOG, "onCreate() completed");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_saved_schedules:
                Intent intent = new Intent(this, ListActivity.class);
                startActivity(intent);
                break;
            case R.id.action_settings_mainactivity:
                Intent intent1 = new Intent(this, SettingsActivity.class);
                startActivity(intent1);
                break;
            case R.id.action_refresh:
                Log.i(LOG, "Refresh");
                if (myFaculty != null && myGroup != null){
                    new RefreshAsyncTask().execute();
                } else {
                    Toast.makeText(getBaseContext(), "Установите ваши факультет и группу в настройках", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        Log.i(LOG, "onResume() started");

        super.onResume();
        if (mSettings.contains(APP_SETTINGS_FACULTY)){
            myFaculty = mSettings.getString(APP_SETTINGS_FACULTY, null);
        }
        if (mSettings.contains(APP_SETTINGS_GROUP)){
            myGroup = mSettings.getString(APP_SETTINGS_GROUP, null);
        }
        Log.i(LOG, "onResume() completed");

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (currentFragment instanceof FgFragment){
            outState.putString("fragment", "FgFragment");
            Bundle bundle = new Bundle();
            bundle.putString("faculty", currentFragment.getFaculty());
            Log.i(LOG, "onSave() Group = " + currentFragment.getGroup());
            bundle.putString("group", currentFragment.getGroup());
            bundle.putInt("facultyId", ((FgFragment) currentFragment).getFacultyId());
            outState.putBundle("bundle", bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.remove(currentFragment);
            Log.i(LOG, "onSave() FgFragment deleting");
            ft.commit();

      //      Log.i(LOG, " onSaveInstanceState() Group = " +outState.getString("group"));
      //      Log.i(LOG, " onSaveInstanceState() Group = " +currentFragment.getGroup());
        } else if (currentFragment instanceof WebViewFragment){
            outState.putString("fragment", "WebViewFragment");
            outState.putString("group", myGroup);
            outState.putString("faculty", myFaculty);
        }
        super.onSaveInstanceState(outState);
    }

    public void onMySchedule(View view){
        // If myFaculty and myGroup are setted up,...
        if ((mSettings.getString(APP_SETTINGS_FACULTY, null) != null) && (mSettings.getString(APP_SETTINGS_GROUP, null) != null) &&  (!mSettings.getString(APP_SETTINGS_GROUP, "").equals(""))){
            myFaculty = mSettings.getString(APP_SETTINGS_FACULTY, null);
            myGroup = mSettings.getString(APP_SETTINGS_GROUP, null);
            // ...search file with the schedule...
            String searchedFile = myFaculty + "_" + myGroup + ".html";
            File first = getFilesDir();
            File second = new File(first, "schedules");
            String[] fileNames = second.list();
            for (int i=0; i < fileNames.length; i++){
                if (fileNames[i].equals(searchedFile)){
                    if (!(currentFragment instanceof WebViewFragment)){
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        // ...and open it in WebView
                        currentFragment = new WebViewFragment();
                        currentFragment.setFaculty(myFaculty);
                        currentFragment.setGroup(myGroup);
                        ft.add(R.id.changebleFG, currentFragment);
                        ft.commit();
                        return;
                    }
                    Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                    intent.putExtra("faculty", myFaculty);
                    intent.putExtra("group", myGroup);
                    intent.putExtra("startingActivity", "onMySchedule");
                    lastButtonPressed = "onMySchedule";
                    startActivity(intent);
                    return;
                }
            }
            // If file does not exist, try to download it
            myShedule.setClickable(false);
            new DownloadAsyncTask().execute();
        } else {
            // Intent to SettingsActivity for making settings
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
    }


    public void onNewRequest(View view) {
        if (currentFragment instanceof FgFragment){
            return;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        currentFragment = new FgFragment();
        ft.replace(R.id.changebleFG, currentFragment);
        ft.commit();
        lastButtonPressed = "onNewRequest";
    }


    class DownloadAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            Log.i(LOG, "DownloadAsyncTask started");
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... strings) {
            MyRequest myRequest = new MyRequest(myFaculty, myGroup);
            return myRequest.createPost();
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("Error")){
                Toast.makeText(getBaseContext(), "Невозможно загрузить", Toast.LENGTH_LONG).show();
                Log.i(LOG, "DownloadAsyncTask download failed");
                return;
            }
            try {
                File first = getFilesDir();
                File second = new File(first, "schedules");
                File path = new File(second,myFaculty + "_" + myGroup + ".html");
                fos = new FileOutputStream(path);
                osw = new OutputStreamWriter(fos);
                osw.write(s);
                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra("faculty", myFaculty);
                intent.putExtra("group", myGroup);
                intent.putExtra("startingActivity", "onMySchedule");
                lastButtonPressed = "onMySchedule";
                startActivity(intent);
            } catch (IOException e){
                Toast.makeText(getBaseContext(), "Can't create file", Toast.LENGTH_LONG).show();
            } finally {
                myShedule.setClickable(true);
                // looks like some dammit thing - I can't output and I can't close =(
                try {
                    if (osw != null){
                        osw.flush();
                        osw.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.i(LOG, "DownloadAsyncTask completed");
        }
    }

    class RefreshAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i(LOG, "RefreshAsyncTask started");
        }
        @Override
        protected String doInBackground(String... strings) {
            MyRequest myRequest = new MyRequest(myFaculty, myGroup);
            return myRequest.createPost();
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                if (s.equals("Error")){
                    Toast.makeText(getBaseContext(), "Невозможно обновить", Toast.LENGTH_LONG).show();
                    Log.i(LOG, "RefreshAsyncTask download failed");
                    return;
                }
                //save downloaded timetable...
                File first = getFilesDir();
                File second = new File(first, "schedules");
                File path = new File(second,myFaculty + "_" + myGroup + ".html");
                fos = new FileOutputStream(path);
                osw = new OutputStreamWriter(fos);
                osw.write(s);

                //... and show it
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                currentFragment = new WebViewFragment();
                currentFragment.setFaculty(myFaculty);
                currentFragment.setGroup(myGroup);
                ft.replace(R.id.changebleFG, currentFragment);
                ft.commit();
                lastButtonPressed = "onMySchedule";
                Toast.makeText(getBaseContext(), "Ваше расписание обновлено", Toast.LENGTH_LONG).show();
                Log.i(LOG, "RefreshAsyncTask output complete");
            } catch (IOException e){
                Toast.makeText(getBaseContext(), "Невозможно обновить", Toast.LENGTH_LONG).show();
            } finally {
                myShedule.setClickable(true);
                // looks like some dammit thing - I can't output and I can't close =(
                try {
                    if (osw != null){
                        osw.flush();
                        osw.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.i(LOG, "RefreshAsyncTask completed");
        }
    }
}
