package Tg.OSEOR.DIWA.Backend.dto.TicketSAVDTO;

import java.time.LocalDateTime;
import java.util.List;
import Tg.OSEOR.DIWA.Backend.entity.enums.StatutTicket;

public class TicketSAVDTOResponse {
    private Long id;
    private String vehiculeImmat;
    private String vehiculeMarque;
    private String description;
    private StatutTicket statut;
    private Long userId;
    private String userName;
    private Long technicienId;
    private String technicienNom;
    private List<CommentaireTicketDTO> commentaires;
    private LocalDateTime dateCreation;

    public static class CommentaireTicketDTO {
        private String message;
        private String auteur;
        private LocalDateTime dateCommentaire;

        public CommentaireTicketDTO(String message, String auteur, LocalDateTime date) {
            this.message = message;
            this.auteur = auteur;
            this.dateCommentaire = date;
        }

        public String getMessage() { return message; }
        public String getAuteur() { return auteur; }
        public LocalDateTime getDateCommentaire() { return dateCommentaire; }
    }

    // Getters / Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getVehiculeImmat() { return vehiculeImmat; }
    public void setVehiculeImmat(String vehiculeImmat) { this.vehiculeImmat = vehiculeImmat; }
    public String getVehiculeMarque() { return vehiculeMarque; }
    public void setVehiculeMarque(String vehiculeMarque) { this.vehiculeMarque = vehiculeMarque; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public StatutTicket getStatut() { return statut; }
    public void setStatut(StatutTicket statut) { this.statut = statut; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public Long getTechnicienId() { return technicienId; }
    public void setTechnicienId(Long technicienId) { this.technicienId = technicienId; }
    public String getTechnicienNom() { return technicienNom; }
    public void setTechnicienNom(String technicienNom) { this.technicienNom = technicienNom; }
    public List<CommentaireTicketDTO> getCommentaires() { return commentaires; }
    public void setCommentaires(List<CommentaireTicketDTO> commentaires) { this.commentaires = commentaires; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
}
