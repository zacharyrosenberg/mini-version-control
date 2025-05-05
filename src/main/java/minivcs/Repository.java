package minivcs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Manages the version control repository structure and operations.
 * This class provides functionality for creating and working with a repository,
 * similar to how Git repositories are structured.
 */
public class Repository {

    /**
     * Initializes a new repository at the specified path.
     * Creates the necessary directory structure and initial files:
     * - .minivcs/ directory as the main repository folder
     * - objects/ directory for storing file content
     * - refs/ and refs/heads/ directories for branch references
     * - HEAD file pointing to the default branch
     * - index file for tracking staged changes
     *
     * @param path The path where the repository should be created
     * @throws IOException If there's an error creating directories or files
     */
    public static void initialize(String path) throws IOException {
        Path repoPath = Path.of(path, ".minivcs");

        // Check if repository already exists
        if (Files.exists(repoPath)) {
            System.out.println("Repository already exists");
            return;
        }

        // Create directory structure
        Path objectsDir = Paths.get(path, ".minivcs", "objects");
        Path refsDir = Paths.get(path, ".minivcs", "refs");
        Path headsDir = Paths.get(path, ".minivcs", "refs", "heads");

        // Create directories
        Files.createDirectories(objectsDir);
        Files.createDirectories(refsDir);
        Files.createDirectories(headsDir);

        // Create HEAD file - points to the default branch (master)
        Path headFile = Paths.get(path, ".minivcs", "HEAD");
        Files.writeString(headFile, "ref: refs/heads/master\n");

        // Create empty index file for staging changes
        Path indexFile = Paths.get(path, ".minivcs", "index");
        Files.createFile(indexFile);

        System.out.println("Initialized empty MiniVCS repository in " + repoPath);
    }

    // TODO: Implement additional repository operations:
    // - Methods for working with the index
    // - Methods for creating commits
    // - Methods for managing branches
    // - Methods for handling file status
}

// Additional notes on repository structure:
// .minivcs/objects/ - Content-addressable storage for file snapshots (blobs)
// .minivcs/refs/ - Stores references to commits (branches, tags)
// .minivcs/refs/heads/ - Branch references
// .minivcs/index - Tracks staged changes ready for commit
// .minivcs/HEAD - Points to the current active branch