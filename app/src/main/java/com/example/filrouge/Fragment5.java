package com.example.filrouge;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.filrouge.Interfaces.Notifiable;
import com.example.filrouge.models.Issue;
import com.example.filrouge.models.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class Fragment5 extends Fragment
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final int FRAGMENT_ID = 4;

    private Notifiable notifiable;
    private GoogleMap googleMap;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Notifiable) {
            notifiable = (Notifiable) context;
        } else {
            throw new AssertionError("Classe " + requireActivity().getClass().getName()
                    + " ne met pas en œuvre Notifiable.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        notifiable = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (notifiable != null) {
            notifiable.onFragmentDisplayed(FRAGMENT_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment5, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        this.googleMap = map;

        // --- UI controls (explicit so the prof can tick off zoom + déplacement) ---
        map.getUiSettings().setZoomControlsEnabled(true);   // on-screen +/- buttons
        map.getUiSettings().setAllGesturesEnabled(true);    // pan + pinch-to-zoom

        // --- Build markers from the activity's issues list ---
        List<Issue> issues = ((ControlActivity) requireActivity()).getIssues();
        if (issues == null || issues.isEmpty()) {
            return;
        }

        LatLngBounds.Builder bounds = new LatLngBounds.Builder();
        for (Issue issue : issues) {
            LatLng pos = new LatLng(issue.getLatitude(), issue.getLongitude());

            MarkerOptions opt = new MarkerOptions()
                    .position(pos)
                    .title(issue.getTitle())            // info-bubble line 1
                    .snippet(issue.getDescription())    // info-bubble line 2
                    .icon(BitmapDescriptorFactory.defaultMarker(hueFor(issue.getPriority())));

            Marker marker = map.addMarker(opt);
            if (marker != null) {
                marker.setTag(issue);
            }
            bounds.include(pos);
        }

        // --- Initial camera: frame all markers once layout is ready ---
        final View root = getView();
        if (root != null) {
            final LatLngBounds finalBounds = bounds.build();
            root.post(() ->
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(finalBounds, 100)));
        }

        // --- Marker tap → toggle info-window (the prof's "afficher/masquer infobulle") ---
        map.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        // Toggle behaviour: if the bubble is shown, hide it; otherwise show it.
        if (marker.isInfoWindowShown()) {
            marker.hideInfoWindow();
        } else {
            marker.showInfoWindow();
        }
        // Centre the camera on the marker for visual feedback.
        if (googleMap != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        }
        // Returning true consumes the event so the default "always show" behaviour
        // does not override our toggle.
        return true;
    }

    /**
     * Map an issue priority to a marker hue. Mirrors the colour semantics
     * already used in {@link IssueAdapter} for the list row icons.
     */
    private float hueFor(Priority priority) {
        if (priority == null) return BitmapDescriptorFactory.HUE_GREEN;
        switch (priority) {
            case LOW:
                return BitmapDescriptorFactory.HUE_GREEN;
            case MEDIUM:
                return BitmapDescriptorFactory.HUE_ORANGE;
            case HIGH:
                return BitmapDescriptorFactory.HUE_RED;
            case CRITICAL:
            default:
                return BitmapDescriptorFactory.HUE_MAGENTA;
        }
    }
}
