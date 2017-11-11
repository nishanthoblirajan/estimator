package com.zaptrapp.estimator2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zaptrapp.estimator2.Models.Product;
import com.zaptrapp.estimator2.Models.VA;

public class AddProductActivity extends AppCompatActivity {
    public static final String TAG = AddProductActivity.class.getSimpleName();
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    //required VAs
    double belowOne;
    double one;
    double two;
    double three;
    double four;
    double five;
    double six;
    double aboveSix;
    private RadioGroup rgAdd;
    private RadioButton rbAddProduct;
    private RadioButton rbAddVa;
    private RadioGroup rgAddGoldOrSilver;
    private RadioButton rbAddGold;
    private RadioButton rbAddSilver;
    private LinearLayout layoutAddProduct;
    private EditText etAddProductName;
    private LinearLayout layoutAddVa;
    private EditText etAddVaBelowOne;
    private EditText etAddVaOne;
    private EditText etAddVaTwo;
    private EditText etAddVaThree;
    private EditText etAddVaFour;
    private EditText etAddVaFive;
    private EditText etAddVaSix;
    private EditText etAddVaAboveSix;
    private RadioGroup rgCustomVa;
    private RadioButton rbVaDefault;
    private RadioButton rbVaCustom;
    private Button btAddProduct;
    private Button btAddVa;

    private void initView() {
        rgAdd = findViewById(R.id.rg_add);
        rbAddProduct = findViewById(R.id.rb_add_product);
        rbAddVa = findViewById(R.id.rb_add_va);
        rgAddGoldOrSilver = findViewById(R.id.rg_add_gold_or_silver);
        rbAddGold = findViewById(R.id.rb_add_gold);
        rbAddSilver = findViewById(R.id.rb_add_silver);
        layoutAddProduct = findViewById(R.id.layout_add_product);
        etAddProductName = findViewById(R.id.et_add_product_name);
        layoutAddVa = findViewById(R.id.layout_add_va);
        etAddVaBelowOne = findViewById(R.id.et_add_va_below_one);
        etAddVaOne = findViewById(R.id.et_add_va_one);
        etAddVaTwo = findViewById(R.id.et_add_va_two);
        etAddVaThree = findViewById(R.id.et_add_va_three);
        etAddVaFour = findViewById(R.id.et_add_va_four);
        etAddVaFive = findViewById(R.id.et_add_va_five);
        etAddVaSix = findViewById(R.id.et_add_va_six);
        etAddVaAboveSix = findViewById(R.id.et_add_va_above_six);
        rgCustomVa = findViewById(R.id.rg_custom_va);
        rbVaDefault = findViewById(R.id.rb_va_default);
        rbVaCustom = findViewById(R.id.rb_va_custom);
        btAddProduct = findViewById(R.id.bt_add_product);
        btAddVa = findViewById(R.id.bt_add_va);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        initDatabase();
        initView();
        initDisplayViews();
        setClickListenersForButtons();


    }

