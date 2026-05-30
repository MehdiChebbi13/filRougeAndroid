package com.example.filrouge;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
    private static final int REQ_LOCATION = 1001;
    // Anchor used to seed mock incidents + compute distances. Defaults to Paris
    // until the device's real location is resolved.
    private double userLat = 48.8566;
    private double userLng = 2.3522;
    private boolean initialized = false;
    private boolean shouldCommitFragments = true;

    private int currentIndex;
    private Fragment mainFragment;
    private MenuFragment menu;
    private final List<Issue> issues = new ArrayList<>();
    private Issue currentIssue; // issue actuellement affichée dans Fragment4

    /** Current user anchor as {lat, lng} (real location or Paris fallback). */
    public double[] getUserLatLng() { return new double[]{userLat, userLng}; }

    /** Re-reads the device location (if permitted) and returns the fresh anchor. */
    public double[] getFreshUserLatLng() {
        resolveAnchorFromLocation();
        return getUserLatLng();
    }

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

        shouldCommitFragments = savedInstanceState == null;

        // Resolve the device location to anchor the mock incidents "near me".
        if (hasLocationPermission()) {
            resolveAnchorFromLocation();
            initContent();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQ_LOCATION);
        }
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    /** Updates {@link #userLat}/{@link #userLng} from the freshest last-known fix. */
    private void resolveAnchorFromLocation() {
        if (!hasLocationPermission()) {
            return; // keep the Paris fallback
        }
        try {
            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (lm == null) return;
            Location best = null;
            for (String provider : new String[]{LocationManager.GPS_PROVIDER,
                    LocationManager.NETWORK_PROVIDER, LocationManager.PASSIVE_PROVIDER}) {
                try {
                    Location loc = lm.getLastKnownLocation(provider);
                    if (loc != null && (best == null || loc.getTime() > best.getTime())) {
                        best = loc;
                    }
                } catch (IllegalArgumentException ignored) {
                    // provider not available on this device
                }
            }
            if (best != null) {
                userLat = best.getLatitude();
                userLng = best.getLongitude();
                Log.d(TAG, "anchor set to device location " + userLat + ", " + userLng);
            }
        } catch (SecurityException ignored) {
            // permission revoked between check and read -> keep fallback
        }
    }

    /** Seeds the mock incidents around the anchor and commits the first screen. */
    private void initContent() {
        if (initialized) return;
        initialized = true;

        issues.clear();
        issues.addAll(new IssueMocks().seed(userLat, userLng));

        if (!shouldCommitFragments) {
            return; // rotation: the system restores the fragments itself
        }

        Bundle args = new Bundle();
        args.putInt(EXTRA_INDEX, currentIndex);
        menu = new MenuFragment();
        menu.setArguments(args);
        mainFragment = tabFragments[currentIndex];
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_menu_container, menu);
        transaction.replace(R.id.fragment_container, mainFragment);
        transaction.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_LOCATION) {
            resolveAnchorFromLocation(); // falls back to Paris if still denied
            initContent();
        }
    }


    @Override
    public void onClick(int numFragment) {
        Log.d(TAG, "Menu " + numFragment +" has clicked!");
    }

    @Override
    public void onDataChange(int numFragment, Object object, int actionCode, Object argsAction) {
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
        Fragment fragment;
        // Fragment4 (position 3) : on recharge l'issue courante si elle existe
        if (position == 3 && currentIssue != null) {
            fragment = Fragment4.newInstance(currentIssue);
        } else {
            fragment = tabFragments[currentIndex];
        }

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