package org.openmrs.client.activities;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.openmrs.client.R;
import org.openmrs.client.adapters.ActiveVisitsArrayAdapter;
import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.dao.VisitDAO;
import org.openmrs.client.utilities.FontsUtil;

public class FindActiveVisitsActivity extends ACBaseActivity {

    private String mQuery;
    private ActiveVisitsArrayAdapter mAdapter;
    private ListView mVisitListView;
    private MenuItem mFindVisitItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_visits);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mVisitListView = (ListView) findViewById(R.id.visits_list_view);
        TextView emptyList = (TextView) findViewById(R.id.empty_visits_list_view);
        emptyList.setText(getString(R.string.search_visits_no_results));
        mVisitListView.setEmptyView(emptyList);

        FontsUtil.setFont((ViewGroup) findViewById(android.R.id.content));
        handleIntent(getIntent());
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getSupportActionBar().setSubtitle(getString(R.string.dashboard_logged_as, OpenMRS.getInstance().getUsername()));
        getMenuInflater().inflate(R.menu.find_patients_menu, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView findVisitView;

        mFindVisitItem = menu.findItem(R.id.action_search);
        if (OpenMRS.getInstance().isRunningHoneycombVersionOrHigher()) {
            findVisitView = (SearchView) mFindVisitItem.getActionView();
        } else {
            findVisitView = (SearchView) MenuItemCompat.getActionView(mFindVisitItem);
        }

        SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
        findVisitView.setSearchableInfo(info);
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter = new ActiveVisitsArrayAdapter(this, R.layout.find_visits_row, new VisitDAO().getAllActiveVisits());
        mVisitListView.setAdapter(mAdapter);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mQuery = intent.getStringExtra(SearchManager.QUERY);
            Intent searchIntent = new Intent(this, FindActiveVisitsSearchActivity.class);
            searchIntent.putExtra(SearchManager.QUERY, mQuery);
            startActivity(searchIntent);
            intent.setAction(null);
            if (null != mFindVisitItem) {
                MenuItemCompat.collapseActionView(mFindVisitItem);
            }
        }
    }

}
