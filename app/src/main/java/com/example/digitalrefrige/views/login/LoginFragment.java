package com.example.digitalrefrige.views.login;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.digitalrefrige.R;
import com.example.digitalrefrige.databinding.FragmentLoginBinding;
import com.example.digitalrefrige.views.common.TimePickerFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.squareup.picasso.Picasso;

import java.util.concurrent.Executor;


public class LoginFragment extends Fragment {

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FragmentLoginBinding binding;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentLoginBinding.inflate(inflater,container,false);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.Auth_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this.getActivity(), gso);
        checkUser();



        binding.googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });

        binding.emailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailSignIn();
            }
        });


        binding.logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"sign out successfully",Toast.LENGTH_LONG).show();
                FirebaseAuth.getInstance().signOut();

                Navigation.findNavController(view).popBackStack();
                //logInContent();

            }
        });
        return binding.getRoot();
    }


    private void emailSignIn(){


        String inputEmail = binding.emailInput.getText().toString();
        String inputPassword = binding.passwordInput.getText().toString();

        if(inputEmail.isEmpty()){
            Toast.makeText(getContext(), "input email plz", Toast.LENGTH_SHORT).show();
        }else if(inputPassword.isEmpty()){
            Toast.makeText(getContext(), "input password plz", Toast.LENGTH_SHORT).show();
        }else{
            mAuth.signInWithEmailAndPassword(inputEmail, inputPassword)
                    .addOnCompleteListener(this.getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(getContext(), "Authentication with Email success.",
                                        Toast.LENGTH_SHORT).show();

                                checkUser();

                            } else {

                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(getContext(), "Authentication with Email failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }



    }


    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        signInResultLauncher.launch(signInIntent);
    }

    ActivityResultLauncher<Intent> signInResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        handleSignInResult(task);
                    }
                }
            });



    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this.getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getContext(),"Google sign in successfully",Toast.LENGTH_LONG).show();

                            checkUser();


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getContext(),"fail to Google sign in",Toast.LENGTH_LONG).show();

                        }

                    }
                });
    }



    private void checkUser(){
        FirebaseUser user = mAuth.getCurrentUser();



        if(user == null){
            // user is not logged in
            Toast.makeText(getContext(),"login in plz",Toast.LENGTH_LONG).show();
            logInContent();


        }else if (user.getEmail().split("@")[1].equals("gmail.com")){
            // user logged in with Google
            Toast.makeText(getContext(),"you already logged in with Google",Toast.LENGTH_LONG).show();
            logOutContentForGoogle();

        }else{
            // user logged in with Email
            Toast.makeText(getContext(),"you already logged in with Email",Toast.LENGTH_LONG).show();
            logOutContentForEmail();

        }





    }





    private void logInContent(){

        binding.googleSignInButton.setVisibility(View.VISIBLE);
        binding.logoutButton.setVisibility(View.GONE);

        binding.email.setVisibility(View.GONE);
        binding.avatar.setVisibility(View.GONE);
        binding.emailInput.setVisibility(View.VISIBLE);
        binding.passwordInput.setVisibility(View.VISIBLE);
        binding.emailSignInButton.setVisibility(View.VISIBLE);
    }

    private void logOutContentForGoogle(){

        binding.logoutButton.setVisibility(View.VISIBLE);
        binding.googleSignInButton.setVisibility(View.GONE);
        binding.email.setVisibility(View.VISIBLE);
        binding.avatar.setVisibility(View.VISIBLE);
        binding.emailInput.setVisibility(View.GONE);
        binding.passwordInput.setVisibility(View.GONE);
        binding.emailSignInButton.setVisibility(View.GONE);
        setInfo();

    }

    private void logOutContentForEmail(){


        binding.logoutButton.setVisibility(View.VISIBLE);
        binding.emailInput.setVisibility(View.GONE);
        binding.passwordInput.setVisibility(View.GONE);
        binding.emailSignInButton.setVisibility(View.GONE);
        binding.email.setVisibility(View.GONE);
        binding.avatar.setVisibility(View.GONE);
        binding.googleSignInButton.setVisibility(View.GONE);

    }



    private void setInfo(){
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if(account == null) {};

        binding.email.setText(account.getEmail());

        String avatarPath = String.valueOf(account.getPhotoUrl());
        Picasso.get()
                .load(avatarPath)
                .resize(150, 150)
                .centerCrop()
                .into(binding.avatar);
    }









}