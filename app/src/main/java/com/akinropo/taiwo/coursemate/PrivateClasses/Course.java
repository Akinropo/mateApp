package com.akinropo.taiwo.coursemate.PrivateClasses;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by TAIWO on 1/11/2017.
 */
public class Course implements Parcelable {
    public static final Creator<Course> CREATOR = new Creator<Course>() {
        @Override
        public Course createFromParcel(Parcel in) {
            return new Course(in);
        }

        @Override
        public Course[] newArray(int size) {
            return new Course[size];
        }
    };
    int id, courseUnit;
    String courseCode;
    String courseTitle;


    public Course(int id, int courseUnit, String courseCode) {
        this.id = id;
        this.courseUnit = courseUnit;
        this.courseCode = courseCode;
    }

    protected Course(Parcel in) {
        id = in.readInt();
        courseUnit = in.readInt();
        courseCode = in.readString();
        courseTitle = in.readString();
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public int getId() {
        return id;
    }

    public int getCourseUnit() {
        return courseUnit;
    }

    public String getCourseCode() {
        return courseCode;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(courseUnit);
        dest.writeString(courseCode);
        dest.writeString(courseTitle);
    }
}
