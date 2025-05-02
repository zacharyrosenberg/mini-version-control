
public class MiniVCS {
    public static void main(String[] args) {

        // Check if any arguments are provided
        if (args.length == 0) {
            System.out.println("No arguments provided");
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
                    rmComand(commandArgs);
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
}
