package com.example.filrouge;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.filrouge.Interfaces.ClickableIssue;
import com.example.filrouge.Interfaces.Notifiable;
import com.example.filrouge.models.HighwayIssue;
import com.example.filrouge.models.Issue;
import com.example.filrouge.models.UrbanIssue;

import java.util.ArrayList;
import java.util.List;

public class Fragment2 extends Fragment implements ClickableIssue {
    private static final int FRAGMENT_ID = 1;
    private final String TAG = "frallo " + getClass().getSimpleName();
    private Notifiable notifiable;

    private IssueAdapter<Issue> adapter;
    private List<Issue> filteredIssues = new ArrayList<>();
    private List<Issue> allIssues;
    private boolean showHighway = true;

    public enum Action {
        DISPLAY, STATUS_CHANGE, CREATE;
    }

    public Fragment2() {
        Log.d(TAG, "screenFragment type 2 created");
    }

    @Override
    public void onRatingBarChange(int itemIndex, float value, IssueAdapter a, List items) {
        Log.d(TAG, "onRatingBarChange");
        notifiable.onDataChange(FRAGMENT_ID, items.get(itemIndex), Action.STATUS_CHANGE.ordinal(), value);
    }

    @Override
    public void onClickItem(List items, int itemIndex) {
        Log.d(TAG, "onClickItem");
        notifiable.onDataChange(FRAGMENT_ID, items.get(itemIndex), Action.DISPLAY.ordinal(), null);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Notifiable) {
            notifiable = (Notifiable) context;
        } else {
            throw new AssertionError("Classe " + requireActivity().getClass().getName() + " ne met pas en œuvre Notifiable.");
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2, container, false);

        allIssues = ((ControlActivity) requireActivity()).getIssues();

        ListView listView = view.findViewById(R.id.list_item);
        adapter = new IssueAdapter<>(this, filteredIssues);
        listView.setAdapter(adapter);

        LinearLayout btnAutoroute = view.findViewById(R.id.btnAutoroute);
        LinearLayout btnUrbain    = view.findViewById(R.id.btnUrbain);

        applyFilter(true, btnAutoroute, btnUrbain);

        btnAutoroute.setOnClickListener(v -> applyFilter(true, btnAutoroute, btnUrbain));
        btnUrbain.setOnClickListener(v -> applyFilter(false, btnAutoroute, btnUrbain));

        return view;
    }

    private void applyFilter(boolean highway, LinearLayout btnAutoroute, LinearLayout btnUrbain) {
        showHighway = highway;
        filteredIssues.clear();
        for (Issue issue : allIssues) {
            if (highway && issue instanceof HighwayIssue) filteredIssues.add(issue);
            if (!highway && issue instanceof UrbanIssue) filteredIssues.add(issue);
        }
        adapter.notifyDataSetChanged();

        ImageView icAutoroute = (ImageView) btnAutoroute.getChildAt(0);
        TextView  tvAutoroute = (TextView)  btnAutoroute.getChildAt(1);
        ImageView icUrbain    = (ImageView) btnUrbain.getChildAt(0);
        TextView  tvUrbain    = (TextView)  btnUrbain.getChildAt(1);

        if (highway) {
            btnAutoroute.setBackgroundResource(R.drawable.bg_toggle_selected);
            btnUrbain.setBackgroundResource(android.R.color.transparent);
            if (tvAutoroute != null) tvAutoroute.setTextColor(Color.parseColor("#0091FF"));
            if (icAutoroute != null) icAutoroute.setColorFilter(Color.parseColor("#0091FF"));
            if (tvUrbain    != null) tvUrbain.setTextColor(Color.WHITE);
            if (icUrbain    != null) icUrbain.setColorFilter(Color.WHITE);
        } else {
            btnUrbain.setBackgroundResource(R.drawable.bg_toggle_selected);
            btnAutoroute.setBackgroundResource(android.R.color.transparent);
            if (tvUrbain    != null) tvUrbain.setTextColor(Color.parseColor("#0091FF"));
            if (icUrbain    != null) icUrbain.setColorFilter(Color.parseColor("#0091FF"));
            if (tvAutoroute != null) tvAutoroute.setTextColor(Color.WHITE);
            if (icAutoroute != null) icAutoroute.setColorFilter(Color.WHITE);
        }
    }
}
