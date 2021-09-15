package com.example.digitalrefrige.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.digitalrefrige.model.ItemRepository;
import com.example.digitalrefrige.model.dataHolder.Item;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ItemListViewModel extends ViewModel {

    private ItemRepository repository;
    private LiveData<List<Item>> allItems;

    @Inject
    public ItemListViewModel(ItemRepository repo) {
        repository = repo;
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
