package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
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
import java.util.Iterator;
import java.util.Map;

public class ListViewActivity extends Activity {
    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        //this should be retrieved from the database and the backend server
        url = getIntent().getStringExtra("url");
        Log.d("url",url);

        //get data from backend
        final ArrayList<Movie> movies = new ArrayList<>();
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        Context context = this;

        final StringRequest movielistRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response){
                try {
                    Log.d("r",response);
                    JSONArray ja = new JSONArray(response);

                    ArrayList<Movie> movies = new ArrayList<Movie>();

                    for(int i=0;i<ja.length();i++){
                        JSONObject jo = (JSONObject) ja.get(i);
                        System.out.println("Movie: "+i);
                        String id = jo.getString("movie_id");
                        System.out.println("id: "+id);
                        String title = jo.getString("movie_title");
                        System.out.println("title: "+title);
                        String year = jo.getString("movie_year");
                        System.out.println("year: "+year);
                        String director = jo.getString("movie_director");
                        System.out.println("director: "+director);
                        String rating = jo.getString("movie_rating");
                        System.out.println("rating: "+rating);
                        ArrayList<String> genres = new ArrayList<String>();
                        for(int j=0; j<jo.getJSONArray("movie_genres").length(); j++){
                            genres.add((String)jo.getJSONArray("movie_genres").get(j));
                        }
                        ArrayList<String> stars = new ArrayList<String>();
                        for(int j=0; j<jo.getJSONArray("movie_stars").length(); j++){
                            stars.add((String)jo.getJSONArray("movie_stars").get(j));
                        }

                        movies.add(new Movie(id, title, year, director, rating, genres, stars));
                    }



                    MovieListViewAdapter adapter = new MovieListViewAdapter(movies, context);

                    ListView listView = findViewById(R.id.list);
                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Movie movie = movies.get(position);
                            String message = String.format("Clicked on position: %d, name: %s, %d", position, movie.getTitle(), movie.getYear());
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });





                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("movielist.error", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                final Map<String, String> params = new HashMap<>();

                return params;
            }
        };
        queue.add(movielistRequest);
        /*
        ArrayList<String> genres = new ArrayList<String>();
        genres.add("genre1");
        genres.add("genre2");
        genres.add("genre3");
        ArrayList<String> stars = new ArrayList<String>();
        stars.add("star1");
        stars.add("star2");
        stars.add("star3");
        movies.add(new Movie("tt123","The Terminal", (short) 2004, "director1", genres, stars));
        movies.add(new Movie("tt234","The Final Season", (short) 2007, "director2", genres, stars));

         */
    }
}