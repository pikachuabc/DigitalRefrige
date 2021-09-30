package com.example.digitalrefrige.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.digitalrefrige.model.ItemLabelCrossRefRepository;
import com.example.digitalrefrige.model.ItemRepository;
import com.example.digitalrefrige.model.LabelRepository;
import com.example.digitalrefrige.model.dataHolder.Item;
import com.example.digitalrefrige.model.dataHolder.Label;
import com.example.digitalrefrige.model.dataQuery.ItemWithLabels;
import com.example.digitalrefrige.utils.Converters;
import com.example.digitalrefrige.views.itemList.ItemDetailFragment;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ItemDetailViewModel extends ViewModel {
    private ItemRepository itemRepository;
    private Item curItem;

    private ItemLabelCrossRefRepository itemLabelCrossRefRepository;


    @Inject
    public ItemDetailViewModel(ItemRepository itemRepo, ItemLabelCrossRefRepository itemLabelCrossRefRepo) {
        itemRepository = itemRepo;
        itemLabelCrossRefRepository = itemLabelCrossRefRepo;
    }

    public void bindWithItem(long id) {
        if (id == ItemDetailFragment.CREATE_NEW_ITEM) {
            // we are adding a new item thus need a holder
            curItem = new Item("", "", Calendar.getInstance().getTime());
        } else {
            curItem = itemRepository.findItemById(id);
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
        return Converters.dateToString(curItem.getCreateDate());
    }

    public void setTimeStr(String timeStr) {
        this.curItem.setCreateDate(Converters.strToDate(timeStr));
    }

    public LiveData<List<Label>> getAllLabelsAssociatedWithItem() {
        return itemLabelCrossRefRepository.getLabelsByItem(curItem.getItemId());

    }
}
