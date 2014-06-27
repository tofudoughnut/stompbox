package com.example.stompbox.utils;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.util.Log;

public class DbUtil extends SQLiteOpenHelper {
	private static final String TAG = "DbUtil";
    private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "stompbox.db";
	private static final String[] TABLE_NAME = {"TABLE1", "TABLE2"};	// ����CREATE_SQL�Ɨv�f�ԍ������킹�鎖�B
	private static final String[] CREATE_SQL = {
		"CREATE TABLE TABLE1 ("
			+"_id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+"name TEXT NOT NULL, "
			+"tel TEXT NOT NULL, "
			+"email TEXT NOT NULL, "
			+"sex INTEGER, "
			+"address TEXT, "
			+"photo BLOB, "
			+"update_date TEXT NOT NULL"
		+"); ",
		"CREATE TABLE TABLE2 ("
			+ "_id INTEGER PRIMARY KEY, "
			+ "update_date TEXT NOT NULL, "
			+ "nid INTEGER NOT NULL, "
			+ "n_update_date TEXT NOT NULL"
		+ "); " 
	};

    public static SQLiteDatabase db = null;	// �ǂ�Activity����ł��Ăт₷���悤�ɂ����Œ�`�B

    // �R���X�g���N�^�B
    public DbUtil(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.v(TAG, "db ready");
        if(db != null) db.close();
        db = this.getWritableDatabase();	// Activity�P�ʂŊJ���ĕ���icleanup()�j�B
    }
    // ����DB�\�z�B
    public void onCreate(SQLiteDatabase db) {
        Log.v(TAG, "create table");
    	for(int i = 0; i < CREATE_SQL.length; i++) {
    		db.execSQL(CREATE_SQL[i]);
    	}
    }
    // DB�o�[�W����������Ă����ꍇ��DB���č\�z�B����͋��炭�ӎ�����K�v�����B
	public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
		Log.v(TAG, "Upgrade db from version " + oldVer + " to " + newVer + ", which will destroy all old data");
		for(int i = 0; i < TABLE_NAME.length; i++){
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME[i] + ";");
		}
		onCreate(db);
	}
	public void cleanup(){
		if(db != null) db.close();
		db = null;
		this.close();
	}
	// �v���t�B�[���L���i������Ԃ��j�B
	public static int getProfileCount(){
		int recNo = 0;
		Cursor c = db.rawQuery("select count(*) from USER_PROFILE", null);
		if(c != null){
			c.moveToFirst();	// �P���R�[�h���������O��B
			recNo = c.getInt(0);
			c.close();
		}
		return recNo;
	}
	public static HashMap<String, String> getProfile(){
		HashMap<String, String> profile = null;
		Cursor c = db.rawQuery("select name,tel,email,sex,address,photo,update_date,ext_update_date,ext_n_update_date from USER_PROFILE", null);
		if(c != null){
			if(c.getCount() > 0){
				c.moveToFirst();	// �P���R�[�h���������O��B
				profile = new HashMap<String, String>();
				profile.put("name", c.getString(0));
				profile.put("tel", c.getString(1));
				profile.put("email", c.getString(2));
				profile.put("sex", (c.getLong(3)==0)?"male":"female");
				profile.put("address", c.getString(4));
			}
			c.close();
		}
		return profile;
	}
	public static boolean delProfile(){
		boolean isProcessOk = false;
		try{
			db.execSQL("delete from USER_PROFILE");
			isProcessOk = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return isProcessOk;
	}
	// �v���t�B�[����EXT���X�V�B���R�[�h�͑��݂���O��Ŏ��s����B
	public static boolean updProfileExt(String extUpdateDate, String extNupdateDate){
		if(getProfileCount() > 0){	// ���R�[�h�����݂��遨update�B
			Log.v(TAG, "EXTUPDATE, upd:"+extUpdateDate+", n_upd:"+extNupdateDate);
			try {
				SQLiteStatement stmt = db.compileStatement("UPDATE USER_PROFILE set ext_update_date=?, ext_n_update_date=?");
				int idx = 0;
				stmt.bindString(++idx, extUpdateDate);
				stmt.bindString(++idx, extNupdateDate);
				stmt.execute();
				stmt.close();
				return true;
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return false;
	}
	// �T�[�o����󂯎�����X�VID��ǉ��B
	public static boolean insExtData(int id, String update_date, int nid, String n_update_date){
		boolean isProcessOk = false;
		try {
			Log.v(TAG, "insert ext, _id:" + id + ", upd:" + update_date + ", nid:" + nid + ", n_upd:" + n_update_date);
			SQLiteStatement stmt = db.compileStatement("INSERT INTO EXT_PHONE_BOOK (_id, update_date, nid, n_update_date) values (?,?,?,?)");
			int idx = 0;
			stmt.bindLong(++idx, id);
			stmt.bindString(++idx, update_date);
			stmt.bindLong(++idx, nid);
			stmt.bindString(++idx, n_update_date);
			stmt.execute();
			stmt.close();
			isProcessOk = true;
		}catch(Exception e){
			Log.v(TAG, "err.");
			e.printStackTrace();
		}
		Log.v(TAG, "inserted ext record");
		return isProcessOk;
	}
	// �T�[�o����󂯎����n_update_date���X�V�B
	public static boolean updExtData(int id, String update_date, int nid, String n_update_date){
		boolean isProcessOk = false;
		try {
			SQLiteStatement stmt = db.compileStatement("UPDATE EXT_PHONE_BOOK set update_date=?, nid=?, n_update_date=? where _id=?");
			int idx = 0;
			stmt.bindString(++idx, update_date);	// PHONE_BOOK�̒l�B
			stmt.bindLong(++idx, nid);				// �T�[�o����擾�����l�B
			stmt.bindString(++idx, n_update_date);	// �T�[�o����擾�����l�B
			stmt.bindLong(++idx, id);	// �L�[�ƂȂ�_id�l�B
			stmt.execute();
			stmt.close();
			isProcessOk = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return isProcessOk;
	}
	// ���������� 1���R�[�h�폜�B
	public static boolean delTelData(int targetId){
		boolean isProcessOk = false;
		try{
			db.execSQL("delete from PHONE_BOOK where _id = '" + targetId + "'");
			isProcessOk = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return isProcessOk;
	}
	public static boolean delAllPhBookData(){
		boolean isProcessOk = false;
		try{
			db.execSQL("delete from PHONE_BOOK");
			isProcessOk = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return isProcessOk;
	}
	public static boolean delAllExtData(){
		boolean isProcessOk = false;
		try{
			db.execSQL("delete from EXT_PHONE_BOOK");
			isProcessOk = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return isProcessOk;
	}
	// EXT_PHONE_BOOK���R�[�h�폜�F_id���L�[�B
	public static boolean delExtDataById(int targetId){
		boolean isProcessOk = false;
		try{
			db.execSQL("delete from EXT_PHONE_BOOK where _id = '" + targetId + "'");
			isProcessOk = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return isProcessOk;
	}
	// EXT_PHONE_BOOK���R�[�h�폜�Fnid���L�[�B
	public static boolean delExtDataByNid(int targetId){
		boolean isProcessOk = false;
		try{
			db.execSQL("delete from EXT_PHONE_BOOK where nid = '" + targetId + "'");
			isProcessOk = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return isProcessOk;
	}
	// EXT_PHONE_BOOK��PHONE_BOOK���R�[�h�폜�Fnid���L�[�B
	public static boolean delPhoneAndExtDataByNid(int targetId){
		boolean isProcessOk = false;
		try{
			Cursor c = db.rawQuery("select _id from EXT_PHONE_BOOK where nid = ?", new String[]{Integer.toString(targetId)});
			c.moveToFirst();
			if(c.getCount() > 0){	// 1���R�[�h���������O�񁨃��[�v�����B
				int delId = c.getInt(0);	// PHONE_BOOK���ɊY�����R�[�h����������폜����B
				db.execSQL("delete from PHONE_BOOK where _id = '" + delId + "'");
			}
			c.close();
			db.execSQL("delete from EXT_PHONE_BOOK where nid = '" + targetId + "'");
			isProcessOk = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return isProcessOk;
	}
	// �R���^�N�g�f�[�^���擾���A���X�g�ԋp�B
	public static ArrayList<HashMap<String, String>> getContacts(Activity activity){
		ArrayList<HashMap<String, String>> contactsList = new ArrayList<HashMap<String, String>>();
		// �R���^�N�g�f�[�^�i�g�b�v�K�w�j�擾�p�̃J�[�\���B
    	Cursor c = activity.getContentResolver().query(
    		Contacts.CONTENT_URI,
    		new String[]{
				Contacts._ID, 
				ContactsContract.Contacts.DISPLAY_NAME, 
				ContactsContract.Contacts.HAS_PHONE_NUMBER, 
				ContactsContract.Contacts.PHOTO_ID
    		},
    		null, //selection(String): column = ?�̂悤�Ȏw��B
            null, //selectionArgs(String[]): ��L?�ɓ��Ă͂߂�l�B
            Contacts._ID  //sortOrder(String)
    	);
//    	c.moveToFirst();	// �ꉞ�����߂��B���������moveToNext()��2���R�[�h�ڂ���ɂȂ��Ă��܂��ido�`while�Ȃ�ok�j�B
    	if(c != null){
    		if(c.getCount() > 0){
	        	// �R���^�N�g�̐��������[�v�B
	            while (c.moveToNext()) {
	            	int clmIdx = 0;
	            	long contactsId = c.getLong(clmIdx++);
	            	String fullName = c.getString(clmIdx++);
	            	int hasPhone = c.getInt(clmIdx++);
	            	String phoneNo = null;
	            	String photoId = c.getString(clmIdx++);
	            	byte[] photoBytes = null;
	            	if(hasPhone > 0){
	            		phoneNo = getPhoneNumber(activity, contactsId);
	            	}
	            	String email = getEmailAddress(activity, contactsId);
	            	String address = getAddress(activity, contactsId);
	            	if(photoId != null){
	            		photoBytes = getPhoto(activity, contactsId, photoId);
	            	}
	            	HashMap<String, String> data = new HashMap<String, String>();
	            	data.put("contactsId", Long.toString(contactsId));
	            	data.put("fullName", fullName);
	            	data.put("phoneNo", phoneNo);
	            	data.put("email", email);
	            	data.put("address", address);
	            	contactsList.add(data);
	            }
    		}
    		c.close();
    	}
    	return contactsList;
	}
	// �R���^�N�gID�Ǝʐ^ID����ʐ^�f�[�^���擾�B
    public static byte[] getPhoto(final Activity activity, long contactsId, String photoId){
		byte[] byteData = null;
    	Cursor c = activity.getContentResolver().query(
    		ContactsContract.Data.CONTENT_URI,
    		new String[]{
    			ContactsContract.CommonDataKinds.Photo.PHOTO
    		},
    		ContactsContract.CommonDataKinds.Photo.PHOTO_ID + "=?",
    		new String[]{photoId}, 
    		null
    	);
    	if(c != null){
    		if(c.getCount() > 0){
        		c.moveToFirst();	// �����R�t���Ă͂��Ȃ��O��B������ɂ���擪���R�[�h���g���B
        		byteData = c.getBlob(0);
    		}
    		c.close();
    	}
   		return byteData;
    }
	// �R���^�N�gID����d�b�ԍ��f�[�^���擾�B
    public static String getPhoneNumber(final Activity activity, long contactsId){
		String str = "";
    	Cursor c = activity.getContentResolver().query(
    		ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
    		new String[]{
    			ContactsContract.CommonDataKinds.Phone.NUMBER
    		},
    		ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
    		new String[]{Long.toString(contactsId)}, 
    		null
    	);
    	if(c != null){
    		if(c.getCount() > 0){
        		c.moveToFirst();	// �����R�t���Ă͂��Ȃ��O��B������ɂ���擪���R�[�h���g���B
        		str = c.getString(0);
    		}
    		c.close();
    	}
   		return str;
    }
	// �R���^�N�gID���烁�[���A�h���X���擾�B
    public static String getEmailAddress(final Activity activity, long contactsId){
		String str = "";
    	Cursor c = activity.getContentResolver().query(
    		ContactsContract.CommonDataKinds.Email.CONTENT_URI,
    		new String[]{
    			ContactsContract.CommonDataKinds.Email.DATA, 
    		},
    		ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?",
    		new String[]{Long.toString(contactsId)}, 
    		null
    	);
    	if(c != null){
    		if(c.getCount() > 0){
        		c.moveToFirst();	// �����R�t���Ă͂��Ȃ��O��B������ɂ���擪���R�[�h���g���B
        		str = c.getString(0);
    		}
    		c.close();
    	}
		return str;
    }
	// �R���^�N�gID����Z���f�[�^���擾�B
    public static String getAddress(final Activity activity, long contactsId){
		String str = "";
    	Cursor c = activity.getContentResolver().query(
    		ContactsContract.Data.CONTENT_URI,
    		new String[]{
    			ContactsContract.CommonDataKinds.StructuredPostal.POBOX, 
    			ContactsContract.CommonDataKinds.StructuredPostal.STREET, 
    			ContactsContract.CommonDataKinds.StructuredPostal.CITY, 
    			ContactsContract.CommonDataKinds.StructuredPostal.REGION, 
    			ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE, 
    			ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY, 
    			ContactsContract.CommonDataKinds.StructuredPostal.TYPE
    		},
    		ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=? and " + ContactsContract.Data.MIMETYPE + "=?",
    		new String[]{
    			Long.toString(contactsId), 
    			ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE
    		}, 
    		null
    	);
    	if(c != null){
    		if(c.getCount() > 0){
        		c.moveToFirst();	// �����R�t���Ă͂��Ȃ��O��B������ɂ���擪���R�[�h���g���B
        		int clmIdx = 0;
        		String pobox = c.getString(clmIdx++);
        		String street = c.getString(clmIdx++);
        		String city = c.getString(clmIdx++);
        		String region = c.getString(clmIdx++);
        		String postcode = c.getString(clmIdx++);
        		String country = c.getString(clmIdx++);
        		String type = c.getString(clmIdx++);
//        		return pobox+","+street+","+city+","+region+","+postcode+","+country+","+type;
        		str = 
    				 ((postcode==null)?"":postcode)
        			+((country==null)?"":country)
        			+((region==null)?"":region)
        			+((city==null)?"":city)
        			+((street==null)?"":street)
        		;
    		}
    		c.close();
    	}
   		return str;
    }
}
