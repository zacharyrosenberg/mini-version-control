package minivcs;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.nio.file.Path;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Tree {

    // List of entries in the tree
    private final List<TreeEntry> entries;

    // Hash of this tree object
    private final String hash;

    public Tree(List<TreeEntry> entries, String hash) {
        this.entries = entries;
        this.hash = hash;
    }

    public String getHash() {
        return hash;
    }

    public List<TreeEntry> getEntries() {
        return entries;
    }

    public static Tree fromIndex(Path repoPath, Map<String, IndexEntry> indexEntries)
            throws IOException, NoSuchAlgorithmException {

        Map<String, List<IndexEntry>> entriesByDirectory = new HashMap<>();

        for (Map.Entry<String, IndexEntry> entry : indexEntries.entrySet()) {
            Path filePath = Path.of(entry.getKey());
            String directory = filePath.getParent() == null ? "" : filePath.getParent().toString();

            if (!entriesByDirectory.containsKey(directory)) {
                entriesByDirectory.put(directory, new ArrayList<>());
            }
            entriesByDirectory.get(directory).add(entry.getValue());

        }
        return null;

    }

    // Inner class representing an entry in the tree
    public static class TreeEntry {
        private final String name; // Name of the file or directory
        private final String hash; // Hash of the content
        private final String mode; // File permissions
        private final EntryType type; // BLOB or TREE

        // Enum representing the type of entry
        public enum EntryType {
            BLOB,
            TREE
        }

        public TreeEntry(String name, String hash, String mode, EntryType type) {
            this.name = name;
            this.hash = hash;
            this.mode = mode;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getHash() {
            return hash;
        }

        public String getMode() {
            return mode;
        }

        public EntryType getType() {
            return type;
        }
    }
}
