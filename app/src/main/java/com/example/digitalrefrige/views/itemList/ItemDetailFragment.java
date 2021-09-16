package com.example.digitalrefrige.views.itemList;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.digitalrefrige.MainActivity;
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

    public static final int EDIT_OR_DELETE_ITEM = 0;
    public static final int CREATE_NEW_ITEM = 1;


    private FragmentItemDetailBinding binding;
    private ItemDetailViewModel itemDetailViewModel;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // inject viewModel
        itemDetailViewModel = new ViewModelProvider(requireActivity()).get(ItemDetailViewModel.class);
        //
        ((MainActivity) getActivity()).mainBottomBar(false);
        // distinguish add item or edit item
        int opeMode = ItemDetailFragmentArgs.fromBundle(getArguments()).getOperationMode();
        if (opeMode == EDIT_OR_DELETE_ITEM) {
            int itemId = ItemDetailFragmentArgs.fromBundle(getArguments()).getItemID();
            itemDetailViewModel.bindWithItem(itemId);
            Item curItem = itemDetailViewModel.getCurItem();
            if (curItem == null) {
                // TODO something wrong....
            } else {
                binding.editTextName.setText(curItem.getName());
                binding.editTextDescription.setText(curItem.getDescription());

                binding.buttonDeleteOrAdd.setText(R.string.delete_current_item);
                binding.buttonDeleteOrAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        binding.editTextName.clearFocus();
                        binding.editTextDescription.clearFocus();
                        itemDetailViewModel.deleteCurItem();
                        Navigation.findNavController(view).popBackStack();
                    }
                });
            }
        } else if (opeMode == CREATE_NEW_ITEM) {
            binding.editTextName.setHint("add item");
            binding.editTextDescription.setHint("add item description");
            binding.buttonDeleteOrAdd.setText(R.string.add_new_item);
            binding.buttonDeleteOrAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String itemName = binding.editTextName.getText().toString();
                    String itemDescription = binding.editTextDescription.getText().toString();
                    binding.editTextName.clearFocus();
                    binding.editTextDescription.clearFocus();
                    if (!"".equals(itemName)) {
                        itemDetailViewModel.insertItem(new Item(itemName, itemDescription));
                        Navigation.findNavController(view).popBackStack();
                    } else {
                        Toast.makeText(getContext(),"Need item name",Toast.LENGTH_SHORT).show();
                    }
                }
            });
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