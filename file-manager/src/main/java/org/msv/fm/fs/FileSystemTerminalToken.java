package org.msv.fm.fs;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Base64;


/**
 * Токен, для идентификации сессии файлового терминала
 */
public class FileSystemTerminalToken implements Serializable {

    private final String token;


    public FileSystemTerminalToken() {
        SecureRandom secureRandom = new SecureRandom();
        Base64.Encoder base64Encoder = Base64.getUrlEncoder();

        byte[] randomBytes = new byte[8];
        secureRandom.nextBytes(randomBytes);

        token = base64Encoder.encodeToString(randomBytes);
    }


    @Override
    public String toString() {
        return token;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj instanceof FileSystemTerminalToken fileSystemToken) {
            return this.token.equals(fileSystemToken.token);
        }

        return false;
    }
}

