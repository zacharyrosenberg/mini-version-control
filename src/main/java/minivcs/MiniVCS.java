package minivcs;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.security.NoSuchAlgorithmException;

/**
 * Main class for the MiniVCS version control system.
 * This class provides the command-line interface and routes commands
 * to the appropriate handlers.
 */
public class MiniVCS {
    /**
     * Entry point for the MiniVCS application.
     * Parses command-line arguments and routes commands to their handlers.
     * 
     * @param args Command-line arguments, where the first argument is the command
     *             and subsequent arguments are command-specific parameters
     */
    public static void main(String[] args) {
        // Check if any arguments are provided
        if (args.length == 0) {
            System.out.println("No arguments provided");
            printUsage();
            return;
        }

        // Get the command
        String command = args[0];

        // Extract remaining arguments to pass to the command handler
        // This creates a new array without the first element (command)
        String[] commandArgs = new String[args.length - 1];
        for (int i = 0; i < commandArgs.length; i++) {
            commandArgs[i] = args[i + 1];
        }

        // Alternatively, you could use Arrays.copyOfRange if you import
        // java.util.Arrays:
        // String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);

        try {
            // Route command to appropriate handler
            switch (command) {
                case "init":
                    initCommand(commandArgs);
                    break;
                case "add":
                    addCommand(commandArgs);
                    break;
                case "rm":
                    rmCommand(commandArgs);
                    break;
                case "status":
                    statusCommand(commandArgs);
                    break;
                case "commit":
                    commitCommand(commandArgs);
                    break;
                case "log":
                    logCommand(commandArgs);
                    break;
                case "diff":
                    diffCommand(commandArgs);
                    break;
                default:
                    System.out.println("Invalid command");
                    printUsage();
                    break;
            }
        } catch (Exception e) {
            // Basic error handling
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Displays usage information, listing all available commands and their syntax.
     */
    private static void printUsage() {
        System.out.println("Usage: minivcs <command> [<args>]");
        System.out.println("Commands:");
        System.out.println("  init                Initialize a new repository");
        System.out.println("  add <file>          Add file contents to the index");
        System.out.println("  rm <file>           Remove files from the working tree and the index");
        System.out.println("  status              Show the working tree status");
        System.out.println("  commit -m <msg>     Record changes to the repository with message");
        System.out.println("  log                 Show commit logs");
        System.out.println("  diff <file>         Show changes between working directory and index");
    }

    /**
     * Initializes a new repository in the current directory or a specified path.
     * 
     * @param args Command arguments. If provided, args[0] is the path where the
     *             repository
     *             should be initialized; otherwise, the current directory is used
     */
    private static void initCommand(String[] args) {
        try {
            // Get current directory path if none is specified
            String path = args.length > 0 ? args[0] : System.getProperty("user.dir");

            // Initialize repository
            Repository.initialize(path);
        } catch (IOException e) {
            System.err.println("Error initializing repository: " + e.getMessage());
        }
    }

    /**
     * Adds file contents to the index (staging area).
     * 
     * @param args Command arguments. args[0] is the path to the file to add
     */
    private static void addCommand(String[] args) {
        if (args.length == 0) {
            System.out.println("No file provided");
            return;
        }

        try {
            Path repoRoot = findRepoRoot();
            if (repoRoot == null) {
                System.out.println("No MiniVCS repository found");
                return;
            }

            Index index = new Index();
            index.load(repoRoot);

            for (String filePath : args) {
                Path currentDir = Paths.get(System.getProperty("user.dir"));
                Path fullPath = currentDir.resolve(filePath);
                if (!Files.exists(fullPath)) {
                    System.out.println("File does not exist: " + filePath);
                    continue;
                }

                index.add(repoRoot, fullPath);
                System.out.println("Added: " + filePath);
            }

        } catch (IOException e) {
            System.err.println("Error adding file: " + e.getMessage());
        }
    }

    /**
     * Removes files from the working tree and the index.
     * 
     * @param args Command arguments. args[0] is the path to the file to remove
     */
    private static void rmCommand(String[] args) {
        System.out.println("'rm' command not yet implemented");
        // TODO: Implement rm command
        // 1. Validate args (file is in index)
        // 2. Remove file from index
        // 3. Optionally remove file from working directory
    }

    /**
     * Shows the working tree status, comparing index with working directory.
     * Identifies files that are:
     * - Deleted: in the index but not in working directory
     * - Modified: in both index and working directory but with different content
     * - Staged: in index with no changes
     * - Untracked: in working directory but not in index
     * 
     * @param args Command arguments (unused)
     */
    private static void statusCommand(String[] args) {
        try {
            // Find repository root and return if not found
            Path repoPath = findRepoRoot();
            if (repoPath == null) {
                System.out.println("No MiniVCS repository found");
                return;
            }

            // Load the index file to get tracked files
            Index index = new Index();
            index.load(repoPath);
            Map<String, IndexEntry> stagedFiles = index.getEntries();

            // Compare each indexed file with its current state in working directory
            for (Map.Entry<String, IndexEntry> entry : stagedFiles.entrySet()) {
                // Check if file has been deleted from working directory
                if (!Files.exists(Paths.get(entry.getKey()))) {
                    System.out.println("Deleted: " + entry.getKey());
                    continue;
                }

                // Check if file has been modified by comparing hashes
                String storedHash = entry.getValue().getHash();
                String currentHash = ObjectStore.createHash(Paths.get(entry.getKey()));

                if (!storedHash.equals(currentHash)) {
                    System.out.println("Modified: " + entry.getKey());
                } else {
                    System.out.println("Staged: " + entry.getKey());
                }
            }

            // Find untracked files by walking the directory tree
            Set<String> indexPaths = stagedFiles.keySet();
            List<String> untrackedFiles = new ArrayList<>();

            Files.walk(repoPath)
                    .filter(Files::isRegularFile) // Only consider regular files
                    .filter(path -> !path.startsWith(repoPath.resolve(".minivcs"))) // Exclude the .minivcs directory
                    .map(path -> repoPath.relativize(path).toString()) // Convert to relative paths
                    .filter(relativePath -> !indexPaths.contains(relativePath)) // Exclude files already in index
                    .forEach(untrackedFiles::add);

            // Display untracked files
            for (String file : untrackedFiles) {
                System.out.println("Untracked: " + file);
            }

        } catch (IOException | NoSuchAlgorithmException e) {
            System.err.println("Error getting status: " + e.getMessage());
        }
    }

    /**
     * Records changes to the repository.
     * 
     * @param args Command arguments. Should include "-m" followed by commit message
     */
    private static void commitCommand(String[] args) {
        System.out.println("'commit' command not yet implemented");
        // TODO: Implement commit command
        // 1. Check for commit message
        // 2. Create tree objects for current directory state
        // 3. Create commit object with parent reference
        // 4. Update HEAD reference
    }

    /**
     * Shows commit logs.
     * 
     * @param args Command arguments (unused)
     */
    private static void logCommand(String[] args) {
        System.out.println("'log' command not yet implemented");
        // TODO: Implement log command
        // 1. Get current HEAD commit
        // 2. Traverse commit parents
        // 3. Display commit info (hash, author, date, message)
    }

    /**
     * Shows changes between working directory and index.
     * 
     * @param args Command arguments. If provided, args[0] is the file to show diff
     *             for
     */
    private static void diffCommand(String[] args) {
        System.out.println("'diff' command not yet implemented");
        // TODO: Implement diff command
        // 1. Get file from index
        // 2. Compare with working directory version
        // 3. Generate diff output
    }

    private static Path findRepoRoot() {
        Path currentDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath();

        // Traverse up the directory tree until we find a .minivcs directory
        while (currentDir != null) {
            Path minivcsDir = currentDir.resolve(".minivcs");
            // Check if .minivcs directory exists and is a directory
            if (Files.exists(minivcsDir) && Files.isDirectory(minivcsDir)) {
                return currentDir;
            }
            // Move to the parent directory
            currentDir = currentDir.getParent();
        }
        // If no .minivcs directory is found, return null
        return null;
    }
}
