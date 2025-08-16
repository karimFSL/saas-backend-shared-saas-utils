package fr.fk.saas.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


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
