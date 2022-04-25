package com.gordonowiredu.fetchrewardsexercise;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    protected ListActivityRecyclerViewAdapter recyclerViewAdapter;
    protected JSONArray jsonArrayExtra;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        getJSONExtra();
        setupRecyclerView();


    }

    /**
     * Initializes the RecyclerView and its adapter.
     */
    private void setupRecyclerView()
    {

        ArrayList<ListActivityItem> listItems = new ArrayList<>();

        try {
            for (int i = 0; i < jsonArrayExtra.length(); i++) {
                // get JSON Array Object and create a new ListActivityItem (row item/instance) with the "id" and "name" information.
                JSONObject jsonObject = jsonArrayExtra.getJSONObject(i);
                ListActivityItem item = new ListActivityItem(jsonObject.getString("id"), jsonObject.getString("name"));
                listItems.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        ListActivityRecyclerViewAdapter recyclerViewAdapter = new ListActivityRecyclerViewAdapter(listItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false));

        // add a vertical divider to the Recyclerview.
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    /**
     * Get JSON Array from Intent Extra data.
     */
    private void getJSONExtra()
    {
        Intent intent = getIntent();
        String jsonArrayString = intent.getStringExtra("jsonArray");

        try {
            // convert string to JSON Array.
            jsonArrayExtra = new JSONArray(jsonArrayString);

            for (int i = 0; i < jsonArrayExtra.length(); i++)
            {
                // divide JSON Array to JSON Objects (for logging purposes)
                JSONObject jsonObj = jsonArrayExtra.getJSONObject(i);

                Log.i("JSON EXTRA", jsonObj.toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}