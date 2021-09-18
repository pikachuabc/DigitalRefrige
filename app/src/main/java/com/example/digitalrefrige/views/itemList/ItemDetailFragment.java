package com.example.digitalrefrige.views.itemList;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.digitalrefrige.MainActivity;
import com.example.digitalrefrige.R;
import com.example.digitalrefrige.databinding.FragmentItemDetailBinding;
import com.example.digitalrefrige.model.dataHolder.Item;
import com.example.digitalrefrige.viewModel.ItemDetailViewModel;
import com.example.digitalrefrige.views.common.TimePickerFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class ItemDetailFragment extends Fragment {

    public static final int CREATE_NEW_ITEM = -1;


    private FragmentItemDetailBinding binding;
    private ItemDetailViewModel itemDetailViewModel;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // inject viewModel
        itemDetailViewModel = new ViewModelProvider(requireActivity()).get(ItemDetailViewModel.class);
        // hide bottom bar
        ((MainActivity) getActivity()).mainBottomBar(false);

        // prepare viewModel and bind model into xml(view)
        int itemId = ItemDetailFragmentArgs.fromBundle(getArguments()).getItemID();
        itemDetailViewModel.bindWithItem(itemId);
        binding.setItemDetailViewModel(itemDetailViewModel);

        // set button listener
        binding.timePickerButton.setOnClickListener(this::showTimePickerDialog);
        if (itemId == CREATE_NEW_ITEM) {
            binding.buttonDelete.setVisibility(View.GONE);
            binding.buttonUpdate.setVisibility(View.GONE);
            binding.buttonAdd.setOnClickListener(this::onAddButtonClicked);
        } else {
            binding.buttonAdd.setVisibility(View.GONE);
            binding.buttonDelete.setOnClickListener(this::onDeleteButtonClicked);
            binding.buttonUpdate.setOnClickListener(this::onUpdateButtonClicked);
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentItemDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                String msg = i + "-" + (i1 + 1) + "-" + i2;
                binding.timePickerButton.setText(msg);
            }
        });
        newFragment.show(getParentFragmentManager(), "timePicker");
    }

    public void onDeleteButtonClicked(View view) {
        binding.editTextName.clearFocus();
        binding.editTextDescription.clearFocus();
        itemDetailViewModel.deleteCurItem();
        Navigation.findNavController(view).popBackStack();
        Toast.makeText(getContext(), "item deleted", Toast.LENGTH_SHORT).show();
    }

    public void onAddButtonClicked(View view) {
        binding.editTextName.clearFocus();
        binding.editTextDescription.clearFocus();
        if (!"".equals(itemDetailViewModel.getCurItem().getName())) {
            // TODO need get date from text
            itemDetailViewModel.insertItem(itemDetailViewModel.getCurItem());
            Navigation.findNavController(view).popBackStack();
            Toast.makeText(getContext(), "Item added", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Need item name", Toast.LENGTH_SHORT).show();
        }
    }

    public void onUpdateButtonClicked(View view) {
        binding.editTextName.clearFocus();
        binding.editTextDescription.clearFocus();
        if (!"".equals(itemDetailViewModel.getCurItem().getName())) {
            // TODO need get date from text
            itemDetailViewModel.updateItem(itemDetailViewModel.getCurItem());
            Navigation.findNavController(view).popBackStack();
            Toast.makeText(getContext(), "Item updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Need item name", Toast.LENGTH_SHORT).show();
        }
    }


}