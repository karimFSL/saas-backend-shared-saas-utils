package fr.fk.saas.core.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContextHolder {

    private static final String UNKNOWN = "UNKNOWN";

    /**
     * Thread local that holds the current context
     * We use here an inheritable thread local so that every child thread created from the main
     * to inherit the context as well.
     */
    private static final ThreadLocal<Context> contextHolderTL = new InheritableThreadLocal<>();

    /**
     * Get current context
     *
     * @return context
     */
    public static Context getCurrentContext() {
        return contextHolderTL.get() != null ? contextHolderTL.get() : Context.builder().build();
    }

    /**
     * Set current context
     *
     * @param context: the current context
     */
    public static void setCurrentContext(Context context) {
        contextHolderTL.set(context);
    }

    /**
     * clear context
     */
    public static void clear() {
        contextHolderTL.remove();
    }
}
