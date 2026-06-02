package Tg.OSEOR.DIWA.Backend.dto.CouponDTO;

public class AppliquerCouponRequest {
    private Long proFormaId;
    private String code;

    public Long getProFormaId() { return proFormaId; }
    public void setProFormaId(Long p) { this.proFormaId = p; }
    public String getCode() { return code; }
    public void setCode(String c) { this.code = c; }
}
