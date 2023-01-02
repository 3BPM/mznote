package com.example.notesapp.CIJ.ToolsandC0;
import java.io.*;

import java.util.Scanner;

public class TestC0andTools {
	public static String homepath = System.getProperty("user.dir") + "\\";
	private static Scanner input = new Scanner(System.in);
	private static PrintStream consoleStream = System.out;

	public static void main(String[] args) throws IOException {

		for (int i = 0; i < 5; i++)
			TestC0andTools.test();
		input.close();

	}

	public static void test() throws IOException {

		// 创建盛放数据的特殊类对象，即动态数组
		System.setOut(TestC0andTools.consoleStream);
		System.out.println("工作目录是：" + homepath);
		String codepath = new String(homepath);

		System.out.print("例：c代码命名为1.c 程序将自动生成(testfile1、input1、output1序号) \n这是第几个捏? ");

		String a = input.next();
		System.out.println();
		codepath = codepath + a + ".c";
		// 创建sb
		StringBuffer tmp = new StringBuffer();

		tmp.append(homepath);
		int homelength = tmp.length();
		tmp.append("testfile" + a + ".txt");
		String testpath = tmp.toString();

		tmp.setLength(homelength);
		tmp.append("input" + a + ".txt");
		String inputpath = tmp.toString();

		System.out.println("\n输入是什么捏? n个随机数就是sjsN nothing就是nth 确定数写下来就行 ");
		String b = input.next();

		Search Pb = new Search(b);
		FileOutputStream fop = pathToStream(inputpath);
		if (Pb.isRd) {
			Rdm rdd = new Rdm(Pb.Rdn); // get the content in bytes
			byte[] contentInBytes = rdd.s.getBytes();
			fop.write(contentInBytes);
			fop.flush();
			fop.close();
		} else if (Pb.isNt) {
			fop.flush();
			fop.close();
		} else {
			byte[] contentInBytes = b.getBytes();
			fop.write(contentInBytes);
			fop.flush();
			fop.close();
		}
		new testc(a);

		tmp.setLength(0);// 清空sb
		try

		{
			tmp = TestC0andTools.BufferFrompath(codepath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String source = tmp.toString();

		pathToOut(testpath);
		System.out.println(source);

	}

	public static StringBuffer BufferFrompath(String filepath) throws IOException {
		// 把文件内容读入sb
		StringBuffer buffer = new StringBuffer();
		InputStream is = new FileInputStream(filepath);
		String line; // 用来保存每行读取的内容
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		line = reader.readLine(); // 读取第一行

		while (line != null) { // 如果 line 为空说明读完了
			Search lp = new Search(line);// 去除有#的行
			if (!lp.isInc) {
				buffer.append(line); // 将读到的内容添加到 buffer 中
				buffer.append("\n"); // 添加换行符
			}
			line = reader.readLine(); // 读取下一行
		}
		reader.close();
		is.close();
		return buffer;
	}

	public static FileOutputStream pathToStream(String filepath) throws IOException {
		File file = new File(filepath);
		// if file doesn't exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}
		FileOutputStream fop = new FileOutputStream(file);
		return fop;
	}

	public static void pathToOut(String filepath) throws IOException {
		File file = new File(filepath);
		// if file doesn't exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}
		PrintStream out = new PrintStream(file);
		System.setOut(out);// 设置 输出方式
	}
}
