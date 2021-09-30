package com.example.digitalrefrige.views.itemList;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
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
import com.example.digitalrefrige.model.dataHolder.Label;
import com.example.digitalrefrige.viewModel.ItemDetailViewModel;
import com.example.digitalrefrige.viewModel.adapters.LabelListAdapter;
import com.example.digitalrefrige.views.common.TimePickerFragment;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class ItemDetailFragment extends Fragment {

    public static final int CREATE_NEW_ITEM = -1;


    private FragmentItemDetailBinding binding;
    private ItemDetailViewModel itemDetailViewModel;
    ActivityResultLauncher<Intent> cameraLauncher;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // hide bottom bar
        ((MainActivity) getActivity()).mainBottomBar(false);

        // Inflate the layout for this fragment
        binding = FragmentItemDetailBinding.inflate(inflater, container, false);

        // config adapter for the recyclerView
        RecyclerView labelRecyclerView = binding.recyclerViewLabelsInDetailFragment;
        labelRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        final LabelListAdapter labelListAdapter = new LabelListAdapter();
        labelRecyclerView.setAdapter(labelListAdapter);

        // inject viewModel
        itemDetailViewModel = new ViewModelProvider(requireActivity()).get(ItemDetailViewModel.class);
        long itemId = ItemDetailFragmentArgs.fromBundle(getArguments()).getItemID();
        itemDetailViewModel.bindWithItem(itemId);
        binding.setItemDetailViewModel(itemDetailViewModel);
        itemDetailViewModel.getAllLabelsAssociatedWithItem().observe(getViewLifecycleOwner(), new Observer<List<Label>>() {
            @Override
            public void onChanged(List<Label> labels) {
                labelListAdapter.submitList(labels);
            }
        });

        // set button listener
        binding.timePickerButton.setOnClickListener(this::showTimePickerDialog);
        binding.buttonCamera.setOnClickListener(this::launchCamera);

        // change UI according to action type
        if (itemId == CREATE_NEW_ITEM) {
            binding.buttonDelete.setVisibility(View.GONE);
            binding.buttonUpdate.setVisibility(View.GONE);
            binding.buttonAdd.setOnClickListener(this::onAddButtonClicked);
        } else {
            // render item image if exist
            String curItemPhotoUri = itemDetailViewModel.getCurItem().getImgUrl();
            if (curItemPhotoUri!=null && !curItemPhotoUri.equals("")) {
                renderImage();
            }

            binding.buttonAdd.setVisibility(View.GONE);
            binding.buttonDelete.setOnClickListener(this::onDeleteButtonClicked);
            binding.buttonUpdate.setOnClickListener(this::onUpdateButtonClicked);
        }

        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        // TODO save image to storage
                        renderImage();
                    }
                }
        );
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                String msg = i + "-" + (i1 + 1) + "-" + i2;
                binding.timePickerButton.setText(msg);
            }
        },itemDetailViewModel.getCurItem().getCreateDate());
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

    public void launchCamera(View view) {
        Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // if doesn't work on emulator, get ride of this if
        if (takePicIntent.resolveActivity(getContext().getPackageManager()) != null) {
            File photo = null;
            try {
                photo = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photo != null) {
                Uri photoUri = FileProvider.getUriForFile(getContext(), "com.example.digitalrefrige.fileprovider", photo);
                takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            }
            cameraLauncher.launch(takePicIntent);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        itemDetailViewModel.getCurItem().setImgUrl(image.getAbsolutePath());
        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }

    private void renderImage() {
        String curItemPhotoUri = itemDetailViewModel.getCurItem().getImgUrl();
        Picasso.get()
                .load(Uri.fromFile(new File(curItemPhotoUri)))
                .fit()
                .centerCrop()
                .into(binding.imageViewItem);
    }


}