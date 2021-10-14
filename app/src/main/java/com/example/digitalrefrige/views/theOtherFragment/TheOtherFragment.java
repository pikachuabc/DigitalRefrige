package com.example.digitalrefrige.views.theOtherFragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
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
import com.example.digitalrefrige.views.itemList.ItemDetailFragment;
import com.example.digitalrefrige.views.itemList.ItemListFragmentDirections;
import com.example.digitalrefrige.views.labelList.LabelListFragment;
import com.example.digitalrefrige.views.login.LoginFragment;

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

        binding = FragmentTheOtherBinding.inflate(inflater, container, false);
        // button to label list

        return binding.getRoot();

    }


}