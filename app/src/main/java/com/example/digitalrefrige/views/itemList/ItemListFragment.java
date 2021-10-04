package com.example.digitalrefrige.views.itemList;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.digitalrefrige.MainActivity;
import com.example.digitalrefrige.databinding.FragmentItemListBinding;
import com.example.digitalrefrige.model.dataHolder.Item;
import com.example.digitalrefrige.viewModel.ItemListViewModel;
import com.example.digitalrefrige.viewModel.adapters.ItemListAdapter;

import java.util.ArrayList;
import java.util.Comparator;
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
        // set main nav bar visible
        ((MainActivity) getActivity()).mainBottomBar(true);
        // config adapter for the recyclerView
        RecyclerView itemListRecyclerView = binding.recyclerView;
        itemListRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        itemListRecyclerView.setHasFixedSize(true);
        final ItemListAdapter itemListAdapter = new ItemListAdapter();
        itemListRecyclerView.setAdapter(itemListAdapter);


        binding.itemFilterSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("MyLog", "onQueryTextSubmit triggered with pattern: " + query);
                itemListViewModel.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("MyLog", "onQueryTextChange triggered with pattern: " + newText);
                itemListViewModel.getFilter().filter(newText);
                return false;
            }
        });


        // inject viewModel and start observing
        itemListViewModel = new ViewModelProvider(requireActivity()).get(ItemListViewModel.class);
        itemListViewModel.getFilteredData().observe(getViewLifecycleOwner(), new Observer<List<Item>>() {
            @Override
            public void onChanged(List<Item> items) {
                List<Item> newList = new ArrayList<>(items);
                // order by date
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    newList.sort(Comparator.comparing(Item::getCreateDate));
                }
                itemListAdapter.submitList(newList);

            }
        });
        SearchView searchBox = binding.itemFilterSearchView;
        itemListViewModel.getAllItems().observe(getViewLifecycleOwner(), new Observer<List<Item>>() {
            @Override
            public void onChanged(List<Item> items) {
                if (items.size() == 0) return;
                Log.d("MyLog", "allItem changed, current size: " + items.size());
                if (TextUtils.isEmpty(searchBox.getQuery())) {
                    // no query available in searchView, put all items into filteredData
                    itemListViewModel.updateFilteredItemList(items);
                } else {
                    // make changes on current queried item list
                    searchBox.setQuery(searchBox.getQuery(), true);
                }
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
                Item toDelete = itemListAdapter.getCurrentList().get(viewHolder.getAdapterPosition());
                itemListViewModel.deleteItem(toDelete);
                Toast.makeText(getContext(), "Item deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(itemListRecyclerView);

        // set Add Button
        binding.buttonAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections directions = (NavDirections) ItemListFragmentDirections.actionItemListFragmentToItemDetailFragment(ItemDetailFragment.CREATE_NEW_ITEM);
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