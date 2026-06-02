package Tg.OSEOR.DIWA.Backend.utils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Column(name="uuid", nullable=false, unique=true, updatable=false)
	private String uuid = UUID.randomUUID().toString();
	
	@CreatedDate
	@Column(updatable = false, nullable = true)
	private LocalDateTime createDate;
	
	@LastModifiedDate
	private LocalDateTime updateDate;
	
	@CreatedBy
	private String createBy;
	
	@LastModifiedBy
	private String updateBy;
	
	public BaseEntity() {
		super();
	}

	public String getUuid() { return uuid; }
	public void setUuid(String uuid) { this.uuid = uuid; }
	
	public LocalDateTime getCreateDate() { return createDate; }
	public void setCreateDate(LocalDateTime createDate) { this.createDate = createDate; }
	
	public LocalDateTime getUpdateDate() { return updateDate; }
	public void setUpdateDate(LocalDateTime updateDate) { this.updateDate = updateDate; }
	
	public String getCreateBy() { return createBy; }
	public void setCreateBy(String createBy) { this.createBy = createBy; }
	
	public String getUpdateBy() { return updateBy; }
	public void setUpdateBy(String updateBy) { this.updateBy = updateBy; }
}
