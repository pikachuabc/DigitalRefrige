package com.example.digitalrefrige.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.digitalrefrige.model.ItemLabelCrossRefRepository;
import com.example.digitalrefrige.model.ItemRepository;
import com.example.digitalrefrige.model.LabelRepository;
import com.example.digitalrefrige.model.dataHolder.Item;
import com.example.digitalrefrige.model.dataHolder.Label;
import com.example.digitalrefrige.model.dataQuery.ItemWithLabels;
import com.example.digitalrefrige.utils.Converters;
import com.example.digitalrefrige.views.itemList.ItemDetailFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ItemDetailViewModel extends ViewModel {
    private ItemRepository itemRepository;
    private Item curItem;
    private LabelRepository labelRepository;
    private LiveData<List<Label>> allLabels;
    private LiveData<List<Label>> labelsAssociatedWithCurItem;

    private ItemLabelCrossRefRepository itemLabelCrossRefRepository;


    @Inject
    public ItemDetailViewModel(ItemRepository itemRepo, ItemLabelCrossRefRepository itemLabelCrossRefRepo, LabelRepository labelRepo) {
        labelRepository = labelRepo;
        itemRepository = itemRepo;
        itemLabelCrossRefRepository = itemLabelCrossRefRepo;
        allLabels = labelRepo.getAllLabels();
    }

    public void bindWithItem(long id) {
        if (id == ItemDetailFragment.CREATE_NEW_ITEM) {
            // we are adding a new item thus need a holder
            curItem = new Item("", "", Calendar.getInstance().getTime(), "");
            labelsAssociatedWithCurItem = new MutableLiveData<>(new ArrayList<>());
        } else {
            curItem = itemRepository.findItemById(id);
            labelsAssociatedWithCurItem = itemLabelCrossRefRepository.getLabelsByItem(id);
        }

    }

    public Item getCurItem() {
        return curItem;
    }

    public void setCurItem(Item curItem) {
        this.curItem = curItem;
    }

    public long insertItem(Item item) {
        return itemRepository.insertItem(item);
    }

    public void updateItem(Item item) {
        itemRepository.updateItem(item);
    }

    public void deleteCurItem() {
        itemRepository.deleteItem(curItem);
        curItem = null;
    }

    public String getTimeStr() {
        return Converters.dateToString(curItem.getExpireDate());
    }

    public LiveData<List<Label>> getAllLabels() {
        return allLabels;
    }

    public void setTimeStr(String timeStr) {
        this.curItem.setExpireDate(Converters.strToDate(timeStr));
    }

    public LiveData<List<Label>> getAllLabelsAssociatedWithItem() {
        return labelsAssociatedWithCurItem;

    }
}
