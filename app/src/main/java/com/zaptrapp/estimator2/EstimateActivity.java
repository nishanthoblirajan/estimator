package com.zaptrapp.estimator2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class EstimateActivity extends AppCompatActivity {

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
        initView();
    }


    //On Estimate Button Click
    public void onClickEstimate(View view) {

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
