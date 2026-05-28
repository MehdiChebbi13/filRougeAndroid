package com.example.filrouge.Services;

import android.util.Log;

import com.example.filrouge.Interfaces.IssueObserver;
import com.example.filrouge.models.Issue;
import com.example.filrouge.models.Status;

public class EmergencyService implements IssueObserver {
    private final String TAG = "frallo "+getClass().getSimpleName();
    private static EmergencyService instance;

    // Constructeur privé pour le Singleton
    private EmergencyService() {}

    public static EmergencyService getInstance() {
        if (instance == null) instance = new EmergencyService();
        return instance;
    }

    @Override
    public void onStatusChanged(Issue issue) {
        Log.d(TAG, "Le nouveau statut de l'incident est : " + issue.getStatus());
        if (issue.getStatus() == Status.CONFIRMED) {
            Log.w(TAG, "ALERTE : Un incident " + issue.getPriority()
                    + " nécessite une intervention : " + issue.getTitle());
        }
    }

    @Override
    public void onPriorityChanged(Issue issue) {
        Log.d(TAG, "Nouvelle priorité [" + issue.getPriority() + "] sur : " + issue.getTitle());
    }


}