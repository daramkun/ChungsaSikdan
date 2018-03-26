package me.daram.chungsasikdan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

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
        this.getActionBar().setIcon(R.drawable.pinned_shortcut_icon);
        getRestaurantListAsync();

        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.restaurant_menu, menu);
        return true;
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

            case R.id.pin_to_home:
                {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setClass(this, RestaurantActivity.class);
                    intent.putExtra("chungsa_name", getTitle());
                    intent.putExtra("chungsa_code", chungsaCode);
                    if(!PinToHomeUtility.pinToHome(this, "chungsasikdan_" + getTitle (), intent,
                            getTitle(), getChungsaShortName(), R.drawable.pinned_shortcut_icon)) {
                        Toast.makeText(this, R.string.pin_to_home_error, Toast.LENGTH_SHORT).show ();
                    }
                }
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private String getChungsaShortName () {
        String title = String.valueOf(getTitle ());
        if (title.contains("세종"))
            return "세종청사";
        if (title.length () <= 4)
            return title;
        return title.substring(0, 4);
    }
}
