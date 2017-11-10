package com.zaptrapp.estimator2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zaptrapp.estimator2.Models.EstimateLog;
import com.zaptrapp.estimator2.Models.ProductHolder;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogActivity extends AppCompatActivity {

    private RecyclerView logRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        initView();
        initRecycler();
    }

    SharedPreferences mSharedPreferences;
    FirebaseRecyclerAdapter<EstimateLog, ProductHolder> logAdapter;
    String dateStamp = new SimpleDateFormat("dd-MM-yy").format(new Date());
    public static final String TAG = LogActivity.class.getSimpleName();

    private void initRecycler() {

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
        Query query = FirebaseDatabase.getInstance().getReference("estimator2").child("Estimates").child(materialChoice).child(dateStamp);
        Log.d(TAG, "initRecycler: " + query);
        logRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange:\n" + dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerOptions<EstimateLog> estimateLogFirebaseRecyclerOptions =
                new FirebaseRecyclerOptions.Builder<EstimateLog>()
                        .setQuery(query, EstimateLog.class)
                        .build();
        Log.d(TAG, "initRecycler: "+estimateLogFirebaseRecyclerOptions.toString());

        logAdapter = new FirebaseRecyclerAdapter<EstimateLog, ProductHolder>(estimateLogFirebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(ProductHolder holder, int position, EstimateLog model) {
                Log.d(TAG, "onBindViewHolder: ");
                holder.product.setText(model.getEstimate());
            }

            @Override
            public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                Log.d(TAG, "onCreateViewHolder: ");
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_items, parent, false);
                return new ProductHolder(view);
            }
        };
        logRecyclerView.setAdapter(logAdapter);
    }

    private void initView() {
        logRecyclerView = (RecyclerView) findViewById(R.id.log_recyclerView);
    }
}
