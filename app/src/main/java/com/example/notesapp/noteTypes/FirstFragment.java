package com.example.notesapp.noteTypes;

import android.content.ContentValues;
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
import com.example.notesapp.R;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.notesapp.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private  int EV;
    private String s;
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


        binding.buttonUp.setOnClickListener(v->{ //要写的

                    binding.tv.setText(String.valueOf(EV=EV+10));

                }
        );
        binding.buttonDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.tv.setText(String.valueOf(EV=EV-10));
            }
        });
        binding.checkLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                s=binding.readIn.getText().toString();

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.framelayout, new SecondFragment(), null)
                        .addToBackStack(null)
                        .commit();

            }
        });
        binding.readIn.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND){
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