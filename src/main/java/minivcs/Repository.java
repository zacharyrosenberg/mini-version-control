package minivcs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Repository {

    public static void initialize(String path) throws IOException {
        Path repoPath = Paths.get(path, ".minivcs");
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

        // Create HEAD file
        Path headFile = Paths.get(path, ".minivcs", "HEAD");
        Files.writeString(headFile, "ref: refs/heads/master\n");

        // Create index file
        Path indexFile = Paths.get(path, ".minivcs", "index");
        Files.createFile(indexFile);

        System.out.println("Initialized empty MiniVCS repository in " + repoPath);

    }

}

// Check if repository already exists
// First, check if .minivcs already exists at the given path
// If it exists, either throw an exception or print a message that repository is
// already initialized
// Create directory structure
// Create main .minivcs directory
// Create subdirectories:
// .minivcs/objects/ - For storing file content
// .minivcs/refs/ - For storing references (branches)
// .minivcs/refs/heads/ - For branch references
// Create initial files
// Create empty index file (.minivcs/index)
// Create HEAD file pointing to main/master branch (.minivcs/HEAD)
// Optionally create a config file (.minivcs/config)