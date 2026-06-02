package Tg.OSEOR.DIWA.Backend.dto;

public class ChauffeurStatusDTO {
    private Long id;
    private String nomComplet;
    private String telephone;
    private String email;
    private String statut; // "LIBRE", "EN_MISSION", "ASSIGNE"
    private String missionActuelle; // Référence de la demande
    private String destination; // Adresse de récupération ou livraison
    private long totalMissions;
    private double score;
    private java.time.LocalDateTime dateAssignation;
    private String heureDebutTravail;
    private String heureFinTravail;
    private Long missionId;

    public ChauffeurStatusDTO(Long id, String nomComplet, String telephone, String email, String statut, 
                             String missionActuelle, String destination, long totalMissions, double score, 
                             java.time.LocalDateTime dateAssignation, String heureDebut, String heureFin, Long missionId) {
        this.id = id;
        this.nomComplet = nomComplet;
        this.telephone = telephone;
        this.email = email;
        this.statut = statut;
        this.missionActuelle = missionActuelle;
        this.destination = destination;
        this.totalMissions = totalMissions;
        this.score = score;
        this.dateAssignation = dateAssignation;
        this.heureDebutTravail = heureDebut;
        this.heureFinTravail = heureFin;
        this.missionId = missionId;
    }

    // Getters
    public Long getId() { return id; }
    public String getNomComplet() { return nomComplet; }
    public String getTelephone() { return telephone; }
    public String getEmail() { return email; }
    public String getStatut() { return statut; }
    public String getMissionActuelle() { return missionActuelle; }
    public String getDestination() { return destination; }
    public long getTotalMissions() { return totalMissions; }
    public double getScore() { return score; }
    public java.time.LocalDateTime getDateAssignation() { return dateAssignation; }
    public String getHeureDebutTravail() { return heureDebutTravail; }
    public String getHeureFinTravail() { return heureFinTravail; }
    public Long getMissionId() { return missionId; }
}
