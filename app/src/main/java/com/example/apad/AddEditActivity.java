package com.example.apad;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class AddEditActivity extends AppCompatActivity {
    private MyEditText myEditText;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Add Note");
        setContentView(R.layout.activity_add_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ToggleButton boldToggle = findViewById(R.id.bold);
        ToggleButton italicsToggle = findViewById(R.id.italic);
        ToggleButton underlinedToggle = findViewById(R.id.underline);
        underlinedToggle.setPaintFlags(underlinedToggle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        myEditText = findViewById(R.id.textarea);

        myEditText.setBoldToggleButton(boldToggle);
        myEditText.setItalicsToggleButton(italicsToggle);
        myEditText.setUnderlineToggleButton(underlinedToggle);

        FontSizeClick();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                SaveTextToFile();
                break;
            default:
                Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(mainActivityIntent, 0);
        }

        return true;
    }

    private void FontSizeClick() {
        final int initialOffset = 8;

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Example");
        ArrayList sizes = new ArrayList<String>();

        for (int i = initialOffset; i < initialOffset + 40; i++) {
            sizes.add(i + "px");
        }

        final Button fontSize = findViewById(R.id.fontSize);

        fontSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder.show();
            }
        });

        dialogBuilder.setItems((CharSequence[]) sizes.toArray(new CharSequence[sizes.size()]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int startSelection = AddEditActivity.this.myEditText.getSelectionStart();
                int endSelection = AddEditActivity.this.myEditText.getSelectionEnd();

                if (startSelection > endSelection) {
                    int tmp = startSelection;
                    startSelection = endSelection;
                    endSelection = tmp;
                }

                if (startSelection != endSelection) {
                    Editable text = AddEditActivity.this.myEditText.getText();
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);

                    AbsoluteSizeSpan absoluteSizeSpan = new AbsoluteSizeSpan(convertDpToPx(which + initialOffset));
                    spannableStringBuilder.setSpan(absoluteSizeSpan, startSelection, endSelection, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                    AddEditActivity.this.myEditText.setText(spannableStringBuilder);
                } else {
                    Button fontSize = findViewById(R.id.fontSize);
                    fontSize.setText((which + initialOffset) + "dp");
                    AddEditActivity.this.myEditText.setTextSize((which + initialOffset) + "dp");
                }

                dialog.dismiss();
            }
        });
    }

    private int convertDpToPx(int dp) {
        return Math.round(dp * (getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private void SaveTextToFile() {
        SaveModal saveModal = new SaveModal();

        Bundle args = new Bundle();
        args.putString("data", Html.toHtml(myEditText.getText()));
        saveModal.setArguments(args);

        saveModal.show(getSupportFragmentManager(), "save modal");
    }
}
