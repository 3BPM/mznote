package com.example.notesapp.noteTypes;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notesapp.Models.AudioBtnUtils;
import com.example.notesapp.Models.Notes;
import com.example.notesapp.NotesTakerActivity;
import com.example.notesapp.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.notesapp.databinding.FragmentFirstBinding;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FirstFragment extends Fragment {
    Notes notes;
    private FragmentFirstBinding binding;
    private int EV;

    private static final String TAG = "FirstFragment";

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        notes = new Notes();


        binding.buttonUp.setOnClickListener(v -> { //要写的
                    AudioBtnUtils btnUtils = new AudioBtnUtils(this.getActivity());
                    binding.tv.setText(String.valueOf(EV = EV + 10));
                }
        );
        binding.buttonDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.tv.setText(String.valueOf(EV = EV - 10));
            }
        });
        binding.commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = binding.readIn.getText().toString();
                StringBuffer sb = new StringBuffer();
                String desc;
                if (EV < 0)
                    desc = new String("功德值" + EV);
                else
                    desc = new String("功德值+" + EV);
                if (desc.isEmpty()) {
                    Toast.makeText(getActivity(), "Please add some notes!", Toast.LENGTH_SHORT).show();
                    return;
                }
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm MMMMdd号 yyyy");
                Date date = new Date(System.currentTimeMillis());


                notes = new Notes();
                notes.setTitle(title);
                notes.setNotes(desc);
                notes.setDate(formatter.format(date));
                SecondFragment fragment = new SecondFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("NT", notes);

                fragment.setArguments(bundle);//传note对象

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.framelayout, fragment, null)
                        .addToBackStack(null)
                        .commit();

            }
        });
        binding.readIn.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.framelayout, new SecondFragment(), null)
                            .addToBackStack(null)
                            .commit();

                }
                return true;
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}