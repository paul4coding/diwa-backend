package Tg.OSEOR.DIWA.Backend.dto.MissionChauffeurDTO;

import jakarta.validation.constraints.NotBlank;

public class ReceptionRequest {
    @NotBlank(message = "La photo avant est obligatoire")
    private String photoAvantUrl;
    
    @NotBlank(message = "La photo arrière est obligatoire")
    private String photoArriereUrl;
    
    @NotBlank(message = "La photo gauche est obligatoire")
    private String photoGaucheUrl;
    
    @NotBlank(message = "La photo droite est obligatoire")
    private String photoDroitUrl;
    
    private String observations;

    public ReceptionRequest() {}

    public String getPhotoAvantUrl() { return photoAvantUrl; }
    public void setPhotoAvantUrl(String s) { this.photoAvantUrl = s; }

    public String getPhotoArriereUrl() { return photoArriereUrl; }
    public void setPhotoArriereUrl(String s) { this.photoArriereUrl = s; }

    public String getPhotoGaucheUrl() { return photoGaucheUrl; }
    public void setPhotoGaucheUrl(String s) { this.photoGaucheUrl = s; }

    public String getPhotoDroitUrl() { return photoDroitUrl; }
    public void setPhotoDroitUrl(String s) { this.photoDroitUrl = s; }

    public String getObservations() { return observations; }
    public void setObservations(String s) { this.observations = s; }
}
