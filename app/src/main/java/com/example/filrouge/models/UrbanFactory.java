package com.example.filrouge.models;

import com.example.filrouge.Interfaces.AccidentFactory;
import com.example.filrouge.Services.EmergencyService;

public class UrbanFactory implements AccidentFactory {

    // Geolocation defaults: random position within roughly +/- 5 km of Paris.
    private static final double BASE_LAT = 48.8566;
    private static final double BASE_LNG = 2.3522;
    private static final double SPREAD  = 0.05;

    @Override
    public Issue createIssue(String title, String description) {
        double latitude  = BASE_LAT + (Math.random() - 0.5) * 2 * SPREAD;
        double longitude = BASE_LNG + (Math.random() - 0.5) * 2 * SPREAD;
        return createIssue(title, description, latitude, longitude);
    }

    @Override
    public Issue createIssue(String title, String description, double latitude, double longitude) {
        Issue issue = new UrbanIssue(title, description,
                Priority.MEDIUM, Status.REPORTED,
                latitude, longitude);
        issue.addObserver(EmergencyService.getInstance());
        return issue;
    }
}
