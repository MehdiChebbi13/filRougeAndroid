package com.example.filrouge.models;

import android.os.Parcel;
import android.os.Parcelable;

public class UrbanIssue extends Issue implements Parcelable {
    public UrbanIssue(String title, String description, Priority priority, Status status,
                      double latitude, double longitude) {
        super(title, description, priority, status, latitude, longitude);
    }

    public UrbanIssue(String title, String description, Priority priority, Status status,
                      double latitude, double longitude, long timestamp) {
        super(title, description, priority, status, latitude, longitude, timestamp);
    }

    protected UrbanIssue(Parcel in) {
        super(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UrbanIssue> CREATOR = new Creator<UrbanIssue>() {
        @Override
        public UrbanIssue createFromParcel(Parcel in) {
            return new UrbanIssue(in);
        }

        @Override
        public UrbanIssue[] newArray(int size) {
            return new UrbanIssue[size];
        }
    };

    @Override
    public String getSafetyProtocol() {
        return "Do not block traffic near site";
    }
}
