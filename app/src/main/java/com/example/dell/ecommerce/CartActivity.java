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
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.ecommerce.ViewHolder.CartViewHolder;
import com.example.dell.ecommerce.model.Cart;
import com.example.dell.ecommerce.prevalent.Prevalent;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    public TextView totalamount,msg;
    public Button next;
    int overTotalPrice = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        totalamount = findViewById(R.id.totalamount);
        msg = findViewById(R.id.msg);
        next = findViewById(R.id.next);
        recyclerView = findViewById(R.id.Cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                totalamount.setText("Total Price = " + String.valueOf(overTotalPrice));

                Intent intent = new Intent(CartActivity.this,FinalOrderActivity.class);
                intent.putExtra("Total Price", String.valueOf(overTotalPrice));
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        CheckOrderState();
        final DatabaseReference cartlistref = FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
            .setQuery(cartlistref.child("User View")
                    .child(Prevalent.currentOnlineUser.getPhone()).child("Products"),Cart.class)
                        .build();
    FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter =
            new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {
                    holder.PQuantity.setText( "Quantity = " + model.getQuantity());
                    holder.PPrice.setText("Price " + model.getPrice() + " ");
                    holder.txtPName.setText(model.getPname());

                    int oneTypeProductPrice = ((Integer.valueOf(model.getPrice())))* Integer.valueOf(model.getQuantity());
                    overTotalPrice = overTotalPrice+oneTypeProductPrice;

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {
                            CharSequence options[] = new CharSequence[]{
                                    "Edit",
                                    "Remove"
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                            builder.setTitle("Cart Options");

                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int i) {
                                    if(i==0){
                                        Intent intent = new Intent(CartActivity.this, ProductdetailActivity.class);
                                        intent.putExtra("pid",model.getPid());
                                        startActivity(intent);
                                    }
                                    if(i==1){
                                        cartlistref.child("User View")
                                                .child(Prevalent.currentOnlineUser.getPhone())
                                                .child("Products")
                                                .child(model.getPid())
                                                .removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            Toast.makeText(CartActivity.this,"Item remove successfully", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                                                            startActivity(intent);
                                                        }
                                                    }
                                                });
                                    }
                                }

                            });
                            builder.show();
                        }
                    });
                }

                @NonNull
                @Override
                public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items,parent,false);
                    CartViewHolder holder = new CartViewHolder(view);
                    return holder;
                }
            };
                recyclerView.setAdapter(adapter);
                adapter.startListening();

    }
    private void CheckOrderState(){
        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()){
                String shipingState= dataSnapshot.child("state").getValue().toString();
                String username= dataSnapshot.child("name").getValue().toString();
                if (shipingState.equals("shipped")){
                    totalamount.setText("Dear" + username + "\n order is shipped successfully" );
                    recyclerView.setVisibility(View.GONE);
                    msg.setVisibility(View.VISIBLE);
                    msg.setText("Congratulation ur order has been shipped Successfully.Soon you will recevied ur order at ur door step");
                    next.setVisibility(View.GONE);
                    Toast.makeText(CartActivity.this," U can purchase more products,once u recevied ur first  final order",Toast.LENGTH_SHORT).show();

                }
                else if (shipingState.equals("not shipped")){
                    totalamount.setText("Shipping State = Not Shipped" );
                    recyclerView.setVisibility(View.GONE);
                    msg.setVisibility(View.VISIBLE);
                    next.setVisibility(View.GONE);
                    Toast.makeText(CartActivity.this," U can purchase more products,once u recevied ur first  final order",Toast.LENGTH_SHORT).show();

                }
            }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
