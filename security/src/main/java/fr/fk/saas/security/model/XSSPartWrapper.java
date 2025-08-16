package fr.fk.saas.security.model;

import jakarta.servlet.http.Part;
import lombok.AllArgsConstructor;

import java.io.*;
import java.util.Collection;


@AllArgsConstructor
public class XSSPartWrapper implements Part {

    private final Part originalPart;

    private final String filename;

    private final byte[] content;


    @Override
    public InputStream getInputStream() throws IOException {
        if(content != null){
            return new ByteArrayInputStream(content);
        } else {
            return originalPart.getInputStream();
        }
    }

    @Override
    public String getContentType() {
        return originalPart.getContentType();
    }

    @Override
    public String getName() {
        return originalPart.getName();
    }

    @Override
    public String getSubmittedFileName() {
        return filename;
    }

    @Override
    public long getSize() {
        return content.length;
    }

    @Override
    public void write(String fileName) throws IOException {
        try (OutputStream os = new FileOutputStream(fileName)) {
            os.write(content);
        }
    }

    @Override
    public void delete() throws IOException {
        originalPart.delete();
    }

    @Override
    public String getHeader(String name) {
        return originalPart.getHeader(name);
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return originalPart.getHeaders(name);
    }

    @Override
    public Collection<String> getHeaderNames() {
        return originalPart.getHeaderNames();
    }
}

