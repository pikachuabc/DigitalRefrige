package com.example.digitalrefrige.model;

import androidx.lifecycle.LiveData;

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

    private ExecutorService executorService;

    private ItemLabelCrossRefDAO itemLabelCrossRefDAO;

    public ItemLabelCrossRefRepository(ItemLabelCrossRefDAO dao) {
        itemLabelCrossRefDAO = dao;
        executorService = Executors.newFixedThreadPool(2);
        labelListWithItems = dao.getLabelOfItems();
        itemListWithLabels = dao.getItemOfLabels();

    }

    public LiveData<List<LabelWithItems>> getAllLabelList() {
        return labelListWithItems;
    }

    public LiveData<List<ItemWithLabels>> getAllItemList() {
        return itemListWithLabels;
    }

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

    public void updateItemLabels(ItemWithLabels itemWithLabels) {
        executorService.execute(() -> itemLabelCrossRefDAO.updateItemLabels(itemWithLabels));
    }



}
