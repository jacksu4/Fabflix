package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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
    private ArrayList<Movie> movies = new ArrayList<>();
    private Button prevButton;
    private Button nextButton;

    public void getMovies(Context context, RequestQueue queue, MovieListViewAdapter adapter){

        final StringRequest movielistRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response){
                try {
                    Log.d("r",response);
                    JSONArray ja = new JSONArray(response);
                    movies.clear();

                    for(int i=0;i<ja.length();i++){
                        JSONObject jo = (JSONObject) ja.get(i);
                        //System.out.println("Movie: "+i);
                        String id = jo.getString("movie_id");
                        //System.out.println("id: "+id);
                        String title = jo.getString("movie_title");
                        //System.out.println("title: "+title);
                        String year = jo.getString("movie_year");
                        //System.out.println("year: "+year);
                        String director = jo.getString("movie_director");
                        //System.out.println("director: "+director);
                        String rating = jo.getString("movie_rating");
                        //System.out.println("rating: "+rating);
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
                    //System.out.println(movies.toString());
                    adapter.notifyDataSetChanged();

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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        //this should be retrieved from the database and the backend server
        url = getIntent().getStringExtra("url");
        //Log.d("url",url);

        //get data from backend
        Context context = this;
        final RequestQueue queue = NetworkManager.sharedManager(context).queue;
        MovieListViewAdapter adapter = new MovieListViewAdapter(movies, context);

        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = movies.get(position);
                Intent single_movie_page = new Intent(ListViewActivity.this, SingleMovieActivity.class);
                //String single_movie_url = "http://10.0.2.2:8080/cs122b-spring20-team-74/api/single-movie?id="+movie.getId();
                String single_movie_url = "https://ec2-54-235-239-224.compute-1.amazonaws.com:8443/cs122b-proj2/api/single-movie?id="+movie.getId();
                //System.out.println(single_movie_url);
                single_movie_page.putExtra("url",single_movie_url);
                startActivity(single_movie_page);

                //String message = String.format("Clicked on position: %d, name: %s, %d", position, movie.getTitle(), movie.getYear());
                //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
        getMovies(context, queue, adapter);
        prevButton = findViewById(R.id.prev);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int page = Integer.parseInt(url.substring(url.length()-1));
                if(page >= 1){
                    url = url.substring(0,url.length()-1)+(page-1);
                    getMovies(context, queue, adapter);
                }
            }
        });

        nextButton = findViewById(R.id.next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int page = Integer.parseInt(url.substring(url.length()-1));
                if(movies.size()==20){
                    url = url.substring(0,url.length()-1)+(page+1);
                    getMovies(context, queue, adapter);
                }
            }
        });



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