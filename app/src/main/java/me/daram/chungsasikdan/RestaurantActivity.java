package me.daram.chungsasikdan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daram on 2018-03-25.
 */

public class RestaurantActivity extends Activity implements AdapterView.OnItemSelectedListener {
    String chungsaCode;
    List<Restaurant> restaurantList;
    List<String> restaurantNames;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        String chungsaName = getIntent().getStringExtra("chungsa_name");
        chungsaCode = getIntent().getStringExtra("chungsa_code");

        Spinner restaurantSpinner = findViewById(R.id.restaurant_spinner);
        restaurantSpinner.setOnItemSelectedListener(this);

        this.setTitle(chungsaName);
        getRestaurantListAsync();

        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getRestaurantListAsync() {
        final Activity self = this;
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    restaurantList = Restaurant.getRestaurantList(chungsaCode);
                    restaurantNames = new ArrayList<String>();
                    for (Restaurant restaurant : restaurantList) {
                        restaurantNames.add(restaurant.getName());
                    }

                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            Spinner restaurantSpinner = findViewById(R.id.restaurant_spinner);

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(self,
                                    android.R.layout.simple_list_item_1, restaurantNames);
                            restaurantSpinner.setAdapter(adapter);

                            getRestaurantMenuImageAsync(restaurantList.get(0));
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        task.execute();
    }

    private void getRestaurantMenuImageAsync (final Restaurant restaurant) {
        final Activity self = this;
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void> () {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    URL menuUrl = new URL(restaurant.getMenuURL());
                    final Bitmap menuImage = BitmapFactory.decodeStream(menuUrl.openStream());

                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            PhotoView menuImageView = findViewById(R.id.restaurant_menu_photoview);
                            menuImageView.setImageBitmap(menuImage);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        task.execute();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int index, long id) {
        getRestaurantMenuImageAsync(restaurantList.get(index));
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                this.finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
