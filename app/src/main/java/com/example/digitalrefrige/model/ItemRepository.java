package com.example.digitalrefrige.model;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.digitalrefrige.model.dataHolder.Item;
import com.example.digitalrefrige.model.dataSource.ItemDAO;
import com.example.digitalrefrige.model.dataSource.LocalDataBase;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Accessing data through this
 */
public class ItemRepository {

    private LiveData<List<Item>> allItems;
    private ExecutorService executorService;

    private ItemDAO itemDAO;

    public ItemRepository(ItemDAO dao) {
        itemDAO = dao;
        executorService = Executors.newFixedThreadPool(2);
        allItems = itemDAO.getAllItems();
    }

    public LiveData<List<Item>> getAllItems() {
        return allItems;
    }
    public List<Item> getExpiringItems(int interval) {
        Future<List<Item>> exp = executorService.submit(() -> itemDAO.getExpiringItems(interval));
        try {
            return exp.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public List<Item> getExpiredItems() {
        Future<List<Item>> exp = executorService.submit(() -> itemDAO.getExpiredItems());
        try {
            return exp.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public long insertItem(Item item) {
        Future<Long> insertRes = executorService.submit(() -> itemDAO.insertItem(item));
        try {
            return insertRes.get();
        } catch (Exception e) {
            e.printStackTrace();
            return -1L;
        }
    }

    public void updateItem(Item item) {
        executorService.execute(() -> itemDAO.updateItem(item));
    }

    public void deleteItem(Item item) {
        executorService.execute(() -> itemDAO.deleteItem(item));
    }

    public Item findItemById(long id) {
        Future<Item> itemFuture = executorService.submit(() -> itemDAO.findItemById(id));
        try {
            return itemFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
