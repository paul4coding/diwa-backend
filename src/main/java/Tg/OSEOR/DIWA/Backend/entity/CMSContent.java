package Tg.OSEOR.DIWA.Backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cms_contents")
public class CMSContent {
    @Id
    @Column(name = "content_key")
    private String contentKey;

    @Column(name = "json_content", columnDefinition = "TEXT")
    private String jsonContent;

    public CMSContent() {}
    public CMSContent(String contentKey, String jsonContent) {
        this.contentKey = contentKey;
        this.jsonContent = jsonContent;
    }

    public String getContentKey() { return contentKey; }
    public void setContentKey(String contentKey) { this.contentKey = contentKey; }

    public String getJsonContent() { return jsonContent; }
    public void setJsonContent(String jsonContent) { this.jsonContent = jsonContent; }
}
