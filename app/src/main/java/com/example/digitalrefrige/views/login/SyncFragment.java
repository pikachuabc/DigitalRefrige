package com.example.digitalrefrige.views.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncFragment extends Fragment {

    private FragmentSyncBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SyncViewModel syncViewModel;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private Converters c;



    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSyncBinding.inflate(inflater, container, false);
        syncViewModel = new ViewModelProvider(requireActivity()).get(SyncViewModel.class);


        // fetch last upload time from fireCloud
        DocumentReference uploadR = db.collection("update_time").document("last_upload");
        if(uploadR != null) {
            uploadR.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    binding.uploadRecord.setText(value.getString("value"));
                }
            });
        }

        // fetch last fetch time from fireCloud
        DocumentReference fetchR = db.collection("update_time").document("last_fetch");
        if(fetchR != null) {
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


        List<Item> localItems = getLocalItem();
        for(Item i: localItems){
            itemIDs.add("item" + i.getItemId());
            db.collection("item_table").document("item" + i.getItemId()).set(i);
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

}
