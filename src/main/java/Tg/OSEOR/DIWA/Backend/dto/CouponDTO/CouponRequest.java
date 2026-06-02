package Tg.OSEOR.DIWA.Backend.dto.CouponDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CouponRequest {
    private String code;
    private String description;
    private BigDecimal valeur;
    private String typeRemise;          // POURCENTAGE | MONTANT_FIXE
    private Integer nbMaxUtilisations;
    private LocalDateTime dateExpiration;
    private Long proFormaId;            // null = coupon global

    public String getCode() { return code; }
    public void setCode(String c) { this.code = c; }
    public String getDescription() { return description; }
    public void setDescription(String d) { this.description = d; }
    public BigDecimal getValeur() { return valeur; }
    public void setValeur(BigDecimal v) { this.valeur = v; }
    public String getTypeRemise() { return typeRemise; }
    public void setTypeRemise(String t) { this.typeRemise = t; }
    public Integer getNbMaxUtilisations() { return nbMaxUtilisations; }
    public void setNbMaxUtilisations(Integer n) { this.nbMaxUtilisations = n; }
    public LocalDateTime getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(LocalDateTime d) { this.dateExpiration = d; }
    public Long getProFormaId() { return proFormaId; }
    public void setProFormaId(Long p) { this.proFormaId = p; }
}
