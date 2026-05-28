package com.example.filrouge;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.filrouge.Interfaces.Notifiable;
import com.example.filrouge.models.Issue;

public class Screen1Fragment extends Fragment {

    static final int FRAGMENT_ID= 0;
    private static final String ARG_ISSUE = "issue";
    private Notifiable notifiable;

    public static Screen1Fragment newInstance(Issue issue){
        Screen1Fragment fragment=new Screen1Fragment();
        Bundle args =new Bundle();

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
        View view=inflater.inflate(R.layout.screen1fragment, container, false);
        TextView title=view.findViewById(R.id.issueTitle);
        TextView priority=view.findViewById(R.id.issuePriority);

        Bundle args=getArguments();
        if (args!=null){
            Issue issue=args.getParcelable(ARG_ISSUE);
            if (issue!=null){
                title.setText(issue.getTitle());
                priority.setText(issue.getPriority().toString());
            }
        }

        return view;


    }

}