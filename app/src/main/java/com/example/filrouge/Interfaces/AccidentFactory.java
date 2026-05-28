package com.example.filrouge.Interfaces;

import com.example.filrouge.models.Issue;

public interface AccidentFactory {
    Issue createIssue(String title,String description);
    //todo: add createEmergencyContact()
}
