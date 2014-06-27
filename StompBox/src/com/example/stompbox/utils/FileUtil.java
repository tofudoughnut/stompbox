package com.example.stompbox.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.util.Log;

public class FileUtil {
	private static final String TAG = "FileUtil";
	private static final boolean isDbg = false;		// Logの出力有無。

	// ファイル有無チェック。
	public static boolean doesFileExists(Context c, String filename){
		return (new File(c.getFilesDir().getPath() + "/" + filename)).exists();
	}

	// ファイル削除。
	public static void deleteFile(Context c, String filename){
		(new File(c.getFilesDir().getPath() + "/" + filename)).delete();
		return;
	}

	// ファイルから文字列読み込み。
	public static String readStrFromFile(Context c, String filename){
		byte[] data = readByteFromFile(c, filename);
		if(data == null){
			return "";
		}
		return new String(data);
	}

	// ファイルからバイナリデータ読み込み。
	public static byte[] readByteFromFile(Context c, String filename){
		if(!doesFileExists(c, filename)){
			if(isDbg) Log.v(TAG, "file to read not found:" + filename);
			return null;
		}
		InputStream in = null;				// ファイルからの読み込みストリーム。
		ByteArrayOutputStream out = null;	// 読み込んだデータを蓄積して出力するストリーム。
		byte[] readBuffer = new byte[128];	// 一回の読み込みで得るデータを格納するバッファ。
		try{
			int size = 0;
			if(isDbg) Log.v(TAG, "start opening file:" + filename);
			in = c.openFileInput(filename);
			out = new ByteArrayOutputStream();
			while((size=in.read(readBuffer)) > 0){
				out.write(readBuffer, 0, size);
				if(isDbg) Log.v(TAG, "reading");
			}
			out.close();
			in.close();
		}catch(Exception e){
			if(out != null)try{out.close();}catch(Exception ignore){}
			if(in != null)try{in.close();}catch(Exception ignore){}
			return null;
		}
		if(isDbg) Log.v(TAG, "finish reading, len:" + out.size());
		return out.toByteArray();
	}

	// ファイルに文字列書き込み。
	public static void writeStrToFile(Context c, String filename, String strData){
		if(isDbg) Log.v(TAG, "write this str:" + strData);
		writeByteToFile(c, filename, strData.getBytes());
		return;
	}

	// ファイルにバイナリデータ書き込み。
	public static void writeByteToFile(Context c, String filename, byte[] data){
		OutputStream out = null;
		if(isDbg) Log.v(TAG, "write to file:" + filename + ", len:" + data.length);
		try{
			out = c.openFileOutput(filename, Context.MODE_PRIVATE | Context.MODE_APPEND);
			out.write(data, 0, data.length);
			out.close();
		}catch(Exception e){
			if(out != null)try{out.close();}catch(Exception ignore){}
		}
		return;
	}
}
