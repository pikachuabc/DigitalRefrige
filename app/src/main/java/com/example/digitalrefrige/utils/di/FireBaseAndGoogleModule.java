package com.example.digitalrefrige.utils.di;


import android.content.Context;

import com.example.digitalrefrige.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class FireBaseAndGoogleModule {

    @Provides
    GoogleSignInClient mGoogleSignInClientProvider(@ApplicationContext Context context) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.Auth_id))
                .requestEmail()
                .build();

        return GoogleSignIn.getClient(context, gso);
    }

    @Provides
    @Singleton
    FirebaseAuth firebaseAuthProvider() {
        return FirebaseAuth.getInstance();
    }
}
