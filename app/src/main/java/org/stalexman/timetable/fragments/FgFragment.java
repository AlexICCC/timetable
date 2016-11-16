package org.stalexman.timetable.fragments;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.stalexman.timetable.MainActivity;
import org.stalexman.timetable.R;
import org.stalexman.timetable.SettingsActivity;
import org.stalexman.timetable.WebViewActivity;
import org.stalexman.timetable.classes.MyRequest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * A simple {@link Fragment} subclass.
 */
public class FgFragment extends FatherFragment {
    private String group;
    private String faculty;
    private int facultyId = -1;

    private FileOutputStream fos;
    private OutputStreamWriter osw;
    private Button btnShow;

    private Spinner spinner;
    private EditText editText;
    String [] faculties;
    public FgFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("LOG FG", "onCreateView() starts");

        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_fg, container, false);
        editText = (EditText)v.findViewById(R.id.fgGroup);
        btnShow = (Button)v.findViewById(R.id.btnShow);
        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                group = editText.getText().toString().toUpperCase();
                if (faculty == null || group.equals("") || group.equals(" ")){
                    Toast.makeText(getContext(), getResources().getString(R.string.toast1), Toast.LENGTH_SHORT).show();
                } else if (group.length() > 4){
                    Toast.makeText(getContext(), getResources().getString(R.string.toast2), Toast.LENGTH_SHORT).show();
                } else {
                    btnShow.setClickable(false);
                    new DownloadAsyncTask().execute();
                }
            }
        });
        faculties = getResources().getStringArray(R.array.faculties);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, faculties);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = (Spinner)v.findViewById(R.id.fgFaculty);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                faculty = faculties[i];
                facultyId = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    //    if (savedInstanceState != null){
   //         facultyId = savedInstanceState.getInt("facultyId");
   //         faculty = savedInstanceState.getString("faculty");
    //        group = savedInstanceState.getString("group");
   //         Log.i("LOG FG bundle", "Group = " + group);
   //     }
        Log.i("LOG FG onCreateView()", "Group = " + group);
        if (facultyId != -1){
            spinner.setSelection(facultyId);
        }
        if ((group != null) && !(group.equals(""))){
            Log.i("LOG FG onCreateView()", "Setting up group " + group);
            editText.setText(group);
            Log.i("LOG FG onCreateView()", "Getting text from editText Group = " + editText.getText().toString());
        }
        Log.i("LOG FG", "onCreateView() ends");

        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        Log.i("LOG FG", "onActivityCreated()");

        if (bundle != null) {
          group = bundle.getString("group");
          faculty = bundle.getString("faculty");
          facultyId = bundle.getInt("facultyId");
          Log.i("LOG FG bundle", "Group = " + group);
        }
        if (facultyId != -1){
            spinner.setSelection(facultyId);
        }
        if ((group != null)){
            Log.i("LOG FG", "onActivityCreated() Setting up group " + group);
            editText.setText(group);
            Log.i("LOG FG", "onActivityCreated() Getting text from editText Group = " + editText.getText().toString());

        }
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("group", editText.getText().toString());
        outState.putString("faculty", faculty);
        outState.putInt("facultyId", facultyId);
        super.onSaveInstanceState(outState);
    }

    public int getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(int facultyId) {
        this.facultyId = facultyId;
    }



    class DownloadAsyncTask extends AsyncTask<String, Void, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... strings) {
            MyRequest myRequest = new MyRequest(faculty, group);
                return myRequest.createPost();

        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("Error")){
                Toast.makeText(getContext(), "Ошибка загрузки и распознавания расписания", Toast.LENGTH_LONG).show();
                btnShow.setClickable(true);
                return;
            }
            try {
                fos = getActivity().openFileOutput("new_timetable.html", getActivity().MODE_PRIVATE);
                osw = new OutputStreamWriter(fos);
                osw.write(s);
            } catch (IOException e){
                Toast.makeText(getContext(), "Can't create file", Toast.LENGTH_LONG).show();
            } finally {
                // looks like some dammit thing - I can't output and I can't close =(
                try {
                    if (osw != null){
                        osw.flush();
                        osw.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("startingActivity", "MainActivity");
                intent.putExtra("faculty", faculty);
                intent.putExtra("group", group);
                btnShow.setClickable(true);
                startActivity(intent);
            }
        }
    }



}
