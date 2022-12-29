package com.example.notesapp.noteTypes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.notesapp.Models.Notes;
import com.example.notesapp.databinding.FragmentSecondBinding;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    Notes notes;
    //监听回调
    FragmentCallBack mFragmentCallBack;


    ///onAttach 当 Fragment 与 Activity 绑定时调用
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ///获取绑定的监听
        if (context instanceof FragmentCallBack) {
            mFragmentCallBack = (FragmentCallBack) context;
        }
    }

    ///onDetach 当 Fragment 与 Activity 解除绑定时调用
    @Override
    public void onDetach() {
        super.onDetach();
        mFragmentCallBack = null;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {


        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        notes=(Notes)getArguments().getSerializable( "NT" );
        binding.textviewSecond.setText(notes.getDate()+"\n"+notes.getTitle()+"\n"+notes.getNotes());
        binding.gdsure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFragmentCallBack != null) {
                    mFragmentCallBack.trasmit(notes);
                }
                startActivityForResult(new Intent(getContext(), XinHaiNoteCreate.class),1);

            }
        });
        binding.gdredo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public interface FragmentCallBack {
        void trasmit(Notes notes);
    }
}