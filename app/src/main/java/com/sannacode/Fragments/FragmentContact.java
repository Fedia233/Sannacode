package com.sannacode.Fragments;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.sannacode.Adapter.RecyclerAdapter;
import com.sannacode.ContactActivity;
import com.sannacode.DB.DBHelperContact;
import com.sannacode.DB.Model.Contacts;
import com.sannacode.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FragmentContact extends Fragment {

    private static final String LOG_TAG = "contactFragment";

    public RecyclerView mRecyclerView;
    public RecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private DBHelperContact dbHelperContact;
    public List<Contacts> contactsList = new ArrayList<>();
    public List<Contacts> contactsSearch;

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_contact, null);

        ((ContactActivity)getActivity()).visibleFab();

        dbHelperContact = new DBHelperContact(getActivity());

        contactsList = dbHelperContact.getAllContacts();
        Collections.sort(contactsList, new Contacts.SortBeName());

        mRecyclerView = (RecyclerView)v.findViewById(R.id.recycler_view);
        recyclerStart();

        return v;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment, menu);
        MenuItem item = menu.findItem(R.id.search);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(searchQueryListener);

    }

    private SearchView.OnQueryTextListener searchQueryListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            search(query);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            search(newText);
            return true;
        }

        public void search(String query) {
            contactsSearch = filter(contactsList, query);
            mAdapter = new RecyclerAdapter((ArrayList) contactsSearch, getActivity());
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
    };

    private static List<Contacts> filter(List<Contacts> models, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final List<Contacts> filteredModelList = new ArrayList<>();
        for (Contacts model : models) {
            final String text = model.getName().toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    private boolean dublicatContact(String number) {
        boolean value = false;

        if (dbHelperContact.getAllContacts().contains(number)) { value = false; }
        else { value = true; }
        return value;
    }

    public void recyclerStart() {
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerAdapter((ArrayList) contactsList, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    public void dataAdd(String name, String number, String email) {
        contactsList.add(new Contacts(name, number, email));
        Collections.sort(contactsList, new Contacts.SortBeName());
        mAdapter = new RecyclerAdapter((ArrayList) contactsList, getActivity());
        mAdapter.dataChanged(contactsList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                new AsinkContact().execute();
                return true;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getContact(){
        ContentResolver resolver = getActivity().getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (cursor.moveToFirst()) {

            String name;
            String phone;

            while (cursor.moveToNext()) {

                name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                if (dublicatContact(phone)) {
                    dbHelperContact.addContact(new Contacts(name, phone));
                } else {
                    Log.v(LOG_TAG, "Cursor is empty");
                }
            }
        } else {
            Log.v(LOG_TAG, "Cursor is empty");
        }
        contactsList = dbHelperContact.getAllContacts();
        Collections.sort(contactsList, new Contacts.SortBeName());
        cursor.close();
    }

    private class AsinkContact extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ((ContactActivity)getActivity()).startProgress();
        }

        @Override
        protected Void doInBackground(Void... params) {
            getContact();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mAdapter.dataChanged(contactsList);
            ((ContactActivity)getActivity()).endProgress();
        }
    }
}
