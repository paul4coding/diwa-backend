package Tg.OSEOR.DIWA.Backend.dto.CouponDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CouponResponse {
    private Long id;
    private String code;
    private String description;
    private BigDecimal valeur;
    private String typeRemise;
    private int nbMaxUtilisations;
    private int nbUtilisations;
    private LocalDateTime dateExpiration;
    private boolean actif;
    private Long proFormaId;
    private String createurNom;

    // Champs calculés à l'affichage
    private boolean expire;
    private boolean epuise;
    private boolean valide;

    // Résultat de l'application (renseigné lors d'un PUT appliquer)
    private BigDecimal montantRemise;
    private BigDecimal totalApresRemise;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String c) { this.code = c; }
    public String getDescription() { return description; }
    public void setDescription(String d) { this.description = d; }
    public BigDecimal getValeur() { return valeur; }
    public void setValeur(BigDecimal v) { this.valeur = v; }
    public String getTypeRemise() { return typeRemise; }
    public void setTypeRemise(String t) { this.typeRemise = t; }
    public int getNbMaxUtilisations() { return nbMaxUtilisations; }
    public void setNbMaxUtilisations(int n) { this.nbMaxUtilisations = n; }
    public int getNbUtilisations() { return nbUtilisations; }
    public void setNbUtilisations(int n) { this.nbUtilisations = n; }
    public LocalDateTime getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(LocalDateTime d) { this.dateExpiration = d; }
    public boolean isActif() { return actif; }
    public void setActif(boolean a) { this.actif = a; }
    public Long getProFormaId() { return proFormaId; }
    public void setProFormaId(Long p) { this.proFormaId = p; }
    public String getCreateurNom() { return createurNom; }
    public void setCreateurNom(String c) { this.createurNom = c; }
    public boolean isExpire() { return expire; }
    public void setExpire(boolean e) { this.expire = e; }
    public boolean isEpuise() { return epuise; }
    public void setEpuise(boolean e) { this.epuise = e; }
    public boolean isValide() { return valide; }
    public void setValide(boolean v) { this.valide = v; }
    public BigDecimal getMontantRemise() { return montantRemise; }
    public void setMontantRemise(BigDecimal m) { this.montantRemise = m; }
    public BigDecimal getTotalApresRemise() { return totalApresRemise; }
    public void setTotalApresRemise(BigDecimal t) { this.totalApresRemise = t; }
}
