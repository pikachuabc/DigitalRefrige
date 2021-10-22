package com.example.digitalrefrige.views.login;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.example.digitalrefrige.R;
import com.example.digitalrefrige.databinding.FragmentRegisterBinding;
import com.example.digitalrefrige.viewModel.UserProfileViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    public UserProfileViewModel userProfileViewModel;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userProfileViewModel = new ViewModelProvider(requireActivity()).get(UserProfileViewModel.class);
        binding.registerButton.setOnClickListener(button -> register());
        binding.loginButton.setOnClickListener(button -> login());
    }

    private void login() {
        NavDirections directions = (NavDirections) RegisterFragmentDirections.actionNavigationRegisterToNavigationLogin();
        NavHostFragment.findNavController(RegisterFragment.this).navigate(directions);

    }

    private void register() {
        String email = binding.emailInput.getText().toString();
        String password = binding.passwordInput.getText().toString();
        String confirmPassword = binding.passwordConfirm.getText().toString();
        if (TextUtils.isEmpty(email)) {
            binding.emailInput.setError("Enter your email");
        } else if (TextUtils.isEmpty(password)) {
            binding.passwordInput.setError("Enter your password");
        } else if (TextUtils.isEmpty(confirmPassword)) {
            binding.passwordConfirm.setError("Confirm your password");
        } else if (!password.equals(confirmPassword)) {
            binding.passwordConfirm.setError("Different Password");
        } else if (password.length() < 6) {
            binding.passwordInput.setError("Length should be > 6");
        } else {
            userProfileViewModel.mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "registerWithEmail:success");
                                Toast.makeText(getContext(), "Register with Email success.",
                                        Toast.LENGTH_SHORT).show();
                                NavHostFragment.findNavController(RegisterFragment.this).navigate(R.id.navigation_profile);

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "registerWithEmail:failure", task.getException());
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    binding.emailInput.setError("This email already in use");
                                }
                                Toast.makeText(getContext(), "Register with Email failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
