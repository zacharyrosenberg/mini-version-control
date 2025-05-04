package minivcs;

import java.io.IOException;

public class MiniVCS {
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

    // Print usage information
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

    // Command implementations (stubs)
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

    private static void addCommand(String[] args) {
        System.out.println("'add' command not yet implemented");
    }

    private static void rmCommand(String[] args) {
        System.out.println("'rm' command not yet implemented");
    }

    private static void statusCommand(String[] args) {
        System.out.println("'status' command not yet implemented");
    }

    private static void commitCommand(String[] args) {
        System.out.println("'commit' command not yet implemented");
    }

    private static void logCommand(String[] args) {
        System.out.println("'log' command not yet implemented");
    }

    private static void diffCommand(String[] args) {
        System.out.println("'diff' command not yet implemented");
    }
}
