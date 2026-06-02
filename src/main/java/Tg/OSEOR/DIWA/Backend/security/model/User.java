package Tg.OSEOR.DIWA.Backend.security.model;

import java.util.HashSet;
import java.util.Set;

import Tg.OSEOR.DIWA.Backend.utils.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users", uniqueConstraints = {
	@UniqueConstraint(columnNames = "username"),
	@UniqueConstraint(columnNames = "email")
})
public class User extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	@Size(max = 100)
	private String username;
	
	@NotBlank
	@Size(max = 50)
	@Email
	private String email;
	
	@NotBlank
	@Size(max = 120)
	private String password;
	
	private String prenom;
	private String nom;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_roles", 
			   joinColumns = @JoinColumn(name = "user_id"),
			   inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

	@Column(nullable = false)
	private Boolean emailVerified = false;

	@Column(nullable = false)
	private Boolean mustResetPassword = false;

	@Column(nullable = false)
	private Boolean twoFactorEnabled = false;

	@Column(length = 255)
	private String expoPushToken;

	@Column(length = 20)
	private String telephone;

	private java.time.LocalTime heureDebutTravail = java.time.LocalTime.of(6, 0);
	private java.time.LocalTime heureFinTravail = java.time.LocalTime.of(18, 0);

    public User() {}

    public User(Long id, String username, String email, String password, Set<Role> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        if (roles != null) this.roles = roles;
    }
	
	public User(String username, String email, String password, String prenom, String nom) {
		this.username = username;
		this.email = email;
		this.password = password;
        this.prenom = prenom;
        this.nom = nom;
	}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

	public Boolean getEmailVerified() { return emailVerified; }
	public void setEmailVerified(Boolean emailVerified) { this.emailVerified = emailVerified; }

	public Boolean getMustResetPassword() { return mustResetPassword; }
	public void setMustResetPassword(Boolean mustResetPassword) { this.mustResetPassword = mustResetPassword; }

	public Boolean getTwoFactorEnabled() { return twoFactorEnabled; }
	public void setTwoFactorEnabled(Boolean twoFactorEnabled) { this.twoFactorEnabled = twoFactorEnabled; }

	public String getExpoPushToken() { return expoPushToken; }
	public void setExpoPushToken(String token) { this.expoPushToken = token; }

	public String getTelephone() { return telephone; }
	public void setTelephone(String t) { this.telephone = t; }

	public java.time.LocalTime getHeureDebutTravail() { return heureDebutTravail; }
	public void setHeureDebutTravail(java.time.LocalTime t) { this.heureDebutTravail = t; }

	public java.time.LocalTime getHeureFinTravail() { return heureFinTravail; }
	public void setHeureFinTravail(java.time.LocalTime t) { this.heureFinTravail = t; }
}
