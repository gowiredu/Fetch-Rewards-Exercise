package com.gordonowiredu.fetchrewardsexercise;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements MainActivityRecyclerViewAdapter.OnIDRecyclerViewClickListener {

    protected final String urlJSONList = "https://fetch-hiring.s3.amazonaws.com/hiring.json"; // URL for List.
    protected ArrayList<String> dialogDataArrayList;
    protected MainActivityRecyclerViewAdapter recyclerViewAdapter;
    protected ProgressDialog downloadProgressDialog; // loading spinner to be shown at startup (when data is downloading).
    protected JSONArray jsonArray;
    protected JSONArray sortedJsonArray;
    protected List<JSONObject> jsonValues;
    protected JSONArray listIDsJsonArray;
    protected ArrayList<Integer> listIDs;
    protected Pattern pattern = Pattern.compile("(\\D*)(\\d*)");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialogDataArrayList = new ArrayList<>();
        downloadProgressDialog = new ProgressDialog(MainActivity.this);


        // Check if there is an internet connection available.
        if (isNetworkAvailable())
        {
            // AsyncTask for pulling JSON List (to avoid running on the Main thread and avoid "Application not Responding").
            new JsonListTask().execute();
            Log.i("NETWORK", "Internet connection available");
        }

        else
        {
            noInternetConnectionDialog(); // dialog that pops up when there is no internet connection. Gives option to retry or quit.
            dismissInitLoadingSpinner();
            Log.i("NETWORK", "No internet connection available");
        }
    }


    /**
     * Dialog box that pops up when there is no internet connection. Gives user the option of retrying for the JSON data or exiting the application.
     */
    protected void noInternetConnectionDialog()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);


        alertDialogBuilder.setMessage("No internet connection available. Try again?");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Reload",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        new JsonListTask().execute();
                    }
                });

        alertDialogBuilder.setNegativeButton("Exit",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }



    /**
     * Shows indeterminable loading dialog box while JSON data is being pulled.
     */
    protected void initLoadingSpinner()
    {
        downloadProgressDialog.setTitle("Downloading");
        downloadProgressDialog.setMessage("Please wait...");
        downloadProgressDialog.setCancelable(false);
        downloadProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        downloadProgressDialog.show();
    }


    /**
     * Dismisses indeterminable loading dialog after JSON data has been pulled.
     */
    protected void dismissInitLoadingSpinner()
    {
        if (downloadProgressDialog.isShowing())
        {
            downloadProgressDialog.dismiss();
        }
    }


    /**
     * OnClickListener that is triggered when the user taps on a list item.
     * @param position the position on the list the user touches.
     */
    @Override
    public void onIDClick(int position) {
        groupByListID(position);

        // start a new Activity and carry grouped JSON Array data along.
        Intent intent = new Intent(getApplicationContext(), ListActivity.class);
        intent.putExtra("jsonArray", listIDsJsonArray.toString());
        startActivity(intent);

    }


    /**
     * Pulls data from the provided JSON link.
     * @return String of the pulled data.
     */
    private String pullJSONData() {

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            // establish a connection to the provided URL.
            URL url = new URL(urlJSONList);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            // StringBuilder to get response/results.
            StringBuilder stringBuilder = new StringBuilder();
            String line = "";

            while ((line = reader.readLine()) != null) {

                stringBuilder.append(line);
                Log.d("Response: ",  "line");
            }

            return stringBuilder.toString();


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;

    }

    /**
     * Removes entries with null or "" in the "name" field of the JSON Objects
     * @param result - data from JSON link.
     */
    private void removeEntrysWithNull(String result) {
        // StringBuilder to string
        try {
            jsonArray = new JSONArray(result);

            jsonValues = new ArrayList<>();

            // divide JSONArray into JSONArray objects and put in an ArrayList (jsonValues)
            for (int i = 0; i < jsonArray.length(); i++) {
                if (!(jsonArray.getJSONObject(i).getString("name").trim().isEmpty()
                        || jsonArray.getJSONObject(i).isNull("name"))) {
                    jsonValues.add(jsonArray.getJSONObject(i));
                    Log.d("ADDED ", jsonArray.getJSONObject(i).toString());
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * AsyncTask for pulling JSON List from the provided link.
     */

    private class JsonListTask extends AsyncTask<Void, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            sortedJsonArray = new JSONArray();
            listIDs = new ArrayList<>();
            listIDsJsonArray = new JSONArray();

            // Load dialog spinner (on UI Thread)
            MainActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    initLoadingSpinner();
                }
            });
        }


        protected Void doInBackground(Void... params) {

            removeEntrysWithNull(pullJSONData());
            countNumberOfListIDs();
            sortByName();
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {

            setupRecyclerView();


            // dismiss indeterminate loading dialog box.
            MainActivity.this.runOnUiThread(() -> dismissInitLoadingSpinner());
        }
    }

    /**
     * checks for the range of list IDs in the JSON data (ex: 1-4, 0-5)
     */
    private void countNumberOfListIDs() {

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int listId = jsonObject.getInt("listId");

                // if the list ID is not in the ArrayList, add it.
                if (!listIDs.contains(listId)) {
                    listIDs.add(listId);
                }

            }
            Collections.sort(listIDs);

            Log.d("LIST IDS ", listIDs.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sorts the JSON Array by name.
     */
    private void sortByName()
    {
        try {
            // sort by name
            Collections.sort(jsonValues, new Comparator<JSONObject>() {
                private final String KEY_NAME = "name";
                @Override
                public int compare(JSONObject a, JSONObject b) {
                    String valueA = null;
                    String valueB = null;
                    try {
                        valueA = a.getString(KEY_NAME);
                        valueB = b.getString(KEY_NAME);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Matcher matcher1 = pattern.matcher(valueA);
                    Matcher matcher2 = pattern.matcher(valueB);

                    while (matcher1.find() && matcher2.find()) {

                        int nonDigitCompare = matcher1.group(1).compareTo(matcher2.group(1));
                        if (0 != nonDigitCompare) {
                            return nonDigitCompare;
                        }

                        if (matcher1.group(2).isEmpty()) {
                            return matcher2.group(2).isEmpty() ? 0 : -1;
                        } else if (matcher2.group(2).isEmpty()) {
                            return +1;
                        }
                        BigInteger n1 = new BigInteger(matcher1.group(2));
                        BigInteger n2 = new BigInteger(matcher2.group(2));
                        int numberCompare = n1.compareTo(n2);
                        if (0 != numberCompare) {
                            return numberCompare;
                        }
                    }

                    return matcher1.hitEnd() && matcher2.hitEnd() ? 0 :
                            matcher1.hitEnd()                ? -1 : +1;
                }
            });


            // list is now sorted by item names
            for (int i = 0; i < jsonValues.size(); i++) {
                sortedJsonArray.put(jsonValues.get(i));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Groups the JSON Array by List ID.
     * @param touchedPosition List ID the user touched.
     */
    private void groupByListID(int touchedPosition)
    {
        listIDsJsonArray = new JSONArray();
        try {

            // group by List ID.
            // traverse through the sorted JSON Array
            for (int i = 0; i < sortedJsonArray.length(); i++) {

                JSONObject jsonObject = sortedJsonArray.getJSONObject(i);
                int value = jsonObject.getInt("listId");

                // if JSON Object List ID is equal to the touched position on the list, move it to the grouped JSONArray.
                if (value == touchedPosition + 1)
                {
                    listIDsJsonArray.put(jsonObject);
                }

            }
            Log.d("GROUPED: ", listIDsJsonArray.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the RecyclerView and its adapter.
     */
    private void setupRecyclerView()
    {
        // convert listIds Arraylist from type "Integer" to type "String" for use in Recyclerview adapter.
        ArrayList<String> listIdsString = new ArrayList<>();
        for (int i = 0; i < listIDs.size(); i++) {
            listIdsString.add("List " + listIDs.get(i));
        }

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerViewAdapter = new MainActivityRecyclerViewAdapter(listIdsString, this);

        // add a vertical divider to the Recyclerview.
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(recyclerViewAdapter);
    }


    /** CHECK IF ANY FORM OF INTERNET CONNECTION IS AVAILABLE
     *
     * @return activeNetworkInfo (if there is an active network, data or Wi-Fi).
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;

        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}