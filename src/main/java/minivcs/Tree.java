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

    public static Tree createTreeFromIndex(Path repoPath, Map<String, IndexEntry> indexEntries) {
        // Create a directory structure map. Key is directory path, value is list of
        // files in that directory
        Map<String, List<IndexEntry>> directoryStructure = new HashMap<>();

        // Group files by directory
        for (Map.Entry<String, IndexEntry> entry : indexEntries.entrySet()) {
            String path = entry.getKey();
            String directory = path.contains("/") ? path.substring(0, path.lastIndexOf('/')) : "";

            // Add directory to map if it doesn't exist
            if (!directoryStructure.containsKey(directory)) {
                directoryStructure.put(directory, new ArrayList<>());
            }
            // Add file to directory
            directoryStructure.get(directory).add(entry.getValue());

        }
        try {
            return buildTree(repoPath, "", directoryStructure);
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to create tree from index", e);
        }
    }

    private static Tree buildTree(Path repoPath, String dirPath, Map<String, List<IndexEntry>> directoryStructure)
            throws IOException, NoSuchAlgorithmException {
        // List to store tree entries
        List<TreeEntry> treeEntries = new ArrayList<>();

        // Process files in current directory
        List<IndexEntry> filesInDir = directoryStructure.get(dirPath);
        if (filesInDir != null) {
            for (IndexEntry indexEntry : filesInDir) {
                // Extract file name from path
                String fullPath = indexEntry.getRelativePath();
                String fileName = fullPath.contains("/") ? fullPath.substring(fullPath.lastIndexOf('/') + 1) : fullPath;

                TreeEntry fileEntry = new TreeEntry(
                        fileName,
                        indexEntry.getHash(),
                        Integer.toString(IndexEntry.REGULAR_FILE),
                        TreeEntry.EntryType.BLOB);

                treeEntries.add(fileEntry);
            }
        }

        // Process subdirectories recursively - MOVED OUTSIDE the file loop
        for (String subDir : directoryStructure.keySet()) {
            // Check if subdir is a direct child of current dirPath
            if (!subDir.equals(dirPath) &&
                    (dirPath.isEmpty() && !subDir.contains("/") ||
                            !dirPath.isEmpty() && subDir.startsWith(dirPath + "/") &&
                                    subDir.substring(dirPath.length() + 1).indexOf("/") == -1)) {
                // Get subdirectory name
                String subDirName = dirPath.isEmpty() ? subDir : subDir.substring(dirPath.length() + 1);

                // Recursively build tree for subdirectory
                Tree subTree = buildTree(repoPath, subDir, directoryStructure);

                // Add entry for this subdirectory
                TreeEntry dirEntry = new TreeEntry(
                        subDirName,
                        subTree.getHash(),
                        "040000", // Directory mode
                        TreeEntry.EntryType.TREE);

                treeEntries.add(dirEntry);
            }
        }

        // Serialize tree to string for storage
        StringBuilder serialized = new StringBuilder();
        for (TreeEntry entry : treeEntries) {
            serialized.append(entry.getMode())
                    .append(" ")
                    .append(entry.getType().name().toLowerCase())
                    .append(" ")
                    .append(entry.getHash())
                    .append("\t")
                    .append(entry.getName())
                    .append("\n");
        }

        // Store tree in object store and get its hash
        String treeHash = ObjectStore.saveObjectFromString(
                serialized.toString(),
                repoPath.resolve(".minivcs"));

        // Return new Tree with entries and hash
        return new Tree(treeEntries, treeHash);
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
