package com.sannacode.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.sannacode.ContactActivity;
import com.sannacode.R;

public class FragmentLogin extends Fragment {

    private ImageView googleButton;

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_login, null);

        ((ContactActivity)getActivity()).inVisibleFab();

        googleButton = (ImageView)v.findViewById(R.id.googleButton);
        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ContactActivity) getActivity()).signIn();
            }
        });
        return v;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.logout).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }


}
