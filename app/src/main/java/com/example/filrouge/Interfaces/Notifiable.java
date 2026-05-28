package com.example.filrouge.Interfaces;

import com.example.filrouge.models.Issue;

public interface Notifiable {
    void onClick(int numFragment);
    void onDataChange(int numFragment, Object object, int actionCode, Object argsAction);
    void onFragmentDisplayed(int fragmentId);
}
