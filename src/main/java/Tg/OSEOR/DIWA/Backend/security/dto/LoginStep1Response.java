package Tg.OSEOR.DIWA.Backend.security.dto;

public class LoginStep1Response {
    private String email;
    private String message;
    private Boolean mustResetPassword;
    private Boolean twoFactorRequired;

    public LoginStep1Response(String email, String message, Boolean mustResetPassword, Boolean twoFactorRequired) {
        this.email = email;
        this.message = message;
        this.mustResetPassword = mustResetPassword;
        this.twoFactorRequired = twoFactorRequired;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Boolean getMustResetPassword() { return mustResetPassword; }
    public void setMustResetPassword(Boolean mustResetPassword) { this.mustResetPassword = mustResetPassword; }

    public Boolean getTwoFactorRequired() { return twoFactorRequired; }
    public void setTwoFactorRequired(Boolean twoFactorRequired) { this.twoFactorRequired = twoFactorRequired; }
}
