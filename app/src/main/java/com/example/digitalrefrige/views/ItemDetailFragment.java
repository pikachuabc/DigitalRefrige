package com.example.digitalrefrige.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.digitalrefrige.R;
import com.example.digitalrefrige.databinding.FragmentItemDetailBinding;
import com.example.digitalrefrige.model.dataHolder.Item;
import com.example.digitalrefrige.viewModel.ItemDetailViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class ItemDetailFragment extends Fragment {

    private FragmentItemDetailBinding binding;


//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    public ItemDetailFragment() {
//        // Required empty public constructor
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment ItemDetailFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static ItemDetailFragment newInstance(String param1, String param2) {
//        ItemDetailFragment fragment = new ItemDetailFragment();
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
//    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        int itemId = ItemDetailFragmentArgs.fromBundle(getArguments()).getItemID();
        ItemDetailViewModel itemDetailViewModel = new ViewModelProvider(requireActivity()).get(ItemDetailViewModel.class);
        itemDetailViewModel.bindWithItem(itemId);

        Item curItem = itemDetailViewModel.getCurItem();
        if (curItem == null) {
            // TODO something wrong....
        } else {
            binding.editTextName.setText(curItem.getName());
            binding.editTextDescription.setText(curItem.getDescription());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentItemDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}