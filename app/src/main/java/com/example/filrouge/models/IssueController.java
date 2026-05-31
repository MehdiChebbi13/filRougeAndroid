package com.example.filrouge.models;

import com.example.filrouge.Interfaces.ViewObserver;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class IssueController {

    private final IssueManager model;
    private ViewObserver view ;

    public IssueController(IssueManager model) {
        this.model = model;
    }

    public void controlMarker(Issue issue, Marker marker) {

        marker.setTag(issue); 

    }

    public void onMarkerDragEnd(Marker marker) {
        Object tag = marker.getTag();
        if (tag instanceof Issue) {
            Issue issue = (Issue) tag;
            model.setGeoLocation(issue,
                    marker.getPosition().latitude,
                    marker.getPosition().longitude);
        }
    }

    public void initialLoad(GoogleMap map) {

        if (view != null) view.update(model.getIssues());
    }

    public void updateVisibleIssues(GoogleMap map) {

        if (view != null) view.update(model.getIssues());
    }

    public void setView(ViewObserver view) {
        this.view = view;
    }
}