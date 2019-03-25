package dummydomain.yetanothercallblocker;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import dummydomain.yetanothercallblocker.sia.DatabaseSingleton;
import dummydomain.yetanothercallblocker.sia.model.database.CommunityDatabaseItem;
import dummydomain.yetanothercallblocker.sia.model.database.FeaturedDatabaseItem;

public class DummyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy);

        PermissionHelper.checkPermissions(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // TODO: handle
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_dummy, menu);
        return true;
    }

    public void onQueryDb(MenuItem item) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                CommunityDatabaseItem item = DatabaseSingleton.getCommunityDatabase()
                        .getDbItemByNumber("74995861192");

                return null;
            }
        }.execute();
    }

    public void onQueryFeaturedDb(MenuItem item) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                FeaturedDatabaseItem item = DatabaseSingleton.getFeaturedDatabase()
                        .getDbItemByNumber("74995861192");

                return null;
            }
        }.execute();
    }

    public void onUpdateDb(MenuItem item) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseSingleton.getCommunityDatabase().updateSecondaryDb();

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(DummyActivity.this, "Update finished; DB ver: "
                        + DatabaseSingleton.getCommunityDatabase().getEffectiveDbVersion(),
                        Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    public void onTestLoadReviews(MenuItem item) {
        ReviewsActivity.startForNumber(this, "74995861192");
    }

}
