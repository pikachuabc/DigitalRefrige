package com.example.digitalrefrige.views.login;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
import java.util.HashMap;
import java.util.List;
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
    //private FirebaseUser user = userProfileViewModel.mAuth.getCurrentUser();


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSyncBinding.inflate(inflater, container, false);
        syncViewModel = new ViewModelProvider(requireActivity()).get(SyncViewModel.class);


        // fetch last upload time from fireCloud
        DocumentReference uploadR = db.collection("update_time").document("last_upload");
        if(uploadR!=null){
            uploadR.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    binding.uploadRecord.setText(value.getString("value"));
                }
            });
        }



        // fetch last fetch time from fireCloud
        DocumentReference fetchR = db.collection("update_time").document("last_fetch");
        if(fetchR!=null){
            fetchR.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    binding.fetchRecord.setText(value.getString("value"));
                }
            });

        }


        binding.backUpStorage.setOnClickListener(button -> uploadData());
        binding.fetchData.setOnClickListener(button -> fetchData());

        return binding.getRoot();
    }

    public void uploadData() {

        List<String> itemIDs = new ArrayList<>();
        List<String> labelIDs = new ArrayList<>();
        List<String> refIDs = new ArrayList<>();

//        Map<String, String> owner = new HashMap<>();
//        owner.put("user", user.getUid());


        List<Item> localItems = getLocalItem();
        for(Item i: localItems){
            itemIDs.add("item" + i.getItemId());
            db.collection("item_table").document("item" + i.getItemId()).set(i);
            //db.collection("item_table").document("item" + i.getItemId()).set(owner, SetOptions.merge());
            if(i.getImgUrl().length() != 0){
                convertImageUrlToCloud(i.getImgUrl(), i.getItemId());
            }
        }

        List<Label> localLabels = getLocalLabel();
        for(Label l: localLabels){
            labelIDs.add(l.getTitle());
            db.collection("label_table").document(l.getTitle()).set(l);
        }

        List<ItemLabelCrossRef> localRefs = getLocalCrossRef();
        for(int i = 0; i < localRefs.size(); i++) {
            refIDs.add("ref" + i);
            db.collection("itemlabelcrossref_table").document("ref" + i).set(localRefs.get(i));
        }

        removeCloudExtra("item_table", itemIDs);
        removeCloudExtra("label_table", labelIDs);
        removeCloudExtra("itemlabelcrossref_table", refIDs);


        Map<String, String> uploadTime = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        String timeDiff = c.getDayDifferences(format.format(calendar.getTime())) + " days ago";
        binding.uploadRecord.setText(timeDiff);
        uploadTime.put("value", timeDiff);
        db.collection("update_time").document("last_upload").set(uploadTime);
        Toast.makeText(getContext(), "Uploading data successfully", Toast.LENGTH_SHORT).show();

    }

    public void removeCloudExtra(String tableName, List<String> itemIDs){
        db.collection(tableName).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                if(!itemIDs.contains(d.getId())){
                                    db.collection(tableName).document(d.getId()).delete();
                                }
                            }
                        }
                    }
                });
    }

    public void fetchData(){
        List<Item> localItems = getLocalItem();
        db.collection("item_table")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Item> cloudItems = new ArrayList<>();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<DocumentSnapshot> cloudList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d: cloudList){
                            Item i = d.toObject(Item.class);
                            cloudItems.add(i);
                            if (!localItems.contains(i)){
                                syncViewModel.insertItem(i);
                                if(i.getImgUrl().length() != 0){
                                    convertImageUrlToLocal(i.getImgUrl(), i);
                                }
                            }else{
                                syncViewModel.updateItem(i);
                            }

                        }
                    }
                    // removeLocalExtra
                    for (Item i : localItems) {
                        if (!cloudItems.contains(i)) {
                            syncViewModel.deleteItem(i);
                        }
                    }
                });

        List<Label> localLabels = getLocalLabel();
        db.collection("label_table")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Label> cloudLabels = new ArrayList<>();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<DocumentSnapshot> cloudList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d: cloudList){
                            Label l = d.toObject(Label.class);
                            cloudLabels.add(l);
                            if (!localLabels.contains(l)){
                                syncViewModel.insertLabel(l);
                            }else{
                                syncViewModel.updateLabel(l);
                            }

                        }
                    }
                    // removeLocalExtra
                    for(Label l: localLabels){
                        if(!cloudLabels.contains(l)){
                            syncViewModel.deleteLabel(l);
                        }
                    }
                });

        List<ItemLabelCrossRef> localRefs = getLocalCrossRef();
        db.collection("itemlabelcrossref_table")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<ItemLabelCrossRef> cloudRefs = new ArrayList<>();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<DocumentSnapshot> cloudList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d: cloudList){
                            ItemLabelCrossRef ref = d.toObject(ItemLabelCrossRef.class);
                            cloudRefs.add(ref);
                            if (!localRefs.contains(ref)){
                                syncViewModel.insertRef(ref);
                            }else{
                                syncViewModel.updateRef(ref);
                            }

                        }
                    }
                    // removeLocalExtra
                    for(ItemLabelCrossRef ref: localRefs){
                        if(!cloudRefs.contains(ref)){
                            syncViewModel.deleteRef(ref);
                        }
                    }
                });

        Map<String, String> fetchTime = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        String timeDiff =  c.getDayDifferences(format.format(calendar.getTime())) + " days ago";
        binding.fetchRecord.setText(timeDiff);
        fetchTime.put("value", timeDiff);
        db.collection("update_time").document("last_fetch").set(fetchTime);

        Toast.makeText(getContext(), "Fetching Data successfully", Toast.LENGTH_SHORT).show();
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

    public List<Label> getLocalLabel(){
        List<Label> localLabels = new ArrayList<>();
        syncViewModel.getAllLables().observe(getViewLifecycleOwner(), new Observer<List<Label>>() {
            @Override
            public void onChanged(List<Label> labels) {
                localLabels.addAll(labels);
            }
        });
       return localLabels;
   }

    public List<ItemLabelCrossRef> getLocalCrossRef(){
        List<ItemLabelCrossRef> localRefs = new ArrayList<>();
        syncViewModel.getAllCrossRefs().observe(getViewLifecycleOwner(), new Observer<List<ItemLabelCrossRef>>() {
            @Override
            public void onChanged(List<ItemLabelCrossRef> refs) {
                localRefs.addAll(refs);
            }
        });
        return localRefs;
    }


    public void convertImageUrlToCloud(String url, long itemID){

        Uri imageUri = Uri.parse(url);
        String imageName = "item" + itemID + "." + getFileExtension(imageUri);
        StorageReference itemRef = firebaseStorage.getReference().child("itemImages/" + "item" + itemID + "/" + imageName);
        itemRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    itemRef.getDownloadUrl()
                            .addOnSuccessListener(downloadURL -> {

                                Map<String,String> data = new HashMap<>();
                                data.put("imgUrl", downloadURL.toString());

                                db.collection("item_table").
                                        document("item" + itemID).
                                        set(data, SetOptions.merge());
                            });
                    Toast.makeText(getContext(), "Update image Url successfully", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Toast.makeText(getContext(), "Image update failed", Toast.LENGTH_SHORT).show();
                }
        });

    }

    public void convertImageUrlToLocal(String url, Item item) {

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            saveImage(url, item);
        }else{
            askPermission();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void askPermission(){
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                1);

    }

    private void saveImage(String url, Item item){
        StorageReference itemRef = firebaseStorage.getReferenceFromUrl(url);

        try {

            File localFile = File.createTempFile("images", ".jpg");
            itemRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                // Local temp file has been created
                Bitmap photo = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                String contentUrl = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), photo, "item" + item.getItemId() +".jpg", "item" + item.getItemId() + "image");
                Toast.makeText(getContext(), "Image successfully saved", Toast.LENGTH_SHORT).show();
                //syncViewModel.updateItemImageURL(item, contentUrl);

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Toast.makeText(getContext(), "Image save failed", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }



    }


}
