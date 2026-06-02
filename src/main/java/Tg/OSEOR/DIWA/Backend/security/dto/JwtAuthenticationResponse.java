package Tg.OSEOR.DIWA.Backend.security.dto;

import java.util.List;

public class JwtAuthenticationResponse {
	private String token;
	private String type = "Bearer";
	private Long id;
	private String username;
	private String email;
	private List<String> roles;
	private String prenom;
	private String nom;
    private UserResponse user;
	private Boolean mustResetPassword;

	public JwtAuthenticationResponse(String accessToken, Long id, String username, String email, String prenom, String nom, List<String> roles, Boolean mustResetPassword) {
		this.token = accessToken;
		this.id = id;
		this.username = username;
		this.email = email;
		this.prenom = prenom;
		this.nom = nom;
		this.roles = roles;
		this.mustResetPassword = mustResetPassword;
        this.user = new UserResponse(id, username, email, prenom, nom, roles);
	}

    public static class UserResponse {
        private Long id;
        private String username;
        private String email;
        private String prenom;
        private String nom;
        private List<String> roles;

        public UserResponse(Long id, String username, String email, String prenom, String nom, List<String> roles) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.prenom = prenom;
            this.nom = nom;
            this.roles = roles;
        }

        public Long getId() { return id; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getPrenom() { return prenom; }
        public String getNom() { return nom; }
        public List<String> getRoles() { return roles; }
    }

    public UserResponse getUser() { return user; }
    public void setUser(UserResponse user) { this.user = user; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

	public Boolean getMustResetPassword() { return mustResetPassword; }
	public void setMustResetPassword(Boolean mustResetPassword) { this.mustResetPassword = mustResetPassword; }
}
