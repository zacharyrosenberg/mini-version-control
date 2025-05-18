package minivcs;

import java.security.MessageDigest;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

/**
 * Provides content-addressable storage for repository objects.
 * This class handles the core functionality of hashing and storing file
 * content,
 * similar to Git's object store.
 */
public class ObjectStore {

    /**
     * Calculates a SHA-1 hash for the contents of a file.
     * 
     * @param filepath Path to the file to hash
     * @return A string representation of the SHA-1 hash in hexadecimal
     * @throws IOException              If there's an error reading the file
     * @throws NoSuchAlgorithmException If SHA-1 algorithm is not available
     */
    public static String createHash(Path filepath) throws IOException, NoSuchAlgorithmException {
        // Initialize SHA-1 message digest
        MessageDigest digest = MessageDigest.getInstance("SHA-1");

        // Read file in chunks and update the digest
        try (InputStream input = Files.newInputStream(filepath)) {
            byte[] buffer = new byte[8192]; // 8KB buffer
            int bytesRead;

            while ((bytesRead = input.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }

        // Get the final hash and convert to hex string
        byte[] hashBytes = digest.digest();
        return bytesToHex(hashBytes);
    }

    /**
     * Calculates a SHA-1 hash for a string content.
     * 
     * @param object The string content to hash
     * @return A string representation of the SHA-1 hash in hexadecimal
     * @throws NoSuchAlgorithmException If SHA-1 algorithm is not available
     */
    public static String createHashFromString(String object) throws NoSuchAlgorithmException {
        // Initialize SHA-1 message digest
        MessageDigest digest = MessageDigest.getInstance("SHA-1");

        // Update digest with string bytes
        digest.update(object.getBytes(StandardCharsets.UTF_8));

        // Get hash and convert to hex string
        byte[] hashBytes = digest.digest();
        return bytesToHex(hashBytes);
    }

    /**
     * Stores a file in the object database using its content hash as identifier.
     * Files are stored in a two-level directory structure (like Git) where the
     * first
     * two characters of the hash are used as a directory name and the rest as the
     * filename.
     * 
     * @param filepath Path to the file to store
     * @param repoPath Path to the .minivcs directory
     * @return The hash of the file content
     * @throws IOException              If there's an error reading or writing the
     *                                  file
     * @throws NoSuchAlgorithmException If SHA-1 algorithm is not available
     */
    public static String saveObject(Path filepath, Path repoPath) throws IOException, NoSuchAlgorithmException {
        // Get the file's content hash
        String hash = createHash(filepath);

        // Use first 2 chars for directory name, rest for filename
        String directory = hash.substring(0, 2);
        String filename = hash.substring(2);

        // Construct the path where object will be stored
        Path objectPath = repoPath.resolve("objects").resolve(directory).resolve(filename);

        // Skip copying if object with same content already exists
        if (Files.exists(objectPath) && Files.size(objectPath) == Files.size(filepath)) {
            return hash;
        }

        // Create directory if needed and copy the file
        Files.createDirectories(objectPath.getParent());
        Files.copy(filepath, objectPath, StandardCopyOption.REPLACE_EXISTING);

        return hash;
    }

    /**
     * Stores a string content in the object database using its hash as identifier.
     * The string is stored in the same two-level directory structure used for
     * files.
     * 
     * @param object   The string content to store
     * @param repoPath Path to the .minivcs directory
     * @return The SHA-1 hash of the stored content
     * @throws IOException              If there's an error writing the string to
     *                                  storage
     * @throws NoSuchAlgorithmException If SHA-1 algorithm is not available
     */
    public static String saveObjectFromString(String object, Path repoPath)
            throws IOException, NoSuchAlgorithmException {
        // Get object hash
        String hash = createHashFromString(object);

        // Use first 2 chars for directory name, rest for file name
        String directory = hash.substring(0, 2);
        String filename = hash.substring(2);

        // Construct the path where object will be stored
        Path objectPath = repoPath.resolve("objects").resolve(directory).resolve(filename);

        // Create directory if needed and write the object to the file
        Files.createDirectories(objectPath.getParent());
        Files.writeString(objectPath, object);

        return hash;
    }

    /**
     * Retrieves an object from the repository by its hash.
     * 
     * @param repoPath Path to the .minivcs directory
     * @param hash     The SHA-1 hash of the object to retrieve
     * @return An InputStream to read the object's content
     * @throws IOException              If there's an error reading the file
     * @throws IllegalArgumentException If the hash is invalid or the object doesn't
     *                                  exist
     */
    public static InputStream getObject(Path repoPath, String hash) throws IOException {

        // Validate hash input - must be non-null and at least 3 chars for proper
        // splitting
        if (hash == null || hash.length() < 3) {
            throw new IllegalArgumentException("Invalid hash: " + hash);
        }

        // Split hash into directory (first 2 chars) and filename (remaining chars)
        String directory = hash.substring(0, 2);
        String filename = hash.substring(2);

        // Construct the full path to the object in storage
        Path objectPath = repoPath.resolve("objects").resolve(directory).resolve(filename);

        // Verify object exists before attempting to read it
        if (!Files.exists(objectPath)) {
            throw new IllegalArgumentException("Object not found: " + hash);
        }

        // Return an input stream to the object content
        // The caller is responsible for closing this stream
        return Files.newInputStream(objectPath);
    }

    /**
     * Converts a byte array to a hexadecimal string.
     * 
     * @param bytes The byte array to convert
     * @return A string with the hexadecimal representation
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0'); // Pad with leading zero if needed
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
