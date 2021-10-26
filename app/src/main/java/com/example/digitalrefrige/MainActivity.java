package com.example.digitalrefrige;


import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.View;


import com.example.digitalrefrige.databinding.ActivityMainBinding;
import com.example.digitalrefrige.services.AlarmBroadcastReceiver;
import com.example.digitalrefrige.viewModel.ItemListViewModel;
import com.example.digitalrefrige.views.itemList.ItemDetailActivity;
import com.example.digitalrefrige.views.itemList.ItemListFragmentDirections;


import java.util.Calendar;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ItemListViewModel itemListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_item_list, R.id.navigation_profile)
                .build();
        NavHostFragment hostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = hostFragment.getNavController();

        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);


        binding.buttonAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections directions = (NavDirections) ItemListFragmentDirections.actionItemListFragmentToItemDetailActivity(ItemDetailActivity.CREATE_NEW_ITEM);
                navController.navigate(directions);
            }
        });

        getSupportActionBar().hide();
    }

    // enable back in appbar when not at top-level destination
    @Override
    public boolean onSupportNavigateUp() {
        return Navigation.findNavController(this, R.id.nav_host_fragment).navigateUp()
                || super.onSupportNavigateUp();
    }

}