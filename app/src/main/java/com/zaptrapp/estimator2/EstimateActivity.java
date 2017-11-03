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
    private RecyclerView recyclerView;

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
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimate);
        initDatabase();
        initView();
        initRecycler(goldOrSilver());
        onCheckChangeListener();

    }

    private void onCheckChangeListener() {

        rgProduct.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_gold:
                        initRecycler("gold");
                        break;
                    case R.id.rb_silver:
                        initRecycler("silver");
                        break;
                    default:
                        break;
                }
            }
        });

    }

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private void initDatabase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("estimator2");
    }


    //On Estimate Button Click
    public void onClickEstimate(View view) {
        String product_estimate = goldOrSilver();
        Toast.makeText(this, product_estimate, Toast.LENGTH_SHORT).show();
        retrieveDataFromFirebase(product_estimate);

    }





    FirebaseRecyclerAdapter<Product, ProductHolder> adapter;

    private void initRecycler(String product) {
        Log.d(TAG, "initRecycler: ");
        Query query = databaseReference.child(product);
        Log.d(TAG, "initRecycler: " + query.getRef());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: "+dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        FirebaseRecyclerOptions<Product> productOptions =
                new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(query,Product.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Product, ProductHolder>(productOptions) {
            @Override
            protected void onBindViewHolder(ProductHolder holder, int position, Product model) {
                Log.d(TAG, "initRecycler onBindViewHolder: ");
                holder.product.setText(model.getProductName());
            }

            @Override
            public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                Log.d(TAG, "initRecycler onCreateViewHolder: ");
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_items,parent,false);
                return new ProductHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
    private String goldOrSilver() {
        int product_id = rgProduct.getCheckedRadioButtonId();
        String product = "Error";
        switch (product_id) {
            case R.id.rb_gold:
                product = "gold";
                break;
            case R.id.rb_silver:
                product = "silver";
                break;
            default:
                product = "Error";
                break;
        }
        return product;
    }

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

    ///////Retrival of Default VAs from Firebase Database
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
    }
}
