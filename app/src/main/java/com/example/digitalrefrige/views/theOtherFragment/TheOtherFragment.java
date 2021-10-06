package com.example.digitalrefrige.views.theOtherFragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.digitalrefrige.R;
import com.example.digitalrefrige.databinding.FragmentItemListBinding;
import com.example.digitalrefrige.databinding.FragmentTheOtherBinding;
import com.example.digitalrefrige.model.dataHolder.Item;
import com.example.digitalrefrige.model.dataHolder.Label;
import com.example.digitalrefrige.model.dataQuery.LabelWithItems;
import com.example.digitalrefrige.viewModel.ItemListViewModel;
import com.example.digitalrefrige.viewModel.LabelListViewModel;
import com.example.digitalrefrige.viewModel.adapters.ItemListAdapter;
import com.example.digitalrefrige.viewModel.adapters.LabelListAdapter;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class TheOtherFragment extends Fragment {

    public LabelListViewModel labelListViewModel;
    private FragmentTheOtherBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        labelListViewModel = new ViewModelProvider(requireActivity()).get(LabelListViewModel.class);
        binding = FragmentTheOtherBinding.inflate(inflater, container, false);
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