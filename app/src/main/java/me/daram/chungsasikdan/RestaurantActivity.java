package me.daram.chungsasikdan;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.print.PrintHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ShareActionProvider;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
//import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daram on 2018-03-25.
 */

public class RestaurantActivity extends Activity implements AdapterView.OnItemSelectedListener {
    String chungsaCode;
    List<Restaurant> restaurantList;
    List<String> restaurantNames;
    Drawable currentDrawable;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        String chungsaName = getIntent().getStringExtra("chungsa_name");
        chungsaCode = getIntent().getStringExtra("chungsa_code");
        boolean startFromList = getIntent().getBooleanExtra("start_from_list", false);

        Spinner restaurantSpinner = findViewById(R.id.restaurant_spinner);
        restaurantSpinner.setOnItemSelectedListener(this);

        this.setTitle(chungsaName);

        ActionBar actionBar = this.getActionBar();
        if (actionBar != null) {
            actionBar.setIcon(R.drawable.pinned_shortcut_icon);
            actionBar.setDisplayHomeAsUpEnabled(startFromList);
        }

        getRestaurantListAsync();
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
                    restaurantList = Restaurant.getRestaurantList(self, chungsaCode);
                    restaurantNames = new ArrayList<>();
                    for (Restaurant restaurant : restaurantList) {
                        restaurantNames.add(restaurant.getName());
                    }

                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            Spinner restaurantSpinner = findViewById(R.id.restaurant_spinner);

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(self,
                                    android.R.layout.simple_list_item_1, restaurantNames);
                            restaurantSpinner.setAdapter(adapter);

                            //getRestaurantMenuImageAsync(restaurantList.get(0));
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
        final PhotoView menuImageView = findViewById(R.id.restaurant_menu_photoview);
        final LoadingDialog loadingDialog = new LoadingDialog (this);
        loadingDialog.show ();
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void> () {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
	                currentDrawable = restaurant.getMenuImage ( self );
	                
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            menuImageView.setImageDrawable ( currentDrawable );
                            loadingDialog.dismiss ();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable () {
                    	@Override
	                    public void run () {
                    		menuImageView.setImageResource ( R.drawable.no_image );
                    		loadingDialog.dismiss ();
	                    }
                    });
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
                    intent.putExtra("start_from_list", false);
                    if(!PinToHomeUtility.pinToHome(this, "chungsasikdan_" + getTitle (), intent,
                            getTitle(), getChungsaShortName(), R.drawable.pinned_shortcut_icon)) {
                        Toast.makeText(this, R.string.pin_to_home_error, Toast.LENGTH_SHORT).show ();
                    }
                }
                break;
                
            case R.id.print_image:
                {
                    final PrintHelper printHelper = new PrintHelper (this);
                    printHelper.setScaleMode ( PrintHelper.SCALE_MODE_FIT );
                    printHelper.printBitmap ( "청사식단", ( ( BitmapDrawable ) currentDrawable ).getBitmap () );
                }
                break;
                
            case R.id.share_image:
                {
                    if ( ContextCompat.checkSelfPermission ( this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED ) {
                        ActivityCompat.requestPermissions ( this, new String [] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 101 );
                        break;
                    }
                    
                    shareImage ();
                }
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
    
    @Override
    public void onRequestPermissionsResult ( int requestCode, String [] permissions, int [] grantResults ) {
        switch ( requestCode ) {
            case 101:
            {
                if ( grantResults.length > 0 && grantResults [ 0 ] == PackageManager.PERMISSION_GRANTED ) {
                    shareImage ();
                } else {
                    Toast.makeText ( this, "식단 이미지를 공유하기 위해서는 외부 저장소에 쓰기와 관련된 권한이 필요합니다.", Toast.LENGTH_LONG ).show ();
                }
            }
            break;
        }
    }
    
    private Uri copyToExternalContentAndReturnUri () {
        File file = new File ( String.format ( "%s/%s", Environment.getExternalStorageDirectory (), "ChungsaSikdanShare.jpg" ) );
        try {
            OutputStream fileOutputStream = new FileOutputStream ( file );
            file.createNewFile ();
            ( ( BitmapDrawable ) currentDrawable ).getBitmap ().compress ( Bitmap.CompressFormat.JPEG, 90, fileOutputStream );
            fileOutputStream.close ();
        } catch ( Exception e ) {
            e.printStackTrace ();
            return null;
        }
        Log.i ( "청사식단", "공유 이미지 경로: " + file.getAbsolutePath () );
        return Uri.parse ( file.getAbsolutePath () );
    }

    private String getChungsaShortName () {
        String title = String.valueOf(getTitle ());
        if (title.contains("세종"))
            return "세종청사";
        if (title.length () <= 4)
            return title;
        return title.substring(0, 4);
    }
    
    private void shareImage () {
        Intent shareIntent = new Intent ();
        shareIntent.setAction ( Intent.ACTION_SEND );
        shareIntent.putExtra ( Intent.EXTRA_STREAM, copyToExternalContentAndReturnUri () );
        shareIntent.setType ( "image/*" );
        startActivity ( Intent.createChooser ( shareIntent, "이미지 공유" ) );
    }
}
