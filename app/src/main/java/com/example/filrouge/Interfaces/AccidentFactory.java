package com.example.filrouge.Interfaces;

import com.example.filrouge.models.Issue;

public interface AccidentFactory {
    Issue createIssue(String title,String description);
    /** Creates the issue at an explicit location (e.g. the user's GPS position). */
    Issue createIssue(String title, String description, double latitude, double longitude);
    //todo: add createEmergencyContact()
}
