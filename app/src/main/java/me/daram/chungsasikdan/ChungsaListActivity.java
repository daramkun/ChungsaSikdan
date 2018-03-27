package me.daram.chungsasikdan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChungsaListActivity extends Activity implements AdapterView.OnItemClickListener {
    private List<Chungsa> chungsaList;
    private List<String> chungsaNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chungsa_list);

        ListView chungsaListView = findViewById(R.id.chungsaListView);
        chungsaListView.setOnItemClickListener(this);

        getChungsaListAsync ();
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chungsalist_menu, menu);
        return true;
    }

    private void getChungsaListAsync() {
        final Activity self = this;
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    chungsaList = Chungsa.getChungsaList(self);
                    chungsaNames = new ArrayList<>();
                    for (Chungsa chungsa : chungsaList) {
                        chungsaNames.add(chungsa.getName());
                    }

                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            ListView chungsaListView = findViewById(R.id.chungsaListView);

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(self,
                                    android.R.layout.simple_list_item_1, chungsaNames);
                            chungsaListView.setAdapter(adapter);
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
    public void onItemClick(AdapterView<?> adapterView, View view, int index, long id) {
        Chungsa chungsa = chungsaList.get(index);

        Intent intent = new Intent(this, RestaurantActivity.class);
        intent.putExtra("chungsa_name", chungsa.getName());
        intent.putExtra("chungsa_code", chungsa.getCode());
        intent.putExtra("start_from_list", true);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear_cache:
            {
                PageGetter.clearCaches(this);
                Toast.makeText(this, R.string.succeed_clear_caches, Toast.LENGTH_SHORT).show();
            }
            break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
