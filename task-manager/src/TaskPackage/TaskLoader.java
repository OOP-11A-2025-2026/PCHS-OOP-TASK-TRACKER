package TaskPackage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class TaskLoader {

    private final TaskRepository repository;
    private final String filePath;

    public TaskLoader(TaskRepository repository, String filePath) {
        this.repository = repository;
        this.filePath = filePath;
    }

    public void load() {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    System.out.println("No file found. Created new file at: " + filePath);
                }
            } catch (IOException e) {
                System.out.println("Could not create file: " + filePath + " - " + e.getMessage());
            }
            return;
        }

        clearRepository();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;
            while ((line = reader.readLine()) != null) {
                parseAndAdd(line);
            }

            System.out.println("Tasks loaded from file: " + filePath);

        } catch (IOException e) {
            System.out.println("Error loading tasks: " + e.getMessage());
        }
    }

    private void clearRepository() {
        if (repository instanceof TaskStorage storage) {
            storage.clear();
            return;
        }

        List<Task> existing = repository.getAllTasks();
        for (Task task : existing) {
            repository.deleteTask(task.getId());
        }
    }

    private void parseAndAdd(String line) {
        if (line.trim().isEmpty()) return;

        String[] parts = line.split("\\|");

        if (parts.length < 6) {
            System.out.println("Invalid line, skipping: " + line);
            return;
        }

        try {
            int id = Integer.parseInt(parts[0].trim());
            String title = parts[1].trim();
            String description = parts[2].trim();
            Category category = Category.valueOf(parts[3].trim());
            String statusStr = parts[4].trim();
            Status status = statusStr.isEmpty() ? null : Status.valueOf(statusStr);
            String owner = parts[5].trim();

            Task task = new Task(id, title, description, category, status, owner);
            repository.addTask(task);

        } catch (NumberFormatException e) {
            System.out.println("Could not parse numeric value in line: " + line + " - " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Could not parse line: " + line + " - " + e.getMessage());
        }
    }
}
