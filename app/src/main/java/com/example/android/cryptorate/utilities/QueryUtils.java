package com.example.android.cryptorate.utilities;

import android.util.Log;

import com.example.android.cryptorate.Rate;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by ANGA KOKO on 10/13/2017.
 */

public class QueryUtils {

    /** Tag for the log messages */
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    //List of Currencies to get their rates in Bitcoin
    private  static final String [] CURRENCIES_SHORT_FORM = {"ETH","USD","EUR","NGN","GBP","CNY","ILS","INR","ITL","KWD",
            "MYR","MXN","MMK","ANG","KPW","RUB","ZAR","CHF","KRW","TRY", "AED"};

    private static final String [] CURRENCIES = {"Ethereum","US Dollars", "Euro", "Nigerian Naira", "British Pound", "Chinese Yuan",
    "Israeli New Shekel", "Indian Rupee", "Italian Lira", "Kuwaiti Dinar", "Malaysian Ringgit", "Mexican Peso", "Myanmar Kyat",
    "Netherlands Antillean Guilder", "North Korean Won", "Russian Ruble", "South African Rand", "Swiss Franc", "South Korean Won",
    "Turkish Lira", "United Arab Emirates Dirham"};

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the CryptoCompare dataset and return an {@link Rate} object to represent a single earthquake.
     */
    public static ArrayList<Rate> fetchCryptoCompareData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Create an empty ArrayList that we can start adding CrytoRates to
        ArrayList<Rate> rates = extractCryptoRates(jsonResponse);

        // Return the {@link Event}
        return rates;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public static ArrayList<Rate> extractCryptoRates(String cryptCompareJSON){
        //Return null if JSON is empty
        if(cryptCompareJSON.isEmpty() || cryptCompareJSON == null)return null;

        //An array where conversion rates will the added to
        ArrayList<Rate> rates = new ArrayList<>();

        Rate rate;

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Parse the response given by the SAMPLE_JSON_RESPONSE string and
            // build up a list of Earthquake objects with the corresponding data.
            JSONObject root = new JSONObject(cryptCompareJSON);

            for(int i = 0; i< CURRENCIES_SHORT_FORM.length; i++ ){
                if(root.has(CURRENCIES_SHORT_FORM[i])){
                    double value = root.getDouble(CURRENCIES_SHORT_FORM[i]);
                    rate = new Rate(1, value, CURRENCIES[i], CURRENCIES_SHORT_FORM[i]);
                    rates.add(rate);
                }
            }
            return rates;

        }catch (JSONException e){

        }
        return null;
    }
}
