package com.example.dell.ecommerce;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dell.ecommerce.model.Orders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminNewOrderActivity extends AppCompatActivity {
RecyclerView orderList;
DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_order);
        orderList = findViewById(R.id.order_list);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Orders");
        orderList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Orders> options =
                new  FirebaseRecyclerOptions.Builder<Orders>()
                .setQuery(databaseReference,Orders.class)
                .build();

        FirebaseRecyclerAdapter<Orders,AdminOrderViewHolder> adapter =
                new FirebaseRecyclerAdapter<Orders, AdminOrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AdminOrderViewHolder holder, final int position, @NonNull final Orders model) {
                holder.userName.setText( "Name:- " + model.getName());
                holder.PhoneNo.setText("Phone No:- " + model.getPhone());
                holder.totalPrice.setText("Total Amount = Rs" + model.getTotalamt());
                holder.dateTime.setText("Order at:- " + model.getDate() + " " + model.getTime());
                holder.addressCity.setText(" Shiipping Address:-"  + model.getAddress() + " " + model.getCity());

                holder.showAllProducts.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String uId = getRef(position).getKey();

                        Intent intent = new Intent(AdminNewOrderActivity.this,AdminUserProductActivity.class);
                        intent.putExtra("uid",uId);
                        startActivity(intent);
                    }
                });
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CharSequence options[] = new CharSequence[]
                                    {
                                            "Yes",
                                            "No"
                                    };
                            AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrderActivity.this);
                            builder.setTitle("Have u shipped this products ?");
                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int i) {
                                 if (i == 0){

                                     String uId = getRef(position).getKey();
                                        removeOrder(uId);
                                 }
                                 else
                                 {
                                     finish();
                                 }
                                }
                            });
                            builder.show();
                        }
                    });
            }

            @NonNull
            @Override
            public AdminOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_list,parent,false);
                return new AdminOrderViewHolder(view);
            }
        };

        orderList.setAdapter(adapter);
        adapter.startListening();
    }



    public  static class AdminOrderViewHolder extends RecyclerView.ViewHolder{
    public TextView userName,PhoneNo,totalPrice,addressCity,dateTime;
    Button showAllProducts;
    public AdminOrderViewHolder(View itemView) {
        super(itemView);
        userName =  itemView.findViewById(R.id.userName);
        PhoneNo =  itemView.findViewById(R.id.PhoneNo);
        totalPrice = itemView.findViewById(R.id.totalPrice);
        addressCity = itemView.findViewById(R.id.addressCity);
        dateTime = itemView.findViewById(R.id.dateTime);
        showAllProducts = itemView.findViewById(R.id.showAllProducts);
    }
}
    private void removeOrder(String uId) {
    databaseReference.child(uId).removeValue();
    }

}
