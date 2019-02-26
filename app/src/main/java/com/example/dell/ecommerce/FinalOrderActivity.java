package com.example.dell.ecommerce;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.ecommerce.prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class FinalOrderActivity extends AppCompatActivity {
     TextView name,phone,address,city;
    Button confirm;
    String totalamt = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_final_order);
        name =  findViewById(R.id.name);
        phone =  findViewById(R.id.phone);
        address =  findViewById(R.id.address);
        city =  findViewById(R.id.city);
        confirm =  findViewById(R.id.confirm);

        totalamt = getIntent().getStringExtra("Total Price");
        Toast.makeText(this,"Total Price = ₹ " + totalamt,  Toast.LENGTH_LONG).show();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Check();
            }
        });
    }

    private void Check() {
        if (TextUtils.isEmpty(name.getText().toString())){
            Toast.makeText(this,"please provide ur  full name",Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(phone.getText().toString())){
            Toast.makeText(this,"please provide ur  phone number",Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(address.getText().toString())){
            Toast.makeText(this,"please provide ur  full address",Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(city.getText().toString())){
            Toast.makeText(this,"please provide ur  city",Toast.LENGTH_SHORT).show();
            }
            else{
            ConfirmOrder();
        }
    }

    private void ConfirmOrder() {
        final  String savecurrentTime, savecurrentDate;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyy");
        savecurrentDate = currentDate.format(calForDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        savecurrentTime = currentDate.format(calForDate.getTime());

        final DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders").child(Prevalent.currentOnlineUser.getPhone());

        final HashMap<String, Object> ordermap = new HashMap<>();
        ordermap.put("totalamt", totalamt);
        ordermap.put("name", name.getText().toString());
        ordermap.put("phone", phone.getText().toString());
        ordermap.put("address", address.getText().toString());
        ordermap.put("city", city.getText().toString());
        ordermap.put("date", savecurrentDate);
        ordermap.put("time", savecurrentTime);
        ordermap.put("state", "not shipped");

        ordersRef.updateChildren(ordermap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                FirebaseDatabase.getInstance().getReference()
                        .child("Cart List")
                        .child("User View")
                        .child(Prevalent.currentOnlineUser.getPhone()).removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                           if (task.isSuccessful()){
                               Toast.makeText(FinalOrderActivity.this,"Ur final order is successful",Toast.LENGTH_SHORT).show();

                               Intent intent = new Intent(FinalOrderActivity.this,HomeActivity.class);
                               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                               startActivity(intent);
                                finish();
                           }
                            }
                        });
            }
        });

    }

}
