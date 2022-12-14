package com.app.healthtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void loginOnClick(View view){
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish();
    }
    public void registerOnClick(View view){
        TextInputLayout tName=findViewById(R.id.tName);
        TextInputLayout tMobile=findViewById(R.id.tMobile);
        TextInputLayout tPassword=findViewById(R.id.tPassword);
        RadioGroup rgGender=findViewById(R.id.rgGender);
        String name=tName.getEditText().getText().toString().trim();
        String mobile=tMobile.getEditText().getText().toString().trim();
        String password=tPassword.getEditText().getText().toString().trim();
        tName.setErrorEnabled(name.isEmpty());
        tMobile.setErrorEnabled(mobile.isEmpty());
        tPassword.setErrorEnabled(password.isEmpty());
        tName.setError(name.isEmpty()?tName.getErrorContentDescription():null);
        tMobile.setError(mobile.isEmpty()?tMobile.getErrorContentDescription():null);
        tPassword.setError(password.isEmpty()?tPassword.getErrorContentDescription():null);
        String gender=findViewById(rgGender.getCheckedRadioButtonId()).getTag().toString();
        if(name.isEmpty()||mobile.isEmpty()||gender.isEmpty()||password.isEmpty()){
            Toast.makeText(getApplicationContext(),R.string.enter_all_values,Toast.LENGTH_SHORT).show();
            return;
        }else if(mobile.length()!=10){
            Toast.makeText(getApplicationContext(),R.string.enter_valid_mobile,Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String,String> map=new HashMap<>();
        map.put("name",name);
        map.put("gender",gender);
        map.put("mobile",mobile);
        map.put("password",password);
        DialogUtil.showProgress(RegisterActivity.this);
        DatabaseReference db= FirebaseDatabase.getInstance().getReference("register/"+mobile);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            if(snapshot.exists()){
                DialogUtil.hideProgress();
                Toast.makeText(getApplicationContext(),"Already registered",Toast.LENGTH_SHORT).show();
            }else{
                snapshot.getRef().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        DialogUtil.hideProgress();
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Registration Successful",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                            finish();
                        }else{
                            Toast.makeText(getApplicationContext(),"Registration failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                DialogUtil.hideProgress();
                Toast.makeText(getApplicationContext(),error.getMessage()+"",Toast.LENGTH_SHORT).show();
            }
        });
    }
}