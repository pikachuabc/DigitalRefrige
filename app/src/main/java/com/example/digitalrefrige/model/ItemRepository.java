package com.example.digitalrefrige.model;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.digitalrefrige.model.dataHolder.Item;
import com.example.digitalrefrige.model.dataSource.ItemDAO;
import com.example.digitalrefrige.model.dataSource.LocalDataBase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ItemRepository {

    private LiveData<List<Item>> allItems;
    private ExecutorService executorService;

    private ItemDAO itemDAO;

    public ItemRepository(ItemDAO dao) {
        itemDAO = dao;
        executorService = Executors.newFixedThreadPool(2);
        allItems = itemDAO.getAllItems();
    }

    public LiveData<List<Item>> getAllNotes() {
        return allItems;
    }

    public void insertItem(Item item) {
        executorService.execute(() -> itemDAO.insertItem(item));
    }
    public void updateItem(Item item) {
        executorService.execute(() -> itemDAO.updateItem(item));
    }
    public void deleteItem(Item item) {
        executorService.execute(() -> itemDAO.deleteItem(item));
    }
    public void findItemById(int id) {
        executorService.execute(() -> itemDAO.findItemById(id));
    }
}
