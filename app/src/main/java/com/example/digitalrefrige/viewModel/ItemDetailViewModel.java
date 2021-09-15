package com.example.digitalrefrige.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.digitalrefrige.model.ItemRepository;
import com.example.digitalrefrige.model.dataHolder.Item;

import java.util.List;

public class ItemDetailViewModel extends ViewModel {
    private ItemRepository repository;
    private LiveData<Item> curItem;

    public ItemDetailViewModel() {

    }
}
