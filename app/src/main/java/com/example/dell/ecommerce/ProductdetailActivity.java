package com.example.dell.ecommerce;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.dell.ecommerce.model.Products;
import com.example.dell.ecommerce.prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductdetailActivity extends AppCompatActivity {
private FloatingActionButton fab;
private ElegantNumberButton number_btn;
private ImageView product_image_details;
private TextView product_name,product_description,product_price;
    private String productID = "",state = "Normal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productdetail);

        productID = getIntent().getStringExtra("pid");
        number_btn =  findViewById(R.id.number_btn);
        product_image_details =  findViewById(R.id.product_image_details);
        product_name =  findViewById(R.id.product_name);
        product_description =  findViewById(R.id.product_description);
        product_price =  findViewById(R.id.product_price);

        getProductdetails(productID);

        fab =  findViewById(R.id.add_product_to_cart);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state.equals("Order Placed") || state.equals("Order Shipped")){
                    Toast.makeText(ProductdetailActivity.this,"U can add purchaes more products, Once ur order is shipped or confirmed", Toast.LENGTH_SHORT).show();
                }
                else{
                    addingToCartList();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        CheckOrderState();
    }

    private void addingToCartList() {
        String savecurrentTime, savecurrentDate;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyy");
        savecurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        savecurrentTime = currentDate.format(calForDate.getTime());

        final DatabaseReference cartlistref = FirebaseDatabase.getInstance().getReference().child("Cart List");
        final HashMap<String, Object> cartmap = new HashMap<>();
        cartmap.put("pid", productID);
        cartmap.put("pname", product_name.getText().toString());
        cartmap.put("price", product_price.getText().toString());
        cartmap.put("date", savecurrentDate);
        cartmap.put("time", savecurrentTime);
        cartmap.put("quantity", number_btn.getNumber());
        cartmap.put("discount", "");
        cartlistref.child("User View").child(Prevalent.currentOnlineUser.getPhone())
                .child("Products").child(productID)
                .updateChildren(cartmap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    cartlistref.child("Admin View").child(Prevalent.currentOnlineUser.getPhone())
                            .child("Products").child(productID)
                            .updateChildren(cartmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ProductdetailActivity.this,"Add to cart List", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ProductdetailActivity.this,HomeActivity.class);
                            startActivity(intent);
                        }
                        }
                    });
                }

            }
        });
    }

    private void getProductdetails(String productID) {
        DatabaseReference ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        ProductsRef.child(productID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Products products = dataSnapshot.getValue(Products.class);
                product_name.setText(products.getPname());
                product_description.setText(products.getDescription());
                product_price.setText(products.getPrice());
                Picasso.get().load(products.getImage()).into(product_image_details);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void CheckOrderState() {
        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String shipingState = dataSnapshot.child("state").getValue().toString();
                    if (shipingState.equals("shipped")) {
                        state = "Order Shipped";
                    } else if (shipingState.equals("not shipped")) {

                        state = "Order Placed";

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
