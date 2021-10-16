package com.example.digitalrefrige.views.theOtherFragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.digitalrefrige.databinding.FragmentTheOtherBinding;
import com.example.digitalrefrige.viewModel.LabelListViewModel;

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