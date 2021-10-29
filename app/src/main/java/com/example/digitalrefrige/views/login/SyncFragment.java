package com.example.digitalrefrige.views.login;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.digitalrefrige.databinding.FragmentSyncBinding;
import com.example.digitalrefrige.model.dataHolder.Item;
import com.example.digitalrefrige.model.dataHolder.ItemLabelCrossRef;
import com.example.digitalrefrige.model.dataHolder.Label;
import com.example.digitalrefrige.model.dataSource.LocalDataBase;
import com.example.digitalrefrige.utils.Converters;
import com.example.digitalrefrige.viewModel.SyncViewModel;
import com.example.digitalrefrige.viewModel.UserProfileViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SyncFragment extends Fragment {

    private FragmentSyncBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private UserProfileViewModel userProfileViewModel;
    private SyncViewModel syncViewModel;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private Converters c;
    private OutputStream outputStream;
    FirebaseUser user;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSyncBinding.inflate(inflater, container, false);
        syncViewModel = new ViewModelProvider(requireActivity()).get(SyncViewModel.class);
        userProfileViewModel = new ViewModelProvider(requireActivity()).get(UserProfileViewModel.class);
        user = userProfileViewModel.mAuth.getCurrentUser();

        // fetch last upload time from fireCloud
        getTime("uploadTime");
        // fetch last fetch time from fireCloud
        getTime("fetchTime");

        binding.backUpStorage.setOnClickListener(button -> uploadData());
        binding.fetchData.setOnClickListener(button -> fetchData());

        return binding.getRoot();
    }

    public void uploadData() {

        uploadItemTable();
        uploadLabelTable();
        uploadCrossRefTable();
        Toast.makeText(getContext(), "Upload Successfully", Toast.LENGTH_SHORT).show();


        Calendar calendar = Calendar.getInstance();
        String timeDiff = c.getDayDifferences(format.format(calendar.getTime())) + " days ago";
        binding.uploadRecord.setText(timeDiff);

        Map<String, String> uploadTime = new HashMap<>();
        uploadTime.put("uploadTime", timeDiff);
        uploadTime.put("user", user.getUid());

        updateTime(uploadTime);
    }

    public void fetchData() {

        fetchItemTable();
        fetchLabelTable();
        fetchCrossRefTable();

        Toast.makeText(getContext(), "Fetch Successfully", Toast.LENGTH_SHORT).show();

        Calendar calendar = Calendar.getInstance();
        String timeDiff = c.getDayDifferences(format.format(calendar.getTime())) + " days ago";
        binding.fetchRecord.setText(timeDiff);

        Map<String, String> fetchTime = new HashMap<>();
        fetchTime.put("fetchTime", timeDiff);
        fetchTime.put("user", user.getUid());

        updateTime(fetchTime);

    }

    public void uploadItemTable() {
        // get user's cloud items
        // check which of them are not in the local database, and remove


        List<Item> localItems = getLocalItem();

        db.collection("item_table")
                .whereEqualTo("user", user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        HashMap<Item, String> cloudItems = new HashMap<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            cloudItems.put(document.toObject(Item.class), document.getId());
                            if (!localItems.contains(document.toObject(Item.class))) {
                                db.collection("item_table").document(document.getId()).delete();
                            }
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }

                        // For every local item, check whether it is in cloud
                        // if it not, add
                        // if it is,  set
                        for (Item i : localItems) {
                            Map<String, Object> item = new HashMap<>();
                            item.put("name", i.getName());
                            item.put("description", i.getDescription());
                            item.put("expireDate", i.getExpireDate());
                            item.put("itemId", i.getItemId());
                            item.put("quantity", i.getQuantity());
                            item.put("imgUrl", i.getImgUrl());
                            item.put("user", user.getUid());

                            if (!cloudItems.keySet().contains(i)) {
                                db.collection("item_table").add(item).addOnCompleteListener(t -> {
                                    if (t.isSuccessful()) {
                                        DocumentReference document = t.getResult();
                                        if (i.getImgUrl().length() != 0) {
                                            convertImageUrlToCloud(i.getImgUrl(), i.getItemId(), document.getId());
                                        }
                                    } else {
                                        System.out.print("Error adding documents");
                                    }
                                });
                            } else {
                                db.collection("item_table").document(cloudItems.get(i)).set(item);
                                if (i.getImgUrl().length() != 0) {
                                    convertImageUrlToCloud(i.getImgUrl(), i.getItemId(), cloudItems.get(i));
                                }
                            }

                        }

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    public void uploadLabelTable() {
        // get user's cloud items
        // check which of them are not in the local database, and remove

        List<Label> localLabels = getLocalLabel();

        db.collection("label_table")
                .whereEqualTo("user", user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        HashMap<Label, String> cloudLabels = new HashMap<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            cloudLabels.put(document.toObject(Label.class), document.getId());
                            if (!localLabels.contains(document.toObject(Label.class))) {
                                db.collection("label_table").document(document.getId()).delete();
                            }
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }

                        // For every local item, check whether it is in cloud
                        // if it not, add
                        // if it is,  set
                        for (Label i : localLabels) {
                            Map<String, Object> label = new HashMap<>();
                            label.put("labelID", i.getLabelId());
                            label.put("title", i.getTitle());
                            label.put("user", user.getUid());
                            if (!cloudLabels.keySet().contains(i)) {
                                db.collection("label_table").add(label);
                            } else {
                                db.collection("label_table").document(cloudLabels.get(i)).set(label);
                            }
                        }

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    public void uploadCrossRefTable() {
        // get user's cloud items
        // check which of them are not in the local database, and remove

        List<ItemLabelCrossRef> localRefs = getLocalCrossRef();

        db.collection("itemlabelcrossref_table")
                .whereEqualTo("user", user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        HashMap<ItemLabelCrossRef, String> cloudRefs = new HashMap<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            cloudRefs.put(document.toObject(ItemLabelCrossRef.class), document.getId());
                            if (!localRefs.contains(document.toObject(ItemLabelCrossRef.class))) {
                                db.collection("itemlabelcrossref_table").document(document.getId()).delete();
                            }
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }

                        // For every local item, check whether it is in cloud
                        // if it not, add
                        // if it is,  set
                        for (ItemLabelCrossRef i : localRefs) {
                            Map<String, Object> ref = new HashMap<>();
                            ref.put("labelID", i.getLabelId());
                            ref.put("itemID", i.getItemId());
                            ref.put("user", user.getUid());
                            if (!cloudRefs.keySet().contains(i)) {
                                db.collection("itemlabelcrossref_table").add(ref);
                            } else {
                                db.collection("itemlabelcrossref_table").document(cloudRefs.get(i)).set(ref);
                            }
                        }

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    public void updateTime(Map<String, String> uploadTime) {
        db.collection("update_time")
                .whereEqualTo("user", user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().size() > 0) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection("update_time").document(document.getId()).set(uploadTime);
                            }
                        } else {
                            db.collection("update_time").add(uploadTime);
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    public void getTime(String timeName) {

        db.collection("update_time")
                .whereEqualTo("user", user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().size() > 0) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection("update_time").document(document.getId()).addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                        if (timeName == "uploadTime") {
                                            binding.uploadRecord.setText(value.getString(timeName));
                                        } else if (timeName == "fetchTime") {
                                            binding.fetchRecord.setText(value.getString(timeName));
                                        }

                                    }
                                });
                            }
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    public void fetchItemTable() {

        // get user's local items
        // check which of them are not in the cloud database, and remove in local
        List<Item> localItems = getLocalItem();
        db.collection("item_table")
                .whereEqualTo("user", user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Item> cloudItems = new ArrayList<>();
                        if (!task.getResult().isEmpty()) {
                            // For every cloud item, check whether it is in local
                            // if it not, insert
                            // if it is,  update
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Item i = document.toObject(Item.class);
                                cloudItems.add(i);
                                if (!localItems.contains(i)) {
                                    syncViewModel.insertItem(i);
                                    if (i.getImgUrl().length() != 0) {
                                        convertImageUrlToLocal(i.getImgUrl(), i);
                                    }
                                } else {
                                    syncViewModel.updateItemWithNotURL(i);
                                }
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        }
                        // removeLocalExtra
                        for (Item i : localItems) {
                            if (!cloudItems.contains(i)) {
                                syncViewModel.deleteItem(i);
                            }
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    public void fetchLabelTable() {

        List<Label> localLabels = getLocalLabel();
        db.collection("label_table")
                .whereEqualTo("user", user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Label> cloudLabels = new ArrayList<>();
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Label i = document.toObject(Label.class);
                                cloudLabels.add(i);
                                if (!localLabels.contains(i)) {
                                    syncViewModel.insertLabel(i);
                                } else {
                                    syncViewModel.updateLabel(i);
                                }
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        }
                        // removeLocalExtra
                        for (Label i : localLabels) {
                            if (!cloudLabels.contains(i)) {
                                syncViewModel.deleteLabel(i);
                            }
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    public void fetchCrossRefTable() {

        // get user's local items
        // check which of them are not in the cloud database, and remove in local
        List<ItemLabelCrossRef> localRefs = getLocalCrossRef();
        db.collection("itemlabelcrossref_table")
                .whereEqualTo("user", user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<ItemLabelCrossRef> cloudRefs = new ArrayList<>();
                        if (!task.getResult().isEmpty()) {
                            // For every cloud item, check whether it is in local
                            // if it not, insert
                            // if it is,  update
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ItemLabelCrossRef i = document.toObject(ItemLabelCrossRef.class);
                                cloudRefs.add(i);
                                if (!localRefs.contains(i)) {
                                    syncViewModel.insertRef(i);
                                } else {
                                    syncViewModel.updateRef(i);
                                }
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        }
                        // removeLocalExtra
                        for (ItemLabelCrossRef i : localRefs) {
                            if (!cloudRefs.contains(i)) {
                                syncViewModel.deleteRef(i);
                            }
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    public List<Item> getLocalItem() {

        List<Item> localItems = new ArrayList<>();
        syncViewModel.getAllItems().observe(getViewLifecycleOwner(), new Observer<List<Item>>() {
            @Override
            public void onChanged(List<Item> items) {
                localItems.addAll(items);
            }
        });
        return localItems;
    }

    public List<Label> getLocalLabel() {
        List<Label> localLabels = new ArrayList<>();
        syncViewModel.getAllLables().observe(getViewLifecycleOwner(), new Observer<List<Label>>() {
            @Override
            public void onChanged(List<Label> labels) {
                localLabels.addAll(labels);
            }
        });
        return localLabels;
    }

    public List<ItemLabelCrossRef> getLocalCrossRef() {
        List<ItemLabelCrossRef> localRefs = new ArrayList<>();
        syncViewModel.getAllCrossRefs().observe(getViewLifecycleOwner(), new Observer<List<ItemLabelCrossRef>>() {
            @Override
            public void onChanged(List<ItemLabelCrossRef> refs) {
                localRefs.addAll(refs);
            }
        });
        return localRefs;
    }

    public void convertImageUrlToCloud(String url, long itemID, String documentID) {

        Map<String, String> data = new HashMap<>();

        Uri imageUri = Uri.parse(url);
        String imageName = "item" + itemID + "." + getFileExtension(imageUri);
        StorageReference itemRef = firebaseStorage.getReference().child(user.getUid() + "/" + "item" + itemID + "/" + imageName);
        itemRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    itemRef.getDownloadUrl()
                            .addOnSuccessListener(downloadURL -> {
                                Log.d(TAG, "url: " + downloadURL.toString());
                                data.put("imgUrl", downloadURL.toString());
                                Log.d(TAG, "data size: " + data.size());
                                Log.d(TAG, "doc ID: " + documentID);
                                db.collection("item_table").
                                        document(documentID).set(data, SetOptions.merge());
                            });
                    //Toast.makeText(getContext(), "Update image Url successfully", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast.makeText(getContext(), "Image update failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public void convertImageUrlToLocal(String url, Item item) {

//        if (ContextCompat.checkSelfPermission(getActivity(),
//                Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            saveImage(url, item);
//        }else{
//            askPermission();
//        }
        saveImage(url, item);
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                1);

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

    private void saveImage(String url, Item item) {
        StorageReference itemRef = firebaseStorage.getReferenceFromUrl(url);
        try {

            File localFile = createImageFile();
            Context context_1 = getContext();
            itemRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                Toast.makeText(context_1, "Image successfully saved", Toast.LENGTH_SHORT).show();
                Uri photoUri = FileProvider.getUriForFile(getContext(), "com.example.digitalrefrige.fileprovider", localFile);
                String imageUri = photoUri.toString();
                syncViewModel.updateItemImageURL(item, imageUri);
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Toast.makeText(context_1, "Image save failed", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
