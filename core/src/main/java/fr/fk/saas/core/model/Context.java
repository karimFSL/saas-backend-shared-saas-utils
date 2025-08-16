package fr.fk.saas.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * @author Walid.BOUHADJI
 * @Created 27/06/2024
 */
@AllArgsConstructor
@Getter
@Builder
public class Context {

    private String tenantId;

    private String appId;

    private String serviceId;

    private String userId;

    private boolean isAdmin;

}
