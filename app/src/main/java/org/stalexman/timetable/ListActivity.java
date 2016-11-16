package org.stalexman.timetable;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class ListActivity extends AppCompatActivity {
    private ListView list;
    static int posit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity_layout);
        list = (ListView)findViewById(R.id.list_activity_listview);
        File first = getFilesDir();
        final File second = new File(first, "schedules");
        String [] filesInDirectory = second.list();
        String [] showedNames = new String[filesInDirectory.length];
        for (int i = 0; i < filesInDirectory.length; i++){
            showedNames[i] = filesInDirectory[i].substring(0, filesInDirectory[i].length()-5).replace("_", " ");
        }
        ArrayAdapter<String>arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, showedNames);
        list.setAdapter(arrayAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String [] facultyAndGroup = ((TextView) view).getText().toString().split(" ");
                Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                intent.putExtra("startingActivity", "ListActivity");
                Log.i("DEV", "faculty = " + facultyAndGroup[0]);
                Log.i("DEV", "group = " + facultyAndGroup[1]);
                intent.putExtra("faculty", facultyAndGroup[0]);
                intent.putExtra("group", facultyAndGroup[1]);
                startActivity(intent);
            }
        });


        list.setLongClickable(true);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                    int pos, long id) {
                Log.v("long clicked","pos: " + pos);
                posit = pos;
                AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);
                builder.setTitle("Опции")
                        .setCancelable(true)
                        .setNegativeButton("Отмена",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                        .setNeutralButton("Удалить файл",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Log.v("long clicked","pos: " + id);
                                        File deleteDir = new File(getFilesDir() , "schedules");
                                        File deleteFile = new File(deleteDir, deleteDir.list()[posit]);
                                        if (!deleteFile.delete()){
                                            Log.v("DEV","Удаление не удалось");
                                        } else {
                                            Log.v("DEV", "Удаление успешно");
                                        }
                                        File first = getFilesDir();
                                        File second = new File(first, "schedules");
                                        String [] filesInDirectory = second.list();
                                        String [] showedNames = new String[filesInDirectory.length];
                                        Log.v("DEV","Длина списка " + showedNames.length);
                                        for (int i = 0; i < filesInDirectory.length; i++){
                                            showedNames[i] = filesInDirectory[i].substring(0, filesInDirectory[i].length()-5).replace("_", " ");
                                        }
                                        ArrayAdapter<String>arrayAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, showedNames);
                                        list.setAdapter(arrayAdapter);
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            }
        });


    }

    @Override
    protected void onResume() {
        File first = getFilesDir();
        File second = new File(first, "schedules");
        String [] filesInDirectory = second.list();
        String [] showedNames = new String[filesInDirectory.length];
        Log.v("DEV","Длина списка " + showedNames.length);
        for (int i = 0; i < filesInDirectory.length; i++){
            showedNames[i] = filesInDirectory[i].substring(0, filesInDirectory[i].length()-5).replace("_", " ");
        }
        ArrayAdapter<String>arrayAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, showedNames);
        list.setAdapter(arrayAdapter);
        super.onResume();
    }
}
