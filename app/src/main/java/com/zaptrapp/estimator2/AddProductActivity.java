package com.zaptrapp.estimator2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class AddProductActivity extends AppCompatActivity {

    private RadioGroup rgAdd;
    private RadioButton rbAddProduct;
    private RadioButton rbAddVa;
    private RadioGroup rgAddGoldOrSilver;
    private RadioButton rbAddVaGold;
    private RadioButton rbAddVaSilver;
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

    private void initView() {
        rgAdd = (RadioGroup) findViewById(R.id.rg_add);
        rbAddProduct = (RadioButton) findViewById(R.id.rb_add_product);
        rbAddVa = (RadioButton) findViewById(R.id.rb_add_va);
        rgAddGoldOrSilver = (RadioGroup) findViewById(R.id.rg_add_gold_or_silver);
        rbAddVaGold = (RadioButton) findViewById(R.id.rb_add_va_gold);
        rbAddVaSilver = (RadioButton) findViewById(R.id.rb_add_va_silver);
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        initView();
        initDisplayViews();

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
    }

    private void initDisplayViews() {
        layoutAddProduct.setVisibility(View.GONE);
        rgAddGoldOrSilver.setVisibility(View.GONE);
        layoutAddVa.setVisibility(View.GONE);
        rgCustomVa.setVisibility(View.GONE);
    }


}
