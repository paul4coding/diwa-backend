package Tg.OSEOR.DIWA.Backend.dto;

public class ContactRequest {
    private String nom;
    private String email;
    private String telephone;
    private String message;

    public ContactRequest() {}

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
