package com.example.digitalrefrige.viewModel;

import android.widget.Filterable;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.digitalrefrige.model.ItemLabelCrossRefRepository;
import com.example.digitalrefrige.model.ItemRepository;
import com.example.digitalrefrige.model.LabelRepository;
import com.example.digitalrefrige.model.dataHolder.Item;
import com.example.digitalrefrige.model.dataHolder.ItemLabelCrossRef;
import com.example.digitalrefrige.model.dataHolder.Label;
import com.example.digitalrefrige.model.dataQuery.ItemWithLabels;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;


@HiltViewModel
public class SyncViewModel extends ViewModel {

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


    @Inject
    public SyncViewModel(ItemRepository itemRepo, LabelRepository labelRepo,
                         ItemLabelCrossRefRepository itemLabelCrossRefRepo) {
        itemRepository = itemRepo;
        labelRepository = labelRepo;
        itemLabelCrossRefRepository = itemLabelCrossRefRepo;
        allItems = itemRepository.getAllItems();
        allItemsWithLabels = itemLabelCrossRefRepo.getAllItemList();
        allLabels = labelRepo.getAllLabels();
    }

    public LiveData<List<Item>> getAllItems() {
        return allItems;
    }

    public LiveData<List<Label>> getAllLables() {
        return allLabels;
    }


    public void insertItem(Item item) {
        itemRepository.insertItem(item);
    }

    public void deleteItem(Item item) {
        itemRepository.deleteItem(item);
    }

    public void updateItem(Item item) {
        itemRepository.updateItem(item);
    }

    public void insertLabel(Label label){ labelRepository.insertLabel(label); }

    public void deleteLabel(Label label) { labelRepository.deleteLabel(label); }

    public void updateLabel(Label label) {
        labelRepository.updateLabel(label);
    }


}
