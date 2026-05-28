package com.example.filrouge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.filrouge.Interfaces.ClickableIssue;
import com.example.filrouge.models.Issue;

import java.util.List;

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
        RatingBar status = layoutItem.findViewById(R.id.rb_status);

        name.setText(((Issue)items.get(position)).getTitle());

        brief.setText(((Issue)items.get(position)).getDescription());

        Issue currentIssue = (Issue) items.get(position);
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
        status.setRating(currentIssue.getStatus().getRating());

        status.setOnRatingBarChangeListener((ratingBar1, value, fromUser) ->
                callBackFragment.onRatingBarChange(position, value, this, items));

        layoutItem.setOnClickListener( clic -> callBackFragment.onClickItem(items,position));
        return layoutItem;
    }

}
