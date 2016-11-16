package org.stalexman.timetable.fragments;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import org.stalexman.timetable.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WebViewFragment extends FatherFragment {

    private String faculty;
    private String group;
    private WebView webView;


    public WebViewFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_web_view, container, false);
        webView = (WebView)v.findViewById(R.id.webView);
        faculty = getFaculty();
        group = getGroup();
        webView.loadUrl("file:///data/data/org.stalexman.timetable/files/schedules/" + faculty + "_" + group + ".html");
        return v;
    }

    public String getFaculty() {
        return faculty;
    }
    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }
    public String getGroup() {
        return group;
    }
    public void setGroup(String group) {
        this.group = group;
    }


}
