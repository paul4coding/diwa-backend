package Tg.OSEOR.DIWA.Backend.dto.DemandeDTO;

public class ReceptionRequest {
    private String vehiculeImmatriculation;
    private String vehiculeNumeroChassis;
    private Integer vehiculeKilometrage;

    public String getVehiculeImmatriculation() { return vehiculeImmatriculation; }
    public void setVehiculeImmatriculation(String v) { this.vehiculeImmatriculation = v; }
    public String getVehiculeNumeroChassis() { return vehiculeNumeroChassis; }
    public void setVehiculeNumeroChassis(String v) { this.vehiculeNumeroChassis = v; }
    public Integer getVehiculeKilometrage() { return vehiculeKilometrage; }
    public void setVehiculeKilometrage(Integer v) { this.vehiculeKilometrage = v; }
}
