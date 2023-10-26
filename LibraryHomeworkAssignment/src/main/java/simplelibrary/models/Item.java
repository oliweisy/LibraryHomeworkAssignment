package main.java.simplelibrary.models;

import java.time.LocalDate;

public abstract class Item {
    private int uniqueId;
    private int itemId;
    private String type;
    private String title;
    private LocalDate dueDate;
    private User borrowedBy;

    public Item(int uniqueId, int itemId, String title) {
        this.uniqueId = uniqueId;
        this.itemId = itemId;
        this.title = title;
    }


    public int getUniqueId() {
        return uniqueId;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public User getBorrowedBy() {
        return borrowedBy;
    }

    public void setBorrowedBy(User borrowedBy) {
        this.borrowedBy = borrowedBy;
    }

    public String getTitle() {
        return this.title;
    }
}

