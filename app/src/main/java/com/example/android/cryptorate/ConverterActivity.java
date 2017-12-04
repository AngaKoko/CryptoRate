package com.example.android.cryptorate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ConverterActivity extends AppCompatActivity {

    /**
     * Static variable to receive values from MainActivity
     */
    private static double mBTCValue;
    private static double mExchangeRate;
    private static String mCurrency;
    private static String mCurrencyShortFrom;

    EditText btcEditText;

    EditText exchangeRateEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);

        /**
         * Find Reference to the views in the layout
         * Set the text for the BTC EditText
         */
        final TextView currencyTextView = (TextView) findViewById(R.id.other_currency_text_view);
        TextView currencySymbol = (TextView) findViewById(R.id.currency_symbol);
        btcEditText = (EditText) findViewById(R.id.btc_edit_text);
        exchangeRateEditText = (EditText) findViewById(R.id.currency_edit_text);

        /**
         * Create an Instance of Intent and Bundle class to check if Extras was put from MainActivity class
         * and if Bundle was able to get Extras, get the values and parse them into declared variables
         */
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            /**
             * Get the values from the MainActivity and parse them into declared variables
             */
            mBTCValue = (double) bundle.get("BTC_VALUE");
            mExchangeRate = (double) bundle.get("CURRENCY_RATE");
            mCurrency = (String) bundle.get("CURRENCY");
            mCurrencyShortFrom = (String) bundle.get("CURRENCY_SHORT_FORM");

            /**
             * Set the text of view with values gotten from the main activity
             */
            currencyTextView.setText(mExchangeRate+" "+mCurrency);
            currencySymbol.setText(mCurrencyShortFrom);
            exchangeRateEditText.setText(String.valueOf(mExchangeRate));
            btcEditText.setText(String.valueOf(mBTCValue));
        }

        /**
         * Use a TextWatcher Interface to check Listen for a change in text
         * when the btcEditText is pressed
         */
        btcEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String btcValue = btcEditText.getText().toString().trim();

                //Check it the EditText is empty
                if (btcValue.isEmpty() || btcValue.equals("")) {
                    //make exchangeRateEditText empty if btcEditText is empty
                    exchangeRateEditText.setText("");
                }else {
                    double i = -1;
                    /**
                     * Try and catch block for NumberFormatException
                     * Prevents crash
                     */
                    try {
                        i = Double.parseDouble(btcValue);

                        //Calculate the value in selected currency
                        //and set exchangeRateEditText to calculated value
                        mBTCValue = i;
                        exchangeRateEditText.setText(String.format("%.2f", calculateCurrency(mBTCValue, mExchangeRate)));
                    }catch(NumberFormatException e){}
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                //Nothing to do here
            }
            @Override
            public void afterTextChanged(Editable s) {
                // Nothing to do here

            }
        });

        exchangeRateEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String currencyValue = exchangeRateEditText.getText().toString().trim();

                    //Check it the EditText is empty
                    if(currencyValue.isEmpty()||currencyValue.equals("")){
                        //make btcEditText empty if btcEditText is empty
                        btcEditText.setText("");
                    }else {
                        double i = -1;
                        /**
                         * Try and catch block for NumberFormatException
                         * Prevents crash
                         */
                        try {
                            i = Double.parseDouble(currencyValue);
                            //Calculate the value on selected currency in BTC
                            //set btcEditText to calculated value
                            btcEditText.setText(String.valueOf(calculateBTC(i, mExchangeRate)));
                        }catch(NumberFormatException e){}
                    }

                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Calculates the equivalent of a bitcoin in selected currency using it's exchange rate
     * @param btcValue the value of bitcoin to be converted
     * @param exchangeRate the exchange rate of selected currency to bitcoin
     * @return the value of selected currency to bitcoin
     */
    private double calculateCurrency(double btcValue, double exchangeRate){
        return btcValue*exchangeRate;
    }

    /**
     * Gives the converted value of selected currency in bitcoin
     * @param currencyValue the value of selected currency to be converted to bitcoin
     * @param exchangeRate the exchange rate of selected currency to bitcoin
     * @return the value of bitcoin to selected currency
     */
    private double calculateBTC(double currencyValue, double exchangeRate){
        return currencyValue/exchangeRate;
    }
}
