package com.zaptrapp.estimator2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.zaptrapp.estimator2.Models.Product;
import com.zaptrapp.estimator2.Models.ProductHolder;

public class ViewProducts extends AppCompatActivity {

    public static final String TAG = ViewProducts.class.getSimpleName();
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseRecyclerAdapter<Product, ProductHolder> mProductListAdapter;
    SharedPreferences mSharedPreferences;
    Context mContext;
    private Toolbar toolbar;
    private RecyclerView productListRecyclerView;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_products);
        mContext = getApplicationContext();
        initView();
        initFirebase();
        setSupportActionBar(toolbar);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "In Progress", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        initproductListRecycler();


    }

    private void initFirebase() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("estimator2");
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        productListRecyclerView = findViewById(R.id.product_list_recycler_view);
        fab = findViewById(R.id.fab);
    }

    private void initproductListRecycler() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String choice = mSharedPreferences.getString("materialPref", "1");
        String materialChoice = "gold";
        switch (choice) {
            case "1":
                //This is gold
                materialChoice = "gold";

                break;
            case "2":
                //This is silver
                materialChoice = "silver";
                break;
            default:
                //This is default
                break;
        }
        Log.d(TAG, "initproductListRecycler: ");
        Query productListQuery = mDatabaseReference.child(materialChoice);
        Log.d(TAG, "initproductListRecycler: " + productListQuery.getRef());
        productListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        productListQuery.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.d(TAG, "onDataChange: " + dataSnapshot.getValue().toString());
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        FirebaseRecyclerOptions<Product> productOptions =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(productListQuery, Product.class)
                        .build();
        mProductListAdapter = new FirebaseRecyclerAdapter<Product, ProductHolder>(productOptions) {
            @Override
            protected void onBindViewHolder(ProductHolder holder, int position, final Product model) {
                Log.d(TAG, "initproductListRecycler onBindViewHolder: ");
                holder.product.setText(model.getProductName());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //set the values for the VAs
                        showDialog(model);

                    }
                });
            }

            @Override
            public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                Log.d(TAG, "initproductListRecycler onCreateViewHolder: ");
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.log_recycler_item, parent, false);
                return new ProductHolder(view);
            }


        };
        productListRecyclerView.setAdapter(mProductListAdapter);
    }

    public void showDialog(final Product product) {

        String description =
                "<1g  : " + product.getLessThanOne() +
                        "%\n1-2g : " + product.getOne() +
                        "%\n2-3g : " + product.getTwo() +
                        "%\n3-4g : " + product.getThree() +
                        "%\n4-5g : " + product.getFour() +
                        "%\n5-6g : " + product.getFive() +
                        "%\n6-7g : " + product.getSix() +
                        "%\n>7g  : " + product.getGreaterThanSix() + "%\n\n";
        new MaterialStyledDialog.Builder(this)
                .setStyle(Style.HEADER_WITH_TITLE)
                .setTitle(product.getProductName())
                .setDescription(description)
                .setPositiveText("Edit")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent(mContext, EditProduct.class);
                        intent.putExtra("productExtra", product);
                        startActivity(intent);

                    }
                })
                .setCancelable(true)
                .show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mProductListAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mProductListAdapter.stopListening();
    }
}
