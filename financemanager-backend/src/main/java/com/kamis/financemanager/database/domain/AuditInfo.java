package com.kamis.financemanager.database.domain;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class AuditInfo {
	@Column(name="last_update_by")
	private String lastUpdateBy;
	
	@Column(name="last_update_dt")
	private Date lastUpdateDt;
	
	@Column(name="create_dt")
	private Date createDt;
}
