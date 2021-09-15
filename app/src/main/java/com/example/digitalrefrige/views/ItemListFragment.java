package com.example.digitalrefrige.views;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.digitalrefrige.databinding.FragmentItemListBinding;
import com.example.digitalrefrige.model.dataHolder.Item;
import com.example.digitalrefrige.viewModel.ItemListViewModel;
import com.example.digitalrefrige.viewModel.adapters.ItemListAdapter;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;


/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
public class ItemListFragment extends Fragment {

    private ItemListViewModel itemListViewModel;
    private FragmentItemListBinding binding;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentItemListBinding.inflate(inflater, container, false);

        // config adapter for the recyclerView
        RecyclerView itemListRecyclerView = binding.recyclerView;
        itemListRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        itemListRecyclerView.setHasFixedSize(true);
        final ItemListAdapter itemListAdapter = new ItemListAdapter();
        itemListRecyclerView.setAdapter(itemListAdapter);

        // inject viewModel and start observing
        itemListViewModel = new ViewModelProvider(requireActivity()).get(ItemListViewModel.class);
        itemListViewModel.getAllItems().observe(getViewLifecycleOwner(), new Observer<List<Item>>() {
            @Override
            public void onChanged(List<Item> items) {
                itemListAdapter.submitList(items);
            }
        });

        // set AddButton
        binding.buttonAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections directions = (NavDirections) ItemListFragmentDirections.actionItemListFragmentToItemDetailFragment(-1, ItemDetailFragment.CREATE_NEW_ITEM);
                Navigation.findNavController(view).navigate(directions);
            }
        });


        return binding.getRoot();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}