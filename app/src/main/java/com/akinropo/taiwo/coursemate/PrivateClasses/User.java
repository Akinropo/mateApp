package com.akinropo.taiwo.coursemate.PrivateClasses;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by TAIWO on 1/6/2017.
 */
public class User implements Parcelable {
    int id;
    String firstname;
    String othername;
    String Sex;
    String major;
    String year;
    String faculty;
    String email;
    String photo;
    String highschool;
    String phone;

    public User(Parcel in) {
        id = in.readInt();
        firstname = in.readString();
        othername = in.readString();
        Sex = in.readString();
        major = in.readString();
        year = in.readString();
        faculty = in.readString();
        email = in.readString();
        photo = in.readString();
        highschool = in.readString();
        phone = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public User() {

    }

    public String getHighschool() {
        return highschool;
    }

    public void setHighschool(String highschool) {
        this.highschool = highschool;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOthername() {
        return othername;
    }

    public void setOthername(String othername) {
        this.othername = othername;
    }

    public String getSex() {
        return Sex;
    }

    public void setSex(String sex) {
        Sex = sex;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }



   /* public User(int id, String firstname, String othername, String sex, String major, String year, String faculty, String email, String photo, String highschool, String phone) {
        this.id = id;
        this.firstname = firstname;
        this.othername = othername;
        Sex = sex;
        this.major = major;
        this.year = year;
        this.faculty = faculty;
        this.email = email;
        this.photo = photo;
        this.highschool = highschool;
        this.phone = phone;
    }
*/

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(firstname);
        dest.writeString(othername);
        dest.writeString(Sex);
        dest.writeString(major);
        dest.writeString(year);
        dest.writeString(faculty);
        dest.writeString(email);
        dest.writeString(photo);
        dest.writeString(highschool);
        dest.writeString(phone);
    }
}
