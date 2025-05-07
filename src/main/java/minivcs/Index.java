package minivcs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.HashMap;
import java.security.NoSuchAlgorithmException;
import java.io.BufferedWriter;
import java.io.BufferedReader;

public class Index {

    private static final String INDEX_FILE = ".minivcs/index";

    private Map<String, IndexEntry> entries;

    public Index() {
        this.entries = new HashMap<>();
    }

    /**
     * Adds a file to the version control index.
     * This method calculates the file's metadata (size and modification time),
     * stores the file content in the object store, and creates an index entry.
     * 
     * @param repoPath The path to the repository root directory
     * @param filepath The path to the file being added
     * @throws IOException If the file doesn't exist or there's an error reading the
     *                     file
     */
    public void add(Path repoPath, Path filepath) throws IOException {
        try {
            // Ensure the file exists
            if (!Files.exists(filepath)) {
                throw new IOException("File not found: " + filepath);
            }

            // Calculate file metadata
            long fileSize = Files.size(filepath);
            long lastModified = Files.getLastModifiedTime(filepath).toMillis();

            // Save file to object store and get its hash
            String hash = ObjectStore.saveObject(filepath, repoPath.resolve(".minivcs"));

            // Create index entry
            Path relativePath = repoPath.relativize(filepath);
            IndexEntry entry = new IndexEntry(relativePath, hash, fileSize, lastModified);

            // Add to our entries map
            entries.put(relativePath.toString(), entry);

        } catch (NoSuchAlgorithmException e) {
            throw new IOException("Error calculating file hash", e);
        }
        // Save the index to disk
        save(repoPath);
    }

    /**
     * Saves the current index entries to the index file.
     * This writes all indexed files' information to the .minivcs/index file
     * for later reference.
     * 
     * @param repoPath The path to the repository root directory
     * @throws IOException If there's an error writing to the index file
     */
    public void save(Path repoPath) throws IOException {
        // Construct path to the index file
        Path indexPath = repoPath.resolve(INDEX_FILE);

        // Open a writer to the index file
        try (BufferedWriter writer = Files.newBufferedWriter(indexPath)) {
            // Write each entry to the file, one per line
            for (IndexEntry entry : entries.values()) {
                writer.write(entry.serialize());
                writer.newLine();
            }
        }
    }

    /**
     * Removes a file from the version control index.
     * This method stops tracking the specified file but does not delete
     * the actual file from the filesystem.
     * 
     * @param repoPath The path to the repository root directory
     * @param filepath The path to the file being removed from tracking
     * @throws IOException If there's an error writing to the index file during save
     */
    public void remove(Path repoPath, Path filepath) throws IOException {
        // Convert to path relative to repository root
        Path relativePath = repoPath.relativize(filepath);

        // Remove entry from tracking
        entries.remove(relativePath.toString());

        // Update the index file on disk
        save(repoPath);
    }

    /**
     * Loads index entries from the index file on disk.
     * This populates the entries map with information about tracked files.
     * 
     * @param repoPath The path to the repository root directory
     * @throws IOException If there's an error reading the index file
     */
    public void load(Path repoPath) throws IOException {
        // Clear existing entries
        entries.clear();

        Path indexPath = repoPath.resolve(INDEX_FILE);

        // Return instead of throwing exception if file doesn't exist
        if (!Files.exists(indexPath)) {
            return; // No index file yet, nothing to load
        }

        try (BufferedReader reader = Files.newBufferedReader(indexPath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    IndexEntry entry = IndexEntry.deserialize(line);
                    entries.put(entry.getRelativePath(), entry);
                } catch (Exception e) {
                    // Log error but continue processing other lines
                    System.err.println("Error parsing index entry: " + line + " - " + e.getMessage());
                }
            }
        }
    }

}
