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
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SyncViewModel syncViewModel;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSyncBinding.inflate(inflater, container, false);
        syncViewModel = new ViewModelProvider(requireActivity()).get(SyncViewModel.class);


        // fetch last update time from fireCloud
        DocumentReference timeR = db.collection("update_time").document("last_update");
        if(timeR != null) {
            timeR.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    binding.timeRecord.setText(value.getString("value"));
                }
            });
        }

        binding.backUpStorage.setOnClickListener(button -> storeData());
        binding.fetchData.setOnClickListener(button -> fetchData());


        return binding.getRoot();
    }

    public void storeData() {
        LocalDataBase localDB = LocalDataBase.getInstance(getContext());
        List<String> itemIDs = new ArrayList<>();
        List<String> labelIDs = new ArrayList<>();
        List<String> refIDs = new ArrayList<>();

        localDB.itemDAO().getAllItems().observe(getViewLifecycleOwner(), new Observer<List<Item>>() {
            @Override
            public void onChanged(List<Item> items) {
                for(Item i: items){
                    itemIDs.add(i.getName());
                    // data in local, not in cloud => add in cloud
                    // data in both local and cloud => update cloud
                    db.collection("item_table").document(i.getName()).set(i);
                }
            }
        });
        localDB.labelDAO().getAllLabels().observe(getViewLifecycleOwner(), new Observer<List<Label>>() {
            @Override
            public void onChanged(List<Label> labels) {
                for(Label l: labels){
                    labelIDs.add(l.getTitle());
                    db.collection("label_table").document(l.getTitle()).set(l);
                }
            }
        });
        localDB.itemLabelCrossRefDAO().getAllCrossRefs().observe(getViewLifecycleOwner(),  new Observer<List<ItemLabelCrossRef>>() {
            @Override
            public void onChanged(List<ItemLabelCrossRef> refs) {
                for(int i = 0; i < refs.size(); i++) {
                    refIDs.add("ref" + i);
                    db.collection("itemlabelcrossref_table").document("ref" + i).set(refs.get(i));
                }
            }
        });

        removeCloudExtra("item_table", itemIDs);
        removeCloudExtra("label_table", labelIDs);
        removeCloudExtra("itemlabelcrossref_table", refIDs);

        Map<String, String> timeR = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(calendar.getTime());
        binding.timeRecord.setText(time);
        timeR.put("value", time);
        db.collection("update_time").document("last_update").set(timeR);
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
        List<Label> localLabels = getLocalLabel();

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

}
