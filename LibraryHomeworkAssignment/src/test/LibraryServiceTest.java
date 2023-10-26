package test;

import main.java.simplelibrary.data.LibraryDataStore;
import main.java.simplelibrary.models.Book;
import main.java.simplelibrary.models.Item;
import main.java.simplelibrary.models.User;
import main.java.simplelibrary.services.CSVUtility;
import main.java.simplelibrary.services.LibraryService;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class LibraryServiceTest {
    private LibraryDataStore dataStore;
    private static final Logger LOGGER = Logger.getLogger(LibraryServiceTest.class.getName());

    private LibraryService libraryService;

    @Before
    public void setup() throws IOException {
        dataStore = new LibraryDataStore();
        CSVUtility.loadItemsFromCSV(dataStore, "src/main/resources/inventory.csv");
        libraryService = new LibraryService(dataStore);
    }

    @Test
    public void testBorrowAndReturnItem() {
        LOGGER.info("Starting testBorrowAndReturnItem...");

        User user = new User("TestUser");
        LOGGER.info("Created user: " + user.getUsername());

        boolean borrowResult = libraryService.borrowItem(1, user);
        LOGGER.info("Attempted to borrow item with ID 1. Success: " + borrowResult);
        assertTrue(borrowResult);

        boolean itemExists = libraryService.getItems().containsKey(1);
        LOGGER.info("Checking if item with ID 1 still exists in inventory: " + itemExists);
        assertFalse(itemExists);

        boolean returnResult = libraryService.returnItem(1, user);
        LOGGER.info("Attempted to return item with ID 1. Success: " + returnResult);
        assertTrue(returnResult);

        LOGGER.info("Finished testBorrowAndReturnItem.");
    }

    @Test
    public void testBorrowItemSetsDueDate() {

        Item testItem = new Book(1, 2, "Test Book");
        dataStore.addItem(testItem);

        User testUser = new User("testUser");

        // Borrow the item
        assertTrue(libraryService.borrowItem(1, testUser));

        // Get the borrowed item and check its due date
        Item borrowedItem = dataStore.getUserItems().get(testUser).iterator().next();

        LocalDate expectedDueDate = LocalDate.now().plusDays(7);
        assertEquals(expectedDueDate, borrowedItem.getDueDate());
    }

    @Test
    public void testGetInventory() {

        List<String> expectedTitles = Arrays.asList(
                "Pi",
                "The Art Of Computer Programming Volumes 1-6",
                "The Pragmatic Programmer",
                "Java Concurrency In Practice",
                "Introduction to Algorithms",
                "WarGames",
                "Hackers",
                "A Test Title"
        );

        List<String> actualTitles = libraryService.getInventory();

        assertEquals(expectedTitles, actualTitles);

    }
    @Test
    public void testGetOverdueItems() throws IOException {

        // Let's simulate that some items are borrowed with past due dates
        User user1 = new User("user1");
        Item item1 = dataStore.getItems().get(1); // Fetching by unique ID for demonstration
        item1.setBorrowedBy(user1);
        item1.setDueDate(LocalDate.now().minusDays(5)); // Setting due date 5 days ago

        Item item2 = dataStore.getItems().get(2);
        item2.setBorrowedBy(user1);
        item2.setDueDate(LocalDate.now().minusDays(3)); // Setting due date 3 days ago

        // Add these items to the user's borrowed items
        HashSet<Item> borrowedItems = new HashSet<>();
        borrowedItems.add(item1);
        borrowedItems.add(item2);
        dataStore.getUserItems().put(user1, borrowedItems);

        List<Item> overdueItems = libraryService.getOverdueItems();

        // Asserting the number of overdue items
        assertEquals(2, overdueItems.size());

        // Asserting that the items are the correct overdue items
        assertTrue(overdueItems.contains(item1));
        assertTrue(overdueItems.contains(item2));
    }

    @Test
    public void testDetermineCurrentInventory() {
        assertNotNull(libraryService.getCurrentInventory());

        int expectedSize = 0;
        try {
            expectedSize = countItemsInCSV("src/main/resources/inventory.csv");
        } catch (IOException e) {
            fail("Failed to read the CSV file.");
        }

        assertEquals(expectedSize, libraryService.getCurrentInventory().size());
    }

    private int countItemsInCSV(String filePath) throws IOException {
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            // Subtracting 1 for the header line
            return (int) lines.count() - 1;
        }
    }

    @Test
    public void testDetermineOverdueItems() {
        User user = new User("TestUser");
        libraryService.borrowItem(9, user); // Assume this was borrowed 10 days ago
        assertTrue(libraryService.isOverdue(LocalDate.now().minusDays(10)));
    }

    @Test
    public void testDetermineBorrowedItemsForUser() {
        User user = new User("TestUser");
        libraryService.borrowItem(2, user);
        libraryService.borrowItem(3, user);
        assertEquals(2, libraryService.getBorrowedItems(user).size());
    }

    @Test
    public void testDetermineIfBookIsAvailable() {
        assertTrue(libraryService.isAvailable(4)); // Check for "The Pragmatic Programmer"
        libraryService.borrowItem(4, new User("TestUser"));
        assertFalse(libraryService.isAvailable(4));
    }

    @Test
    public void testBorrowItemsWithSameItemID() {
        User user = new User("TestUser");
        LOGGER.info("Created user: " + user.getUsername());

        // From the CSV, "Pi" DVD has multiple copies with the same ItemID.
        int uniqueId1 = 1;  // First unique ID for "Pi" DVD.
        int uniqueId2 = 7;  // Second unique ID for "Pi" DVD.

        LOGGER.info("Attempting to borrow item with UniqueID: " + uniqueId1);
        assertTrue(libraryService.borrowItem(uniqueId1, user));
        LOGGER.info("Item with UniqueID: " + uniqueId1 + " borrowed successfully.");

        LOGGER.info("Attempting to borrow item with UniqueID: " + uniqueId2);
        assertTrue(libraryService.borrowItem(uniqueId2, user));
        LOGGER.info("Item with UniqueID: " + uniqueId2 + " borrowed successfully.");

        List<Item> borrowedItems = libraryService.getBorrowedItems(user);
        LOGGER.info("Number of items borrowed by user: " + borrowedItems.size());

        assertTrue(borrowedItems.stream().anyMatch(item -> item.getUniqueId() == uniqueId1));
        LOGGER.info("Verified item with UniqueID: " + uniqueId1 + " is borrowed.");

        assertTrue(borrowedItems.stream().anyMatch(item -> item.getUniqueId() == uniqueId2));
        LOGGER.info("Verified item with UniqueID: " + uniqueId2 + " is borrowed.");
    }


    @Test
    public void testIsAvailable() {
        User user = new User("TestUser");
        int uniqueId = 5;  // Hypothetical ID.
        assertTrue(libraryService.isAvailable(uniqueId));

        libraryService.borrowItem(uniqueId, user);
        assertFalse(libraryService.isAvailable(uniqueId));
    }

    @Test
    public void testGetCurrentInventory() {
        List<Item> inventory = libraryService.getCurrentInventory();
        assertFalse(inventory.isEmpty());
        assertTrue(inventory.stream().allMatch(item -> item.getDueDate() == null));
    }


    @Test
    public void testIsOverdue() {

        LOGGER.info("Starting testIsOverdue...");

        User user = new User("TestUser");
        LOGGER.info("Created user: " + user.getUsername());

        int uniqueId = 12;
        LOGGER.info("Using uniqueId: " + uniqueId);

        libraryService.borrowItem(uniqueId, user);
        LOGGER.info("Item with uniqueId: " + uniqueId + " borrowed by user: " + user.getUsername());

        Item borrowedItem = libraryService.getBorrowedItems(user).get(0);
        LOGGER.info("Fetched borrowed item: " + borrowedItem.getUniqueId() + " by user: " + user.getUsername());

        borrowedItem.setDueDate(LocalDate.now().minusDays(10));
        LOGGER.info("Set due date of borrowed item to: " + borrowedItem.getDueDate());

        boolean isItemOverdue = libraryService.isOverdue(borrowedItem.getDueDate());
        if (isItemOverdue) {
            LOGGER.info("Verified the item is overdue");
        } else {
            LOGGER.warning("The item is NOT overdue!");
        }
        assertTrue(isItemOverdue);
    }


    @Test
    public void testThreadSafetyForBorrowing() throws InterruptedException {


        User user1 = new User("User1");
        User user2 = new User("User2");
        int uniqueId = 15;  // Hypothetical ID.

        LOGGER.info("Test started...");

        Thread t1 = new Thread(() -> {
            LOGGER.info("Thread t1 attempting to borrow item with uniqueId: " + uniqueId + " for user: " + user1.getUsername());
            libraryService.borrowItem(uniqueId, user1);
            LOGGER.info("Thread t1 finished borrowing for user: " + user1.getUsername());
        });

        Thread t2 = new Thread(() -> {
            LOGGER.info("Thread t2 attempting to borrow item with uniqueId: " + uniqueId + " for user: " + user2.getUsername());
            libraryService.borrowItem(uniqueId, user2);
            LOGGER.info("Thread t2 finished borrowing for user: " + user2.getUsername());
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        boolean itemAvailable = libraryService.isAvailable(uniqueId);
        boolean user1Borrowed = libraryService.getBorrowedItems(user1).size() == 1;
        boolean user2Borrowed = libraryService.getBorrowedItems(user2).size() == 1;

        LOGGER.info("Item with uniqueId: " + uniqueId + " is available: " + itemAvailable);
        LOGGER.info("User: " + user1.getUsername() + " borrowed item: " + user1Borrowed);
        LOGGER.info("User: " + user2.getUsername() + " borrowed item: " + user2Borrowed);

        assertTrue((itemAvailable ^ user1Borrowed) ^ user2Borrowed);
        LOGGER.info("Test ended successfully!");
    }


}
