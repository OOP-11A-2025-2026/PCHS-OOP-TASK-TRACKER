package TaskPackage;

import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private static final String DEFAULT_FILE = "tasks.txt";
    private final TaskRepository repository;
    private final Scanner scanner = new Scanner(System.in);
    private String activeFilePath = DEFAULT_FILE;

    public ConsoleUI(TaskRepository repository) {
        this.repository = repository;
    }

    public void start() {
        printWelcome();
        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine();
            if (line == null) break;
            line = line.trim();
            if (line.isEmpty()) continue;
            if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("quit")) {
                System.out.println("Exiting. Bye!");
                break;
            }
            try {
                handleCommand(line);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void printWelcome() {
        System.out.println("Task Manager Console UI \nType 'help' for commands. Type 'exit' to quit.");
    }

    private void handleCommand(String line) {
        String[] parts = line.split("\\s+", 2);
        String cmd = parts[0].toLowerCase();
        String args = parts.length > 1 ? parts[1].trim() : "";

        switch (cmd) {
            case "help":
                printHelp();
                break;
            case "add":
                cmdAdd(args);
                break;
            case "list":
                cmdList();
                break;
            case "update":
                cmdUpdate(args);
                break;
            case "delete":
                cmdDelete(args);
                break;
            case "save":
                cmdSave(args);
                break;
            case "load":
                cmdLoad(args);
                break;
            case "file":
                cmdFile(args);
                break;
            default:
                System.out.println("Unknown command. Type 'help' to see available commands.");
        }
    }

    private void printHelp() {
        System.out.println("Commands:");
        System.out.println("  add <title> | <description> | <category> | <owner>") ;
        System.out.println("    - create a task. Use '|' to separate fields. Category examples: SCHOOL, UNIVERSITY, SPORT, OTHER");
        System.out.println("  list");
        System.out.println("    - list all tasks");
        System.out.println("  update <id> <field> <value>");
        System.out.println("    - update one field (title, description, category, status, owner)");
        System.out.println("  delete <id>");
        System.out.println("  save [filePath]");
        System.out.println("    - save current tasks to the specified .txt file (defaults to " + DEFAULT_FILE + ")");
        System.out.println("  load [filePath]");
        System.out.println("    - load tasks from file and replace current in-memory tasks");
        System.out.println("  file [filePath]");
        System.out.println("    - show or change the default file path used by save/load");
        System.out.println("  help");
        System.out.println("  exit | quit");
    }

    private void cmdAdd(String args) {
        if (args.isEmpty()) {
            System.out.println("Usage: add <title> | <description> | <category> | <owner>");
            return;
        }
        String[] parts = args.split("\\|", -1);
        if (parts.length < 4) {
            System.out.println("Error: expected 4 fields separated by '|'. Example: title | desc | SPORT | Alice");
            return;
        }
        String title = parts[0].trim();
        String description = parts[1].trim();
        String categoryStr = parts[2].trim().toUpperCase();
        String owner = parts[3].trim();

        if (title.isEmpty() || owner.isEmpty()) {
            System.out.println("Error: title and owner cannot be empty.");
            return;
        }

        Category category;
        try {
            category = Category.valueOf(categoryStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid category: " + categoryStr + ". Allowed: SCHOOL, UNIVERSITY, SPORT, OTHER");
            return;
        }

        Task t = new Task(0, title, description, category, null, owner);

        if (repository == null) {
            System.out.println("Repository not available — task validated but not saved:\n" + t);
            return;
        }

        try {
            repository.addTask(t);
            System.out.println("Task added (delegated to repository): " + t);
        } catch (Exception e) {
            System.out.println("Failed to add task via repository: " + e.getMessage());
        }
    }

    private void cmdList() {
        if (repository == null) {
            System.out.println("Repository not available — cannot list tasks.");
            return;
        }
        List<Task> all = repository.getAllTasks();
        if (all == null || all.isEmpty()) {
            System.out.println("No tasks found.");
            return;
        }
        for (Task t : all) {
            System.out.println(t);
        }
    }

    private void cmdUpdate(String args) {
        if (args.isEmpty()) {
            System.out.println("Usage: update <id> <field> <value>");
            return;
        }
        String[] parts = args.split("\\s+", 3);
        if (parts.length < 3) {
            System.out.println("Error: expected id, field, and value.");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            System.out.println("Error: id must be an integer.");
            return;
        }
        String field = parts[1].toLowerCase();
        String value = parts[2].trim();

        if (repository == null) {
            System.out.println("Repository not available — cannot update task.");
            return;
        }

        Task existing = repository.getTaskById(id);
        if (existing == null) {
            System.out.println("Task with id " + id + " not found.");
            return;
        }

        try {
            switch (field) {
                case "title":
                    existing.setTitle(value);
                    break;
                case "description":
                    existing.setDescription(value);
                    break;
                case "category":
                    existing.setCategory(Category.valueOf(value.toUpperCase()));
                    break;
                case "status":
                    existing.setStatus(Status.valueOf(value.toUpperCase()));
                    break;
                case "owner":
                    existing.setOwner(value);
                    break;
                default:
                    System.out.println("Unknown field: " + field + ". Allowed: title, description, category, status, owner");
                    return;
            }
            repository.updateTask(id, existing);
            System.out.println("Task updated: " + existing);
        } catch (IllegalArgumentException iae) {
            System.out.println("Validation error: " + iae.getMessage());
        } catch (Exception e) {
            System.out.println("Failed to update task: " + e.getMessage());
        }
    }

    private void cmdDelete(String args) {
        if (args.isEmpty()) {
            System.out.println("Usage: delete <id>");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(args.split("\\s+")[0]);
        } catch (NumberFormatException e) {
            System.out.println("Error: id must be an integer.");
            return;
        }

        if (repository == null) {
            System.out.println("Repository not available — cannot delete task.");
            return;
        }

        boolean ok = repository.deleteTask(id);
        if (ok) System.out.println("Task deleted: " + id);
        else System.out.println("Task not found: " + id);
    }

    private void cmdSave(String args) {
        if (!ensureRepository()) return;

        String path = args.isEmpty() ? activeFilePath : args;
        if (path.trim().isEmpty()) {
            System.out.println("Usage: save [filePath]");
            return;
        }

        TaskSaver saver = new TaskSaver(repository, path);
        saver.save();
        activeFilePath = path;
    }

    private void cmdLoad(String args) {
        if (!ensureRepository()) return;

        String path = args.isEmpty() ? activeFilePath : args;
        if (path.trim().isEmpty()) {
            System.out.println("Usage: load [filePath]");
            return;
        }

        TaskLoader loader = new TaskLoader(repository, path);
        loader.load();
        activeFilePath = path;
    }

    private void cmdFile(String args) {
        if (args.isEmpty()) {
            System.out.println("Current default file: " + activeFilePath);
            System.out.println("Usage: file <filePath> to change the default.");
            return;
        }
        activeFilePath = args.trim();
        System.out.println("Default file path updated to: " + activeFilePath);
    }

    private boolean ensureRepository() {
        if (repository == null) {
            System.out.println("Repository not available — operation not possible.");
            return false;
        }
        return true;
    }
}
