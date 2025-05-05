package minivcs;

import java.security.MessageDigest;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;

public class ObjectStore {

    public static String createHash(Path filepath) throws IOException, NoSuchAlgorithmException {

        MessageDigest digest = MessageDigest.getInstance("SHA-1");

        try (InputStream input = Files.newInputStream(filepath)) {
            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = input.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }

        byte[] hashBytes = digest.digest();
        return bytesToHex(hashBytes);
    }

    public static String saveObject(Path filepath, Path repoPath) throws IOException, NoSuchAlgorithmException {

        String hash = createHash(filepath);

        Path objectPath = Path.of(repoPath.toString(), "objects", hash.substring(0, 2), hash.substring(2));

        Files.createDirectories(objectPath.getParent());
        Files.copy(filepath, objectPath, StandardCopyOption.REPLACE_EXISTING);

        return hash;

    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
