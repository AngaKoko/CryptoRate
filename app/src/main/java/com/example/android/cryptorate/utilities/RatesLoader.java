
package com.example.android.cryptorate.utilities;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.cryptorate.Rate;

import java.util.ArrayList;

/**
 * Created by ANGA KOKO on 10/14/2017.
 */

public class RatesLoader extends AsyncTaskLoader<ArrayList<Rate>> {

    String mUrl;
    public RatesLoader(Context context, String url){
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<Rate> loadInBackground() {
        if(mUrl != null || !mUrl.isEmpty()){
            ArrayList<Rate> rates = QueryUtils.fetchCryptoCompareData(mUrl);
            return rates;
        }
        return null;
    }
}
