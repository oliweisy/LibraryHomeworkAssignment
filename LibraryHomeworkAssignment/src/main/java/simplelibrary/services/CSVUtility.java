package main.java.simplelibrary.services;

import main.java.simplelibrary.data.LibraryDataStore;
import main.java.simplelibrary.models.Book;
import main.java.simplelibrary.models.DVD;
import main.java.simplelibrary.models.VHS;
import main.java.simplelibrary.models.CD;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CSVUtility {
    public static void loadItemsFromCSV(LibraryDataStore dataStore, String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            // Skip the header line
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                int uniqueId = Integer.parseInt(data[0]);
                int itemId = Integer.parseInt(data[1]);
                String type = data[2];
                String title = data[3];

                switch (type) {
                    case "Book":
                        dataStore.addItem(new Book(uniqueId, itemId, title));
                        break;
                    case "DVD":
                        dataStore.addItem(new DVD(uniqueId, itemId, title));
                        break;
                    case "VHS":
                        dataStore.addItem(new VHS(uniqueId, itemId, title));
                        break;
                    case "CD":
                        dataStore.addItem(new CD(uniqueId, itemId, title));
                        break;
                }

            }
        }
    }

}
