package com.example.racunko;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

public class SettingsActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize views
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the DrawerToggle (this handles opening/closing the drawer)
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.open_nav, R.string.close_nav
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Set the color of the hamburger icon to white
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.white));

        // Set up the navigation item listener for drawer
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                finish();
            } else if (id == R.id.nav_settings) {
                // Already in settings
            }
            drawerLayout.closeDrawers();
            return true;
        });
    }
}
