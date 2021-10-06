package com.example.digitalrefrige.views.labelList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.digitalrefrige.databinding.FragmentLabelListBinding;
import com.example.digitalrefrige.databinding.FragmentTheOtherBinding;
import com.example.digitalrefrige.model.dataHolder.Label;
import com.example.digitalrefrige.model.dataQuery.LabelWithItems;
import com.example.digitalrefrige.viewModel.LabelListViewModel;
import com.example.digitalrefrige.viewModel.adapters.LabelListAdapter;

import java.util.List;

public class LabelListFragment extends Fragment {
    public LabelListViewModel labelListViewModel;
    private FragmentLabelListBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        labelListViewModel = new ViewModelProvider(requireActivity()).get(LabelListViewModel.class);
        binding = FragmentLabelListBinding.inflate(inflater, container, false);
        RecyclerView labelListRecyclerView = binding.labelListRecyclerView;
        labelListRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        labelListRecyclerView.setHasFixedSize(true);
        final LabelListAdapter labelListAdapter = new LabelListAdapter();
        labelListRecyclerView.setAdapter(labelListAdapter);


        // inject viewModel and start observing

        labelListViewModel.getAllLabels().observe(getViewLifecycleOwner(), new Observer<List<Label>>() {
            @Override
            public void onChanged(List<Label> label) {
                labelListAdapter.submitList(label);
            }
        });

        labelListViewModel.getAllItemsWithLabels().observe(getViewLifecycleOwner(), new Observer<List<LabelWithItems>>() {
            @Override
            public void onChanged(List<LabelWithItems> labelWithItems) {

            }
        });




        return binding.getRoot();

    }
}
