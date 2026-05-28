package com.example.filrouge.Helpers;


import com.example.filrouge.Services.EmergencyService;
import com.example.filrouge.models.HighwayIssue;
import com.example.filrouge.models.Issue;
import com.example.filrouge.models.Priority;
import com.example.filrouge.models.Status;
import com.example.filrouge.models.UrbanIssue;

import java.util.ArrayList;
import java.util.List;

public class IssueMocks {

    // Deterministic grid around Paris so demo markers always land in the
    // same positions across runs (5 columns, ~0.005° step ≈ 500 m).
    private static final double BASE_LAT = 48.8566;
    private static final double BASE_LNG = 2.3522;
    private static final double STEP     = 0.005;
    private static final int    COLS     = 5;

    public List<Issue> seed(){
        List<Issue> issueList = new ArrayList<>();

        // Incidents de type "Danger Immédiat"
        addHighway(issueList, "Accident grave A7",
                "Collision entre deux poids lourds, voie de gauche bloquée",
                Priority.CRITICAL, Status.ON_SITE);

        addHighway(issueList, "Véhicule à contresens",
                "Signalé sur la rocade Sud au niveau de la sortie 12",
                Priority.CRITICAL, Status.REPORTED);

        addHighway(issueList, "Obstacle sur la chaussée",
                "Perte de chargement (palettes) sur la voie centrale",
                Priority.HIGH, Status.CONFIRMED);

        // Incidents de type "Travaux et Ralentissements"
        addHighway(issueList, "Bouchon massif",
                "Ralentissement de 5km suite à un rétrécissement de chaussée",
                Priority.HIGH, Status.REPORTED);

        addHighway(issueList, "Travaux de nuit",
                "Peinture au sol en cours, circulation sur une seule voie",
                Priority.MEDIUM, Status.ON_SITE);

        addUrban(issueList, "Panne de signalisation",
                "Feux tricolores HS à l'intersection Jean Jaurès",
                Priority.HIGH, Status.REPORTED);

        // Incidents de type "Météo et Visibilité"
        addHighway(issueList, "Brouillard givrant",
                "Visibilité inférieure à 50 mètres sur le secteur forestier",
                Priority.MEDIUM, Status.CONFIRMED);

        addHighway(issueList, "Inondation chaussée",
                "Forte pluie, risque d'aquaplaning sur la bretelle d'accès",
                Priority.MEDIUM, Status.REPORTED);

        addHighway(issueList, "Présence de verglas",
                "Pont suspendu glissant, saleuse en route",
                Priority.HIGH, Status.ON_SITE);

        // Incidents de type "Divers"
        addHighway(issueList, "Véhicule en panne",
                "Voiture sur la bande d'arrêt d'urgence, triangle posé",
                Priority.LOW, Status.CLEARING);

        addHighway(issueList, "Animal errant",
                "Chien signalé aux abords de la départementale D10",
                Priority.MEDIUM, Status.REPORTED);

        addUrban(issueList, "Nid-de-poule profond",
                "Risque de crevaison sur la voie de droite",
                Priority.LOW, Status.CONFIRMED);

        // Extras
        addUrban(issueList, "Manifestation",
                "Cortège avançant lentement en centre-ville",
                Priority.MEDIUM, Status.ON_SITE);

        addUrban(issueList, "Débris de verre",
                "Suite à un bris de glace, nettoyage nécessaire",
                Priority.LOW, Status.RESOLVED);

        addUrban(issueList, "Route barrée",
                "Fermeture exceptionnelle pour un événement sportif",
                Priority.MEDIUM, Status.CLEARING);

        // Plug the singleton "brain" onto every mock issue so status / priority
        // changes are reported automatically (loose coupling: the Issue does
        // not know which observer is listening).
        EmergencyService brain= EmergencyService.getInstance();
        for (Issue issue : issueList) {
            issue.addObserver(brain);
        }

        return issueList;
    }

    private void addHighway(List<Issue> list, String title, String description,
                            Priority priority, Status status) {
        int idx = list.size();
        list.add(new HighwayIssue(title, description, priority, status, latFor(idx), lngFor(idx)));
    }

    private void addUrban(List<Issue> list, String title, String description,
                          Priority priority, Status status) {
        int idx = list.size();
        list.add(new UrbanIssue(title, description, priority, status, latFor(idx), lngFor(idx)));
    }

    private static double latFor(int idx) { return BASE_LAT + (idx / COLS) * STEP; }
    private static double lngFor(int idx) { return BASE_LNG + (idx % COLS) * STEP; }
}
