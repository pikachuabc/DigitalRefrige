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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.digitalrefrige.MainActivity;
import com.example.digitalrefrige.databinding.FragmentItemListBinding;
import com.example.digitalrefrige.model.dataHolder.Item;
import com.example.digitalrefrige.model.dataHolder.Label;
import com.example.digitalrefrige.model.dataQuery.ItemWithLabels;
import com.example.digitalrefrige.viewModel.ItemListViewModel;
import com.example.digitalrefrige.viewModel.adapters.ItemListAdapter;
import com.example.digitalrefrige.views.common.LabelSelectorDialogFragment;
import com.google.android.material.appbar.AppBarLayout;

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
        RecyclerView itemListRecyclerView = binding.itemListRecyclerView;
        itemListRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        itemListRecyclerView.setHasFixedSize(true);
        final ItemListAdapter itemListAdapter = new ItemListAdapter();
        itemListRecyclerView.setAdapter(itemListAdapter);


        binding.itemFilterSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                itemListViewModel.getFilter().filter(newText);
                return true;
            }
        });


        // inject viewModel and start observing
        itemListViewModel = new ViewModelProvider(requireActivity()).get(ItemListViewModel.class);
        itemListViewModel.getFilteredItems().observe(getViewLifecycleOwner(), new Observer<List<Item>>() {
            @Override
            public void onChanged(List<Item> items) {
                List<Item> newList = new ArrayList<>(items);
                // order by date
                newList.sort(Comparator.comparing(Item::getExpireDate));
                itemListAdapter.submitList(newList);
            }
        });
        itemListViewModel.getAllItems().observe(getViewLifecycleOwner(), new Observer<List<Item>>() {
            @Override
            public void onChanged(List<Item> items) {
                refreshItemList();
            }
        });
        itemListViewModel.getAllLabels().observe(getViewLifecycleOwner(), new Observer<List<Label>>() {
            @Override
            public void onChanged(List<Label> labels) {
                if (itemListViewModel.getCurSelectedLabel().size() == 0) {
                    itemListViewModel.setCurSelectedLabel(new ArrayList<>(labels));
                } else {
                    refreshItemList();
                }
            }
        });
        itemListViewModel.getAllItemsWithLabels().observe(getViewLifecycleOwner(), new Observer<List<ItemWithLabels>>() {
            @Override
            public void onChanged(List<ItemWithLabels> itemWithLabels) {
                refreshItemList();
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

        binding.labelPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Label> allLabels = new ArrayList<>(itemListViewModel.getAllLabels().getValue());
                List<Label> curSelected = new ArrayList<>(itemListViewModel.getCurSelectedLabel());
                DialogFragment dialog = new LabelSelectorDialogFragment(allLabels, curSelected, new LabelSelectorDialogFragment.OnLabelsChosenListener() {
                    @Override
                    public void onPositiveClicked(List<Label> selectedLabels) {
                        //Log.d("MyLog", "selector returned");
                        itemListViewModel.setCurSelectedLabel(new ArrayList<>(selectedLabels));
                        refreshItemList();
                    }
                });
                dialog.show(getChildFragmentManager(), "LabelSelectorFragment");
            }
        });

        // resolve scroll conflict between recyclerView and SwipeRefreshLayout
        itemListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition = recyclerView.getChildCount() == 0 ? 0 : recyclerView.getChildAt(0).getTop();
                binding.refreshList.setEnabled(topRowVerticalPosition >= 0);

            }
        });
        binding.appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                binding.refreshList.setEnabled(verticalOffset == 0);
            }
        });

        binding.refreshList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItemList();
                binding.refreshList.setRefreshing(false);
            }
        });

        return binding.getRoot();
    }

    public void refreshItemList() {
        itemListViewModel.getFilter().filter(binding.itemFilterSearchView.getQuery());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

}