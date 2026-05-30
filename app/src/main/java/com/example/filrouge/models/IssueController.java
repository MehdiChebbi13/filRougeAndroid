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

    // Appelé pour chaque marqueur après sa création sur la carte
    public void controlMarker(Issue issue, Marker marker) {

        // Toggle infobulle au clic
        marker.setTag(issue); // on attache l'issue au marker pour pouvoir la retrouver

        // Le listener de clic est géré dans Fragment5 via OnMarkerClickListener,
        // mais ici on gère le drag (déplacement du marqueur)
        // Note : Google Maps ne supporte pas le drag listener via setOnMarkerDragListener
        // directement sur le Marker, il faut le faire sur la GoogleMap.
        // On stocke donc l'issue dans le tag du marker pour y accéder lors du drag.
    }

    // Appelé par Fragment5 quand un marker a été déplacé
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
        // Affichage initial : notifie la vue avec toutes les issues
        if (view != null) view.update(model.getIssues());
    }

    public void updateVisibleIssues(GoogleMap map) {
        // Re-notifie la vue pour qu'elle recalcule la liste visible
        if (view != null) view.update(model.getIssues());
    }

    public void setView(ViewObserver view) {
        this.view = view;
    }
}