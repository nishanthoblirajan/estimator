package com.zaptrapp.estimator2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zaptrapp.estimator2.Models.Product;
import com.zaptrapp.estimator2.Models.ProductHolder;
import com.zaptrapp.estimator2.Models.VA;

public class EstimateActivity extends AppCompatActivity {


    public static final String TAG = EstimateActivity.class.getSimpleName();

    private EditText etGramRate;
    private EditText etProductGram;
    private RadioGroup rgProduct;
    private RadioButton rbGold;
    private RadioButton rbSilver;
    private RadioGroup rgHOrK;
    private RadioButton rbHallmark;
    private RadioButton rbKdm;
    private Button btEstimate;
    private TextView tvEstimateOut;
    private RecyclerView goldRecyclerView;
    private RecyclerView silverRecyclerView;
    private TextView testingTv;

    //View initalization
    private void initView() {
        etGramRate = (EditText) findViewById(R.id.et_gram_rate);
        etProductGram = (EditText) findViewById(R.id.et_product_gram);
        rgProduct = (RadioGroup) findViewById(R.id.rg_product);
        rbGold = (RadioButton) findViewById(R.id.rb_gold);
        rbSilver = (RadioButton) findViewById(R.id.rb_silver);
        rgHOrK = (RadioGroup) findViewById(R.id.rg_h_or_k);
        rbHallmark = (RadioButton) findViewById(R.id.rb_hallmark);
        rbKdm = (RadioButton) findViewById(R.id.rb_kdm);
        btEstimate = (Button) findViewById(R.id.bt_estimate);
        tvEstimateOut = (TextView) findViewById(R.id.tv_estimate_out);
        goldRecyclerView = (RecyclerView) findViewById(R.id.gold_recyclerView);
        silverRecyclerView = (RecyclerView) findViewById(R.id.silver_recyclerView);
        testingTv = (TextView) findViewById(R.id.testing_tv);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimate);
        initDatabase();
        initView();
        initRadioButtons();
        initGoldRecycler("gold");
        initSilverRecycler("silver");

    }

    private void initRadioButtons() {
        rgProduct.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_gold:
                        product = "gold";
                        Log.d(TAG, "onCheckedChanged: " + product);

                        break;
                    case R.id.rb_silver:
                        product = "silver";
                        Log.d(TAG, "onCheckedChanged: " + product);
                        break;
                    default:
                        product = "silver";
                        Log.d(TAG, "onCheckedChanged: " + product);
                        break;
                }
                showRecycler(product);
            }
        });
    }

    private void showRecycler(String product) {
        switch (product) {
            case "silver":
                silverRecyclerView.setVisibility(View.VISIBLE);
                goldRecyclerView.setVisibility(View.GONE);
                break;
            case "gold":
                goldRecyclerView.setVisibility(View.VISIBLE);
                silverRecyclerView.setVisibility(View.GONE);
                break;
            default:
                goldRecyclerView.setVisibility(View.GONE);
                silverRecyclerView.setVisibility(View.GONE);
                break;
        }
    }

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private void initDatabase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("estimator2");
    }


    //On Estimate Button Click
    public void onClickEstimate(View view) {
        String product_estimate = product;
        Toast.makeText(this, product_estimate, Toast.LENGTH_SHORT).show();
        retrieveDataFromFirebase(product_estimate);

    }

    String product = "silver";

    //onOptionsmenuCreated
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    //onOptionsItemSelected

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                startActivity(new Intent(this, AddProductActivity.class));
                return true;
            default:
                return false;
        }
    }

    ////////////////////////////////////////Setting the gold and silver recycler view///////////////////////////////////
    FirebaseRecyclerAdapter<Product, ProductHolder> goldAdapter;
    FirebaseRecyclerAdapter<Product, ProductHolder> silverAdapter;

    Query goldQuery;
    Query silverQuery;

    private void initGoldRecycler(String product) {
        Log.d(TAG, "initGoldRecycler: ");
        goldQuery = databaseReference.child(product);
        Log.d(TAG, "initGoldRecycler: " + goldQuery.getRef());
        goldRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        goldQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerOptions<Product> productOptions =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(goldQuery, Product.class)
                        .build();
        goldAdapter = new FirebaseRecyclerAdapter<Product, ProductHolder>(productOptions) {
            @Override
            protected void onBindViewHolder(ProductHolder holder, int position, final Product model) {
                Log.d(TAG, "initGoldRecycler onBindViewHolder: ");
                holder.product.setText(model.getProductName());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //set the values for the VAs
                        initVAFromProduct(model);

                        //testing
                        testingTv.setText(model.toString());
                    }
                });
            }

            @Override
            public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                Log.d(TAG, "initGoldRecycler onCreateViewHolder: ");
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_items, parent, false);
                return new ProductHolder(view);
            }


        };
        goldRecyclerView.setAdapter(goldAdapter);
    }

    private void initSilverRecycler(String product) {
        Log.d(TAG, "initSilverRecycler: ");
        silverQuery = databaseReference.child(product);
        Log.d(TAG, "initSilverRecycler: " + silverQuery.getRef());
        silverRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        silverQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        FirebaseRecyclerOptions<Product> productOptions =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(silverQuery, Product.class)
                        .build();
        silverAdapter = new FirebaseRecyclerAdapter<Product, ProductHolder>(productOptions) {
            @Override
            protected void onBindViewHolder(ProductHolder holder, int position, final Product model) {
                Log.d(TAG, "initSilverRecycler onBindViewHolder: ");
                holder.product.setText(model.getProductName());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //set the values for the VAs
                        initVAFromProduct(model);

                        //testing
                        testingTv.setText(model.toString());
                    }
                });
            }

            @Override
            public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                Log.d(TAG, "initSilverRecycler onCreateViewHolder: ");
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_items, parent, false);
                return new ProductHolder(view);
            }
        };
        silverRecyclerView.setAdapter(silverAdapter);
    }


    @Override
    protected void onStart() {
        super.onStart();
        goldAdapter.startListening();
        silverAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        goldAdapter.stopListening();
        silverAdapter.stopListening();
    }

    /////////////////////////////////////Retrival of Default VAs from Firebase Database////////////////////////////////
    private void retrieveDataFromFirebase(String product_estimate) {
        Log.d(TAG, "retrieveDataFromFirebase: " + product_estimate);
        DatabaseReference databaseReference1 = firebaseDatabase.getReference("estimator2/VA/" + product_estimate);
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                VA valueAdded = dataSnapshot.getValue(VA.class);
                Log.d(TAG, valueAdded.toString());
                initVAs(valueAdded);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                initVAs();
            }
        });
    }

    //required VAs
    double belowOne;
    double one;
    double two;
    double three;
    double four;
    double five;
    double six;
    double aboveSix;

    //onCancelled
    private void initVAs() {
        belowOne = 0;
        one = 0;
        two = 0;
        three = 0;
        four = 0;
        five = 0;
        six = 0;
        aboveSix = 0;
    }

    //on receiving the value from the Firebase Database
    private void initVAs(VA valueAdded) {
        belowOne = valueAdded.getLessThanOne();
        one = valueAdded.getOne();
        two = valueAdded.getTwo();
        three = valueAdded.getThree();
        four = valueAdded.getFour();
        five = valueAdded.getFive();
        six = valueAdded.getSix();
        aboveSix = valueAdded.getGreaterThanSix();
        Log.d(TAG, "VALUES: "+valueAdded.toString());
    }
    
    private void initVAFromProduct(Product product){
        belowOne = product.getLessThanOne();
        one = product.getOne();
        two = product.getTwo();
        three = product.getThree();
        four = product.getFour();
        five = product.getFive();
        six = product.getSix();
        aboveSix = product.getGreaterThanSix();
        Log.d(TAG, "VALUES: "+product.toString());
    }
}
