package com.example.apad;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;

public class SaveModal extends AppCompatDialogFragment {
    private String data = null;

    private String absolutePath = null;
    private String fileName = null;
    private EditText fileNameInput;
    private TextView fullPathLabel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("data")) {
            data = arguments.getString("data");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.save_modal, null);
        builder.setView(view).setTitle("Save file");

        fileNameInput = view.findViewById(R.id.absolutePathInput);
        fileNameInput.setOnKeyListener(new View.OnKeyListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                fileName = fileNameInput.getText().toString();
                fullPathLabel.setText("The file will be saved under " + Paths.get(absolutePath, fileName).toString() + ".note");

                File file = new File(Paths.get(absolutePath, fileName).toString() + ".note");
                if (file.exists()) {
                    fileNameInput.setError("This file already exists.");
                }

                return true;
            }
        });

        fullPathLabel = view.findViewById(R.id.absoluteDirectory);

        absolutePath = getContext().getDataDir().getAbsolutePath() + "/";

        Button selectFolderButton = view.findViewById(R.id.selectFolderButton);
        selectFolderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeSaveDirectory();
            }
        });

        Button saveButton = view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (data != null && SaveModal.this.fileNameInput.getError() != null) {
                        FileOutputStream fos = new FileOutputStream(Paths.get(absolutePath, fileNameInput.getText().toString()).toString() + ".note");
                        PrintWriter printWriter = new PrintWriter(fos);
                        printWriter.write(data);
                        printWriter.close();
                        fos.close();
                    }

                    SaveModal.this.dismiss();
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return builder.create();
    }

    private void ChangeSaveDirectory() {
        Intent folderIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(Intent.createChooser(folderIntent, "Choose directory"), 10);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == 10 && resultCode == Activity.RESULT_OK) {
                absolutePath = FileUtil.getFullPathFromTreeUri(data.getData(), getContext());

                if (fileName != null) {
                    String path = Paths.get(absolutePath, fileName).toString();
                    fullPathLabel.setText("The file will be saved under " + path + ".note");
                }
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
