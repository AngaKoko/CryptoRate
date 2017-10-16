package com.example.android.cryptorate;

/**
 * Created by ANGA KOKO on 10/12/2017.
 */

public class Rate {

    private double mBitcoinRate;
    private double mExchangeRate;
    private String mCurrency;
    private String mCurrencyShortForm;

    public Rate(double bitcoinRate, double exchangeRate, String currency, String currencyShortForm){
        mBitcoinRate = bitcoinRate;
        mExchangeRate = exchangeRate;
        mCurrency = currency;
        mCurrencyShortForm = currencyShortForm;
    }

    public double getBitcoinRate(){return  mBitcoinRate;}

    public double getExchangeRate(){return mExchangeRate;}

    public String getCurrency(){return mCurrency;}

    public String getCurrencyShortForm(){return mCurrencyShortForm;}
}
