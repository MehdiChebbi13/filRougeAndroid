package com.example.filrouge.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.filrouge.Interfaces.IssueObserver;

public class HighwayIssue extends Issue implements Parcelable {

    public HighwayIssue(String title, String description, Priority priority, Status status,
                        double latitude, double longitude) {
        super(title, description, priority, status, latitude, longitude);
    }

    public HighwayIssue(String title, String description, Priority priority, Status status,
                        double latitude, double longitude, long timestamp) {
        super(title, description, priority, status, latitude, longitude, timestamp);
    }

    protected HighwayIssue(Parcel in) {
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

    public static final Creator<HighwayIssue> CREATOR = new Creator<HighwayIssue>() {
        @Override
        public HighwayIssue createFromParcel(Parcel in) {
            return new HighwayIssue(in);
        }

        @Override
        public HighwayIssue[] newArray(int size) {
            return new HighwayIssue[size];
        }
    };

    @Override
    public String getSafetyProtocol() {
        return "Do not try to avoid road blockage, it can be fatal.";
    }


}
