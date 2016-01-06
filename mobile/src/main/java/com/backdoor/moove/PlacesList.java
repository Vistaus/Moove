package com.backdoor.moove;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.backdoor.moove.core.adapters.PlaceRecyclerAdapter;
import com.backdoor.moove.core.consts.Constants;
import com.backdoor.moove.core.consts.QuickReturnViewType;
import com.backdoor.moove.core.data.PlaceDataProvider;
import com.backdoor.moove.core.helper.Coloring;
import com.backdoor.moove.core.helper.DataBase;
import com.backdoor.moove.core.helper.Module;
import com.backdoor.moove.core.helper.Permissions;
import com.backdoor.moove.core.interfaces.SimpleListener;
import com.backdoor.moove.core.utils.LocationUtil;
import com.backdoor.moove.core.utils.QuickReturnUtils;
import com.backdoor.moove.core.views.ReturnScrollListener;

public class PlacesList extends AppCompatActivity implements SimpleListener {

    private RecyclerView listView;
    private LinearLayout emptyItem;
    private Coloring cs = new Coloring(PlacesList.this);
    private FloatingActionButton mFab;

    private PlaceDataProvider provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(cs.getStyle());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(cs.colorPrimaryDark());
        }
        setContentView(R.layout.places_activity_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ic_clear_white_24dp);
        toolbar.setTitle(getString(R.string.places));

        emptyItem = (LinearLayout) findViewById(R.id.emptyItem);
        emptyItem.setVisibility(View.VISIBLE);

        TextView emptyText = (TextView) findViewById(R.id.emptyText);
        emptyText.setText(getString(R.string.no_places));

        ImageView emptyImage = (ImageView) findViewById(R.id.emptyImage);
        emptyImage.setImageResource(R.drawable.place_white);

        listView = (RecyclerView) findViewById(R.id.currentList);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LocationUtil.checkGooglePlayServicesAvailability(PlacesList.this)) {
                    if (Permissions.checkPermission(PlacesList.this, Permissions.ACCESS_COARSE_LOCATION)) {
                        startActivity(new Intent(PlacesList.this, NewPlace.class));
                    } else {
                        Permissions.requestPermission(PlacesList.this, new String[]{Permissions.ACCESS_COARSE_LOCATION,
                                Permissions.ACCESS_FINE_LOCATION}, 101);
                    }
                }
            }
        });
    }

    private void loadPlaces(){
        provider = new PlaceDataProvider(this, true);
        reloadView();
        PlaceRecyclerAdapter adapter = new PlaceRecyclerAdapter(this, provider, false);
        adapter.setEventListener(this);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(adapter);
        listView.setItemAnimator(new DefaultItemAnimator());
        ReturnScrollListener scrollListener = new
                ReturnScrollListener.Builder(QuickReturnViewType.FOOTER)
                .footer(mFab)
                .minFooterTranslation(QuickReturnUtils.dp2px(this, 88))
                .isSnappable(true)
                .build();

        if (Module.isLollipop()) {
            listView.addOnScrollListener(scrollListener);
        } else {
            listView.setOnScrollListener(scrollListener);
        }
    }

    private void reloadView() {
        int size = provider.getCount();
        if (size > 0){
            listView.setVisibility(View.VISIBLE);
            emptyItem.setVisibility(View.GONE);
        } else {
            listView.setVisibility(View.GONE);
            emptyItem.setVisibility(View.VISIBLE);
        }
    }

    private void deletePlace(int position){
        long id = provider.getItem(position).getId();
        if (id != 0) {
            DataBase db = new DataBase(this);
            db.open();
            db.deletePlace(id);
            db.close();
            Snackbar.make(mFab, R.string.deleted, Snackbar.LENGTH_LONG)
                    .show();
            loadPlaces();
        }
    }

    private void editPlace(int position){
        startActivity(new Intent(this, NewPlace.class)
                .putExtra(Constants.ITEM_ID_INTENT, provider.getItem(position).getId()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPlaces();
    }

    @Override
    public void onItemClicked(int position, View view) {
        editPlace(position);
    }

    @Override
    public void onItemLongClicked(final int position, View view) {
        final CharSequence[] items = {getString(R.string.edit), getString(R.string.delete)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                dialog.dismiss();
                if (item == 0) {
                    editPlace(position);
                }
                if (item == 1) {
                    deletePlace(position);
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startActivity(new Intent(PlacesList.this, NewPlace.class));
                } else {
                    Permissions.showInfo(PlacesList.this, Permissions.READ_CALENDAR);
                }
                break;
        }
    }
}