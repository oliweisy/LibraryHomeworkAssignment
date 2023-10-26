package main.java.simplelibrary.data;

import main.java.simplelibrary.models.Item;
import main.java.simplelibrary.models.User;


import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LibraryDataStore {
    private final ConcurrentHashMap<Integer, Item> items = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<User, HashSet<Item>> userItems = new ConcurrentHashMap<>();

    public Map<Integer, Item> getItems() {
        return (items);
    }

    public Map<String, User> getUsers() {
        return (users);
    }

    public Map<User, HashSet<Item>> getUserItems() {
        return (userItems);
    }

    public void addItem(Item item) {
        items.put(item.getUniqueId(), item);
    }

}
