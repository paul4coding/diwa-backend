package Tg.OSEOR.DIWA.Backend.dto.ChatDTO;

import java.time.LocalDateTime;

public class MessageChatDTO {
    private Long id;
    private String contenu;
    private String auteurNom;
    private String roleAuteur;
    private LocalDateTime createdAt;
    private Boolean lu;
    private String pieceJointeUrl;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContenu() { return contenu; }
    public void setContenu(String c) { this.contenu = c; }
    public String getAuteurNom() { return auteurNom; }
    public void setAuteurNom(String a) { this.auteurNom = a; }
    public String getRoleAuteur() { return roleAuteur; }
    public void setRoleAuteur(String r) { this.roleAuteur = r; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime c) { this.createdAt = c; }
    public Boolean getLu() { return lu; }
    public void setLu(Boolean l) { this.lu = l; }
    public String getPieceJointeUrl() { return pieceJointeUrl; }
    public void setPieceJointeUrl(String p) { this.pieceJointeUrl = p; }
}
