package com.example.digitalrefrige.viewModel;

import android.util.Log;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.digitalrefrige.model.ItemRepository;
import com.example.digitalrefrige.model.LabelRepository;
import com.example.digitalrefrige.model.dataHolder.Item;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ItemListViewModel extends ViewModel implements Filterable {

    private ItemRepository itemRepository;
    private LabelRepository labelRepository;
    private LiveData<List<Item>> allItems;
    private MutableLiveData<List<Item>> filteredData;

    private Filter itemFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            if (allItems.getValue() == null) return null;
            List<Item> filteredList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                // match all items
                filteredList.addAll(allItems.getValue());
            } else {
                // filter with given pattern
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
            if (filterResults == null) return;
            updateFilteredItemList((List) filterResults.values);
        }
    };

    @Inject
    public ItemListViewModel(ItemRepository itemRepo,LabelRepository labelRepo) {
        itemRepository = itemRepo;
        labelRepository = labelRepo;
        allItems = itemRepository.getAllItems();
        filteredData = new MutableLiveData<>(new ArrayList<>());
    }

    public void updateFilteredItemList(List<Item> list) {
        Log.d("MyLog", "update list with size" + list.size());
        List<Item> newFilteredItems = new ArrayList<>(list);
        filteredData.setValue(newFilteredItems);
    }

    public LiveData<List<Item>> getFilteredData() {
        return filteredData;
    }


    public LiveData<List<Item>> getAllItems() {
        return allItems;
    }

    public long insertItem(Item item) {
        return itemRepository.insertItem(item);
    }

    public void updateItem(Item item) {
        itemRepository.updateItem(item);
    }

    public void deleteItem(Item item) {
        itemRepository.deleteItem(item);
    }

    @Override
    public Filter getFilter() {
        return itemFilter;
    }
}
