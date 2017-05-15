package com.sannacode.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sannacode.DB.DBHelperContact;
import com.sannacode.DB.Model.Contacts;
import com.sannacode.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private Context context;
    List<Contacts> contacts;
    DBHelperContact dbHelperContact;

    public RecyclerAdapter(ArrayList<Contacts> contacts, Context context) {
        this.contacts = contacts;
        this.context = context;
    }

    public void dataChanged(List<Contacts> itemGetServerInfoLoad){
        contacts = itemGetServerInfoLoad;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.name.setText(contacts.get(position).getName());
        //System.out.println("RRRRRRRRRRRRRRR " + contacts.get(position).getNumber());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfo(contacts, position, context);
            }
        });

        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callMethod(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public void showInfo(final List<Contacts> contacts, final int position, final Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView textView = new TextView(context);
        final TextView name = new TextView(context);
        final TextView number = new TextView(context);
        final TextView email = new TextView(context);

        textView.setText("^");
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

        name.setText(contacts.get(position).getName());
        name.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        number.setText("Номер : " + contacts.get(position).getNumber());
        email.setText(contacts.get(position).getEmail());

        name.setTextSize((float) 20.0);
        number.setTextSize((float) 19.0);
        email.setTextSize((float) 19.0);

        if (email.getText().length()==0) {
            email.setText("Email не указан");
        } else {
            email.setText("Email : " + contacts.get(position).getEmail());
        }

        layout.addView(name);
        layout.addView(textView);
        layout.addView(number);
        layout.addView(email);

        alertDialog.setView(layout);

        alertDialog.setPositiveButton("Изменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showChangeMenu(contacts, position, context);
            }
        });

        alertDialog.setNeutralButton("Удалить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteContact(contacts, position, context);
            }
        });

        alertDialog.setNegativeButton("Назад", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDialog.show();
    }

    private void showChangeMenu(final List<Contacts> contacts, final int position, final Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText name = new EditText(context);
        final EditText number = new EditText(context);
        final EditText email = new EditText(context);

        name.setHint("name");
        number.setHint("number");
        email.setHint("email");

        name.setText(contacts.get(position).getName());
        number.setText(contacts.get(position).getNumber());
        email.setText(contacts.get(position).getEmail());

        name.setTextSize((float) 17.0);

        layout.addView(name);
        layout.addView(number);
        layout.addView(email);

        alertDialog.setView(layout);

        alertDialog.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Contacts contacts1 = new Contacts();
                contacts1.setName(name.getText().toString());
                contacts1.setNumber(number.getText().toString());
                contacts1.setEmail(email.getText().toString());
                saveContact(contacts, position, context, contacts1);
            }
        });

        alertDialog.setNegativeButton("Назад", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showInfo(contacts, position, context);
            }
        });
        alertDialog.show();
    }

    private void deleteContact(List<Contacts> contacts, int position, Context context){
        dbHelperContact = new DBHelperContact(context);
        dbHelperContact.deleteConatct(new Contacts(contacts.get(position).getId(),
                contacts.get(position).getName(),
                contacts.get(position).getNumber(),
                contacts.get(position).getEmail()));
        updateList();
    }

    private void saveContact(List<Contacts> contacts, int position, Context context, Contacts contact) {
        dbHelperContact = new DBHelperContact(context);
        deleteContact(contacts, position, context);
        addContact(contact, context);
        updateList();
    }

    private void addContact(Contacts contacts, Context context) {
        dbHelperContact = new DBHelperContact(context);
        dbHelperContact.addContact(contacts);
    }

    public void callMethod(final int position) {
        try {
            Intent intentCall = new Intent();
            intentCall.setAction(Intent.ACTION_CALL);
            intentCall.setData(Uri.parse("tel:" + contacts.get(position).getNumber()));
            context.startActivity(intentCall);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateList() {
        dbHelperContact = new DBHelperContact(context);
        contacts.clear();
        contacts = dbHelperContact.getAllContacts();
        Collections.sort(contacts, new Contacts.SortBeName());
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private ImageView call;

        public ViewHolder(View v) {
            super(v);
            name = (TextView)v.findViewById(R.id.name);
            call = (ImageView) v.findViewById(R.id.call);
        }
    }
}
