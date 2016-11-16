package org.stalexman.timetable.classes;

import android.util.Log;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Алекс on 18.07.2016.
 */
public class MyRequest {

    private OkHttpClient client = new OkHttpClient();
    private String LOG = "DEV MyRequest";
    private String url = "http://www.dvgups.ru/index.php?Itemid=1246&option=com_timetable&view=timetable";
    private String faculty;
    private String group;
    private String selector = "grp";

    public MyRequest(String faculty, String group, String selector){
        this.faculty = faculty;
        this.group = group;
        this.selector = selector;
    }
    public MyRequest(String faculty, String group){
        this.faculty = faculty;
        this.group = group;
    }

    public String createPost(){

        RequestBody rb = new FormBody.Builder()
                .add("sel1", faculty)
                .add("sel2", group + ".TXT")
                .add("selector", selector)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(rb)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String respStr = response.body().string();
            int first = respStr.indexOf("Версия для печати</a>") + 21;
            int last = respStr.indexOf("</pre>") + 6;
            if (last <= first || last == -1){
                Log.i(LOG,"Error");
                return "Error";
            }
            Log.i(LOG,"Начало строки " + first);
            Log.i(LOG,"Конец строки " + last);
            String [] arrStr = respStr.substring(first, last).split("\n");
            Log.i(LOG,"Количество строк в массиве = " + arrStr.length);
            StringBuilder sb = new StringBuilder("");
            String str;
            for (int i = 0; i < arrStr.length; i++){
                str = arrStr[i];
                if (str.contains(";────")){
                    str = str.substring(0, 57);
                } else if (str.contains("─────")){
                    str = str.substring(0, 50);
                }
                sb.append(str + "\n");
            }


            String out = "<!DOCTYPE html>\n" +
                    "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\n" +
                    "\t<head>\n" +
                    "  \t\t<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" />\n" +
                    "\t</head>\n" +
                    "\t<Body>" + sb.toString() +  "\t</body>\n" +
                    "</html>";
            Log.i(LOG,"Длина строки на write " + out.length() + " ");
            return out;

        } catch (IOException e){
            e.printStackTrace();
        }
        return "Error";
    }


}
