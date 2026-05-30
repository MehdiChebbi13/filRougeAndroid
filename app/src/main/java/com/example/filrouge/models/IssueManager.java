package com.example.filrouge.models;

import com.example.filrouge.Interfaces.ModelObservable;
import com.example.filrouge.Interfaces.ViewObserver;

import java.util.ArrayList;
import java.util.List;

public class IssueManager implements ModelObservable {

    private final List<Issue> issueList = new ArrayList<>();
    private final List<ViewObserver> observers = new ArrayList<>();

    public IssueManager(List<Issue> issues) {
        issueList.addAll(issues);
    }

    public List<Issue> getIssues() {
        return issueList;
    }

    // Appelé quand un marker est déplacé : met à jour la géoloc et notifie les vues
    public void setGeoLocation(Issue issue, double latitude, double longitude) {
        issue.setLatitude(latitude);
        issue.setLongitude(longitude);
        notifyAllObservers();
    }

    @Override
    public void addObserver(ViewObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(ViewObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyAllObservers() {
        for (ViewObserver observer : observers) {
            observer.update(issueList);
        }
    }
}