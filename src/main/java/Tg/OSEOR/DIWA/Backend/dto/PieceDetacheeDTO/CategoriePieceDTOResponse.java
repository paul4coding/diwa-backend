package Tg.OSEOR.DIWA.Backend.dto.PieceDetacheeDTO;

import java.time.LocalDateTime;

public class CategoriePieceDTOResponse {
    private Long id;
    private String uuid;
    private String libelle;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    public CategoriePieceDTOResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    public LocalDateTime getCreateDate() { return createDate; }
    public void setCreateDate(LocalDateTime createDate) { this.createDate = createDate; }

    public LocalDateTime getUpdateDate() { return updateDate; }
    public void setUpdateDate(LocalDateTime updateDate) { this.updateDate = updateDate; }
}
