package fr.fk.saas.database.multitenant.client.dto;


import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * ConfigDTO
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigDTO implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  private String tenantId;

  private String appId;

  private String serviceId;

  private TenantDataSourceResponseDTO dataSource;

}

