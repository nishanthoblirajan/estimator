package com.zaptrapp.estimator2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zaptrapp.estimator2.Models.VA;

public class EstimateActivity extends AppCompatActivity {

    //required VAs
    double lessThanOne;
    double one;
    double two;
    double three;
    double four;
    double five;
    double six;
    double greaterThanSix;



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
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimate);
        initDatabase();
        initView();
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

    private void retrieveDataFromFirebase(String product_estimate) {
        Log.d(TAG, "retrieveDataFromFirebase: "+product_estimate);
        DatabaseReference databaseReference1 = firebaseDatabase.getReference("estimator2/VA/"+product_estimate);
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                VA valueAdded = dataSnapshot.getValue(VA.class);
                Log.d(TAG,valueAdded.toString());
                initVAs(valueAdded);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initVAs(VA valueAdded) {
        double lessThanOne;
        double one;
        double two;
        double three;
        double four;
        double five;
        double six;
        double greaterThanSix;

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
        menuInflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    //onOptionsItemSelected

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_add:
                startActivity(new Intent(this,AddProductActivity.class));
                return true;
            default:
                return false;
        }
    }
}
