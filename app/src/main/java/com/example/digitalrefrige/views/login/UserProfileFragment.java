package com.example.digitalrefrige.views.login;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.digitalrefrige.R;
import com.example.digitalrefrige.databinding.FragmentUserProfileBinding;
import com.example.digitalrefrige.model.ItemRepository;
import com.example.digitalrefrige.model.dataHolder.Item;
import com.example.digitalrefrige.model.dataHolder.ItemLabelCrossRef;
import com.example.digitalrefrige.model.dataHolder.Label;
import com.example.digitalrefrige.model.dataSource.ItemDAO;
import com.example.digitalrefrige.model.dataSource.LocalDataBase;
import com.example.digitalrefrige.viewModel.ItemDetailViewModel;
import com.example.digitalrefrige.viewModel.ItemListViewModel;
import com.example.digitalrefrige.viewModel.UserProfileViewModel;
import com.example.digitalrefrige.views.common.ProgressDialog;
import com.example.digitalrefrige.views.itemList.ItemDetailActivity;
import com.example.digitalrefrige.views.itemList.ItemListFragmentDirections;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
        binding.tags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = UserProfileFragmentDirections.actionNavigationProfileToLabelListFragment();
                Navigation.findNavController(view).navigate(action);
            }
        });
        binding.expire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = UserProfileFragmentDirections.actionNavigationProfileToExpireFragment();
                Navigation.findNavController(view).navigate(action);
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userProfileViewModel = new ViewModelProvider(requireActivity()).get(UserProfileViewModel.class);

        FirebaseUser user = userProfileViewModel.mAuth.getCurrentUser();
        if (user != null) {
            showProfileInfo(user);
            binding.sync.setOnClickListener(button -> sync());
        } else {
            // show button which navigate to login
            goLogin();
            binding.sync.setOnClickListener(button ->
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
            NavDirections directions = (NavDirections) UserProfileFragmentDirections.actionNavigationProfileToNavigationLogin();
            Navigation.findNavController(view).navigate(directions);
        });
        binding.userProfileStatus.setVisibility(View.GONE);
        binding.logoutButton.setVisibility(View.GONE);
    }



    public void sync(){
        NavDirections directions = (NavDirections) UserProfileFragmentDirections.actionNavigationProfileToNavigationSync();
        NavHostFragment.findNavController(UserProfileFragment.this).navigate(directions);
    }



}