package com.example.digitalrefrige.viewModel;

import android.content.Context;
import android.util.Log;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.preference.PreferenceManager;

import com.example.digitalrefrige.model.ItemLabelCrossRefRepository;
import com.example.digitalrefrige.model.ItemRepository;
import com.example.digitalrefrige.model.LabelRepository;
import com.example.digitalrefrige.model.dataHolder.Item;
import com.example.digitalrefrige.model.dataHolder.Label;
import com.example.digitalrefrige.model.dataQuery.ItemWithLabels;
import com.example.digitalrefrige.utils.Converters;

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

    /**
     * items liveData from database for observing instant change from database
     * and change recyclerview accordingly
     */
    private LiveData<List<Item>> allItems;

    /**
     * labels liveData from database for observing instant change from database
     * and change recyclerview accordingly
     */
    private LiveData<List<Label>> allLabels;

    /**
     * items labels relationships from database for filtering and recyclerview updating
     */
    private LiveData<List<ItemWithLabels>> allItemsWithLabels;

    /**
     * labels current selected
     */
    private List<Label> curSelectedLabel;

    /**
     * expiring selector mode currently selected
     * and options
     */
    private int currentSelectedExpiringDaysMode = ALL_MODE;
    public static final int ALL_MODE = 0;
    public static final int EXPIRING_MODE = 1;
    public static final int EXPIRED_MODE = 2;

    /**
     * default setting in EXPIRING_MODE when user
     * haven't set it
     */
    private int userSettingExpirationDays = 3;


    /**
     * actual item list(filtered) shown in the recyclerview
     */
    private MutableLiveData<List<Item>> filteredItems;

    private Filter itemFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Item> filteredList = new ArrayList<>();
            try {
                Log.d("filter", "\nStart filtering with config:\npattern:" + charSequence + "\n" + "labels:" + curSelectedLabel.toString() + "\n" + "expDateMode:" + currentSelectedExpiringDaysMode + "\n" + "userSettingExpirationDate:" + userSettingExpirationDays);
                if (allItems.getValue() == null) return null;
                filteredList.addAll(allItems.getValue());
                filteredList = filteredList.stream()
                        .filter(x -> nameFilter(charSequence, x))
                        .filter(x -> labelFilter(x))
                        .filter(x -> expiringDayFilter(x))
                        .collect(Collectors.toList());

            } catch (NullPointerException e) {
                Log.d("filter", "filtering failed");
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
        Log.d("filter", "filteredItems change to size " + list.size());
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
        // handle items with no labels
        if (labelsOfCurItem.size() == 0 && curSelectedLabel.contains(Label.NONE_LABEL)) return true;

        for (Label label : labelsOfCurItem) {
            if (curSelectedLabel.contains(label)) {
                return true;
            }
        }
        return false;

    }


    public boolean expiringDayFilter(Item item) {
        if (currentSelectedExpiringDaysMode == ALL_MODE) {
            return true;
        } else if (currentSelectedExpiringDaysMode == EXPIRING_MODE) {
            long daysLeft = Converters.getDayDifferences(Converters.dateToString(item.getExpireDate()));
            if (daysLeft < 0) {
                return false;
            } else {
                return daysLeft < userSettingExpirationDays;
            }
        } else if (currentSelectedExpiringDaysMode == EXPIRED_MODE) {
            return Converters.getDayDifferences(Converters.dateToString(item.getExpireDate())) < 0;
        }
        return true;
    }


    public LiveData<List<Item>> getFilteredItems() {
        return filteredItems;
    }

    public void deleteDisplayedItems() {
        itemRepository.deleteSelectedItems(new ArrayList<>(filteredItems.getValue()));
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


    public int getCurrentSelectedExpiringDaysMode() {
        return currentSelectedExpiringDaysMode;
    }

    public void setCurrentSelectedExpiringDaysMode(int currentSelectedExpiringDaysMode) {
        this.currentSelectedExpiringDaysMode = currentSelectedExpiringDaysMode;
    }

    public int getUserSettingExpirationDays() {
        return userSettingExpirationDays;
    }

    public void setUserSettingExpirationDays(int userSettingExpirationDays) {
        this.userSettingExpirationDays = userSettingExpirationDays;
    }

    @Override
    public Filter getFilter() {
        return itemFilter;
    }
}
