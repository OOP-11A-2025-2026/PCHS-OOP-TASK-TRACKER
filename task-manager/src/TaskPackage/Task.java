package TaskPackage;

public class Task {
    private int id;
    private String title;
    private String description;
    private Category category;
    private Status status;
    private String owner;

    public Task(int id, String title, String description, Category category, Status status, String owner) {
        if(title == null||title.isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        if(owner == null||owner.isEmpty()) {
            throw new IllegalArgumentException("Owner cannot be null or empty");
        }
        if(category != null) {
            this.category = category;
        }
        else {
            this.category = Category.OTHER;
        }
        if(status != null) {
            this.status = status;
        }
        else {
            this.status = Status.TODO;
        }
        this.id = id;
        this.title = title;
        this.description = description;
        this.owner = owner;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        if(title == null||title.isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        if(description == null||description.isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        this.description = description;
    }
    public Category getCategory() {
        return category;
    }
    public void setCategory(Category category) {
        if(category!=null)
        {
            this.category = category;
        }
    }
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        if(status!=null)
        {
            this.status = status;
        }
    }
    public String getOwner() {
        return owner;
    }
    public void setOwner(String owner) {
        if(owner == null||owner.isEmpty()) {
            throw new IllegalArgumentException("Owner cannot be null or empty");
        }
        this.owner = owner;
    }

    @Override
    public String toString() {
        return  "-------------------------\n" +
                " Task ID: " + id + "\n" +
                " Title: " + title + "\n" +
                " Description: " + description + "\n" +
                " Category: " + category + "\n" +
                " Status: " + status + "\n" +
                " Owner: " + owner + "\n" +
                "-------------------------";
    }

}


