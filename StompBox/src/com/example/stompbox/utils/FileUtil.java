package com.example.stompbox.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.util.Log;

public class FileUtil {
	private static final String TAG = "FileUtil";
	private static final boolean isDbg = false;		// Log�̏o�͗L���B

	// �t�@�C���L���`�F�b�N�B
	public static boolean doesFileExists(Context c, String filename){
		return (new File(c.getFilesDir().getPath() + "/" + filename)).exists();
	}

	// �t�@�C���폜�B
	public static void deleteFile(Context c, String filename){
		(new File(c.getFilesDir().getPath() + "/" + filename)).delete();
		return;
	}

	// �t�@�C�����當����ǂݍ��݁B
	public static String readStrFromFile(Context c, String filename){
		byte[] data = readByteFromFile(c, filename);
		if(data == null){
			return "";
		}
		return new String(data);
	}

	// �t�@�C������o�C�i���f�[�^�ǂݍ��݁B
	public static byte[] readByteFromFile(Context c, String filename){
		if(!doesFileExists(c, filename)){
			if(isDbg) Log.v(TAG, "file to read not found:" + filename);
			return null;
		}
		InputStream in = null;				// �t�@�C������̓ǂݍ��݃X�g���[���B
		ByteArrayOutputStream out = null;	// �ǂݍ��񂾃f�[�^��~�ς��ďo�͂���X�g���[���B
		byte[] readBuffer = new byte[128];	// ���̓ǂݍ��݂œ���f�[�^���i�[����o�b�t�@�B
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

	// �t�@�C���ɕ����񏑂����݁B
	public static void writeStrToFile(Context c, String filename, String strData){
		if(isDbg) Log.v(TAG, "write this str:" + strData);
		writeByteToFile(c, filename, strData.getBytes());
		return;
	}

	// �t�@�C���Ƀo�C�i���f�[�^�������݁B
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
