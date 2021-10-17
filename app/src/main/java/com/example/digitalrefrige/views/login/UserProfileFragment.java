package com.example.digitalrefrige.views.login;

import android.media.Image;
import android.nfc.Tag;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.compose.ui.layout.LayoutIdParentData;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.example.digitalrefrige.R;
import com.example.digitalrefrige.databinding.FragmentUserProfileBinding;
import com.example.digitalrefrige.model.dataHolder.Item;
import com.example.digitalrefrige.model.dataHolder.Label;
import com.example.digitalrefrige.model.dataSource.ItemDAO;
import com.example.digitalrefrige.model.dataSource.LocalDataBase;
import com.example.digitalrefrige.viewModel.UserProfileViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
            System.out.println("having user...");
            binding.backUpStorage.setOnClickListener(button -> storeData());
        } else {
            // show button which navigate to login
            goLogin();
            binding.backUpStorage.setOnClickListener(button ->
                    Toast.makeText(getContext(), "login first", Toast.LENGTH_SHORT).show());
        }
    }

    public void showProfileInfo(FirebaseUser user) {

        LinearLayout layout = binding.layout;
        LinearLayout userProfileStatus = binding.userProfileStatus;
        TextView email = new TextView(getContext());
        ImageView avatar = new ImageView(getContext());
        Button logoutButton = new Button(getContext());

        LayoutParams avatarLp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        String avatarPath = String.valueOf(user.getPhotoUrl());
        Picasso.get()
                .load(avatarPath)
                .resize(150, 150)
                .centerCrop()
                .into(avatar);
        avatarLp.gravity = Gravity.CENTER;

        LayoutParams emailLp = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        emailLp.gravity = Gravity.CENTER;
        email.setText(user.getEmail());

        userProfileStatus.addView(avatar, avatarLp);
        userProfileStatus.addView(email, emailLp);

        LayoutParams logoutLp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        logoutButton.setText("LogOut");
        logoutLp.setMargins(0, 0, 0, 178);
        layout.addView(logoutButton, logoutLp);
        logoutButton.setOnClickListener(view -> {
            Toast.makeText(getContext(), "sign out successfully", Toast.LENGTH_LONG).show();
            userProfileViewModel.mAuth.signOut();
            userProfileViewModel.mGoogleSignInClient.signOut();
            // navigate to this Fragment after logout
            Navigation.findNavController(view).navigate(R.id.navigation_profile);
        });

    }

    public void goLogin(){

        Button signInsignUp = new Button(getContext());
        signInsignUp.setText("Sign In/Sign Up");

        LinearLayout userProfileStatus = binding.userProfileStatus;
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        lp.setMargins(0, 24, 0, 0);

        signInsignUp.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.navigation_login);
        });

        userProfileStatus.addView(signInsignUp, lp);

    }



    public void storeData() {

        LiveData<List<Item>> itemList = LocalDataBase.getInstance(getContext()).itemDAO().getAllItems();
        LiveData<List<Label>> labelList = LocalDataBase.getInstance(getContext()).labelDAO().getAllLabels();






        itemList.observe(getViewLifecycleOwner(), new Observer<List<Item>>() {
            @Override
            public void onChanged(List<Item> items) {
                for(Item i: items){
                    db.collection("item_table")
                            .document(i.getName())
                            .set(i);
                }

                LinearLayout userProfileStatus = binding.userProfileStatus;
                LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);


                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                String time = format.format(calendar.getTime());
                TextView timeNotify = new TextView(getContext());
                timeNotify.setText(time);
                userProfileStatus.addView(timeNotify, lp);

            }
        });

        labelList.observe(getViewLifecycleOwner(), new Observer<List<Label>>() {
            @Override
            public void onChanged(List<Label> labels) {
                for(Label l: labels){
                    db.collection("label_table")
                            .document(l.getTitle())
                            .set(l);
                }
            }
        });



    }




}