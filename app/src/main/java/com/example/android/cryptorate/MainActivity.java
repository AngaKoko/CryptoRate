package com.example.android.cryptorate;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.cryptorate.utilities.ConverterAdapter;
import com.example.android.cryptorate.utilities.RatesLoader;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Rate>>, ConverterAdapter.ConverterAdapterOnClickHandler{

    //Url to query Cryptocompare database
    private static final String CRYPTOCOMPARE_URL
            = "https://min-api.cryptocompare.com/data/price?fsym=BTC" +
            "&tsyms=ETH,USD,EUR,NGN,GBP,CNY,ILS,INR,ITL,KWD,MYR,MXN,MMK,ANG,KPW,RUB,ZAR,CHF,KRW,TRY, AED";

    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    public static final int RATES_LOADER_ID = 0;

    //Object of SwipeRefreshLayout for the SwipeRefreshLayout
    SwipeRefreshLayout mSwipeRefreshLayout;

    //Object of RecycleView for the recycle view
    RecyclerView mRecycleView;

    //Object of TextView for the empty state text view
    TextView mEmptyStatTextView;

    //Object of ProgressBar
    ProgressBar mProgressBar;

    //Instance of the ConverterAdapter class
    ConverterAdapter mConverterAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Reference to the SwipeRefreshLayout from our Layout
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

        //Reference to the RecycleView from our Layout
        mRecycleView = (RecyclerView) findViewById(R.id.rates_recycle_view);
        //Creating a Layout for our RecycleView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //Assigning the layout to our RecycleView
        mRecycleView.setLayoutManager(linearLayoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecycleView.setHasFixedSize(true);

        //Reference to the error_msg_text_view from our layout
        mEmptyStatTextView = (TextView) findViewById(R.id.error_msg_tex_view);

        //Reference to the progressBar from our Layout
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */

        //Instance of a new ConverterAdapter
        mConverterAdapter = new ConverterAdapter(this);

        //Setting the ConverterAdapter to our RecycleView
        mRecycleView.setAdapter(mConverterAdapter);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            android.app.LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(RATES_LOADER_ID, null, this);
        }else{
            // Otherwise, display error
            // Update empty state with no connection error message
            mEmptyStatTextView.setText(R.string.no_internet_connection);

            //Call  displayError method to hid progress bar and display error
            displayError();
        }

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */

        //Listener for the SwipeRefreshLayout
        //Refreshes the list and updates the rates if there is an update
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){
                //Refresh items
                refreshItems();
            }
        });
    }

    void displayError(){
        //Hide progress bar
        mProgressBar.setVisibility(View.GONE);
        //Hide RecycleView
        mRecycleView.setVisibility(View.GONE);
        //Make Error Message Text View visible
        mEmptyStatTextView.setVisibility(View.VISIBLE);
    }

    void displayData(){
        //Make RecycleView visible
        mRecycleView.setVisibility(View.VISIBLE);
        //Hide ProgressBar
        mProgressBar.setVisibility(View.GONE);
        //Hide ErrorMessage View
        mEmptyStatTextView.setVisibility(View.GONE);
    }

    void refreshItems(){
        //Restart Loader
        getLoaderManager().restartLoader(RATES_LOADER_ID, null, MainActivity.this);

        //Load Complete
        onItemLoadComplete();
    }

    void onItemLoadComplete(){

        //Stop refreshing animation
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public Loader<ArrayList<Rate>> onCreateLoader(int id, Bundle args) {
        //Show the progress bar
        mProgressBar.setVisibility(View.VISIBLE);

        //Hide the error message
        mEmptyStatTextView.setVisibility(View.GONE);

        return new RatesLoader(this, CRYPTOCOMPARE_URL);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Rate>> loader, ArrayList<Rate> data) {

        //If data is not empty display data
        //Else Show error message
        if(data != null &&  !data.isEmpty()){
            mConverterAdapter.setRatesDate(data);
            displayData();
        }else{
            mConverterAdapter.setRatesDate(null);
            mEmptyStatTextView.setText(R.string.no_data_to_show);
            displayError();
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Rate>> loader) {

        //Clear Data in the Adapter
        mConverterAdapter.setRatesDate(null);
    }

    @Override
    public void onClick(int position) {

        //Find the current Item that was clicked on
        Rate currentRate = mConverterAdapter.getItem(position);

        //Create a new Intent for the ConverterActivity class
        Intent intent = new Intent(MainActivity.this, ConverterActivity.class);

        /**
         * put details of currency to be received by the intent
         */
        intent.putExtra("CURRENCY_RATE", currentRate.getExchangeRate());
        intent.putExtra("CURRENCY", currentRate.getCurrency());
        intent.putExtra("CURRENCY_SHORT_FORM", currentRate.getCurrencyShortForm());
        intent.putExtra("BTC_VALUE", currentRate.getBitcoinRate());

        //Launch the activity
        startActivity(intent);
    }
}
