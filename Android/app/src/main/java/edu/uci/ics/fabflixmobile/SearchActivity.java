package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends Activity {
    private EditText advance_title;
    private Button search_button;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        advance_title = findViewById(R.id.advance_title);
        search_button = findViewById(R.id.search);

        url = "http://10.0.2.2:8080/cs122b-spring20-team-74/";


        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });
    }

    public void search(){
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        final StringRequest searchRequest = new StringRequest(Request.Method.POST, url + "api/advancesearch", new Response.Listener<String>() {
            @Override
            public void onResponse(String response){
                Intent listPage = new Intent(SearchActivity.this, ListViewActivity.class);
                String new_url = url + "api/movielist?start=null&genre=null&search=null&advance=true&title="+advance_title.getText().toString()+"&director=null&year=null&star_name=null&firstsort=rating&secondsort=title&firstmethod=desc&secondmethod=asc&resultperpage=20&page=0";
                listPage.putExtra("url",new_url);
                startActivity(listPage);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("search.error", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                final Map<String, String> params = new HashMap<>();
                params.put("advance-title", advance_title.getText().toString());

                return params;
            }
        };
        queue.add(searchRequest);
    }
}