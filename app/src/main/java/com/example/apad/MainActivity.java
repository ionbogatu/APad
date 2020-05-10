package com.example.apad;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private ArrayList<HashMap<String, Object>> notes = new ArrayList<>();
    private ListView notesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("APad");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.databaseHelper = new DatabaseHelper(this);
        this.notesListView = findViewById(R.id.notesListView);

        OnFabClick();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();

        notes = databaseHelper.retrieveData();

        NoteAdapter adapter = new NoteAdapter(this, notes);
        notesListView.setAdapter(adapter);
    }

    private void OnFabClick() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addEditActivityIntent = new Intent(MainActivity.this, AddEditActivity.class);
                startActivity(addEditActivityIntent);
            }
        });
    }

    class NoteAdapter extends ArrayAdapter<String> {
        Context context;
        ArrayList<HashMap<String, Object>> notes;

        NoteAdapter(Context c, ArrayList<HashMap<String, Object>> notes) {
            super(c, R.layout.activity_main, R.id.noteName);
            this.context = c;
            this.notes = notes;
        }

        @Override
        public int getCount() {
            return notes.size();
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.note_list_view, parent, false);
            TextView title = row.findViewById(R.id.noteName);

            title.setText(this.notes.get(position).get("name").toString());

            return row;
        }
    }
}
