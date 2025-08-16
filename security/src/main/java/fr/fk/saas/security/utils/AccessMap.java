package fr.fk.saas.security.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static java.util.Collections.emptyList;


public class AccessMap {
    private final Map<RequestMatcher, List<String>> access;

    public AccessMap() {
        access = new HashMap<>();
    }

    public void put(RequestMatcher  key, List<String> value) {
        access.put(key, value);
    }


    public List<String> get(HttpServletRequest request) {
        for (var entry : access.entrySet()) {
            var matcher = entry.getKey();
            if (matcher.matches(request)) {
                return this.access.get(matcher);
            }
        }

        return emptyList();
    }

    /**
     * Vérifie si une requête correspond à une règle d'accès définie.
     *
     * @return true si une règle correspond, false sinon
     */
    public boolean hasMatch(HttpServletRequest request) {
        return access.keySet().stream()
                .anyMatch(matcher -> matcher.matches(request));
    }


    /**
     * Retourne le nombre de règles d'accès configurées.
     *
     * @return le nombre de règles
     */
    public int size() {
        return access.size();
    }

    /**
     * Vide toutes les règles d'accès.
     */
    public void clear() {
        access.clear();
    }
}
