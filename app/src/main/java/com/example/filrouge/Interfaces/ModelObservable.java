package com.example.filrouge.Interfaces;

public interface ModelObservable {
    void addObserver(ViewObserver observer);
    void removeObserver(ViewObserver observer);
    void notifyAllObservers();
}
