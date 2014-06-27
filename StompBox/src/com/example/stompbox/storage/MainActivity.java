package com.example.stompbox.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.stompbox.R;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainActivity extends Activity {
	private EditText input;
	private Button btn;
	private ListView list;
	private SQLiteDatabase db = null;
	private List<Map<String, String>> dataList = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.storage_activity_main);
		input = (EditText)findViewById(R.id.editText1);
		btn = (Button)findViewById(R.id.button1);
		list = (ListView)findViewById(R.id.listView1);
		db = new DatabaseHelper(this).getWritableDatabase();
		btn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				ContentValues vals = new ContentValues();
				vals.put("strValue", input.getText().toString());
				db.insert("table1", null, vals);
				updateList();
			}
		});
		list.setOnItemLongClickListener(new OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				db.delete("table1", "_id = ?", new String[]{dataList.get(position).get("firstLine")});
				updateList();
				Toast.makeText(MainActivity.this, "削除しました、pos:" + position, Toast.LENGTH_SHORT).show();
				return false;
			}
		});
		updateList();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(db != null) db.close();
	}
	private void updateList(){
		// データベースの値を格納するリストを用意。
		dataList = new ArrayList<Map<String, String>>();
		// 画面上の一覧を更新するためにデータベースから値を取得する。
		Cursor cursor = db.query("table1", 
			new String[]{"_id", "strValue"},	// 取得する列。
			null,	// where 
			null, 	// parameter
			null, 	// group by
			null,	// having
			"_id asc", 	// order by
			null	// limit
		);
		if(cursor.moveToFirst()){
			// カーソルを先頭に移動し、レコードの数だけループ。
			do{
				int recordId = cursor.getInt(0);
				String strVal = cursor.getString(1);
				// ListViewのAdapterにあてはめるためにMapに格納する。
				Map<String, String> data = new HashMap<String, String>();
				// 1行目はレコードID。
				data.put("firstLine", Integer.toString(recordId));
				// 2行目は暗号化したデータ。
				data.put("secondLine", strVal);
				dataList.add(data);
			}while(cursor.moveToNext());
		}
		cursor.close();	// カーソルは必ず閉じる。
		SimpleAdapter adapter = new SimpleAdapter(this, 
			dataList, 
			android.R.layout.simple_list_item_2,
			new String[]{"firstLine", "secondLine"}, 
			new int[]{android.R.id.text1, android.R.id.text2}
		);
		list.setAdapter(adapter);
	}
}
