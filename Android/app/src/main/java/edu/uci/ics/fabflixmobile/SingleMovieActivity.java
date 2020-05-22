package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SingleMovieActivity extends Activity {
    private TextView titleView;
    private TextView yearView;
    private TextView directorView;
    private TextView ratingView;
    private TextView genresView;
    private TextView starsView;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_movie);
        titleView = findViewById(R.id.title);
        yearView = findViewById(R.id.year);
        directorView = findViewById(R.id.director);
        ratingView = findViewById(R.id.rating);
        genresView = findViewById(R.id.genres);
        starsView = findViewById(R.id.stars);
        url = getIntent().getStringExtra("url");

        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        final StringRequest singleMovieRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response){
                try {
                    Log.d("r",response);
                    JSONArray ja = new JSONArray(response);
                    JSONObject jo = (JSONObject) ja.get(0);
                    titleView.setText(jo.getString("movie_title"));
                    yearView.setText("Year: "+jo.getString("movie_year"));
                    directorView.setText("Director: "+jo.getString("movie_director"));
                    if(ja.length()<3){
                        ratingView.setText("Rating: N/A");
                    }else{
                        jo = (JSONObject) ja.get(2);
                        ratingView.setText("Rating: "+jo.getString("movie_rating"));
                    }
                    jo = (JSONObject) ja.get(1);
                    String genres_str = "Genres: ";
                    for(int j=0; j<jo.getJSONArray("genres").length(); j++){
                        genres_str += (String)jo.getJSONArray("genres").get(j)+", ";
                    }
                    genres_str = genres_str.substring(0,genres_str.length()-2);
                    genresView.setText(genres_str);

                    String stars_str = "Stars: ";
                    for(int j=0; j<jo.getJSONArray("stars_name").length(); j++){
                        stars_str += (String)jo.getJSONArray("stars_name").get(j)+", ";
                    }
                    stars_str = stars_str.substring(0,stars_str.length()-2);
                    starsView.setText(stars_str);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("single_movie.error", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                final Map<String, String> params = new HashMap<>();

                return params;
            }
        };
        queue.add(singleMovieRequest);

    }
}
