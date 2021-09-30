package com.example.digitalrefrige.model;

import androidx.lifecycle.LiveData;

import com.example.digitalrefrige.model.dataHolder.Label;
import com.example.digitalrefrige.model.dataSource.LabelDAO;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LabelRepository {
    private LiveData<List<Label>> allLabels;
    private ExecutorService executorService;

    private LabelDAO labelDAO;

    public LabelRepository(LabelDAO dao) {
        labelDAO = dao;
        executorService = Executors.newFixedThreadPool(2);
        allLabels = labelDAO.getAllLabels();
    }

    public LiveData<List<Label>> getAllLabels() {
        return allLabels;
    }

    public long insertLabel(Label label) {
        Future<Long> insertRes = executorService.submit(() -> labelDAO.insertLabel(label));
        try {
            return insertRes.get();
        } catch (Exception e) {
            e.printStackTrace();
            return -1L;
        }
    }

    public void updateLabel(Label label) {
        executorService.execute(() -> labelDAO.updateLabel(label));
    }

    public void deleteLabel(Label label) {
        executorService.execute(() -> labelDAO.deleteLabel(label));
    }

    public Label findLabelById(int id) {
        Future<Label> labelFuture = executorService.submit(() -> labelDAO.findLabelById(id));
        try {
            return labelFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
