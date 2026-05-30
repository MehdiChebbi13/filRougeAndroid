package com.example.filrouge;

import android.content.Context;
import android.location.Location;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.filrouge.Interfaces.ClickableIssue;
import com.example.filrouge.models.Issue;

import java.util.List;
import java.util.Locale;

public class IssueAdapter<T> extends ArrayAdapter<T> {
    private final List<T> items;
    private final LayoutInflater mInflater;
    ClickableIssue<T> callBackFragment;
    public IssueAdapter(@NonNull ClickableIssue callback, List<T> items) {
        super(callback.getContext(), 0);
        this.items = items;
        this.callBackFragment = callback;
        mInflater = LayoutInflater.from(callback.getContext());
    }

    public int getCount() {
        return items.size();
    }
    public T getItem(int position) {
        return items.get(position);
    }
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View layoutItem;
        layoutItem = mInflater.inflate(R.layout.item_issue, parent, false);

        TextView name = layoutItem.findViewById(R.id.tv_title);
        ImageView priority = layoutItem.findViewById(R.id.iv_priority);
        TextView brief = layoutItem.findViewById(R.id.tv_description);
        TextView distance = layoutItem.findViewById(R.id.tv_distance);
        TextView eta = layoutItem.findViewById(R.id.tv_eta);
        Spinner status = layoutItem.findViewById(R.id.spinner_status);

        name.setText(((Issue)items.get(position)).getTitle());

        brief.setText(((Issue)items.get(position)).getDescription());

        Issue currentIssue = (Issue) items.get(position);

        // Distance from the user's anchor (real GPS location or fallback).
        distance.setText(formatDistance(currentIssue));

        // Relative reported time, e.g. "il y a 12 min".
        eta.setText(DateUtils.getRelativeTimeSpanString(
                currentIssue.getTimestamp(),
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE));

        switch (currentIssue.getPriority()) {
            case LOW:
                priority.setImageResource(R.drawable.gravity_low);
                break;
            case MEDIUM:
                priority.setImageResource(R.drawable.gravity_medium);
                break;
            case HIGH:
            case CRITICAL:
                priority.setImageResource(R.drawable.gravity_high);
                break;
        }

        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(
                callBackFragment.getContext(),
                R.array.status_options,
                android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status.setAdapter(statusAdapter);

        // Set the current value before attaching the listener so the initial
        // programmatic selection is not reported as a user-driven change.
        final int currentOrdinal = currentIssue.getStatus().ordinal();
        status.setSelection(currentOrdinal, false);

        status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                // Spinner fires once on bind; only forward genuine changes.
                if (pos == currentOrdinal) {
                    return;
                }
                float rating = pos + 1f; // Spinner position -> Status rating (1..5)
                callBackFragment.onRatingBarChange(position, rating, IssueAdapter.this, items);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // no-op
            }
        });

        layoutItem.setOnClickListener( clic -> callBackFragment.onClickItem(items,position));
        return layoutItem;
    }

    /** Distance between the user anchor and the issue, e.g. "1,2 km" or "850 m". */
    private String formatDistance(Issue issue) {
        Context ctx = callBackFragment.getContext();
        if (!(ctx instanceof ControlActivity)) {
            return "";
        }
        double[] me = ((ControlActivity) ctx).getUserLatLng();
        float[] result = new float[1];
        Location.distanceBetween(me[0], me[1],
                issue.getLatitude(), issue.getLongitude(), result);
        float meters = result[0];
        if (meters < 1000f) {
            return String.format(Locale.getDefault(), "%d m", Math.round(meters));
        }
        return String.format(Locale.getDefault(), "%.1f km", meters / 1000f);
    }

}
