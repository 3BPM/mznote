package com.example.notesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.notesapp.Models.Notes;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotesTakerActivity extends AppCompatActivity {
    EditText editText_title, editText_notes;
    ImageView imageview_save, imageview_back;
    Button showzh;
    Notes notes;
    boolean isOldNote = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_taker);

        getSupportActionBar().hide();

        imageview_save = findViewById(R.id.imageview_save);
        editText_title = findViewById(R.id.editText_title);
        editText_notes = findViewById(R.id.editText_notes);
        imageview_back = findViewById(R.id.imageview_back);
        showzh=findViewById(R.id.xszw);
    StringBuffer sb=new StringBuffer();
        notes = new Notes();
        try {
            notes = (Notes) getIntent().getSerializableExtra("old_note");
            editText_title.setText(notes.getTitle());

            editText_notes.setText(notes.getNotes());
            isOldNote = true;
        }catch (Exception e){
            e.printStackTrace();
        }

showzh.setOnClickListener(new View.OnClickListener() {

    @Override
    public void onClick(View view) {

        String str1 = new String( editText_notes.getText().toString());
            for (int i = 0; i < str1.length(); i++) {//循环遍历敏感词汇
                if (!Notes.isChineseChar(str1.charAt(i))) {//判断字符串中是否包含敏感词汇
                    sb.append(str1.charAt(i));
                }
            }




        editText_notes.setText(sb.toString());

    }
});
        imageview_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        imageview_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String title = editText_title.getText().toString();
                String desc = editText_notes.getText().toString();

                if (desc.isEmpty()){
                    Toast.makeText(NotesTakerActivity.this, "Please add some notes!", Toast.LENGTH_SHORT).show();
                    return;
                }
                SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM, yyyy");
                Date date = new Date();

                if (!isOldNote){
                    notes = new Notes();
                }

                notes.setTitle(title);
                notes.setNotes(desc);
                notes.setDate(formatter.format(date));

                Intent intent = new Intent();
                intent.putExtra("note", notes);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }
}