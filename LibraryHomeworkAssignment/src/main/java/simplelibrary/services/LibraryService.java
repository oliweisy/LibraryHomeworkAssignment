package main.java.simplelibrary.services;

import main.java.simplelibrary.data.LibraryDataStore;
import main.java.simplelibrary.models.Item;
import main.java.simplelibrary.models.User;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class LibraryService {
    private final LibraryDataStore dataStore;
    private static final Logger LOGGER = Logger.getLogger(LibraryService.class.getName());

    public LibraryService(LibraryDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public synchronized boolean borrowItem(int uniqueId, User user) {
        LOGGER.info("Attempting to borrow item with uniqueId: " + uniqueId + " for user: " + user.getUsername());

        Item item = dataStore.getItems().get(uniqueId);
        if (item == null) {
            LOGGER.info("Item with uniqueId: " + uniqueId + " does not exist. Borrowing failed for user: " + user.getUsername());
            return false;
        }

        // Set due date for the borrowed item
        LocalDate dueDate = LocalDate.now().plusDays(7);
        item.setDueDate(dueDate);
        LOGGER.info("Due date for item with uniqueId: " + uniqueId + " set to: " + dueDate);

        dataStore.getItems().remove(uniqueId);
        dataStore.getUserItems().computeIfAbsent(user, k -> new HashSet<>()).add(item);

        LOGGER.info("Item with uniqueId: " + uniqueId + " successfully borrowed by user: " + user.getUsername());
        return true;
    }



    public synchronized boolean returnItem(int uniqueId, User user) {
        HashSet<Item> borrowedItems = dataStore.getUserItems().get(user);
        if (borrowedItems == null) return false;

        Iterator<Item> iterator = borrowedItems.iterator();
        while (iterator.hasNext()) {
            Item item = iterator.next();
            if (item.getUniqueId() == uniqueId) {
                iterator.remove();
                dataStore.getItems().put(uniqueId, item);
                return true;
            }
        }
        return false;
    }

    public synchronized boolean isOverdue(LocalDate borrowedDate) {

        long days = ChronoUnit.DAYS.between(borrowedDate, LocalDate.now());

        // Logging the borrowedDate and the calculated days difference
        LOGGER.info("Received borrowedDate: " + borrowedDate);
        LOGGER.info("Days between borrowedDate and current date: " + days);

        return days > 7;
    }

    // Returns the current items in the inventory
    public Map<Integer, Item> getItems() {
        return dataStore.getItems();
    }

    // Returns a list of currently available items for loan
    public synchronized List<Item> getCurrentInventory() {
        return dataStore.getItems().values().stream()
                .filter(item -> item.getDueDate() == null)
                .collect(Collectors.toList());
    }
    public List<String> getInventory() {
        return dataStore.getItems().values().stream()
                .map(Item::getTitle)
                .distinct() // To remove duplicates
                .collect(Collectors.toList());
    }

    // Returns a list of borrowed items by a particular user
    public synchronized List<Item> getBorrowedItems(User user) {
        LOGGER.info("Fetching borrowed items for user: " + user.getUsername());

        HashSet<Item> borrowedItems = dataStore.getUserItems().get(user);
        if (borrowedItems == null) {
            LOGGER.info("No items borrowed by user: " + user.getUsername());
            return Collections.emptyList();
        }

        LOGGER.info("User " + user.getUsername() + " has borrowed " + borrowedItems.size() + " items.");
        return new ArrayList<>(borrowedItems);
    }
    public synchronized List<Item> getOverdueItems() {
        LOGGER.info("Fetching all overdue items.");

        List<Item> overdueItems = new ArrayList<>();

        // Today's date
        LocalDate today = LocalDate.now();

        for (Map.Entry<User, HashSet<Item>> entry : dataStore.getUserItems().entrySet()) {
            for (Item item : entry.getValue()) {
                if (item.getDueDate().isBefore(today)) {
                    overdueItems.add(item);
                    LOGGER.info("Item with title '" + item.getTitle() + "' borrowed by " + entry.getKey().getUsername() + " is overdue.");
                }
            }
        }

        LOGGER.info("Found " + overdueItems.size() + " overdue items.");
        return overdueItems;
    }

    // Checks if an item is available
    public synchronized boolean isAvailable(int uniqueId) {
        LOGGER.info("Checking availability for item with uniqueId: " + uniqueId);

        Item item = dataStore.getItems().get(uniqueId);
        if (item == null) {
            LOGGER.info("Item with uniqueId: " + uniqueId + " does not exist.");
            return false;
        }

        boolean availability = item.getDueDate() == null;
        LOGGER.info("Item with uniqueId: " + uniqueId + " is available: " + availability);
        return availability;
    }

}
