package com.example.filrouge;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.filrouge.Interfaces.ClickableIssue;
import com.example.filrouge.Interfaces.Notifiable;
import com.example.filrouge.models.Issue;
import java.util.List;

public class Fragment2 extends Fragment implements ClickableIssue {
    private static final int FRAGMENT_ID= 1;
    private final String TAG = "frallo " + getClass().getSimpleName();
    private Notifiable notifiable;

    @Override
    public void onRatingBarChange(int itemIndex, float value, IssueAdapter adapter, List items) {
        Log.d(TAG, "onRatingBarChange");
        notifiable.onDataChange(FRAGMENT_ID,items.get(itemIndex), Action.STATUS_CHANGE.ordinal(), value);
    }

    @Override
    public void onClickItem(List items, int itemIndex) {
        Log.d(TAG, "onClickItem");
        notifiable.onDataChange(FRAGMENT_ID,items.get(itemIndex), Action.DISPLAY.ordinal(), null);
    }

    public enum Action{
        DISPLAY,STATUS_CHANGE,CREATE;
    }

    public Fragment2() {
        Log.d(TAG, "screenFragment type 2 created");

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
        View view= inflater.inflate(R.layout.fragment2, container, false);
        ListView listView=view.findViewById(R.id.list_item);
        List<Issue> issues = ((ControlActivity) requireActivity()).getIssues();
        ArrayAdapter<Issue> adapter=new IssueAdapter<Issue>(this,issues);
        listView.setAdapter(adapter);

        return view;
    }

}
