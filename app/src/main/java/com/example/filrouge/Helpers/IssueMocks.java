package com.example.filrouge.Helpers;

import com.example.filrouge.Services.EmergencyService;
import com.example.filrouge.models.HighwayIssue;
import com.example.filrouge.models.Issue;
import com.example.filrouge.models.Priority;
import com.example.filrouge.models.Status;
import com.example.filrouge.models.UrbanIssue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IssueMocks {

    private static final double FALLBACK_LAT = 48.8566;
    private static final double FALLBACK_LNG = 2.3522;

    private static final double MIN_DISTANCE_M = 200;
    private static final double MAX_DISTANCE_M = 10_000;
    private static final double METERS_PER_DEG_LAT = 111_320.0;

    private final Random random = new Random();

    private double anchorLat = FALLBACK_LAT;
    private double anchorLng = FALLBACK_LNG;

    public List<Issue> seed() {
        return seed(FALLBACK_LAT, FALLBACK_LNG);
    }

    public List<Issue> seed(double anchorLat, double anchorLng) {
        this.anchorLat = anchorLat;
        this.anchorLng = anchorLng;

        List<Issue> issueList = new ArrayList<>();

        addHighway(issueList, "Accident grave A7",
                "Collision entre deux poids lourds, voie de gauche bloquée",
                Priority.CRITICAL, Status.ON_SITE, 2);

        addHighway(issueList, "Véhicule à contresens",
                "Signalé sur la rocade Sud au niveau de la sortie 12",
                Priority.CRITICAL, Status.REPORTED, 6);

        addHighway(issueList, "Obstacle sur la chaussée",
                "Perte de chargement (palettes) sur la voie centrale",
                Priority.HIGH, Status.CONFIRMED, 12);

        addHighway(issueList, "Bouchon massif",
                "Ralentissement de 5km suite à un rétrécissement de chaussée",
                Priority.HIGH, Status.REPORTED, 18);

        addHighway(issueList, "Travaux de nuit",
                "Peinture au sol en cours, circulation sur une seule voie",
                Priority.MEDIUM, Status.ON_SITE, 27);

        addUrban(issueList, "Panne de signalisation",
                "Feux tricolores HS à l'intersection Jean Jaurès",
                Priority.HIGH, Status.REPORTED, 35);

        addHighway(issueList, "Brouillard givrant",
                "Visibilité inférieure à 50 mètres sur le secteur forestier",
                Priority.MEDIUM, Status.CONFIRMED, 48);

        addHighway(issueList, "Inondation chaussée",
                "Forte pluie, risque d'aquaplaning sur la bretelle d'accès",
                Priority.MEDIUM, Status.REPORTED, 65);

        addHighway(issueList, "Présence de verglas",
                "Pont suspendu glissant, saleuse en route",
                Priority.HIGH, Status.ON_SITE, 90);

        addHighway(issueList, "Véhicule en panne",
                "Voiture sur la bande d'arrêt d'urgence, triangle posé",
                Priority.LOW, Status.CLEARING, 120);

        addHighway(issueList, "Animal errant",
                "Chien signalé aux abords de la départementale D10",
                Priority.MEDIUM, Status.REPORTED, 150);

        addUrban(issueList, "Nid-de-poule profond",
                "Risque de crevaison sur la voie de droite",
                Priority.LOW, Status.CONFIRMED, 180);

        addUrban(issueList, "Manifestation",
                "Cortège avançant lentement en centre-ville",
                Priority.MEDIUM, Status.ON_SITE, 240);

        addUrban(issueList, "Débris de verre",
                "Suite à un bris de glace, nettoyage nécessaire",
                Priority.LOW, Status.RESOLVED, 300);

        addUrban(issueList, "Route barrée",
                "Fermeture exceptionnelle pour un événement sportif",
                Priority.MEDIUM, Status.CLEARING, 360);

        EmergencyService brain= EmergencyService.getInstance();
        for (Issue issue : issueList) {
            issue.addObserver(brain);
        }

        return issueList;
    }

    private void addHighway(List<Issue> list, String title, String description,
                            Priority priority, Status status, int minutesAgo) {
        double[] pos = randomNearbyLatLng();
        list.add(new HighwayIssue(title, description, priority, status,
                pos[0], pos[1], timestampFor(minutesAgo)));
    }

    private void addUrban(List<Issue> list, String title, String description,
                          Priority priority, Status status, int minutesAgo) {
        double[] pos = randomNearbyLatLng();
        list.add(new UrbanIssue(title, description, priority, status,
                pos[0], pos[1], timestampFor(minutesAgo)));
    }

    private double[] randomNearbyLatLng() {
        double distance = MIN_DISTANCE_M + random.nextDouble() * (MAX_DISTANCE_M - MIN_DISTANCE_M);
        double bearing = random.nextDouble() * 2 * Math.PI;
        double dLat = (distance * Math.cos(bearing)) / METERS_PER_DEG_LAT;
        double dLng = (distance * Math.sin(bearing))
                / (METERS_PER_DEG_LAT * Math.cos(Math.toRadians(anchorLat)));
        return new double[]{anchorLat + dLat, anchorLng + dLng};
    }

    private static long timestampFor(int minutesAgo) {
        return System.currentTimeMillis() - minutesAgo * 60_000L;
    }
}
