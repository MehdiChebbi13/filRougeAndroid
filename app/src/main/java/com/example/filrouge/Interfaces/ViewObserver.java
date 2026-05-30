package com.example.filrouge.Interfaces;

import com.example.filrouge.models.Issue;
import java.util.List;

public interface ViewObserver {
    void update(List<Issue> issues);
}
