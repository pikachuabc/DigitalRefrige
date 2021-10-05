package com.example.digitalrefrige.viewModel;

import android.os.Build;
import android.util.Log;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.digitalrefrige.model.ItemLabelCrossRefRepository;
import com.example.digitalrefrige.model.ItemRepository;
import com.example.digitalrefrige.model.LabelRepository;
import com.example.digitalrefrige.model.dataHolder.Item;
import com.example.digitalrefrige.model.dataHolder.Label;
import com.example.digitalrefrige.model.dataQuery.ItemWithLabels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ItemListViewModel extends ViewModel implements Filterable {

    private ItemRepository itemRepository;
    private LabelRepository labelRepository;
    private ItemLabelCrossRefRepository itemLabelCrossRefRepository;

    private LiveData<List<Item>> allItems;
    private LiveData<List<Label>> allLabels;
    private LiveData<List<ItemWithLabels>> allItemsWithLabels;

    private List<Label> curSelectedLabel;
    private MutableLiveData<List<Item>> filteredItems;

    private Filter itemFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Item> filteredList = new ArrayList<>();
            try {
                Log.d("MyLog", "\nStart filtering with config:\npattern:" + charSequence + "\n" + "labels:" + curSelectedLabel.toString());
                if (allItems.getValue() == null) return null;
                filteredList.addAll(allItems.getValue());
                filteredList = filteredList.stream()
                        .filter(x -> nameFilter(charSequence, x))
                        .filter(x -> labelFilter(x))
                        .collect(Collectors.toList());

            } catch (NullPointerException e) {
                Log.d("MyLog", "filtering failed");
            }


            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            if (filterResults == null) return;
            Object res = filterResults.values;
            updateFilteredItemList((List) filterResults.values);
        }
    };

    @Inject
    public ItemListViewModel(ItemRepository itemRepo, LabelRepository labelRepo, ItemLabelCrossRefRepository itemLabelCrossRefRepo) {
        itemRepository = itemRepo;
        labelRepository = labelRepo;
        itemLabelCrossRefRepository = itemLabelCrossRefRepo;
        allItems = itemRepository.getAllItems();
        allItemsWithLabels = itemLabelCrossRefRepo.getAllItemList();
        allLabels = labelRepo.getAllLabels();
        curSelectedLabel = new ArrayList<>();
        filteredItems = new MutableLiveData<>(new ArrayList<>());
    }

    public void updateFilteredItemList(List<Item> list) {
        if (list == null) return;
        Log.d("MyLog", "filteredItems change to size " + list.size());
        List<Item> newFilteredItems = new ArrayList<>(list);
        filteredItems.setValue(newFilteredItems);
    }

    public boolean nameFilter(CharSequence charSequence, Item item) {
        if (charSequence == null || charSequence.length() == 0) return true;
        String filterPattern = charSequence.toString().toLowerCase().trim();
        return item.getName().toLowerCase().contains(filterPattern)
                || item.getDescription().toLowerCase().contains(filterPattern);

    }

    public boolean labelFilter(Item item) {
        List<ItemWithLabels> itemWithLabels = allItemsWithLabels.getValue();
        Map<Item, List<Label>> tempMap = new HashMap<>();
        for (ItemWithLabels curItemWithLabels : itemWithLabels) {
            tempMap.put(curItemWithLabels.item, curItemWithLabels.labels);
        }
        List<Label> labelsOfCurItem = tempMap.get(item);
        for (Label label : labelsOfCurItem) {
            if (curSelectedLabel.contains(label)) {
                return true;
            }
        }
        return false;

    }


    public LiveData<List<Item>> getFilteredItems() {
        return filteredItems;
    }


    public LiveData<List<Item>> getAllItems() {
        return allItems;
    }

    public LiveData<List<Label>> getAllLabels() {
        return allLabels;
    }


    public LiveData<List<ItemWithLabels>> getAllItemsWithLabels() {
        return allItemsWithLabels;
    }

    public void setCurSelectedLabel(List<Label> curSelectedLabel) {
        this.curSelectedLabel = curSelectedLabel;
    }

    public List<Label> getCurSelectedLabel() {
        return curSelectedLabel;
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
