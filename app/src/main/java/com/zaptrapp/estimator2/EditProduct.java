package com.zaptrapp.estimator2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zaptrapp.estimator2.Models.Product;

public class EditProduct extends AppCompatActivity {

    public static final String TAG = EditProduct.class.getSimpleName();
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    SharedPreferences mSharedPreferences;
    String materialChoice;
    Product product;
    private EditText etEditProductName;
    private EditText etEditVaBelowOne;
    private EditText etEditVaOne;
    private EditText etEditVaTwo;
    private EditText etEditVaThree;
    private EditText etEditVaFour;
    private EditText etEditVaFive;
    private EditText etEditVaSix;
    private EditText etEditVaAboveSix;
    private Button btChangeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        initView();
        initDatabase();
        initSharedPreference();
        initData();
    }

    private void initDatabase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("estimator2");
    }

    private void initSharedPreference() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String choice = mSharedPreferences.getString("materialPref", "1");
        materialChoice = "gold";
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
    }

    private void doubleToEditText(EditText editText, double value) {
        editText.setText(String.valueOf(value));
    }

    private void initData() {
        product = getIntent().getParcelableExtra("productExtra");
        if (!(product == null)) {
            etEditProductName.setText(product.getProductName());
            doubleToEditText(etEditVaBelowOne, product.getLessThanOne());
            doubleToEditText(etEditVaOne, product.getOne());
            doubleToEditText(etEditVaTwo, product.getTwo());
            doubleToEditText(etEditVaThree, product.getThree());
            doubleToEditText(etEditVaFour, product.getFour());
            doubleToEditText(etEditVaFive, product.getFive());
            doubleToEditText(etEditVaSix, product.getSix());
            doubleToEditText(etEditVaAboveSix, product.getGreaterThanSix());
            btChangeData.setVisibility(View.VISIBLE);
        }


    }

    private void initView() {
        etEditProductName = findViewById(R.id.et_edit_product_name);
        etEditVaBelowOne = findViewById(R.id.et_edit_va_below_one);
        etEditVaOne = findViewById(R.id.et_edit_va_one);
        etEditVaTwo = findViewById(R.id.et_edit_va_two);
        etEditVaThree = findViewById(R.id.et_edit_va_three);
        etEditVaFour = findViewById(R.id.et_edit_va_four);
        etEditVaFive = findViewById(R.id.et_edit_va_five);
        etEditVaSix = findViewById(R.id.et_edit_va_six);
        etEditVaAboveSix = findViewById(R.id.et_edit_va_above_six);
        btChangeData = findViewById(R.id.bt_change_data);
        btChangeData.setVisibility(View.GONE);
    }

    private double stringToDouble(EditText input) {
        if (input.getText() != null) {
            return Double.parseDouble(input.getText().toString());
        }
        return 0;
    }

    public void onClickChange(View view) {
        Log.d(TAG, "onClickChange: " + materialChoice);

        Product newProduct = new Product(etEditProductName.getText().toString(),
                stringToDouble(etEditVaBelowOne),
                stringToDouble(etEditVaOne),
                stringToDouble(etEditVaTwo),
                stringToDouble(etEditVaThree),
                stringToDouble(etEditVaFour),
                stringToDouble(etEditVaFive),
                stringToDouble(etEditVaSix),
                stringToDouble(etEditVaAboveSix));
        Log.d(TAG, "onClickChange: " + newProduct.toString());
        databaseReference.child(materialChoice).child(newProduct.getProductName()).setValue(newProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(EditProduct.this, "Process Completed", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProduct.this, "Process Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
