package com.example.filrouge.Interfaces;

public interface IssueObservable {

    void addObserver(IssueObserver observer);

    void removeObserver(IssueObserver observer);

    void notifyObservers();
}
