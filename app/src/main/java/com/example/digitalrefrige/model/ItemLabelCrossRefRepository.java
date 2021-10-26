package com.example.digitalrefrige.model;

import androidx.lifecycle.LiveData;

import com.example.digitalrefrige.model.dataHolder.Item;
import com.example.digitalrefrige.model.dataHolder.ItemLabelCrossRef;
import com.example.digitalrefrige.model.dataHolder.Label;
import com.example.digitalrefrige.model.dataQuery.ItemWithLabels;
import com.example.digitalrefrige.model.dataQuery.LabelWithItems;
import com.example.digitalrefrige.model.dataSource.ItemLabelCrossRefDAO;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ItemLabelCrossRefRepository {
    private LiveData<List<LabelWithItems>> labelListWithItems;
    private LiveData<List<ItemWithLabels>> itemListWithLabels;
    private LiveData<List<ItemLabelCrossRef>> allCrossRefs;

    private ExecutorService executorService;

    private ItemLabelCrossRefDAO itemLabelCrossRefDAO;

    public ItemLabelCrossRefRepository(ItemLabelCrossRefDAO dao) {
        itemLabelCrossRefDAO = dao;
        executorService = Executors.newFixedThreadPool(2);
        labelListWithItems = dao.getLabelOfItems();
        itemListWithLabels = dao.getItemOfLabels();
        allCrossRefs = dao.getAllCrossRefs();

    }

    public LiveData<List<LabelWithItems>> getAllLabelList() {
        return labelListWithItems;
    }

    public LiveData<List<ItemWithLabels>> getAllItemList() {
        return itemListWithLabels;
    }

    public LiveData<List<ItemLabelCrossRef>> getAllCrossRefs(){ return allCrossRefs; }

    public List<Label> getLabelsByItem(long itemId) {
        Future<List<Label>> res = executorService.submit(() -> itemLabelCrossRefDAO.getLabelsByItem(itemId));
        try {
            return res.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public long insertItemLabelCrossRef(ItemLabelCrossRef itemLabelCrossRef) {
        Future<Long> insertRes = executorService.submit(() -> itemLabelCrossRefDAO.insertItemLabelCrossRef(itemLabelCrossRef));
        try {
            return insertRes.get();
        } catch (Exception e) {
            e.printStackTrace();
            return -1L;
        }
    }

    public void updateItemLabelCrossRef(ItemLabelCrossRef itemLabelCrossRef) {
        executorService.execute(() -> itemLabelCrossRefDAO.updateItemLabelCrossRef(itemLabelCrossRef));
    }

    public void deleteItemLabelCrossRef(ItemLabelCrossRef itemLabelCrossRef) {
        executorService.execute(() -> itemLabelCrossRefDAO.deleteItemLabelCrossRef(itemLabelCrossRef.getItemId()));
    }


    public void updateItemLabels(ItemWithLabels itemWithLabels) {
        executorService.execute(() -> itemLabelCrossRefDAO.updateItemLabels(itemWithLabels));
    }



}
