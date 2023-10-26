# Simple Library System

A basic system to manage and track library items and user interactions.

## Features

1. **Borrow Items**: Users can borrow items which will be loaned out for a period of one week.
2. **Return Items**: Users can return the items they have borrowed.
3. **Check Inventory**: The system provides a list of current loanable items.
4. **Overdue Items**: The system can detect and list all items that are overdue.
5. **User's Borrowed Items**: The system can list all items borrowed by a specific user.
6. **Check Item Availability**: Users can check if a specific item is available for borrowing.

## System Design

- **Data Models**: The system uses object-oriented principles to define different types of items like `Book`, `DVD`, etc., and a class for `User`.
- **Data Storage**: The system uses in-memory data structures (`HashMap`) to store items, users, and transactions for simplicity and performance. This also ensures thread-safety.
- **Service Layer**: The primary business logic of the library system resides in the service layer, allowing separation of concerns and better maintainability.

## Design Choices and Assumptions

**Design Choices**:
- **In-memory Data Storage**: For simplicity and quick access, the system relies on in-memory storage. This also aids in ensuring thread-safety.
- **Service Layer Abstraction**: Separates the core logic from data access, ensuring maintainability and scalability.
- **Object-Oriented Models**: Allows easy addition of new item types and user functionalities.

**Assumptions**:
- The library system is designed for a single library branch. Multi-branch functionalities are not considered.
- Due to in-memory storage, data persistence across sessions is not considered.


## Setup and Execution
Inside src/main/java.simplelibrary, you can run the program from the Main.java class.
An example of the described setup below is there to follow.

1. **Initialize `LibraryDataStore`**: This will act as your main data store.
2. **Load Initial Data**:
   - Use the `CSVUtility.loadItemsFromCSV(dataStore, "path_to_csv")` utility method.
   - This reads from a given CSV and populates the in-memory data store.
3. **Service Instance**:
   - Create an instance of `LibraryService` using the initialized data store.
   - This instance provides methods to interact with the library system.
4. **Interactions**:
   - Use the service layer functions (`borrowItem`, `returnItem`, etc.) for interactions.

To test the functionality, please navigate to src/test and run the Junit tests to ensure functional code.
The provided tests cover major functionalities, and the results of these tests validate the correctness of the implemented functions.

## Code Structure

- **src/main/java/simplelibrary**:
   - **data**:
      - **LibraryDataStore.java**: In-memory storage for items, users, and their transactions.
   - **models**:
      - **Item.java**: Base class for all items. It has child classes for different types of items (`Book`, `DVD`, etc.).
      - **User.java**: Represents the user of the library.
   - **services**:
      - **LibraryService.java**: Service layer containing business logic. This is where all operations like borrowing, returning, etc. are implemented.
      - **CSVUtility.java**: Utility to load items into the library from a CSV.
