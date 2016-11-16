package org.stalexman.timetable.fragments;

import android.support.v4.app.Fragment;

/**
 * Created by Алекс on 30.09.2016.
 */
public class FatherFragment extends Fragment {

    private String faculty;
    private String group;

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
