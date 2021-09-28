package com.example.digitalrefrige.views.itemList;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.icu.number.Scale;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.digitalrefrige.MainActivity;
import com.example.digitalrefrige.databinding.FragmentItemDetailBinding;
import com.example.digitalrefrige.viewModel.ItemDetailViewModel;
import com.example.digitalrefrige.views.common.TimePickerFragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        binding.buttonCamera.setOnClickListener(this::launchCamera);
        if (itemId == CREATE_NEW_ITEM) {
            binding.buttonDelete.setVisibility(View.GONE);
            binding.buttonUpdate.setVisibility(View.GONE);
            binding.buttonAdd.setOnClickListener(this::onAddButtonClicked);
        } else {
            String curItemPhotoUri = itemDetailViewModel.getCurItem().getPhotoPath();
            if (!curItemPhotoUri.equals("none")) {
                renderImage();
            }

            binding.buttonAdd.setVisibility(View.GONE);
            binding.buttonDelete.setOnClickListener(this::onDeleteButtonClicked);
            binding.buttonUpdate.setOnClickListener(this::onUpdateButtonClicked);
        }

    }

    private void renderImage() {
        String curItemPhotoUri = itemDetailViewModel.getCurItem().getPhotoPath();
        ContentResolver contentResolver = getContext().getContentResolver();
        try {
            if (Build.VERSION.SDK_INT < 28) {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(contentResolver, Uri.fromFile(new File(curItemPhotoUri)));
                binding.imageViewItem.setImageBitmap(bitmap);
            } else {
                new Thread(() -> {
                    ImageDecoder.Source source = ImageDecoder.createSource(contentResolver, Uri.fromFile(new File(curItemPhotoUri)));
                    try {
                        Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                        binding.imageViewItem.post(() -> binding.imageViewItem.setImageBitmap(bitmap));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentItemDetailBinding.inflate(inflater, container, false);
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
                        Intent res = result.getData();
                        renderImage();
//                        Toast.makeText(getContext(), path, Toast.LENGTH_SHORT).show();
//                        if (res == null) {
//                            return;
//                        }
//                        Bundle extras = res.getExtras();
//                        Bitmap imageBitmap = (Bitmap) extras.get("data");
//                        binding.imageViewItem.setImageBitmap(imageBitmap);
//                        Toast.makeText(getContext(), "camera returned", Toast.LENGTH_SHORT).show();
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
        }, itemDetailViewModel.getCurItem().getCreateDate());
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

        itemDetailViewModel.getCurItem().setPhotoPath(image.getAbsolutePath());
        binding.photoPath.setText(image.getAbsolutePath());

        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }


}