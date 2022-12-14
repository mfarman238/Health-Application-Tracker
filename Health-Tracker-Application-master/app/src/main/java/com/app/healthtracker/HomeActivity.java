package com.app.healthtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home_activity,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)finish();
        else if(item.getItemId()==R.id.action_logout){
            FirebaseAuth.getInstance().signOut();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    public void openCalculator(View view){
startActivity(new Intent(getApplicationContext(),HealthCheckActivity.class));
    }
    public void openDietChart(View view){
        String url = "file:///android_asset/DiabeticDietChart.html";
        Intent it=new Intent(getApplicationContext(),BrowserActivity.class);
        it.putExtra(BrowserActivity.URL,url);
        startActivity(it);
    }
    public void openExerciseTips(View view){
        String url = "file:///android_asset/ExercisesTips.html";
        Intent it=new Intent(getApplicationContext(),BrowserActivity.class);
        it.putExtra(BrowserActivity.URL,url);
        startActivity(it);
    }
    public void openExerciseImages(View view){
        String url = "file:///android_asset/exercise_images.html";
        Intent it=new Intent(getApplicationContext(),BrowserActivity.class);
        it.putExtra(BrowserActivity.URL,url);
        startActivity(it);
    }
}