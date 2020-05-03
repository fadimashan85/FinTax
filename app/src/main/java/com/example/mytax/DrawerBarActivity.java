
//Menu drawer


package com.example.mytax;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;


public class DrawerBarActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_bar);
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,  R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                final String appPackageName = getPackageName();

                switch (item.getItemId()) {

                    case R.id.nav_House:
                        Intent houseOption = new Intent(getApplicationContext(), House.class);
                        startActivity(houseOption);
//                        finish();
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.nav_car:
                        Intent carOption = new Intent(getApplicationContext(), CarMain.class);
                        startActivity(carOption);
//                        finish();
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.nav_salary:
                        Intent salaryOption = new Intent(getApplicationContext(), Salary.class);
                        startActivity(salaryOption);
//                        finish();
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.nav_summary:
                        Intent summaryOption = new Intent(getApplicationContext(), Summary.class);
                        startActivity(summaryOption);
//                        finish();
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.nav_percentage:
                        Intent percentageOption = new Intent(getApplicationContext(), Percentage.class);
                        startActivity(percentageOption);
//                        finish();
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.nav_setting:
                        Intent settingOption = new Intent(getApplicationContext(), SettingActivity.class);
                        startActivity(settingOption);
//                        finish();
                        drawerLayout.closeDrawers();
                        break;
                }
                return false;
            }
        });


    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransitionExit();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransitionEnter();
    }

    /**
     * Overrides the pending Activity transition by performing the "Enter" animation.
     */
    protected void overridePendingTransitionEnter() {
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    /**
     * Overrides the pending Activity transition by performing the "Exit" animation.
     */
    protected void overridePendingTransitionExit() {
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}