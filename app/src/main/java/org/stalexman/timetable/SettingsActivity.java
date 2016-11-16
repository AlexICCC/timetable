package org.stalexman.timetable;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class SettingsActivity extends AppCompatActivity {
    private SharedPreferences mSettings;
    public static final String APP_SETTINGS = "mySettings";
    public static final String APP_SETTINGS_FACULTY = "mFaculty";
    public static final String APP_SETTINGS_FACULTY_NUMBER = "mFacultyNumber";
    public static final String APP_SETTINGS_GROUP = "mGroup";
    private String myGroup;
    private String myFaculty;
    private int myFacultyNumber = -1;
    private String [] faculties;
    private Spinner spinner;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        mSettings = getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
        myFacultyNumber = mSettings.getInt(APP_SETTINGS_FACULTY_NUMBER, 0);
        editText = (EditText)findViewById(R.id.settingsGroup);
        faculties = getResources().getStringArray(R.array.faculties);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, faculties);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = (Spinner)findViewById(R.id.settingsFaculty);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                myFaculty = faculties[i];
                myFacultyNumber = i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        //...if Activity recreated after rotation
        if (savedInstanceState != null){
            if (savedInstanceState.containsKey("myFaculty")) {
                myFaculty = savedInstanceState.getString("myFaculty");
            }
            if (savedInstanceState.containsKey("myGroup")) {
                myGroup = savedInstanceState.getString("myGroup");
            }
            if (savedInstanceState.containsKey("myFacultyNumber")){
            myFacultyNumber = savedInstanceState.getInt("myFacultyNumber");
            }
            return;
        }
        //...if Activity created first time
        if ((mSettings.contains(APP_SETTINGS_GROUP) && mSettings.contains(APP_SETTINGS_FACULTY)) && (mSettings.getString(APP_SETTINGS_FACULTY, null) != null) && (mSettings.getString(APP_SETTINGS_GROUP, null) != null)) {
            myFaculty = mSettings.getString(APP_SETTINGS_FACULTY, null);
            myGroup = mSettings.getString(APP_SETTINGS_GROUP, null);
            if (myGroup != null){
                editText.setText(myGroup);
            }
            //myFacultyNumber есть смысл проверять, только если установлен myFaculty
            if(myFaculty != null){
                spinner.setSelection(myFacultyNumber);
            }
        }
    }

    public void onSave(View view){
        myGroup = editText.getText().toString().toUpperCase();
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_SETTINGS_FACULTY, myFaculty);
        editor.putString(APP_SETTINGS_GROUP, myGroup);
        editor.putInt(APP_SETTINGS_FACULTY_NUMBER, myFacultyNumber);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD){
            editor.apply();
        } else {
            editor.commit();
        }
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onCancel(View view) {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (myFaculty != null){
        outState.putString("myFaculty", myFaculty);
        }
        if (myGroup != null){
            outState.putString("myGroup", myGroup);
        }
        if (myFacultyNumber != -1){
            outState.putInt("myFacultyNumber", myFacultyNumber);
        }
        super.onSaveInstanceState(outState);
    }
}
