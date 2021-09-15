package com.example.digitalrefrige.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.digitalrefrige.model.ItemRepository;
import com.example.digitalrefrige.model.dataHolder.Item;

import java.util.List;

public class ItemListViewModel extends AndroidViewModel {

    private ItemRepository repository;
    private LiveData<List<Item>> allItems;

    public ItemListViewModel(@NonNull Application application) {
        super(application);
        repository = new ItemRepository(application);
        allItems = repository.getAllNotes();
    }

    public LiveData<List<Item>> getAllItems() {
        return allItems;
    }

    public void insertItem(Item item) {
        repository.insertItem(item);
    }

    public void updateItem(Item item) {
        repository.updateItem(item);
    }

    public void deleteItem(Item item) {
        repository.deleteItem(item);
    }
}
