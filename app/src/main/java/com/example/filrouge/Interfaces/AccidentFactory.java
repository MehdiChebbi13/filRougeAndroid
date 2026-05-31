package com.example.filrouge.Interfaces;

import com.example.filrouge.models.Issue;

public interface AccidentFactory {
    Issue createIssue(String title,String description);

    Issue createIssue(String title, String description, double latitude, double longitude);

}
