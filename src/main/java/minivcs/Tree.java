package minivcs;

import java.util.List;

public class Tree {

    // List of entries in the tree
    private List<TreeEntry> entries;

    // Hash of this tree object
    private String hash;

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

    // Inner class representing an entry in the tree
    public static class TreeEntry {
        private String name; // Name of the file or directory
        private String hash; // Hash of the content
        private String mode; // File permissions
        private EntryType type; // BLOB or TREE

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
