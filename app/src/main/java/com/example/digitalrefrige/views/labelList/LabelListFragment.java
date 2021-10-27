package com.example.digitalrefrige.views.labelList;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.digitalrefrige.databinding.FragmentLabelListBinding;
import com.example.digitalrefrige.databinding.FragmentTheOtherBinding;
import com.example.digitalrefrige.model.dataHolder.Item;
import com.example.digitalrefrige.model.dataHolder.Label;
import com.example.digitalrefrige.model.dataQuery.LabelWithItems;
import com.example.digitalrefrige.viewModel.LabelListViewModel;
import com.example.digitalrefrige.viewModel.adapters.LabelListAdapter;

import java.util.Arrays;
import java.util.List;

public class LabelListFragment extends Fragment {
    public LabelListViewModel labelListViewModel;
    private FragmentLabelListBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentLabelListBinding.inflate(inflater, container, false);
        RecyclerView labelListRecyclerView = binding.labelListRecyclerView;
        labelListRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        labelListRecyclerView.setHasFixedSize(true);
        final LabelListAdapter labelListAdapter = new LabelListAdapter();
        labelListRecyclerView.setAdapter(labelListAdapter);
        binding.buttonAddLabel.setOnClickListener(this::onAddButtonClicked);

        // inject viewModel and start observing
        labelListViewModel = new ViewModelProvider(requireActivity()).get(LabelListViewModel.class);


        labelListViewModel.getAllLabels().observe(getViewLifecycleOwner(), new Observer<List<Label>>() {
            @Override
            public void onChanged(List<Label> label) {
                Log.d(String.valueOf(label.size()),"dddd");
                labelListAdapter.submitList(label);

            }
        });



        // enable swipe delete on our recyclerView
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Label toDelete = labelListAdapter.getCurrentList().get(viewHolder.getAdapterPosition());
                labelListViewModel.deleteLabel(toDelete);
                Toast.makeText(getContext(), "label deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(labelListRecyclerView);



        return binding.getRoot();

    }

    public void onAddButtonClicked(View view) {
        long r;
        binding.labelEditText.clearFocus();
        String vaildLabel = binding.labelEditText.getText().toString();
        if(labelListViewModel.findLabelByTitle(vaildLabel)== null){
            r = labelListViewModel.insertLabel(new Label(binding.labelEditText.getText().toString()));
        }else{
            r = -1L;
        }
        if(r == -1L){
            Toast.makeText(getContext(), "failed to add", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(), "label added", Toast.LENGTH_SHORT).show();
        }

    }

}
