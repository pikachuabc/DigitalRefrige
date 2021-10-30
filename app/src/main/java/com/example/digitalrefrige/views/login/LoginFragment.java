package com.example.digitalrefrige.views.login;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.digitalrefrige.R;
import com.example.digitalrefrige.databinding.FragmentLoginBinding;
import com.example.digitalrefrige.viewModel.UserProfileViewModel;
import com.example.digitalrefrige.views.common.ProgressDialog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class LoginFragment extends Fragment {


    private FragmentLoginBinding binding;
    private ProgressDialog dialog;
    public UserProfileViewModel userProfileViewModel;


    ActivityResultLauncher<Intent> googleSignInResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        showProgressingCircle();
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        handleGoogleSignInResult(task);
                    }
                }
            });


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userProfileViewModel = new ViewModelProvider(requireActivity()).get(UserProfileViewModel.class);
        binding.googleSignInButton.setOnClickListener(button -> googleSignIn());
        binding.emailSignInButton.setOnClickListener(button -> emailSignIn());
        binding.emailSignUpButton.setOnClickListener(button -> register());
    }

    private void register() {
        NavDirections directions = (NavDirections) LoginFragmentDirections.actionNavigationLoginToNavigationRegister();
        NavHostFragment.findNavController(LoginFragment.this).navigate(directions);
    }

    private void emailSignIn() {


        String inputEmail = binding.emailInput.getText().toString();
        String inputPassword = binding.passwordInput.getText().toString();

        if (TextUtils.isEmpty(inputEmail)) {
            binding.emailSignInButton.setError("Enter your email");
        } else if (TextUtils.isEmpty(inputPassword)) {
            binding.passwordInput.setError("Enter your password");
        } else {
            userProfileViewModel.mAuth.signInWithEmailAndPassword(inputEmail, inputPassword)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                Toast.makeText(getContext(), "Authentication with Email success.",
                                        Toast.LENGTH_SHORT).show();
                                NavDirections directions = (NavDirections) LoginFragmentDirections.actionNavigationLoginToNavigationProfile();
                                NavHostFragment.findNavController(LoginFragment.this).navigate(directions);

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
        Intent signInIntent = userProfileViewModel.mGoogleSignInClient.getSignInIntent();
        googleSignInResultLauncher.launch(signInIntent);
    }


    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
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
        userProfileViewModel.mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(getContext(), "Google sign in successfully", Toast.LENGTH_LONG).show();
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                            NavDirections directions = (NavDirections) LoginFragmentDirections.actionNavigationLoginToNavigationProfile();
                            NavHostFragment.findNavController(LoginFragment.this).navigate(directions);


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getContext(), "fail to Google sign in", Toast.LENGTH_LONG).show();

                        }

                    }
                });
    }


    public void showProgressingCircle() {
        dialog = new ProgressDialog("signing you in...");
        dialog.show(getParentFragmentManager(), "progressing");
    }


}