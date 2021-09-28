package com.example.digitalrefrige.viewModel;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;


@HiltViewModel
public class UserProfileViewModel extends ViewModel {
    public GoogleSignInClient mGoogleSignInClient;
    public FirebaseAuth mAuth;
    public FirebaseUser user;


    @Inject
    public UserProfileViewModel(GoogleSignInClient mGoogleSignInClient, FirebaseAuth mAuth) {
        this.mGoogleSignInClient = mGoogleSignInClient;
        this.mAuth = mAuth;
    }


}