    private void initDatabase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("estimator2");
    }

    private void setClickListenersForButtons() {
        rbAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProductMode();
            }
        });
        rbAddVa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVaMode();
            }
        });
    }

    private void showCustomVAInput() {
        layoutAddVa.setVisibility(View.VISIBLE);
    }

    private void addProductMode() {
        layoutAddProduct.setVisibility(View.VISIBLE);
        rgAddGoldOrSilver.setVisibility(View.VISIBLE);
        layoutAddVa.setVisibility(View.GONE);
        rgCustomVa.setVisibility(View.VISIBLE);
        btAddProduct.setVisibility(View.VISIBLE);
        btAddVa.setVisibility(View.GONE);
        rbVaCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomVAInput();
            }
        });
        rbVaDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDefaultProductInput();
            }
        });

        productType();


    }

    private void productType() {
        rbAddGold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveDataFromFirebase("gold");
            }
        });

        rbAddSilver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveDataFromFirebase("silver");
            }
        });
    }

    private void showDefaultProductInput() {
        layoutAddVa.setVisibility(View.GONE);
    }

    private void addVaMode() {
        layoutAddProduct.setVisibility(View.GONE);
        rgAddGoldOrSilver.setVisibility(View.VISIBLE);
        layoutAddVa.setVisibility(View.VISIBLE);
        rgCustomVa.setVisibility(View.GONE);
        btAddProduct.setVisibility(View.GONE);
        btAddVa.setVisibility(View.VISIBLE);

    }

    private void initDisplayViews() {
        layoutAddProduct.setVisibility(View.GONE);
        rgAddGoldOrSilver.setVisibility(View.GONE);
        layoutAddVa.setVisibility(View.GONE);
        rgCustomVa.setVisibility(View.GONE);
        btAddProduct.setVisibility(View.GONE);
        btAddVa.setVisibility(View.GONE);
    }

    private String stringFromET(EditText et) {
        String input = et.getText().toString();
        if (input.matches("")) {
            Toast.makeText(this, "No input " + et.getHint(), Toast.LENGTH_SHORT).show();
        } else {
            return input;
        }
        return "";
    }

    private double doubleFromET(EditText et) {
        String input = et.getText().toString();
        if (input.matches("")) {
            Toast.makeText(this, "No input " + et.getHint(), Toast.LENGTH_SHORT).show();
        } else {
            return Double.parseDouble(input);
        }
        return 0;
    }

    public void onClickAddProduct(View view) {

        int va_choice = rgCustomVa.getCheckedRadioButtonId();
        String vaChoice = "Error";
        switch (va_choice) {
            case R.id.rb_va_default:
                vaChoice = "default";
                break;
            case R.id.rb_va_custom:
                vaChoice = "custom";
                break;
            default:
                vaChoice = "Error";
                break;
        }
        Product productModel;
        switch (vaChoice) {
            case "custom":
                productModel = new Product(
                        stringFromET(etAddProductName),
                        doubleFromET(etAddVaBelowOne),
                        doubleFromET(etAddVaOne),
                        doubleFromET(etAddVaTwo),
                        doubleFromET(etAddVaThree),
                        doubleFromET(etAddVaFour),
                        doubleFromET(etAddVaFive),
                        doubleFromET(etAddVaSix),
                        doubleFromET(etAddVaAboveSix));
                break;
            case "default":
                productModel = new Product(
                        stringFromET(etAddProductName),
                        belowOne,
                        one,
                        two,
                        three,
                        four,
                        five,
                        six,
                        aboveSix);
                break;
            default:
                productModel = new Product();
                break;
        }
        int product_type = rgAddGoldOrSilver.getCheckedRadioButtonId();
        String product_choice = "Error";
        switch (product_type) {
            case R.id.rb_add_gold:
                product_choice = "gold";
                break;
            case R.id.rb_add_silver:
                product_choice = "silver";
                break;
            default:
                product_choice = "Error";
                break;
        }

        databaseReference.child(product_choice).child(productModel.getProductName()).setValue(productModel);


    }

    public void onClickAddVA(View view) {
        int product_type = rgAddGoldOrSilver.getCheckedRadioButtonId();
        String product_choice = "Error";
        switch (product_type) {
            case R.id.rb_add_gold:
                product_choice = "gold";
                break;
            case R.id.rb_add_silver:
                product_choice = "silver";
                break;
            default:
                product_choice = "Error";
                break;
        }

        String product = product_choice;
        VA vaModel = new VA(
                product,
                doubleFromET(etAddVaBelowOne),
                doubleFromET(etAddVaOne),
                doubleFromET(etAddVaTwo),
                doubleFromET(etAddVaThree),
                doubleFromET(etAddVaFour),
                doubleFromET(etAddVaFive),
                doubleFromET(etAddVaSix),
                doubleFromET(etAddVaAboveSix));

        databaseReference.child("VA").child(vaModel.getMaterial()).setValue(vaModel);

    }

    ///////Retrival of Default VAs from Firebase Database
    private void retrieveDataFromFirebase(String product_estimate) {
        Log.d(TAG, "retrieveDataFromFirebase: " + product_estimate);
        DatabaseReference databaseReference1 = firebaseDatabase.getReference("estimator2/VA/" + product_estimate);
//        databaseReference1.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                VA valueAdded = dataSnapshot.getValue(VA.class);
//                Log.d(TAG, valueAdded.toString());
//                initVAs(valueAdded);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                initVAs();
//            }
//        });
    }

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
