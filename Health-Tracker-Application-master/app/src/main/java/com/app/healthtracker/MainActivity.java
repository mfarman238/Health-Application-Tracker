package com.app.healthtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.opencsv.CSVReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class MainActivity extends AppCompatActivity {
private TextInputLayout tPreg,tGlucose,tBP,tSkin,tInsulin,tBMI,tDiabet,tAge;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_check);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tPreg=findViewById(R.id.tPreg);
        tGlucose=findViewById(R.id.tGlucose);
        tBP=findViewById(R.id.tBP);
        tSkin=findViewById(R.id.tSkin);
        tInsulin=findViewById(R.id.tInsulin);
        tBMI=findViewById(R.id.tBMI);
        tDiabet=findViewById(R.id.tDiabet);
        tAge=findViewById(R.id.tAge);
        new DatasetImportTask().execute();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)finish();
        return super.onOptionsItemSelected(item);
    }

    public void checkOnClick(View view){
        String preg=tPreg.getEditText().getText().toString();
        String glu=tGlucose.getEditText().getText().toString();
        String bp=tBP.getEditText().getText().toString();
        String skin=tSkin.getEditText().getText().toString();
        String ins=tInsulin.getEditText().getText().toString();
        String bmi=tBMI.getEditText().getText().toString();
        String dia=tDiabet.getEditText().getText().toString();
        String age=tAge.getEditText().getText().toString();
        if(preg.isEmpty()||glu.isEmpty()||bp.isEmpty()||skin.isEmpty()||ins.isEmpty()||bmi.isEmpty()||dia.isEmpty()||age.isEmpty()){
            Toast.makeText(this, "Enter all values", Toast.LENGTH_SHORT).show();
        }else {
            //pregnant=? AND glucose=? AND bp=? AND skin=? AND insulin=? AND bmi=? AND diabet=? AND age=?
            String[] where=new String[]{preg,glu,bp,skin,ins,bmi,dia,age};
            KNNalgo knn = KNNalgo.getInstance(this);
            int res= knn.prediction(where);

            if(res==1){
                View cusView=View.inflate(this,R.layout.dialog_result,null);
                AppCompatTextView text=cusView.findViewById(R.id.text);
                AppCompatImageView image=cusView.findViewById(R.id.image);

                int ag=Integer.parseInt(age);
                if(ag>=2 && ag<=16) {
                    text.setText(getString(R.string.diabetes_positive)+"\n\n"+getString(R.string.age_2_16));
                    image.setBackgroundResource(R.drawable.exercise_2_16);
                }else if(ag>=17 && ag<=35) {
                    text.setText(getString(R.string.diabetes_positive)+"\n\n"+getString(R.string.age_17_35));
                    image.setBackgroundResource(R.drawable.exercise_17_35);
                }else if(ag>=36) {
                    text.setText(getString(R.string.diabetes_positive)+"\n\n"+getString(R.string.age_36_more));
                    image.setBackgroundResource(R.drawable.exercise_above_36);
                }
                new MaterialDialog.Builder(this)
                        .title("Result")
                        .customView(cusView,true)
                        .positiveText("Ok")
                        .onPositive((dialog, which) -> dialog.dismiss())
                        .negativeText("Exercise Tips")
                        .onNegative((dialog, which) -> {
                            String url = "file:///android_asset/ExercisesTips.html";
                            Intent it=new Intent(getApplicationContext(),BrowserActivity.class);
                            it.putExtra(BrowserActivity.URL,url);
                            startActivity(it);
                        })
                        .neutralText("Diet Chart")
                        .onNeutral((dialog, which) -> {
                            String url = "file:///android_asset/DiabeticDietChart.html";
                            Intent it=new Intent(getApplicationContext(),BrowserActivity.class);
                            it.putExtra(BrowserActivity.URL,url);
                            startActivity(it);
                        }).show();
            }else if(res==0){
                String msg=getString(R.string.diabetes_negative);
                new MaterialDialog.Builder(this)
                        .title("Result")
                        .content(msg)
                        .positiveText("Ok")
                        .onPositive((dialog, which) -> dialog.dismiss())
                        .negativeText("Food Tips")
                        .onNegative((dialog, which) -> {
                            String url = "file:///android_asset/FoodChart.html";
                            Intent it=new Intent(getApplicationContext(),BrowserActivity.class);
                            it.putExtra(BrowserActivity.URL,url);
                            startActivity(it);
                        }).show();
            }
        }
    }
    private class DatasetImportTask extends AsyncTask<Void,Void,Void>{
        @Override      protected void onPreExecute() {
            super.onPreExecute();
            DialogUtil.showProgress(MainActivity.this);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            KNNalgo knn = KNNalgo.getInstance(getApplicationContext());
            if(knn.getDatasetCount()==0)
            try{
                InputStream is=getAssets().open("diabetes.csv");
                CSVReader csv=new CSVReader(new InputStreamReader(is));
                List<String[]> list=csv.readAll();
                list.remove(0);
                csv.close();
                knn.bulkInsertDataset(list);
            }catch (Exception ex){
                ex.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            DialogUtil.hideProgress();
        }
    }
}