package TaskPackage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class TaskSaver {

    private final TaskRepository repository;
    private final String filePath;

    public TaskSaver(TaskRepository repository, String filePath) {
        this.repository = repository;
        this.filePath = filePath;
    }

    public void save() {
        List<Task> tasks = repository.getAllTasks();

        File targetFile = new File(filePath);
        File parent = targetFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(targetFile))) {

            for (Task task : tasks) {
                writer.write(formatTask(task));
                writer.newLine();
            }

            System.out.println("Tasks saved to file: " + filePath);

        } catch (IOException e) {
            System.out.println("Error saving tasks: " + e.getMessage());
        }
    }

    private String formatTask(Task task) {

        String status = task.getStatus() == null ? "" : task.getStatus().toString();

        return task.getId() + "|" +
                task.getTitle() + "|" +
                task.getDescription() + "|" +
                task.getCategory() + "|" +
                status + "|" +
                task.getOwner();
    }
}
