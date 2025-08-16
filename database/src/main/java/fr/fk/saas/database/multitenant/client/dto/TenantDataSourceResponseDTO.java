package fr.fk.saas.database.multitenant.client.dto;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * TenantDataSourceResponseDTO
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class TenantDataSourceResponseDTO implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  private String component;

  private UUID datasourceId;

  private String type;

  private String host;

  private String name;

  private String schema;

  private String encryptedUsername;

  private String encryptedPassword;


}

