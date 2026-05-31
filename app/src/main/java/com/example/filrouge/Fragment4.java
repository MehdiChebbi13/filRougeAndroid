package com.example.filrouge;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Fragment4 extends Fragment {
    private static final int FRAGMENT_ID= 3;
    private static final String ARG_ISSUE = "issue";

    private Notifiable notifiable;

    public static Fragment4 newInstance(Issue issue) {
        Fragment4 fragment = new Fragment4();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ISSUE, issue);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        if(context instanceof Notifiable){
            notifiable= (Notifiable) context;
        }else{
            throw new AssertionError("Classe " + requireActivity().getClass().getName() + " ne met pas en œuvre Notifiable.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        notifiable = null;
    }

    @Override
    public void onStart(){
        super.onStart();
        if(notifiable!=null){
            notifiable.onFragmentDisplayed(FRAGMENT_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment4, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView title = view.findViewById(R.id.tv_issue_title);
        TextView description = view.findViewById(R.id.tv_issue_description);
        TextView priority = view.findViewById(R.id.tv_issue_priority);
        TextView status = view.findViewById(R.id.tv_issue_status);
        TextView timestamp = view.findViewById(R.id.tv_issue_timestamp);
        ImageView priorityIcon = view.findViewById(R.id.iv_issue_priority);

        Bundle args = getArguments();
        Issue issue = (args != null) ? args.getParcelable(ARG_ISSUE) : null;

        if (issue == null) {

            title.setText(R.string.placeholder_select_issue);
            description.setText("");
            priority.setText("");
            status.setText("");
            timestamp.setText("");
            priorityIcon.setImageDrawable(null);
            return;
        }

        title.setText(issue.getTitle());
        description.setText(issue.getDescription());
        priority.setText(getString(R.string.label_priority) + ": " + issue.getPriority());
        status.setText(getString(R.string.label_status) + ": " + issue.getStatus());

        String formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(new Date(issue.getTimestamp()));
        timestamp.setText(getString(R.string.label_reported_on, formattedDate));

        priorityIcon.setImageResource(priorityDrawable(issue.getPriority()));

        SupportMapFragment mapFrag = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map_fragment4);
        if (mapFrag != null) {
            mapFrag.getMapAsync(googleMap -> {
                LatLng pos = new LatLng(issue.getLatitude(), issue.getLongitude());
                googleMap.addMarker(new MarkerOptions()
                        .position(pos)
                        .title(issue.getTitle()));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15f));
                googleMap.getUiSettings().setAllGesturesEnabled(false); 
                googleMap.getUiSettings().setZoomControlsEnabled(false);
            });
        }

        if (savedInstanceState == null) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.camera_fragment_container, new CameraFragment())
                    .commit();
        }

        if (issue.getPicture() != null && !issue.getPicture().isEmpty()) {
            android.os.Bundle result = new android.os.Bundle();
            result.putString("photo_path", issue.getPicture());
            getChildFragmentManager().setFragmentResult("picture_channel", result);
        }
    }

    private int priorityDrawable(Priority priority) {
        if (priority == null) return R.drawable.gravity_low;
        switch (priority) {
            case LOW:
                return R.drawable.gravity_low;
            case MEDIUM:
                return R.drawable.gravity_medium;
            case HIGH:
            case CRITICAL:
            default:
                return R.drawable.gravity_high;
        }
    }
}
