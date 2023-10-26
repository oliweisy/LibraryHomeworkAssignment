package main.java.simplelibrary;

import main.java.simplelibrary.data.LibraryDataStore;
import main.java.simplelibrary.models.User;
import main.java.simplelibrary.services.CSVUtility;
import main.java.simplelibrary.services.LibraryService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        LibraryDataStore dataStore = new LibraryDataStore();

        // Load data from CSV
        try {
            CSVUtility.loadItemsFromCSV(dataStore, "src/main/resources/inventory.csv");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading items from CSV", e);
            return;
        }

        LibraryService libraryService = new LibraryService(dataStore);

        User user1 = new User("Alice");
        dataStore.getUsers().put(user1.getUsername(), user1);

        // Borrow an item
        libraryService.borrowItem(1, user1);

        // Return an item
        libraryService.returnItem(1, user1);

        // Check if an item is overdue
        boolean isOverdue = libraryService.isOverdue(LocalDate.of(2022, 1, 10));
        System.out.println("Is item overdue? " + isOverdue);
    }
}
