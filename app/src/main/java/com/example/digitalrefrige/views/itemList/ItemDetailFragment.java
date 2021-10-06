package com.example.digitalrefrige.views.itemList;

import static android.app.Activity.RESULT_OK;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.core.graphics.PathUtils;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.digitalrefrige.MainActivity;
import com.example.digitalrefrige.databinding.FragmentItemDetailBinding;
import com.example.digitalrefrige.model.dataHolder.Label;
import com.example.digitalrefrige.viewModel.ItemDetailViewModel;
import com.example.digitalrefrige.viewModel.adapters.LabelListAdapter;
import com.example.digitalrefrige.views.common.LabelSelectorDialogFragment;
import com.example.digitalrefrige.views.common.TimePickerFragment;
import com.example.digitalrefrige.views.common.picSelectorDialogFragment;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    ActivityResultLauncher<Intent> photoLibraryLauncher;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // hide bottom bar
        ((MainActivity) getActivity()).mainBottomBar(false);

        // Inflate the layout for this fragment
        binding = FragmentItemDetailBinding.inflate(inflater, container, false);

        // config adapter for the recyclerView
        RecyclerView labelRecyclerView = binding.recyclerViewLabelsInDetailFragment;
        labelRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
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

        itemDetailViewModel.getAllLabels().observe(getViewLifecycleOwner(), new Observer<List<Label>>() {
            @Override
            public void onChanged(List<Label> labels) {
            }
        });

        // set button listener
        binding.timePickerButton.setOnClickListener(this::showTimePickerDialog);
        binding.buttonCamera.setOnClickListener(this::launchCameraOrSelectFromGallery);

        // change UI according to action type
        if (itemId == CREATE_NEW_ITEM) {
            binding.buttonDelete.setVisibility(View.GONE);
            binding.buttonUpdate.setVisibility(View.GONE);
            binding.buttonAdd.setOnClickListener(this::onAddButtonClicked);
        } else {
            // render item image if exist
            String curItemPhotoUri = itemDetailViewModel.getCurItem().getImgUrl();
            if (curItemPhotoUri != null && !curItemPhotoUri.equals("")) {
                renderImage();
            }

            binding.buttonAdd.setVisibility(View.GONE);
            binding.buttonDelete.setOnClickListener(this::onDeleteButtonClicked);
            binding.buttonUpdate.setOnClickListener(this::onUpdateButtonClicked);
        }

        binding.labelPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Label> allLabels = new ArrayList<>(itemDetailViewModel.getAllLabels().getValue());
                List<Label> curSelected = new ArrayList<>(itemDetailViewModel.getAllLabelsAssociatedWithItem().getValue());

                DialogFragment dialog = new LabelSelectorDialogFragment(allLabels, curSelected, new LabelSelectorDialogFragment.OnLabelsChosenListener() {
                    @Override
                    public void onPositiveClicked(List<Label> selectedLabels) {
                        //Log.d("MyLog", "selector returned");
                        itemDetailViewModel.getAllLabelsAssociatedWithItem().setValue(new ArrayList<>(selectedLabels));
//                        refreshItemList();
                    }
                });
                dialog.show(getChildFragmentManager(), "LabelSelectorFragment");
            }
        });

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

                        if (result.getResultCode() == RESULT_OK) {
                            itemDetailViewModel.getCurItem().setImgUrl(itemDetailViewModel.getTempUrl());
                        } else {
                            // user didn't take a photo
                            itemDetailViewModel.setTempUrl(itemDetailViewModel.getCurItem().getImgUrl());
                        }
                        renderImage();
                    }
                }
        );
        photoLibraryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        if (result.getData() != null) {
                            // copy image from gallery in case user delete from gallery
                            Uri uri = result.getData().getData();
                            try {
                                File photo = createImageFile();
                                InputStream is = getActivity().getContentResolver().openInputStream(uri);
                                FileOutputStream os = new FileOutputStream(photo);
                                copyStream(is, os);
                                is.close();
                                os.close();
                                Uri photoUri = FileProvider.getUriForFile(getContext(), "com.example.digitalrefrige.fileprovider", photo);
                                String imageUri = photoUri.toString();
                                itemDetailViewModel.setTempUrl(imageUri);
                                itemDetailViewModel.getCurItem().setImgUrl(imageUri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            renderImage();
                        }
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
        }, itemDetailViewModel.getCurItem().getExpireDate());
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
            itemDetailViewModel.insertItem(itemDetailViewModel.getCurItem(), itemDetailViewModel.getAllLabelsAssociatedWithItem().getValue());
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
            itemDetailViewModel.updateItem(itemDetailViewModel.getCurItem(), itemDetailViewModel.getAllLabelsAssociatedWithItem().getValue());
            Navigation.findNavController(view).popBackStack();
            Toast.makeText(getContext(), "Item updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Need item name", Toast.LENGTH_SHORT).show();
        }
    }

    public void launchCameraOrSelectFromGallery(View view) {
        String[] availableMode = new String[]{"LIBRARY", "CAMERA"};
        DialogFragment dialogFragment = new picSelectorDialogFragment(availableMode, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    Intent selectPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    photoLibraryLauncher.launch(selectPhoto);

                } else if (i == 1) {
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
                            itemDetailViewModel.setTempUrl(photoUri.toString());
                            takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        }
                        cameraLauncher.launch(takePicIntent);
                    }
                }
            }
        });
        dialogFragment.show(getParentFragmentManager(), "picSelector");


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
        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }

    private void renderImage() {
        String uri = itemDetailViewModel.getTempUrl();
        Log.d("MyLog", "rendering image: " + uri);
        if (uri.equals("")) return;
        Picasso.get()
                .load(Uri.parse(uri))
                .fit()
                .centerCrop()
                .into(binding.imageViewItem);
    }


    public static void copyStream(InputStream input, OutputStream output)
            throws IOException {

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

}