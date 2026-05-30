package com.example.filrouge.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.filrouge.Interfaces.IssueObservable;
import com.example.filrouge.Interfaces.IssueObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract   class Issue implements Parcelable,IssueObservable{

    private final String id;
    private final String title;
    private final String description;
    private final long timestamp;
    private double latitude;
    private double longitude;
    private String picture;
    private Priority priority;
    private Status status;

    private transient List<IssueObserver> observers= new ArrayList<>();



    // Constructeur mis à jour avec géolocalisation
    public Issue(String title, String description, Priority priority, Status status,
                 double latitude, double longitude) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = System.currentTimeMillis();
    }

    public String getPicture() { return picture; }

    public void setPicture(String picture) {
        this.picture = picture;
        notifyObservers(); // notifie les vues comme pour setStatus
    }

    public void setLatitude(double latitude){
        this.latitude = latitude ;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    // --- Implémentation de Parcelable ---
    protected Issue(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        timestamp = in.readLong();
        priority = Priority.valueOf(in.readString());
        status = Status.valueOf(in.readString());
        latitude = in.readDouble();
        longitude = in.readDouble();
        picture = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeLong(timestamp);
        dest.writeString(priority.name());
        dest.writeString(status.name());
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(picture);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // --- Getters ---
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public long getTimestamp() { return timestamp; }
    public Priority getPriority() { return priority; }
    public Status getStatus() { return status; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    public void setStatus(Status status) {
        this.status = status;
        notifyObservers();
    }

    public void setPriority(Priority newPriority) {
        if (newPriority != null && newPriority != this.priority) {
            this.priority = newPriority;
            notifyPriorityChanged();
        }
    }


    public abstract String getSafetyProtocol();


    @NonNull
    @Override
    public String toString() {
        return title + " [" + priority + "] - Status: " + status;
    }

    // --- Implémentation de Observable ---
    @Override
    public void addObserver(IssueObserver observer) {
        if (observers == null) observers = new ArrayList<>();  //risque d'être null car transient
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(IssueObserver observer) {
        if (observers != null) {
            observers.remove(observer);
        }
    }

    @Override
    public void notifyObservers() {
        if (observers == null) return;
        // Iterate over a snapshot so an observer that unsubscribes itself
        // during the callback does not provoke a ConcurrentModificationException.
        for (IssueObserver observer : new ArrayList<>(observers)) {
            try {
                observer.onStatusChanged(this);
            } catch (Exception e) {
                Log.e("Issue", "Observer onStatusChanged failed for "
                        + observer.getClass().getSimpleName(), e);
            }
        }
    }

    private void notifyPriorityChanged() {
        if (observers == null) return;
        for (IssueObserver observer : new ArrayList<>(observers)) {
            try {
                observer.onPriorityChanged(this);
            } catch (Exception e) {
                Log.e("Issue", "Observer onPriorityChanged failed for "
                        + observer.getClass().getSimpleName(), e);
            }
        }
    }
}
