package com.zaptrapp.estimator2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.zaptrapp.estimator2.Models.CreateEstimate;
import com.zaptrapp.estimator2.Models.EstimateLog;
import com.zaptrapp.estimator2.Models.Product;
import com.zaptrapp.estimator2.Models.ProductHolder;
import com.zaptrapp.estimator2.Models.VA;
import com.zaptrapp.estimator2.Printer.ShowMsg;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import io.reactivex.functions.Consumer;

public class EstimateActivity extends AppCompatActivity implements ReceiveListener {

    public static final String TAG = EstimateActivity.class.getSimpleName();
    public String PRINTER = "BT:00:01:90:C2:AE:35";
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
    int selected_printer = 1;
    Drawer result;
    String hallmarkOrKDM = "";
    boolean buyingItem;
    double estimateVaPercent = 0;
    double estimateVaNumber = 0;
    double estimateProductGram = 0;
    double estimateBuyingPrice = 0;
    double estimateBuyingGrossWeight = 0;
    double estimateBuyingNetWeight = 0;
    double estimateExtraInput = 0;
    String material;
    String printer;
    double gramRate;
    double sgst;
    double cgst;
    String modelName;
    String ipAddressM30 = "0";
    List<CreateEstimate> mCreateEstimateList = new ArrayList<CreateEstimate>();
    Printer mPrinter;
    Context mContext = null;
    String dateStamp = new SimpleDateFormat("dd-MM-yy").format(new Date());
    String timeStamp = new SimpleDateFormat("HH-mm-ss").format(new Date());
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
    private CheckBox cbBuying;
    private LinearLayout llBuying;
    private EditText etBuyingPrice;
    private EditText etGrossWeight;
    private EditText etNetWeight;
    private TextView tvPrinter;

    private Button btAddAnotherEstimate;
    private EditText etExtraInput;
    private MaterialSearchView searchView;

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
        cbBuying = findViewById(R.id.cb_buying);
        llBuying = findViewById(R.id.ll_buying);
        etBuyingPrice = findViewById(R.id.et_buying_price);
        etGrossWeight = findViewById(R.id.et_gross_weight);
        etNetWeight = findViewById(R.id.et_net_weight);
        tvPrinter = findViewById(R.id.tv_printer);

        btAddAnotherEstimate = findViewById(R.id.bt_add_another_estimate);


