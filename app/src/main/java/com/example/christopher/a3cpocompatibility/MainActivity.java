package com.example.christopher.a3cpocompatibility;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        switch (menuItem.getItemId()) {
                            case R.id.nav_home:
                                return true;
                            case R.id.nav_account:
                                return true;
                            case R.id.nav_properties:
                                openActivity(PropertiesActivity.class);
                                return true;
                            case R.id.nav_customers:
                                openActivity(CustomersActivity.class);
                                return true;
                            case R.id.nav_vehicles:
                                openActivity(VehiclesActivity.class);
                                return true;
                            default:
                                return onOptionsItemSelected(menuItem);
                        }
                    }
                });
    }
    /*
    public static int getPlate(){
        return this.plateNumber;
    }
    */
    /** Called when the user taps the Send button
     public void sendMessage(View view) {
     Intent intent = new Intent(this, DisplayMessageActivity.class);
     EditText editText = (EditText) findViewById(R.id.editText);
     String message = editText.getText().toString();
     intent.putExtra(EXTRA_MESSAGE, message);
     startActivity(intent);
     }*/

    public void openProperties (View view) {
        Intent intent = new Intent(this, PropertiesActivity.class);
        startActivity(intent);
    }

    public void openCustomers (View view) {
        Intent intent = new Intent(this, CustomersActivity.class);
        startActivity(intent);
    }

    public void openVehicles (View view) {
        Intent intent = new Intent(this, VehiclesActivity.class);
        startActivity(intent);
    }

    public void openAddProperties (View view) {
        Intent intent = new Intent(this, AddPropertyActivity.class);
        startActivity(intent);
    }

    public void openAddVehicles (View view) {
        Intent intent = new Intent(this, AddVehicleActivity.class);
        startActivity(intent);
    }

    public void openActivity(Class activityName) {
        Intent intent = new Intent(this, activityName);
        startActivity(intent);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
