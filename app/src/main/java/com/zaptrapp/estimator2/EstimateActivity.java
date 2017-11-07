package com.zaptrapp.estimator2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.zaptrapp.estimator2.Models.Product;
import com.zaptrapp.estimator2.Models.ProductHolder;
import com.zaptrapp.estimator2.Models.VA;

import java.math.BigDecimal;
import java.math.RoundingMode;

import io.reactivex.functions.Consumer;

public class EstimateActivity extends AppCompatActivity {


    public static final String TAG = EstimateActivity.class.getSimpleName();
    SharedPreferences sharedPreferences;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String product = "silver";
    ////////////////////////////////////////Setting the gold and silver recycler view///////////////////////////////////
    FirebaseRecyclerAdapter<Product, ProductHolder> goldAdapter;
    FirebaseRecyclerAdapter<Product, ProductHolder> silverAdapter;
    FirebaseRecyclerAdapter<Product, ProductHolder> searchAdapter;
    Query goldQuery;
    Query silverQuery;
    //required VAs
    double belowOne;
    double one;
    double two;
    double three;
    double four;
    double five;
    double six;
    double aboveSix;
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
    private EditText etVaPercentage;
    private EditText etVaNumber;
    private TextView tvChoiceClicked;
    private FrameLayout toolbarContainer;
    private Toolbar toolbar;
    private RecyclerView searchRecyclerView;
    private CheckBox cbBuying;
    private LinearLayout llBuying;
    private EditText etBuyingPrice;
    private EditText etGrossWeight;
    private EditText etNetWeight;

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    //View initalization
    private void initView() {
        etGramRate = findViewById(R.id.et_gram_rate);
        etProductGram = findViewById(R.id.et_product_gram);
        rgProduct = findViewById(R.id.rg_product);
        rbGold = findViewById(R.id.rb_gold);
        rbSilver = findViewById(R.id.rb_silver);
        rgHOrK = findViewById(R.id.rg_h_or_k);
        rbHallmark = findViewById(R.id.rb_hallmark);
        rbKdm = findViewById(R.id.rb_kdm);
        btEstimate = findViewById(R.id.bt_estimate);
        tvEstimateOut = findViewById(R.id.tv_estimate_out);
        goldRecyclerView = findViewById(R.id.gold_recyclerView);
        silverRecyclerView = findViewById(R.id.silver_recyclerView);
        testingTv = findViewById(R.id.testing_tv);
        etVaPercentage = findViewById(R.id.et_va_percentage);
        etVaNumber = findViewById(R.id.et_va_number);
        tvChoiceClicked = findViewById(R.id.tv_choice_clicked);

        etVaPercentage.setVisibility(View.GONE);
        etVaNumber.setVisibility(View.GONE);
        toolbarContainer = findViewById(R.id.toolbar_container);
        toolbar = findViewById(R.id.toolbar);

        searchRecyclerView = (RecyclerView) findViewById(R.id.search_recyclerView);
        cbBuying = (CheckBox) findViewById(R.id.cb_buying);
        llBuying = (LinearLayout) findViewById(R.id.ll_buying);
        etBuyingPrice = (EditText) findViewById(R.id.et_buying_price);
        etGrossWeight = (EditText) findViewById(R.id.et_gross_weight);
        etNetWeight = (EditText) findViewById(R.id.et_net_weight);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimate);

        buyingItem = false;
        initVAs();
        initDatabase();
        initView();
        initToolbar();

        initRadioButtons();
        initGoldRecycler("gold");
        initSilverRecycler("silver");
        initSharedPreference();

        setupListeners();
    }


    private void initToolbar() {
        setSupportActionBar(toolbar);

    }
    //onOptionsItemSelected

    //Listeners to know whether the program gram has been inputed
    private void setupListeners() {
        RxTextView.textChanges(etProductGram)
                .subscribe(new Consumer<CharSequence>() {
                    @Override
                    public void accept(CharSequence charSequence) throws Exception {
                        if (charSequence.length() > 0) {
                            showVAs();
                            if (etGramRate.getText() != null) {
                                double productGram = Double.parseDouble(charSequence.toString());
                                if (productGram >= 7) {
                                    vaPercentShow(aboveSix, etGramRate.getText().toString());
                                } else if (productGram < 7 && productGram >= 6) {
                                    vaPercentShow(six, etGramRate.getText().toString());
                                } else if (productGram < 6 && productGram >= 5) {
                                    vaPercentShow(five, etGramRate.getText().toString());
                                } else if (productGram < 5 && productGram >= 4) {
                                    vaPercentShow(four, etGramRate.getText().toString());
                                } else if (productGram < 4 && productGram >= 3) {
                                    vaPercentShow(three, etGramRate.getText().toString());
                                } else if (productGram < 3 && productGram >= 2) {
                                    vaPercentShow(two, etGramRate.getText().toString());
                                } else if (productGram < 2 && productGram >= 1) {
                                    vaPercentShow(one, etGramRate.getText().toString());
                                } else {
                                    vaPercentShow(belowOne, etGramRate.getText().toString());
                                }
                            } else {
                                unshowVAs();
                            }
                        }
                    }
                });

        cbBuying.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    llBuying.setVisibility(View.VISIBLE);
                    buyingItem = true;
                }else{
                    llBuying.setVisibility(View.GONE);
                    buyingItem = false;
                }
            }
        });

    }

    boolean buyingItem;
    private void vaPercentShow(double input, String gramRateString) {
        double gramRate = Double.parseDouble(gramRateString);
        etVaPercentage.setHint(String.valueOf(input) + "%");
        double vaNumberDouble = (input / 100) * gramRate;
        etVaNumber.setHint(String.valueOf(round(vaNumberDouble, 2)));
    }

    private void unshowVAs() {
        etVaPercentage.setVisibility(View.GONE);
        etVaNumber.setVisibility(View.GONE);
    }

    private void showVAs() {
        etVaPercentage.setVisibility(View.VISIBLE);
        etVaNumber.setVisibility(View.VISIBLE);
    }

    private void initSharedPreference() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String choice = sharedPreferences.getString("materialPref", "1");
        Toast.makeText(this, choice, Toast.LENGTH_SHORT).show();

        //setting the gramrate from the preference screen
        etGramRate.setText(sharedPreferences.getString("gramRatePref", "0"));
        etGramRate.setEnabled(false);
        //TODOCOMPLETED show only gold or silver based on the sharedpreference
        switch (choice) {
            case "1":
                //This is gold
                rbGold.setChecked(true);
                rbGold.setEnabled(true);
                rbSilver.setChecked(false);
                rbSilver.setEnabled(false);

                break;
            case "2":
                //This is silver
                rbGold.setChecked(false);
                rbGold.setEnabled(false);
                rbSilver.setChecked(true);
                rbSilver.setEnabled(true);
                break;
            default:
                //This is default
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initSharedPreference();
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

    //onOptionsmenuCreated
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                startActivity(new Intent(this, AddProductActivity.class));
                return true;
            case R.id.menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return false;
        }
    }


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
                        tvChoiceClicked.setText(model.getProductName());
                        setupListeners();

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
                        tvChoiceClicked.setText(model.getProductName());
                        setupListeners();

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
        Log.d(TAG, "VALUES: " + valueAdded.toString());
    }

    private void initVAFromProduct(Product product) {
        belowOne = product.getLessThanOne();
        one = product.getOne();
        two = product.getTwo();
        three = product.getThree();
        four = product.getFour();
        five = product.getFive();
        six = product.getSix();
        aboveSix = product.getGreaterThanSix();
        Log.d(TAG, "VALUES: " + product.toString());
    }
}
