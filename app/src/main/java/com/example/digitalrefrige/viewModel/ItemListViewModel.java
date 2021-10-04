package com.example.digitalrefrige.viewModel;

import android.os.Build;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.digitalrefrige.model.ItemRepository;
import com.example.digitalrefrige.model.dataHolder.Item;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ItemListViewModel extends ViewModel implements Filterable {

    private ItemRepository repository;
    private LiveData<List<Item>> allItems;
    private MutableLiveData<List<Item>> filteredData;

    private Filter itemFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Item> filteredList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(allItems.getValue());
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (Item item : allItems.getValue()) {
                    if (item.getName().contains(filterPattern)
                            || item.getDescription().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            updateFilteredItemList((List) filterResults.values);
        }
    };

    @Inject
    public ItemListViewModel(ItemRepository repo) {
        repository = repo;
        allItems = repository.getAllItems();
        filteredData = new MutableLiveData<>(new ArrayList<>());
    }

    public void updateFilteredItemList(List<Item> list) {
        List<Item> newFilteredItems = new ArrayList<>(filteredData.getValue());
        List<Item> needRemove = new ArrayList<>();
        for (Item item : newFilteredItems) {
            if (!list.contains(item)) {
                needRemove.add(item);
            } else {
                list.remove(item);
            }
        }
        newFilteredItems.removeAll(needRemove);
        newFilteredItems.addAll(list);
        filteredData.setValue(newFilteredItems);
    }

    public LiveData<List<Item>> getFilteredData() {
        return filteredData;
    }


    public LiveData<List<Item>> getAllItems() {
        return allItems;
    }

    public long insertItem(Item item) {
        return repository.insertItem(item);
    }

    public void updateItem(Item item) {
        repository.updateItem(item);
    }

    public void deleteItem(Item item) {
        repository.deleteItem(item);
    }

    @Override
    public Filter getFilter() {
        return itemFilter;
    }
}
