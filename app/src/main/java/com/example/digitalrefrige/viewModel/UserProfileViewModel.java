package com.example.digitalrefrige.viewModel;

import androidx.lifecycle.ViewModel;

import com.example.digitalrefrige.model.dataHolder.Label;
import com.example.digitalrefrige.model.dataSource.LocalDataBase;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;


@HiltViewModel
public class UserProfileViewModel extends ViewModel {
    public GoogleSignInClient mGoogleSignInClient;
    public FirebaseAuth mAuth;
    public FirebaseUser user;
    public String lastUpdateTime;


    @Inject
    public UserProfileViewModel(GoogleSignInClient mGoogleSignInClient, FirebaseAuth mAuth) {
        this.mGoogleSignInClient = mGoogleSignInClient;
        this.mAuth = mAuth;
    }

    public void setUpdateTime(String t){
        this.lastUpdateTime = t;
    }

    public String getUpdateTime() {
        return this.lastUpdateTime;
    }





}
