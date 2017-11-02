package com.zaptrapp.estimator2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
        rgAdd = (RadioGroup) findViewById(R.id.rg_add);
        rbAddProduct = (RadioButton) findViewById(R.id.rb_add_product);
        rbAddVa = (RadioButton) findViewById(R.id.rb_add_va);
        rgAddGoldOrSilver = (RadioGroup) findViewById(R.id.rg_add_gold_or_silver);
        rbAddGold = (RadioButton) findViewById(R.id.rb_add_gold);
        rbAddSilver = (RadioButton) findViewById(R.id.rb_add_silver);
        layoutAddProduct = (LinearLayout) findViewById(R.id.layout_add_product);
        etAddProductName = (EditText) findViewById(R.id.et_add_product_name);
        layoutAddVa = (LinearLayout) findViewById(R.id.layout_add_va);
        etAddVaBelowOne = (EditText) findViewById(R.id.et_add_va_below_one);
        etAddVaOne = (EditText) findViewById(R.id.et_add_va_one);
        etAddVaTwo = (EditText) findViewById(R.id.et_add_va_two);
        etAddVaThree = (EditText) findViewById(R.id.et_add_va_three);
        etAddVaFour = (EditText) findViewById(R.id.et_add_va_four);
        etAddVaFive = (EditText) findViewById(R.id.et_add_va_five);
        etAddVaSix = (EditText) findViewById(R.id.et_add_va_six);
        etAddVaAboveSix = (EditText) findViewById(R.id.et_add_va_above_six);
        rgCustomVa = (RadioGroup) findViewById(R.id.rg_custom_va);
        rbVaDefault = (RadioButton) findViewById(R.id.rb_va_default);
        rbVaCustom = (RadioButton) findViewById(R.id.rb_va_custom);
        btAddProduct = (Button) findViewById(R.id.bt_add_product);
        btAddVa = (Button) findViewById(R.id.bt_add_va);
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

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

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
        String product = goldOrSilver();

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
                productModel = new Product(stringFromET(etAddProductName));
                break;
            default:
                productModel = new Product();
                break;
        }
        databaseReference.child(product).child(productModel.getProductName()).setValue(productModel);


    }

    private String goldOrSilver() {
        int product_id = rgAddGoldOrSilver.getCheckedRadioButtonId();
        String product = "Error";
        switch (product_id) {
            case R.id.rb_add_gold:
                product = "gold";
                break;
            case R.id.rb_add_silver:
                product = "silver";
                break;
            default:
                product = "Error";
                break;
        }
        return product;
    }

    public void onClickAddVA(View view) {
        String product = goldOrSilver();
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

}
