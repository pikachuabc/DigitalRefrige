package com.example.digitalrefrige.viewModel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.digitalrefrige.model.ItemRepository;
import com.example.digitalrefrige.model.dataHolder.Item;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ItemDetailViewModel extends ViewModel {
    private ItemRepository repository;
    private Item curItem;

    @Inject
    public ItemDetailViewModel(ItemRepository repo) {
        repository = repo;
    }

    public void bindWithItem(int id) {
        Item res = repository.findItemById(id);
        if (res != null) {
            curItem = res;
        }
    }

    public Item getCurItem() {
        return curItem;
    }

    public void setCurItem(Item curItem) {
        this.curItem = curItem;
    }

    public void insertItem(Item item) {
        repository.insertItem(item);
    }

    public void updateItem(Item item) {
        repository.updateItem(item);
    }

    public void deleteCurItem() {
        repository.deleteItem(curItem);
        curItem = null;
    }
}
