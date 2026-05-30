package com.example.filrouge;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.filrouge.Interfaces.Notifiable;
import com.example.filrouge.Interfaces.ViewObserver;
import com.example.filrouge.models.Issue;
import com.example.filrouge.models.IssueController;
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

import java.util.ArrayList;
import java.util.List;

public class Fragment5 extends Fragment
        implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMarkerDragListener,
        ViewObserver {

    public static final int FRAGMENT_ID = 4;
    public static final int CODE_READY   = 1;

    private Notifiable notifiable;
    private IssueController controller;
    private GoogleMap googleMap;

    private ListView listView;
    private ArrayAdapter<String> listAdapter;
    private final List<String> visibleTitles = new ArrayList<>();
    private List<Issue> lastIssues = new ArrayList<>();
    private boolean initialCameraSet = false;

    // ------------------------------------------------------------------ lifecycle

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Notifiable) {
            notifiable = (Notifiable) context;
        } else {
            throw new AssertionError(requireActivity().getClass().getName()
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
        if (notifiable != null) notifiable.onFragmentDisplayed(FRAGMENT_ID);
    }

    // ------------------------------------------------------------------ view

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

        // ListView du bas
        listView = view.findViewById(R.id.list_visible_issues);
        listAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, visibleTitles) {
            @Override
            public android.view.View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
                android.widget.TextView tv = (android.widget.TextView) super.getView(position, convertView, parent);
                tv.setTextColor(android.graphics.Color.BLACK);
                tv.setPadding(8, 18, 8, 18);
                return tv;
            }
        };
        listView.setAdapter(listAdapter);

        // Carte
        SupportMapFragment mapFrag = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFrag != null) mapFrag.getMapAsync(this);

        // Signale à ControlActivity que la Vue est prête → elle câblera le MVC
        if (notifiable != null) {
            notifiable.onDataChange(FRAGMENT_ID, this, CODE_READY, null);
        }
    }

    // ------------------------------------------------------------------ MVC

    public void setController(IssueController controller) {
        this.controller = controller;
    }

    // Appelé par IssueManager quand les données changent (ex: marker déplacé)
    @Override
    public void update(List<Issue> issues) {
        if (googleMap == null) return;
        this.lastIssues = issues;
        displayMarkers(issues);
        updateListView(issues);
    }

    // ------------------------------------------------------------------ carte

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        this.googleMap = map;

        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setAllGesturesEnabled(true);
        map.setOnMarkerClickListener(this);
        map.setOnMarkerDragListener(this);

        // Quand la caméra s'arrête (scroll ou zoom), on rafraîchit seulement la liste
        map.setOnCameraIdleListener(() -> updateListView(lastIssues));

        // Affichage initial
        if (controller != null) controller.initialLoad(googleMap);
    }

    private void displayMarkers(List<Issue> issues) {
        if (googleMap == null) return;
        googleMap.clear();

        LatLngBounds.Builder bounds = new LatLngBounds.Builder();
        for (Issue issue : issues) {
            LatLng pos = new LatLng(issue.getLatitude(), issue.getLongitude());
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .title(issue.getTitle())
                    .snippet(issue.getDescription())
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.defaultMarker(hueFor(issue.getPriority()))));
            if (marker != null) marker.setTag(issue);
            bounds.include(pos);
        }

        if (!initialCameraSet) {
            initialCameraSet = true;
            final LatLngBounds finalBounds = bounds.build();
            View root = getView();
            if (root != null) {
                root.post(() -> googleMap.moveCamera(
                        CameraUpdateFactory.newLatLngBounds(finalBounds, 100)));
            }
        }
    }

    private void updateListView(List<Issue> issues) {
        visibleTitles.clear();
        if (googleMap != null) {
            LatLngBounds screen = googleMap.getProjection().getVisibleRegion().latLngBounds;
            for (Issue issue : issues) {
                LatLng pos = new LatLng(issue.getLatitude(), issue.getLongitude());
                if (screen.contains(pos)) {
                    visibleTitles.add(issue.getTitle()
                            + " (" + issue.getLatitude() + ", " + issue.getLongitude() + ")");
                }
            }
        }
        listAdapter.notifyDataSetChanged();
    }

    // ------------------------------------------------------------------ marker listeners

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        if (marker.isInfoWindowShown()) marker.hideInfoWindow();
        else marker.showInfoWindow();
        return true;
    }

    @Override public void onMarkerDragStart(@NonNull Marker marker) { marker.hideInfoWindow(); }
    @Override public void onMarkerDrag(@NonNull Marker marker) {}

    @Override
    public void onMarkerDragEnd(@NonNull Marker marker) {
        if (controller != null) controller.onMarkerDragEnd(marker);
    }

    // ------------------------------------------------------------------ couleur

    private float hueFor(Priority priority) {
        if (priority == null) return BitmapDescriptorFactory.HUE_GREEN;
        switch (priority) {
            case LOW:    return BitmapDescriptorFactory.HUE_GREEN;
            case MEDIUM: return BitmapDescriptorFactory.HUE_ORANGE;
            case HIGH:   return BitmapDescriptorFactory.HUE_RED;
            default:     return BitmapDescriptorFactory.HUE_MAGENTA;
        }
    }
}