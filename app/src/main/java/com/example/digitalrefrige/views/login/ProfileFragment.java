package com.example.digitalrefrige.views.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.digitalrefrige.R;
import com.example.digitalrefrige.databinding.FragmentLoginBinding;
import com.example.digitalrefrige.databinding.FragmentLogoutBinding;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {


    private TextView email;
    private ImageView avatar;
    private Button logOut;
    private FragmentLogoutBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentLogoutBinding.inflate(inflater,container,false);
        email = binding.email;
        avatar = binding.avatar;
        logOut = binding.logoutButton;

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if(account == null) {};

        email.setText(account.getEmail());

//        Bitmap image = BitmapFactory.decodeFile(String.valueOf(account.getPhotoUrl()));
//        avatar.setImageBitmap(image);

        String avatarPath = String.valueOf(account.getPhotoUrl());
        Picasso.get()
                .load(avatarPath)
                .resize(150, 150)
                .centerCrop()
                .into(avatar);







        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"sign out successfully",Toast.LENGTH_LONG).show();

                FirebaseAuth.getInstance().signOut();
                Navigation.findNavController(view).popBackStack();

            }
        });
        return binding.getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}



