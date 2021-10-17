package com.example.digitalrefrige.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.digitalrefrige.model.ItemLabelCrossRefRepository;
import com.example.digitalrefrige.model.LabelRepository;
import com.example.digitalrefrige.model.dataHolder.Item;
import com.example.digitalrefrige.model.dataHolder.Label;
import com.example.digitalrefrige.model.dataQuery.ItemWithLabels;
import com.example.digitalrefrige.model.dataQuery.LabelWithItems;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class LabelListViewModel extends ViewModel {

    private LabelRepository labelRepository;
    private ItemLabelCrossRefRepository itemLabelCrossRefRepository;

    private LiveData<List<Label>> allLabels;
    private LiveData<List<LabelWithItems>> allLabelsWithItems;


    @Inject
    public LabelListViewModel(LabelRepository labelRepo, ItemLabelCrossRefRepository itemLabelCrossRepo){
        labelRepository = labelRepo;
        itemLabelCrossRefRepository = itemLabelCrossRepo;
        allLabels = labelRepository.getAllLabels();
        allLabelsWithItems = itemLabelCrossRefRepository.getAllLabelList();
    }

    public LiveData<List<LabelWithItems>> getAllItemsWithLabels() {
        return allLabelsWithItems;
    }

    public LiveData<List<Label>> getAllLabels() {
        return allLabels;
    }

    public long insertLabel(Label label){return labelRepository.insertLabel(label);}

    public void deleteLabel(Label label){labelRepository.deleteLabel(label);}

    public void updateLabel(Label label){labelRepository.updateLabel(label);}


}