        etExtraInput = findViewById(R.id.et_extra_input);
        searchView = findViewById(R.id.search_view);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_estimate);

        mContext = this;
        buyingItem = false;


        initVAs();
        initDatabase();
        initView();
        initRadioButtons();
        initDrawer();
        initToolbar();
        initGoldRecycler("gold");
        initSilverRecycler("silver");
        initSharedPreference();

        setupListeners();

        //TODO  change all adapters to searchAdapter
        initSearchListener();
    }

    //TODO implementation of search view
    private void initSearchListener() {
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                initSearchRecycler(query);
                //Do some magic
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                initSearchRecycler(newText);
                //Do some magic
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
//                Toast.makeText(mContext, "showing", Toast.LENGTH_SHORT).show();
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
//                Toast.makeText(mContext, "closed", Toast.LENGTH_SHORT).show();
                switch (product) {
                    case "gold":
                        goldRecyclerView.invalidate();
                        goldRecyclerView.setAdapter(goldAdapter);
                        break;
                    case "silver":
                        silverRecyclerView.invalidate();
                        silverRecyclerView.setAdapter(silverAdapter);
                        break;
                }
                searchAdapter.stopListening();
                //Do some magic
            }
        });
    }

    private void initSearchRecycler(String query) {
        Log.d(TAG, "initSearchRecycler: ");
        Query searchQuery = databaseReference.child(product).orderByChild("productName").startAt(query).endAt(query + "\uf8ff");
        Log.d(TAG, "initSearchRecycler: " + searchQuery.getRef());

        FirebaseRecyclerOptions<Product> productOptions =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(searchQuery, Product.class)
                        .build();
        searchAdapter = new FirebaseRecyclerAdapter<Product, ProductHolder>(productOptions) {
            @Override
            protected void onBindViewHolder(ProductHolder holder, int position, final Product model) {
                Log.d(TAG, "initSearchRecycler onBindViewHolder: ");
                holder.product.setText(model.getProductName());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //set the values for the VAs
                        initVAFromProduct(model);
                        //testing
                        testingTv.setText(model.toString());
                        tvChoiceClicked.setText(model.getProductName());
                        modelName = model.getProductName();
                        setupListeners();

                    }
                });
            }

            @Override
            public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                Log.d(TAG, "initSearchRecycler onCreateViewHolder: ");
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_items, parent, false);
                return new ProductHolder(view);
            }


        };

        searchAdapter.startListening();
        switch (product) {
            case "gold":
                goldRecyclerView.invalidate();
                goldRecyclerView.setAdapter(searchAdapter);
                break;
            case "silver":
                silverRecyclerView.invalidate();
                silverRecyclerView.setAdapter(searchAdapter);
                break;
        }


    }

    private void initDrawer() {

//if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("Estimate");
        SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName("Add Product");

//create the drawer and remember the `Drawer` result object
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .addDrawerItems(
                        item1,
                        new DividerDrawerItem(),
                        item2,
                        new SecondaryDrawerItem().withIdentifier(3).withName("Log"),
                        new SecondaryDrawerItem().withIdentifier(4).withName("Product List")
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        switch (position) {
                            case 2:
                                startActivity(new Intent(mContext, AddProductActivity.class));
                                break;
                            case 3:
                                Intent intent = new Intent(mContext, LogActivity.class);
                                intent.putExtra("printerExtra", selected_printer);
                                intent.putExtra("productExtra", product);
                                intent.putExtra("ipExtra", ipAddressM30);
                                startActivity(intent);
                                break;
                            case 4:
                                startActivity(new Intent(mContext, ViewProducts.class));
                        }
                        return true;
                    }
                })
                .build();
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.openDrawer();
            }
        });

    }

    //Listeners to know whether the program gram has been inputed
    private void setupListeners() {

        //get the product gram input and set the default VAs respective to the product gram
        RxTextView.textChanges(etProductGram)
                .subscribe(new Consumer<CharSequence>() {
                    @Override
                    public void accept(CharSequence charSequence) throws Exception {
                        if (charSequence.length() > 0) {
                            showVAs();
                            if (etGramRate.getText() != null) {
                                double productGram = Double.parseDouble(charSequence.toString());
                                estimateProductGram = productGram;
                                setDefaultVA(productGram);
                                etExtraInput.setVisibility(View.VISIBLE);
                                btEstimate.setVisibility(View.VISIBLE);
                            } else {
                                etExtraInput.setVisibility(View.GONE);
                                btEstimate.setVisibility(View.GONE);

                                unshowVAs();
                            }
                        } else {
                            etExtraInput.setVisibility(View.GONE);
                            btEstimate.setVisibility(View.GONE);

                            unshowVAs();
                        }
                        viewLog();

                    }
                });

        //get the checkbox buying and set the buying boolean the buying boolean to true
        //to check whether there is any buying input
        cbBuying.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    llBuying.setVisibility(View.VISIBLE);
                    buyingItem = true;

                } else {
                    llBuying.setVisibility(View.GONE);
                    buyingItem = false;
                }
                viewLog();
            }
        });


        //get the Hallmark or KDM String from the H_or_K radiogroup
        rgHOrK.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_hallmark:
                        hallmarkOrKDM = "Hallmark";
                        break;
                    case R.id.rb_kdm:
                        hallmarkOrKDM = "KDM";
                        break;
                }
                viewLog();
            }
        });

        RxTextView.textChanges(etVaPercentage)
                .subscribe(new Consumer<CharSequence>() {
                    @Override
                    public void accept(CharSequence charSequence) throws Exception {
                        if (charSequence.length() > 0) {
                            etVaNumber.setText("");
                            estimateVaPercent = Double.parseDouble(charSequence.toString());
                            estimateVaNumber = setVANumber(estimateVaPercent);
                            etVaNumber.setHint(String.valueOf(estimateVaNumber));
                        } else {

                            //not working
                            setDefaultVA(estimateProductGram);
//                            estimateVaNumber = Double.parseDouble(etVaNumber.getHint().toString());
//                            estimateVaPercent = Double.parseDouble(etVaPercentage.getHint().toString());
                        }

                        viewLog();
                    }
                });
        RxTextView.textChanges(etVaNumber)
                .subscribe(new Consumer<CharSequence>() {
                    @Override
                    public void accept(CharSequence charSequence) throws Exception {
                        if (charSequence.length() > 0) {
                            etVaPercentage.setText("");
                            estimateVaNumber = Double.parseDouble(charSequence.toString());
                            estimateVaPercent = setVAPercent(estimateVaNumber);
                            etVaPercentage.setHint(String.valueOf(estimateVaPercent));
                        } else {

                            setDefaultVA(estimateProductGram);
                        }
                        viewLog();
                    }
                });
        RxTextView.textChanges(etExtraInput)
                .subscribe(new Consumer<CharSequence>() {
                    @Override
                    public void accept(CharSequence charSequence) throws Exception {
                        if (charSequence.length() > 0) {
                            estimateExtraInput = Double.parseDouble(charSequence.toString());
                        } else {
                            estimateExtraInput = 0;
                        }
                        viewLog();
                    }
                });
    }

    private void setDefaultVA(double productGram) {
        if (productGram >= 7) {
            vaPercentShow(aboveSix, productGram);
        } else if (productGram < 7 && productGram >= 6) {
            vaPercentShow(six, productGram);
        } else if (productGram < 6 && productGram >= 5) {
            vaPercentShow(five, productGram);
        } else if (productGram < 5 && productGram >= 4) {
            vaPercentShow(four, productGram);
        } else if (productGram < 4 && productGram >= 3) {
            vaPercentShow(three, productGram);
        } else if (productGram < 3 && productGram >= 2) {
            vaPercentShow(two, productGram);
        } else if (productGram < 2 && productGram >= 1) {
            vaPercentShow(one, productGram);
        } else {
            vaPercentShow(belowOne, productGram);
        }
    }


    //TODO already changed the below code for settings the va number for gram value

    private double setVAPercent(double vaNumber) {
        if (etProductGram.getText() != null) {
            return round((vaNumber / (estimateProductGram)) * 100, 2);
        } else {
            return 0;
        }
    }

    private double setVANumber(double vaPercent) {
        if (etProductGram.getText() != null) {
            return round((vaPercent / 100) * (estimateProductGram), 2);
        } else {
            return 0;
        }
    }

    private void viewLog() {
        Log.d(TAG, "viewLog: buying Item " + buyingItem + "\nvaPercent " + estimateVaPercent + "\nvaNumber " + estimateVaNumber
                + "\nhallmarkOrKDM " + hallmarkOrKDM
                + "\nproductGram " + estimateProductGram
                + "\nExtra Input " + estimateExtraInput);
    }

    //TODO made changes here
    private void vaPercentShow(double input, double gramInput) {
        etVaPercentage.setHint(String.valueOf(input));
        double vaNumberDouble = (input / 100) * gramInput;
        etVaNumber.setHint(String.valueOf(round(vaNumberDouble, 2)));
        estimateVaPercent = Double.valueOf(etVaPercentage.getHint().toString());
        estimateVaNumber = Double.valueOf(etVaNumber.getHint().toString());
        viewLog();
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

        //retrieve gold/silver
        String choice = sharedPreferences.getString("materialPref", "1");
        material = choice;

        ipAddressM30 = sharedPreferences.getString("ipPref", "");
        //retrieve printer
        printer = sharedPreferences.getString("printerPref", "1");
        selected_printer = Integer.parseInt(printer);
        switch (selected_printer) {
            case 1:
                PRINTER = "TCP:" + ipAddressM30;
                tvPrinter.setText("Epson m30 " + PRINTER);
                break;
            case 2:
                PRINTER = "BT:00:01:90:C2:AE:35";
                tvPrinter.setText("Epson p20 " + PRINTER);
                break;
        }
        //retrieve gramrate
        gramRate = Double.parseDouble(sharedPreferences.getString("gramRatePref", "0"));
        etGramRate.setText(String.valueOf(gramRate));
        etGramRate.setEnabled(false);

        //retrieve sgst and cgst
        sgst = Double.parseDouble(sharedPreferences.getString("sgstRatePref", "0"));
        cgst = Double.parseDouble(sharedPreferences.getString("cgstRatePref", "0"));

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
                        etExtraInput.setHint("MC");
                        rgHOrK.setVisibility(View.VISIBLE);
                        toolbar.setBackgroundColor(Color.parseColor("#D4AF37"));
                        break;
                    case R.id.rb_silver:
                        product = "silver";
                        Log.d(TAG, "onCheckedChanged: " + product);
                        rgHOrK.clearCheck();
                        etExtraInput.setHint("MC/g");

                        rgHOrK.setVisibility(View.INVISIBLE);
                        toolbar.setBackgroundColor(Color.parseColor("#C0C0C0"));
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

    public String ediTextToString(EditText editText) {
        if (editText.getText() != null) {
            return editText.getText().toString();
        } else {
            return "";
        }
    }

    public double editTextToDouble(EditText editText) {
        if (editText.getText() != null) {
            return Double.parseDouble(editText.getText().toString());
        } else {
            return 0;
        }
    }

    //onClickAddAnother
    public void onClickAddAnotherEstimate(View view) {

        String product_estimate = product;
        retrieveDataFromFirebase(product_estimate);


        //TODOcompleted implement the Add Another Button Click
        //retrieve all datas from the preference.xml file
        //check if all inputs are available
        createEstimate(
                material,
                printer,
                modelName,
                hallmarkOrKDM,
                gramRate,
                estimateProductGram,
                estimateVaPercent,
                estimateVaNumber,
                estimateExtraInput,
                sgst,
                cgst,
                buyingItem
        );

        resetViews();
    }

    private void resetViews() {
        etProductGram.setText("");
        rgHOrK.clearCheck();
        cbBuying.setChecked(false);
        etVaNumber.setText("");
        etVaPercentage.setText("");
        etExtraInput.setText("");
        etProductGram.requestFocus();
        etBuyingPrice.setText("");
        etGrossWeight.setText("");
        etNetWeight.setText("");

    }

    public void createBuyingEstimate(View view) {
        StringBuilder stringBuilder = new StringBuilder();
        CreateEstimate createEstimate = new CreateEstimate();
        createEstimate.buyingItem = true;
        createEstimate.estimateBuyingPrice = editTextToDouble(etBuyingPrice);
        createEstimate.estimateBuyingGrossWeight = editTextToDouble(etGrossWeight);
        createEstimate.estimateBuyingNetWeight = editTextToDouble(etNetWeight);
        initiatedBuyingTemplate(stringBuilder);
        insertBuyingProduct(createEstimate, stringBuilder);
        double total = 0;
        total += buyingCalculation(createEstimate);
        stringBuilder.append(total + "_" + total);
        Log.d(TAG, "createBuyingEstimate: " + stringBuilder.toString());

        showDialog(stringBuilder.toString());
//        runPrintReceiptSequence(stringBuilder.toString());
    }

    //On Estimate Button Click
    public void onClickEstimate(View view) {
        onClickAddAnotherEstimate(view);
        final StringBuilder stringBuilder = initiatedEstimateTemplate();

        for (int i = 0; i < mCreateEstimateList.size(); i++) {
            Log.d(TAG, "Estimate List: " + mCreateEstimateList.get(i).toString());
            insertHallmarkOrKDM(mCreateEstimateList.get(i), stringBuilder);
            insertSellingProducts(mCreateEstimateList.get(i), stringBuilder);
        }

//        insertGSTValues(mCreateEstimateList, stringBuilder);
        insertTotal(mCreateEstimateList, stringBuilder);

        Log.d(TAG, "onClickEstimate: \n" + stringBuilder.toString());


        //show Material Dialog
        showDialog(stringBuilder.toString());

//        runPrintReceiptSequence(stringBuilder.toString());


    }


    public void showDialog(final String string) {
        new MaterialStyledDialog.Builder(this)
                .setStyle(Style.HEADER_WITH_TITLE)
                .setTitle("\u20B9 " + string.split("_")[1])
                .setDescription(string.split("_")[0])
                .withDialogAnimation(true)
                .setPositiveText("Print")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        runPrintReceiptSequence(string);
                        mCreateEstimateList.clear();
                        resetViews();
                    }
                })
                .setScrollable(true, 20)
                .setNegativeText("Clear")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        clearData();
                    }
                })
                .setCancelable(true)
                .show();
    }

    private void createEstimate(String material, String printer, String modelName, String hallmarkOrKDM, double gramRate, double estimateProductGram, double estimateVaPercent, double estimateVaNumber, double estimateExtraInput, double sgst, double cgst, boolean buyingItem) {

        CreateEstimate createEstimate = new CreateEstimate(material,
                printer,
                modelName,
                hallmarkOrKDM,
                gramRate,
                estimateProductGram,
                estimateVaPercent,
                estimateVaNumber,
                estimateExtraInput,
                sgst,
                cgst,
                buyingItem, 0, 0, 0);


        //if buying is true
        if (createEstimate.buyingItem) {
            createEstimate.estimateBuyingPrice = editTextToDouble(etBuyingPrice);
            createEstimate.estimateBuyingGrossWeight = editTextToDouble(etGrossWeight);
            createEstimate.estimateBuyingNetWeight = editTextToDouble(etNetWeight);
        }
        Log.d(TAG, "createEstimate: " + createEstimate.toString());
        mCreateEstimateList.add(createEstimate);
        Log.d(TAG, "createEstimate: " + mCreateEstimateList.size());
    }

    private StringBuilder initiatedEstimateTemplate() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("%-22s", String.valueOf("Gram Rate")) + " " + String.format("%15s", gramRate + "/Gms\n"));
        stringBuilder.append("------------------------------------------\n");
        stringBuilder.append(String.format("%-7s", "Desc") + String.format("%-7s", "Wt") + String.format("%-7s", "VA") + String.format("%-7s", "MC") + String.format("%12s", "Total") + "\n");
        stringBuilder.append("------------------------------------------\n");
        return stringBuilder;
    }

    private void initiatedBuyingTemplate(StringBuilder stringBuilder) {
        stringBuilder.append("\n------------------------------------------");
        stringBuilder.append("\n--------------BUYING ITEM-----------------");
        stringBuilder.append("\n------------------------------------------\n");
        stringBuilder.append(String.format("%-7s", "Desc") + String.format("%-7s", "G.Wt") + String.format("%-7s", "N.Wt") + String.format("%12s", "Total") + "\n");
        stringBuilder.append("------------------------------------------\n");
    }

    private void insertTotal(List<CreateEstimate> createEstimate, StringBuilder stringBuilder) {
        double total = 0;
        for (int i = 0; i < createEstimate.size(); i++) {
            CreateEstimate estimateProduct = createEstimate.get(i);
            total += silverSellingCalculation(estimateProduct);
            double buyingTotal = 0;
            if (estimateProduct.buyingItem) {
                for (int j = 0; j < 1; j++) {
                    initiatedBuyingTemplate(stringBuilder);
                }
                insertBuyingProduct(estimateProduct, stringBuilder);

            }
            total = total - buyingTotal;
        }
        total = round(total, 2);
        stringBuilder.append(String.format("%-22s", String.valueOf("Total")) + "-" + String.format("%15s", total) + "\n");
        stringBuilder.append("\n(Inclusive of GST)");
        stringBuilder.append("_" + String.valueOf(total));
    }


    private void insertHallmarkOrKDM(CreateEstimate createEstimate, StringBuilder stringBuilder) {
        if (createEstimate.hallmarkOrKDM != "") {
            stringBuilder.append(createEstimate.hallmarkOrKDM + "\n");
        }
    }


    private void insertSellingProducts(CreateEstimate createEstimate, StringBuilder stringBuilder) {
        stringBuilder.append(String.format("%-7s", createEstimate.modelName) +
                String.format("%-7s", createEstimate.estimateProductGram) +
                String.format("%-7s", createEstimate.estimateVaPercent + "%") +
                String.format("%-7s", createEstimate.extraInput));
//        TODO bookmark
        Log.d(TAG, "insertSellingProducts: product "+product);
        switch (product) {
            case "silver":
                stringBuilder.append(String.format("%12s", silverSellingCalculation(createEstimate)) + "\n");
                break;
            case "gold":
                stringBuilder.append(String.format("%12s", goldSellingCalculation(createEstimate)) + "\n");
                break;
        }

        stringBuilder.append("\n");
    }


    private double silverSellingCalculation(CreateEstimate createEstimate) {
        double gramTimesWeight = createEstimate.estimateProductGram * createEstimate.gramRate;
        double vaPercentInput = gramTimesWeight * (createEstimate.estimateVaPercent / 100);
        double extraInput = createEstimate.estimateProductGram * createEstimate.extraInput;
        Log.d(TAG, "silverSellingCalculation: productGram"+createEstimate.estimateProductGram);
        Log.d(TAG, "silverSellingCalculation: extraInput"+createEstimate.extraInput);
        double total = (gramTimesWeight + vaPercentInput + extraInput);
        Log.d(TAG, "silverSellingCalculation: total "+total);
        double cgstInput = total * (createEstimate.cgst / 100);
        double sgstInput = total * (createEstimate.sgst / 100);
        double return_total = total + cgstInput + sgstInput;
        return round(return_total, 2);
    }

    private double goldSellingCalculation(CreateEstimate createEstimate) {
        double gramTimesWeight = createEstimate.estimateProductGram * createEstimate.gramRate;
        double vaPercentInput = gramTimesWeight * (createEstimate.estimateVaPercent / 100);
        double extraInput = createEstimate.extraInput;

        Log.d(TAG, "goldSellingCalculation: productGram"+createEstimate.estimateProductGram);
        Log.d(TAG, "goldSellingCalculation: extraInput"+createEstimate.extraInput);
        double total = (gramTimesWeight + vaPercentInput + extraInput);
        Log.d(TAG, "goldSellingCalculation: total "+total);
        double cgstInput = total * (createEstimate.cgst / 100);
        double sgstInput = total * (createEstimate.sgst / 100);
        double return_total = total + cgstInput + sgstInput;
        return round(return_total, 2);
    }
    private void insertBuyingProduct(CreateEstimate createEstimate, StringBuilder stringBuilder) {
        stringBuilder.append(String.format("%-7s", createEstimate.estimateBuyingPrice) +
                String.format("%-7s", createEstimate.estimateBuyingGrossWeight) +
                String.format("%-7s", createEstimate.estimateBuyingNetWeight) +
                String.format("%12s", buyingCalculation(createEstimate)) + "\n");
        stringBuilder.append("\n");
    }


    private double buyingCalculation(CreateEstimate createEstimate) {
        double buyingPrice = createEstimate.estimateBuyingPrice;
        double grossWeight = createEstimate.estimateBuyingGrossWeight;
        double netWeight = createEstimate.estimateBuyingNetWeight;
        return round(buyingPrice * netWeight, 2);

    }


    //onOptionsmenuCreated
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.menu_search);
        searchView.setMenuItem(item);
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
        Log.d(TAG, "initGoldRecycler: " + goldQuery.toString());
        goldRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//        goldQuery.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.d(TAG, "onDataChange: " + dataSnapshot.getValue().toString());
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

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
                        modelName = model.getProductName();
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
//        silverQuery.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.d(TAG, "onDataChange: " + dataSnapshot.getValue().toString());
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
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
                        modelName = model.getProductName();
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
//                Log.d(TAG, valueAdded.toString());
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

    /*
    CAUTION: PROCEED WITH CARE
     */

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

    private boolean runPrintReceiptSequence(String printString) {
        dateStamp = new SimpleDateFormat("dd-MM-yy").format(new Date());
        timeStamp = new SimpleDateFormat("HH-mm-ss").format(new Date());
        EstimateLog estimateLog = new EstimateLog(timeStamp, printString);
        Log.d(TAG, "runPrintReceiptSequence: ");
        Log.d(TAG, "runPrintReceiptSequence: " + estimateLog.toString());
        databaseReference.child("Estimates").child(product).child(dateStamp).child(estimateLog.getTimeStamp()).setValue(estimateLog);
        Log.d(TAG, "runPrintReceiptSequence: ");
        try {

            if (!initializeObject()) {
                Log.d(TAG, "runPrintReceiptSequence: initalise");
                return false;
            }

            if (!createReceiptData(printString)) {
                Log.d(TAG, "runPrintReceiptSequence: create");
                finalizeObject();
                return false;
            }

            if (!printData()) {
                Log.d(TAG, "runPrintReceiptSequence: ");
                finalizeObject();
                return false;
            }
        } catch (Exception e) {
            Toast.makeText(mContext, "Print Failed, Try Again", Toast.LENGTH_SHORT).show();
        }
        return true;

    }

    private boolean initializeObject() {
        Log.d(TAG, "initializeObject: ");
        try {

            mPrinter = new Printer(selected_printer,
                    Printer.MODEL_ANK,
                    this);
            Log.d(TAG, "initializeObject: inside try");
        } catch (Exception e) {
            Log.d(TAG, "initializeObject: Exception");
            ShowMsg.showException(e, "Printer", mContext);
            return false;
        }

        mPrinter.setReceiveEventListener((ReceiveListener) mContext);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    private boolean createReceiptData(String printString) {
        Log.d(TAG, "createReceiptData: ");

        if (mPrinter == null) {
            return false;
        }

        try {
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            String dateStamp = new SimpleDateFormat("dd/MM/yy").format(new Date());
            String timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
            mPrinter.addText("Date: " + dateStamp);
            mPrinter.addFeedLine(1);
            mPrinter.addTextSize(2, 2);
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addFeedLine(1);
            mPrinter.addText("ESTIMATE");
            mPrinter.addFeedLine(2);
            mPrinter.addTextSize(1, 1);
            mPrinter.addText(printString.split("_")[0]);
            mPrinter.addText("------------------------------------------\n");
            mPrinter.addFeedLine(1);
            mPrinter.addTextSize(2, 2);
            mPrinter.addText("Rs " + printString.split("_")[1]);
            mPrinter.addFeedLine(3);
            mPrinter.addTextSize(1, 1);
            mPrinter.addText("------------------------------------------\n");
            mPrinter.addText("Thank You\n");
            mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
            mPrinter.addFeedLine(4);
            mPrinter.addText("Time " + timeStamp + "\n");
            mPrinter.addCut(Printer.CUT_FEED);
            FirebaseDatabase firebase = FirebaseDatabase.getInstance();
            String dateStampChild = new SimpleDateFormat("dd").format(new Date());
            firebase.getReference("estimates").child(dateStampChild).child(timeStamp).setValue(printString.split("_")[1]);
        } catch (Exception e) {
            ShowMsg.showException(e, "", mContext);
            return false;
        }

        return true;
    }

    private boolean isPrintable(PrinterStatusInfo status) {
        if (status == null) {
            return false;
        }

        if (status.getConnection() == Printer.FALSE) {
            return false;
        } else if (status.getOnline() == Printer.FALSE) {
            return false;
        } else {
            //print available
        }

        return true;
    }

    private boolean printData() {
        if (mPrinter == null) {
            return false;
        }

        if (!connectPrinter()) {
            return false;
        }

        PrinterStatusInfo status = mPrinter.getStatus();


        if (!isPrintable(status)) {
            try {
                mPrinter.disconnect();
            } catch (Exception ex) {
                // Do nothing
            }
            return false;
        }

        try {
            mPrinter.sendData(Printer.PARAM_DEFAULT);
        } catch (Exception e) {
            ShowMsg.showException(e, "sendData", mContext);
            try {
                mPrinter.disconnect();
            } catch (Exception ex) {
                // Do nothing
            }
            return false;
        }

        return true;
    }

    private boolean connectPrinter() {
        boolean isBeginTransaction = false;

        if (mPrinter == null) {
            return false;
        }

        try {
            mPrinter.connect(PRINTER, Printer.PARAM_DEFAULT);
        } catch (Exception e) {
            ShowMsg.showException(e, "connect", mContext);
            return false;
        }

        try {
            mPrinter.beginTransaction();
            isBeginTransaction = true;
        } catch (Exception e) {
            ShowMsg.showException(e, "beginTransaction", mContext);
        }

        if (isBeginTransaction == false) {
            try {
                mPrinter.disconnect();
            } catch (Epos2Exception e) {
                // Do nothing
                return false;
            }
        }

        return true;
    }

    private void finalizeObject() {
        Log.d(TAG, "finalizeObject: ");
        if (mPrinter == null) {
            return;
        }

        mPrinter.clearCommandBuffer();

        mPrinter.setReceiveEventListener(null);

        mPrinter = null;
    }

    //
    @Override
    public void onPtrReceive(final Printer printerObj, final int code, final PrinterStatusInfo status, final String printJobId) {
        runOnUiThread(new Runnable() {
            @Override
            public synchronized void run() {
                ShowMsg.showResult(code, makeErrorMessage(status), mContext);


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        disconnectPrinter();
                    }
                }).start();
            }
        });
    }

    private void disconnectPrinter() {
        if (mPrinter == null) {
            return;
        }

        try {
            mPrinter.endTransaction();
        } catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    ShowMsg.showException(e, "endTransaction", mContext);
                }
            });
        }

        try {
            mPrinter.disconnect();
        } catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    ShowMsg.showException(e, "disconnect", mContext);
                }
            });
        }

        finalizeObject();
    }

    private String makeErrorMessage(PrinterStatusInfo status) {
        String msg = "";

        if (status.getOnline() == Printer.FALSE) {
            msg += getString(R.string.handlingmsg_err_offline);
        }
        if (status.getConnection() == Printer.FALSE) {
            msg += getString(R.string.handlingmsg_err_no_response);
        }
        if (status.getCoverOpen() == Printer.TRUE) {
            msg += getString(R.string.handlingmsg_err_cover_open);
        }
        if (status.getPaper() == Printer.PAPER_EMPTY) {
            msg += getString(R.string.handlingmsg_err_receipt_end);
        }
        if (status.getPaperFeed() == Printer.TRUE || status.getPanelSwitch() == Printer.SWITCH_ON) {
            msg += getString(R.string.handlingmsg_err_paper_feed);
        }
        if (status.getErrorStatus() == Printer.MECHANICAL_ERR || status.getErrorStatus() == Printer.AUTOCUTTER_ERR) {
            msg += getString(R.string.handlingmsg_err_autocutter);
            msg += getString(R.string.handlingmsg_err_need_recover);
        }
        if (status.getErrorStatus() == Printer.UNRECOVER_ERR) {
            msg += getString(R.string.handlingmsg_err_unrecover);
        }
        if (status.getErrorStatus() == Printer.AUTORECOVER_ERR) {
            if (status.getAutoRecoverError() == Printer.HEAD_OVERHEAT) {
                msg += getString(R.string.handlingmsg_err_overheat);
                msg += getString(R.string.handlingmsg_err_head);
            }
            if (status.getAutoRecoverError() == Printer.MOTOR_OVERHEAT) {
                msg += getString(R.string.handlingmsg_err_overheat);
                msg += getString(R.string.handlingmsg_err_motor);
            }
            if (status.getAutoRecoverError() == Printer.BATTERY_OVERHEAT) {
                msg += getString(R.string.handlingmsg_err_overheat);
                msg += getString(R.string.handlingmsg_err_battery);
            }
            if (status.getAutoRecoverError() == Printer.WRONG_PAPER) {
                msg += getString(R.string.handlingmsg_err_wrong_paper);
            }
        }
        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_0) {
            msg += getString(R.string.handlingmsg_err_battery_real_end);
        }

        return msg;
    }

    public void testPrinter(View view) {
        runPrintReceiptSequence("Testing0_20");

    }


    public void clearData() {
        if (mCreateEstimateList.size() > 0) {
            mCreateEstimateList.clear();
            Toast.makeText(mContext, "Data Cleared", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "No Data to Clear", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickClearEstimate(View view) {
        clearData();

    }
}
