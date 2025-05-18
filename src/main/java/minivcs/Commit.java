package minivcs;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.nio.file.Path;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Commit {

    private final String parentHash;
    private final String treeHash;
    private final String author;
    private final String timestamp;
    private final String message;
    private final String commitHash;

    private Commit(String parentHash, String treeHash, String author, String timestamp, String message,
            String commitHash) throws IOException, NoSuchAlgorithmException {
        this.parentHash = parentHash;
        this.treeHash = treeHash;
        this.author = author;
        this.timestamp = timestamp;
        this.message = message;
        this.commitHash = commitHash;
    }

    public static Commit createCommit(String parentHash, String treeHash, String author, String timestamp,
            String message, Path repoPath) throws IOException, NoSuchAlgorithmException {
        // Create serialized representation of the commit
        String serializedCommit = parentHash + "\n" +
                treeHash + "\n" +
                author + "\n" +
                timestamp + "\n" +
                message;

        // Store the commit in the object store and get hash
        String commitHash = ObjectStore.saveObjectFromString(serializedCommit, repoPath);

        // Create and return the commit object
        return new Commit(parentHash, treeHash, author, timestamp, message, commitHash);
    }

    public static Commit loadCommit(String commitHash, Path repoPath) throws IOException, NoSuchAlgorithmException {
        // Load the commit from object store
        try (InputStream in = ObjectStore.getObject(repoPath.resolve(".minivcs"), commitHash);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {

            // Read the serialized commit
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            String serializedCommit = content.toString().trim();

            // Parse the serialized commit
            String[] parts = serializedCommit.split("\n");
            if (parts.length < 5) {
                throw new IOException("Invalid commit format");
            }

            // Handle multi-line commit messages
            String message = parts[4];
            for (int i = 5; i < parts.length; i++) {
                message += "\n" + parts[i];
            }

            // Create and return the commit object
            return new Commit(parts[0], parts[1], parts[2], parts[3], message, commitHash);
        }
    }

    public String getCommitHash() {
        return commitHash;
    }

    public String getParentHash() {
        return parentHash;
    }

    public String getTreeHash() {
        return treeHash;
    }

    public String getAuthor() {
        return author;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }
}
