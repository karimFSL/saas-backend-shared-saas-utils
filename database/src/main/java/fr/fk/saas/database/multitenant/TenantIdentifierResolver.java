package fr.fk.saas.database.multitenant;


import fr.fk.saas.core.model.Context;
import fr.fk.saas.core.model.ContextHolder;
import org.hibernate.context.TenantIdentifierMismatchException;
import org.hibernate.context.spi.CurrentSessionContext;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver<String> {


    /**
     * Resolve the current tenant identifier.
     *
     * @return The current tenant identifier
     */
    @Override
    public String resolveCurrentTenantIdentifier() {
        Context currentContext = ContextHolder.getCurrentContext();
        return currentContext.getTenantId();
    }

    /**
     * Should we validate that the tenant identifier of a "current sessions" that
     * already exists when {@link CurrentSessionContext#currentSession()} is called
     * matches the value returned here from {@link #resolveCurrentTenantIdentifier()}?
     *
     * @return {@code true} indicates that the extra validation will be performed;
     * {@code false} indicates it will not.
     * @see TenantIdentifierMismatchException
     */
    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }

    /**
     * Does the given tenant id represent a "root" tenant with access to all partitions?
     *
     * @param tenantId a tenant id produced by {@link #resolveCurrentTenantIdentifier()}
     * @return true is this is root tenant
     */
    @Override
    public boolean isRoot(String tenantId) {
        Context currentContext = ContextHolder.getCurrentContext();
        return currentContext.isAdmin();
    }


}
