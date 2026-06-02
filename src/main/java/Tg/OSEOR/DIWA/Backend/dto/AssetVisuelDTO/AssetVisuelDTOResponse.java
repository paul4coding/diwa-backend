package Tg.OSEOR.DIWA.Backend.dto.AssetVisuelDTO;

import java.time.LocalDateTime;

public class AssetVisuelDTOResponse {
    private Long id;
    private String uuid;
    private String urlCloudinary;
    private String vue;
    private Integer zIndex;
    
    private Long optionId;
    private String optionNom;
    
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    public AssetVisuelDTOResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getUrlCloudinary() { return urlCloudinary; }
    public void setUrlCloudinary(String urlCloudinary) { this.urlCloudinary = urlCloudinary; }

    public String getVue() { return vue; }
    public void setVue(String vue) { this.vue = vue; }

    public Integer getzIndex() { return zIndex; }
    public void setzIndex(Integer zIndex) { this.zIndex = zIndex; }

    public Long getOptionId() { return optionId; }
    public void setOptionId(Long optionId) { this.optionId = optionId; }

    public String getOptionNom() { return optionNom; }
    public void setOptionNom(String optionNom) { this.optionNom = optionNom; }

    public LocalDateTime getCreateDate() { return createDate; }
    public void setCreateDate(LocalDateTime createDate) { this.createDate = createDate; }

    public LocalDateTime getUpdateDate() { return updateDate; }
    public void setUpdateDate(LocalDateTime updateDate) { this.updateDate = updateDate; }
}
