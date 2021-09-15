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

//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    public ItemListFragment() {
        // Required empty public constructor
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment ItemListFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static ItemListFragment newInstance(String param1, String param2) {
//        ItemListFragment fragment = new ItemListFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//
//    }

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


        return binding.getRoot();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}