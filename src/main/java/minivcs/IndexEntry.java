package minivcs;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Represents an entry in the index (staging area) of the version control
 * system.
 * Each entry contains metadata about a file including its path, content hash,
 * and file attributes.
 */
public class IndexEntry {

    private final String relativePath;

    private final String hash;

    private final long fileSize;

    private final long lastModified;

    private final int fileMode;

    /**
     * Standard file mode for regular, non-executable files (equivalent to 100644 in
     * Git)
     */
    public static final int REGULAR_FILE = 0100644;

    /**
     * Creates a new index entry with all metadata specified.
     *
     * @param filepath     The path to the file (will be stored as a relative path
     *                     string)
     * @param hash         The content hash of the file
     * @param fileSize     The size of the file in bytes
     * @param lastModified The last modified timestamp of the file
     * @param fileMode     The file mode/permissions
     * @throws NullPointerException if filepath or hash is null
     */
    public IndexEntry(Path filepath, String hash, long fileSize, long lastModified, int fileMode) {
        Objects.requireNonNull(filepath, "File path cannot be null");
        Objects.requireNonNull(hash, "Hash cannot be null");

        this.relativePath = filepath.toString();
        this.hash = hash;
        this.fileSize = fileSize;
        this.lastModified = lastModified;
        this.fileMode = fileMode;
    }

    /**
     * Convenience constructor that uses the default REGULAR_FILE mode.
     *
     * @param filepath     The path to the file
     * @param hash         The content hash of the file
     * @param fileSize     The size of the file in bytes
     * @param lastModified The last modified timestamp of the file
     * @throws NullPointerException if filepath or hash is null
     */
    public IndexEntry(Path filepath, String hash, long fileSize, long lastModified) {
        this(filepath, hash, fileSize, lastModified, REGULAR_FILE);
    }

    /**
     * Gets the relative path of this entry.
     * 
     * @return The relative path as a string
     */
    public String getRelativePath() {
        return relativePath;
    }

    /**
     * Gets the content hash of this entry.
     * 
     * @return The SHA-1 hash as a string
     */
    public String getHash() {
        return hash;
    }

    /**
     * Gets the file size in bytes.
     * 
     * @return The file size
     */
    public long getFileSize() {
        return fileSize;
    }

    /**
     * Gets the last modified timestamp.
     * 
     * @return The last modified time in milliseconds since epoch
     */
    public long getLastModified() {
        return lastModified;
    }

    /**
     * Gets the file mode/permissions.
     * 
     * @return The file mode as an integer
     */
    public int getFileMode() {
        return fileMode;
    }

    /**
     * Serializes this entry to a string format for storage.
     * Format: relativePath|hash|fileSize|lastModified|fileMode
     * 
     * @return A string representation that can be stored in the index file
     */
    public String serialize() {
        return String.format("%s|%s|%d|%d|%d", relativePath, hash, fileSize, lastModified, fileMode);
    }

    /**
     * Creates an IndexEntry from a serialized string.
     * 
     * @param serialized The string in the format:
     *                   relativePath|hash|fileSize|lastModified|fileMode
     * @return A new IndexEntry created from the serialized data
     * @throws IllegalArgumentException if the string format is invalid
     * @throws NumberFormatException    if numeric fields cannot be parsed
     */
    public static IndexEntry deserialize(String serialized) {
        Objects.requireNonNull(serialized, "Serialized string cannot be null");

        String[] parts = serialized.split("\\|");

        if (parts.length != 5) {
            throw new IllegalArgumentException("Invalid serialized index entry");
        }

        String path = parts[0];
        String hash = parts[1];
        long fileSize = Long.parseLong(parts[2]);
        long lastModified = Long.parseLong(parts[3]);
        int fileMode = Integer.parseInt(parts[4]);

        return new IndexEntry(Path.of(path), hash, fileSize, lastModified, fileMode);
    }

    /**
     * Returns a string representation of this index entry.
     * 
     * @return A human-readable representation of this entry
     */
    @Override
    public String toString() {
        return String.format("IndexEntry{path='%s', hash='%s', size=%d, modified=%d, mode=%o}",
                relativePath, hash, fileSize, lastModified, fileMode);
    }

    /**
     * Determines if this index entry is equal to another object.
     * Two index entries are considered equal if they have the same path, hash,
     * last modified time, and file mode.
     * 
     * @param o The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        IndexEntry that = (IndexEntry) o;
        return lastModified == that.lastModified &&
                fileSize == that.fileSize &&
                fileMode == that.fileMode &&
                Objects.equals(relativePath, that.relativePath) &&
                Objects.equals(hash, that.hash);
    }

    /**
     * Computes the hash code for this index entry.
     * The hash code is based on all fields to ensure it's consistent with equals().
     * 
     * @return The hash code value for this entry
     */
    @Override
    public int hashCode() {
        return Objects.hash(relativePath, hash, fileSize, lastModified, fileMode);
    }

}
