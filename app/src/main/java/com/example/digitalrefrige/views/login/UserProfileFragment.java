package com.example.digitalrefrige.views.login;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.digitalrefrige.R;
import com.example.digitalrefrige.databinding.FragmentUserProfileBinding;
import com.example.digitalrefrige.model.dataHolder.Item;
import com.example.digitalrefrige.model.dataHolder.ItemLabelCrossRef;
import com.example.digitalrefrige.model.dataHolder.Label;
import com.example.digitalrefrige.model.dataSource.LocalDataBase;
import com.example.digitalrefrige.viewModel.UserProfileViewModel;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProfileFragment extends Fragment {

    private FragmentUserProfileBinding binding;
    public UserProfileViewModel userProfileViewModel;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUserProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userProfileViewModel = new ViewModelProvider(requireActivity()).get(UserProfileViewModel.class);

        FirebaseUser user = userProfileViewModel.mAuth.getCurrentUser();
        if (user != null) {
            showProfileInfo(user);
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
        } else {
            // show button which navigate to login
            goLogin();
            binding.backUpStorage.setOnClickListener(button ->
                    Toast.makeText(getContext(), "login first", Toast.LENGTH_SHORT).show());
        }
    }

    public void showProfileInfo(FirebaseUser user) {

        String avatarPath = String.valueOf(user.getPhotoUrl());
        Picasso.get()
                .load(avatarPath)
                .resize(150, 150)
                .centerCrop()
                .into(binding.avatar);
        binding.email.setText(user.getEmail());

        binding.logoutButton.setText("LogOut");
        binding.logoutButton.setOnClickListener(view -> {
            Toast.makeText(getContext(), "sign out successfully", Toast.LENGTH_LONG).show();
            userProfileViewModel.mAuth.signOut();
            userProfileViewModel.mGoogleSignInClient.signOut();
            // navigate to this Fragment after logout
            Navigation.findNavController(view).navigate(R.id.navigation_profile);
        });
        binding.signinSignup.setVisibility(View.GONE);
    }

    public void goLogin(){
        binding.signinSignup.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.navigation_login);
        });
        binding.userProfileStatus.setVisibility(View.GONE);
        binding.logoutButton.setVisibility(View.GONE);
    }


    public void storeData() {
        LocalDataBase localDB = LocalDataBase.getInstance(getContext());
        localDB.itemDAO().getAllItems().observe(getViewLifecycleOwner(), new Observer<List<Item>>() {
            @Override
            public void onChanged(List<Item> items) {
                for(Item i: items){
                    db.collection("item_table").document(i.getName()).set(i);
                }
            }
        });
        localDB.labelDAO().getAllLabels().observe(getViewLifecycleOwner(), new Observer<List<Label>>() {
            @Override
            public void onChanged(List<Label> labels) {
                for(Label l: labels){
                    db.collection("label_table").document(l.getTitle()).set(l);
                }
            }
        });
        localDB.itemLabelCrossRefDAO().getAllCrossRefs().observe(getViewLifecycleOwner(),  new Observer<List<ItemLabelCrossRef>>() {
            @Override
            public void onChanged(List<ItemLabelCrossRef> refs) {
                for(int i = 0; i < refs.size(); i++) {
                    db.collection("itemlabelcrossref_table").document("ref" + i).set(refs.get(i));
                }
            }
        });
        Map<String, String> timeR = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String time = format.format(calendar.getTime());
        binding.timeRecord.setText(time);
        timeR.put("value", time);
        db.collection("update_time").document("last_update").set(timeR);
    }
}