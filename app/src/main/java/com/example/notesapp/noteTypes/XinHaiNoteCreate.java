package com.example.notesapp.noteTypes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.notesapp.Models.Notes;
import com.example.notesapp.R;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.notesapp.databinding.ActivityXinHaiNoteBinding;

public class XinHaiNoteCreate extends AppCompatActivity implements SecondFragment.FragmentCallBack {

    private AppBarConfiguration appBarConfiguration;
    private ActivityXinHaiNoteBinding binding;
    Notes notes = new Notes();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityXinHaiNoteBinding.inflate(getLayoutInflater()); // 初始化binding
        View view = binding.getRoot();
        setContentView(binding.getRoot());

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.framelayout, new FirstFragment())//创建事务
                .addToBackStack(null)//transaction 的方法采用fragment管理栈 用add 可以一页一页退出
                .commit();//提交事务才可运行


    }



    @Override
    public void trasmit(Notes text) {
        this.notes = text;

        Intent intent = new Intent();
        intent.putExtra("note", notes);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}