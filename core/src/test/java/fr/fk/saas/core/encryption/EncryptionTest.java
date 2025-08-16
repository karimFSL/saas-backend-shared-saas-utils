package fr.fk.saas.core.encryption;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class EncryptionTest {

    /**
     *
     */
    private static final String SECRET_KEY = "B4AB1EE597A058952091DB2299968D107AFA08CEB0568BCF2789601C409FD901";

    @Test
    void testEncryptionDecryption() {
        String plainText = "2AFD0FE5A241DF267092F519FF014C82F55912228FF7E39805BE322B269EDF43";
        String encryptedText = Encryption.encrypt(plainText, SECRET_KEY);
        System.out.println("Encrypted key = " + encryptedText);
        String decryptedText = Encryption.decrypt(encryptedText, SECRET_KEY);
        System.out.println("Decrypted key = " + decryptedText);
        assertEquals(plainText, decryptedText);
    }

}
