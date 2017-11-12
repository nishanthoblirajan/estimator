package com.zaptrapp.estimator2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    SharedPreferences mSharedPreferences;
    String materialChoice;
    private RadioGroup rgAdd;
    private RadioButton rbAddProduct;
    private RadioButton rbAddVa;
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
    private TextView tvChosenProduct;

    private void initView() {
        rgAdd = findViewById(R.id.rg_add);
        rbAddProduct = findViewById(R.id.rb_add_product);
        rbAddVa = findViewById(R.id.rb_add_va);
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
        tvChosenProduct = findViewById(R.id.tv_chosen_product);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        initDatabase();
        initView();
        initSharedPreference();
        initDisplayViews();
        setClickListenersForButtons();


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
        tvChosenProduct.setText(materialChoice);
    }

    private void initDatabase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("estimator2");
    }

    private void saveVAToFirebase(VA va) {
        databaseReference.child("VA").child(va.getMaterial()).setValue(va);
    }


    ///////Retrival of Default VAs from Firebase Database
    private void retrieveVAFromFirebase() {
        Log.d(TAG, "retrieveDataFromFirebase: " + materialChoice);
        DatabaseReference databaseReference1 = databaseReference.child("VA").child(materialChoice);
        Log.d(TAG, "retrieveDataFromFirebase: " + databaseReference1.getRef());
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                VA valueAdded = dataSnapshot.getValue(VA.class);
//                Log.d(TAG, "datachange"+valueAdded.toString());
                initVAs(valueAdded);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                initVAs();
            }
        });
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
        Log.d(TAG, "initVAs: " + valueAdded.toString());
    }

    private void showCustomVAInput() {
        layoutAddVa.setVisibility(View.VISIBLE);
    }

    private void addProductMode() {
        layoutAddProduct.setVisibility(View.VISIBLE);
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

    private void productType() {
        retrieveVAFromFirebase();
    }

    private void showDefaultProductInput() {
        layoutAddVa.setVisibility(View.GONE);
    }

    private void addVaMode() {
        layoutAddProduct.setVisibility(View.GONE);
        layoutAddVa.setVisibility(View.VISIBLE);
        rgCustomVa.setVisibility(View.GONE);
        btAddProduct.setVisibility(View.GONE);
        btAddVa.setVisibility(View.VISIBLE);

    }

    private void initDisplayViews() {
        layoutAddProduct.setVisibility(View.GONE);
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
                Log.d(TAG, "onClickAddProduct: " + productModel.toString());
                break;
            default:
                productModel = new Product();
                break;
        }
        String product_choice = "Error";
        product_choice = materialChoice;

        if (productModel != null) {
            saveProductToFirebase(productModel, product_choice);
            Toast.makeText(this, "Process Completed", Toast.LENGTH_SHORT).show();
            initDisplayViews();
        } else {
            Toast.makeText(this, "Adding product failed. Try Again", Toast.LENGTH_SHORT).show();
        }

    }

    private void saveProductToFirebase(Product productModel, String product_choice) {
        databaseReference.child(product_choice).child(productModel.getProductName()).setValue(productModel);
    }

    public void onClickAddVA(View view) {

        String product = materialChoice;
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

        if (vaModel != null) {
            saveVAToFirebase(vaModel);
            Toast.makeText(this, "Process Completed", Toast.LENGTH_SHORT).show();
            initDisplayViews();
        } else {
            Toast.makeText(this, "Adding VA Failed... Try Again", Toast.LENGTH_SHORT).show();
        }

    }
}
