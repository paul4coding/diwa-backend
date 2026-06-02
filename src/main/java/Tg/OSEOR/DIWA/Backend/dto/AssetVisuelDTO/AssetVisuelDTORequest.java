package Tg.OSEOR.DIWA.Backend.dto.AssetVisuelDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AssetVisuelDTORequest {
    
    @NotBlank(message = "L'URL Cloudinary est obligatoire")
    private String urlCloudinary;
    
    @NotBlank(message = "La vue est obligatoire (ex: FACE, PROFIL, HABITACLE)")
    private String vue;
    
    private Integer zIndex = 0;
    
    @NotNull(message = "L'ID de l'option associée est obligatoire")
    private Long optionId;

    public AssetVisuelDTORequest() {}

    public String getUrlCloudinary() { return urlCloudinary; }
    public void setUrlCloudinary(String urlCloudinary) { this.urlCloudinary = urlCloudinary; }

    public String getVue() { return vue; }
    public void setVue(String vue) { this.vue = vue; }

    public Integer getzIndex() { return zIndex; }
    public void setzIndex(Integer zIndex) { this.zIndex = zIndex; }

    public Long getOptionId() { return optionId; }
    public void setOptionId(Long optionId) { this.optionId = optionId; }
}
