package fr.fk.saas.security.model;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.Part;
import org.owasp.encoder.Encode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;


public class XSSRequestWrapper extends HttpServletRequestWrapper {

    /**
     * Default constructor
     *
     * @param request : the initial request
     */
    public XSSRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    /**
     * Utilité : Protège contre XSS dans les paramètres URL/form (?param=<script>alert('xss')</script>)
     *
     * @param parameter a <code>String</code> containing the name of the parameter whose value is requested
     *
     * @return
     */
    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (values == null) {
            return null;
        }
        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = sanitizeInput(values[i]);
        }
        return encodedValues;
    }

    /**
     * Utilité : Protège le JSON/body contre XSS
     *
     * @return
     * @throws IOException
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        // get body as input stream
        ServletInputStream originalInputStream = super.getInputStream();

        // wrap it in a string
        String requestBody = new String(originalInputStream.readAllBytes());

        // Sanitize the JSON body
        String sanitizedBody = sanitizeInput(requestBody);

        return new ServletInputStream() {
            private final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                    sanitizedBody.getBytes());

            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }

            @Override
            public void setReadListener(ReadListener listener) {
                // Instructs the ServletInputStream to invoke the provided ReadListener when it
                // is possible to read
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }
        };
    }

    /**
     Récupère la part originale
     Sanitize le nom du fichier → évite XSS dans les noms de fichiers
     Si contenu JSON → sanitize le contenu aussi
     Sinon → garde le contenu binaire tel quel
     Retourne un wrapper avec le contenu sanitized
     *
     * @param name the name of the requested <code>Part</code>
     *
     * @return
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public Part getPart(String name) throws IOException, ServletException {
        Part part = super.getPart(name);
        String filename = part.getSubmittedFileName();
        String sanitzedFileName = sanitizeInput(filename);
        if (part.getContentType() != null && part.getContentType().startsWith("application/json")) {
            String value = new String(part.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            String sanitizedValue = sanitizeInput(value);
            return new XSSPartWrapper(part, sanitzedFileName, sanitizedValue.getBytes(StandardCharsets.UTF_8));
        } else {
            return new XSSPartWrapper(part, sanitzedFileName, part.getInputStream().readAllBytes());
        }
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        Collection<Part> parts = super.getParts();
        Collection<Part> sanitizedParts = new ArrayList<>();

        for (Part part : parts) {
            var filename = part.getSubmittedFileName();
            var sanitizedFileName = this.sanitizeInput(filename);

            if (part.getContentType() != null && part.getContentType().startsWith("application/json")) {
                var value = new String(part.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                var sanitizedValue = this.sanitizeInput(value);
                sanitizedParts.add(new XSSPartWrapper(part, sanitizedFileName,
                        sanitizedValue.getBytes(StandardCharsets.UTF_8)));
            } else {
                sanitizedParts.add(new XSSPartWrapper(part, sanitizedFileName, part.getInputStream().readAllBytes()));
            }
        }

        return sanitizedParts;
    }

    /**
     * Encodes for HTML text content such as '<', '>' , '&', by escaping quotation
     * characters necessary for jackson deserializing process
     *
     * @param input: the input
     * @return the encoded input
     */
    private String sanitizeInput(String input) {
        return Encode.forHtmlContent(input);

    }

}
