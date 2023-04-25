package com.example.notesapp.CIJ;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.text.InputFilter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.example.notesapp.CIJ.LA.*;
import com.example.notesapp.NotesTakerActivity;

public class Compiler {
    static FileOutputStream outputS;
//	TextView tvNickname1 = findViewById(R.id.shuru);
    static int inputnextInt() {
//        final EditText inputServer = new EditText(NotesTakerActivity.CONTEXT_IGNORE_SECURITY);
//        inputServer.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});//设置最多只能输入50个字符
//        AlertDialog.Builder builder = new AlertDialog.Builder(NotesTakerActivity.CONTEXT_IGNORE_SECURITY);//构建对话框，一个对话框，上面有输入框了，然后就还要有取消和确认键
//        builder.setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
//                .setNegativeButton("取消", null);//设置取消键
//        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {//设置确认键，且对确认键进行监听
//            public void onClick(DialogInterface dialog, int which) {
//                String sign = inputServer.getText().toString();//点击确认后获取输入框的内容
//                if (sign != null && !sign.isEmpty()) {//如果内容不为空，这个判断是为了防止空指针
//                    tvNickname1.setText(sign);//settext
//                    Map<String, String> params = new HashMap<>();
//                    params.put("nickname", sign);
////					 OkHttp.create(ModInfActivity.this).updateSelfInfo(params).enqueue((Call, httpRes) -> {//发送okhttp请求，将修改的信息上传到服务器
////						 if (httpRes.isSuccessful()) {
////							 Log.v("updateSelfInfo", httpRes.toString());
////
////						 }
////					 });
//
//
//                } else {
//                    Toast.makeText(ModInfActivity.this, "签名为空", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//        builder.show();//启动
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void main(String[] args) throws IOException {
        String inPath = "testfile.txt";

        String content = new String(Files.readAllBytes(Paths.get(inPath)));// 读入到str

        new LexicalAnalyser(content);
        new GrammaAnalyser();

        if (outputS != null)
            outputS.close();
    }

}
