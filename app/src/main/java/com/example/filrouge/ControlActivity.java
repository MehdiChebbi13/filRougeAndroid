package com.example.filrouge;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.filrouge.Helpers.IssueMocks;
import com.example.filrouge.Interfaces.Menuable;
import com.example.filrouge.Interfaces.Notifiable;
import com.example.filrouge.Interfaces.Picturable;
import com.example.filrouge.models.Issue;
import com.example.filrouge.models.IssueController;
import com.example.filrouge.models.IssueManager;
import com.example.filrouge.models.Status;

import java.util.ArrayList;
import java.util.List;

public class ControlActivity extends AppCompatActivity implements Menuable, Notifiable, Picturable {
    public static final String EXTRA_INDEX = "index";
    private static final String DATA_IS_STARTING = "sauvegarde";

    private IssueManager issueManager;
    private IssueController issueController;

    private final String TAG = "frallo "+getClass().getSimpleName();
    private static final String DATA_MENU_NUMBER = "num";
    private int currentIndex;
    private Fragment mainFragment;
    private MenuFragment menu;
    private final List<Issue> issues = new ArrayList<>();
    private Issue currentIssue; // issue actuellement affichée dans Fragment4

    private Fragment[] tabFragments = {new Screen1Fragment(), new Fragment2(),new Fragment3(), new Fragment4(), new Fragment5(), new Fragment6(), new Fragment7()};


    public List<Issue> getIssues(){ return issues;}
    private boolean isStarting= true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_control);

        Intent intent = getIntent();
        if(intent!=null){
            currentIndex = intent.getIntExtra(EXTRA_INDEX,0);
            Log.d(TAG,"received menu#"+currentIndex);
        }
        Bundle args = new Bundle();
        args.putInt(EXTRA_INDEX, currentIndex);


        issues.addAll(new IssueMocks().seed());
        issueManager = new IssueManager(issues);
        issueController = new IssueController(issueManager);
        if (savedInstanceState == null) {
            menu=new MenuFragment();
            menu.setArguments(args);
            mainFragment = tabFragments[currentIndex];
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_menu_container,menu);
            transaction.replace(R.id.fragment_container,mainFragment);
            transaction.commit();
        }


    }


    @Override
    public void onClick(int numFragment) {
        Log.d(TAG, "Menu " + numFragment +" has clicked!");
    }

    @Override
    public void onDataChange(int numFragment, Object object, int actionCode, Object argsAction) {
        if (actionCode == Fragment5.CODE_READY && object instanceof Fragment5) {
            Fragment5 vue = (Fragment5) object;
            vue.setController(issueController);
            issueController.setView(vue);
            issueManager.addObserver(vue);
            issueManager.notifyAllObservers(); // déclenche l'affichage initial
            return;
        }
        if (!(object instanceof Issue)) {
            return;
        }
        Fragment2.Action[] actions = Fragment2.Action.values();
        if (actionCode < 0 || actionCode >= actions.length) {
            return;
        }
        Issue issue = (Issue) object;
        switch (actions[actionCode]) {
            case DISPLAY:
                currentIssue = issue;
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, Fragment4.newInstance(issue))
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(null)
                        .commit();
                break;
            case STATUS_CHANGE:
                if (argsAction instanceof Float) {
                    Status status = Status.fromRating((Float) argsAction);
                    if (status != null) {
                        issue.setStatus(status);
                    }
                }
                break;
            case CREATE:
                issues.add(issue);
                break;
        }
    }


    @Override
    public void onFragmentDisplayed(int fragmentIndex) {
        if (fragmentIndex != currentIndex) {
            currentIndex = fragmentIndex;
            // Find the menu fragment and tell it to update its highlighted item
            MenuFragment menuFragment = (MenuFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_menu_container); // adjust the ID
            if (menuFragment != null) {
                menuFragment.updateSelection(currentIndex);
            }
        }
    }

    @Override
    public void onMenuClick(int position) {
        currentIndex = position;
        Fragment fragment=tabFragments[currentIndex];

        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        if (!isStarting) {
            // Si ce n'est plus le démarrage, on veut pouvoir revenir en arrière
            transaction.addToBackStack(null);
        } else {
            // C'est le premier appel (auto), on ne l'ajoute pas à la pile car on ne veut pas pouvoir revenir en arrière
            isStarting = false;
        }

        transaction.commit();


    }

    @Override
    public void onPictureTaken(String photopath) {
        Log.d(TAG, "Photo reçue : " + photopath);
        if (currentIssue != null) {
            currentIssue.setPicture(photopath);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(DATA_IS_STARTING, isStarting);
        outState.putInt(DATA_MENU_NUMBER, currentIndex);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isStarting = savedInstanceState.getBoolean(DATA_IS_STARTING);
        currentIndex = savedInstanceState.getInt(DATA_MENU_NUMBER);
    }
}