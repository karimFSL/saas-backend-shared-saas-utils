package fr.fk.saas.core.encryption;

import org.jasypt.util.text.BasicTextEncryptor;

import java.util.Base64;

public class Encryption {

    private Encryption() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Crypte une chaîne de caractères
     *
     * @param plainText           chaine à décrypter
     * @param key clé de décryptage
     * @return chaîne décryptée
     */
    public static String encrypt(String plainText, String key) {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(key);
        return textEncryptor.encrypt(plainText);
    }

    /**
     * Décrypte une chaîne de caractères
     *
     * @param encryptedText           chaine à décrypter
     * @param key clé de décryptage
     * @return chaîne décryptée
     */
    public static String decrypt(String encryptedText, String key) {

        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(key);
        return textEncryptor.decrypt(encryptedText);
    }

    /**
     * Encode basic auth credentials
     *
     * @param username the username
     * @param password the password
     * @return the header value for basic authentication
     */
    public static String encodeBasicAuthentCredentials(String username, String password) {
        // Set up basic authentication credentials
        String authHeader = username + ":" + password;
        byte[] authHeaderBytes = authHeader.getBytes();
        return Base64.getEncoder().encodeToString(authHeaderBytes);
    }


}
