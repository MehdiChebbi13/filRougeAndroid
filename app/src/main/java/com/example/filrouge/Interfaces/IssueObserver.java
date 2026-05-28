package com.example.filrouge.Interfaces;

import com.example.filrouge.models.Issue;

public interface IssueObserver {

    void onStatusChanged(Issue issue);

    void onPriorityChanged(Issue issue);
}
