package com.Lightweight.soso;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class Bookmark extends Activity {

	ArrayList<HashMap<String, Object>> history_data_list = new ArrayList<HashMap<String, Object>>();// ������ʾ��ʷ��list
	private SQLiteHelper sqliteHelper;
	private Cursor myCursor;
	private ListView history_listview;
	public String operaString = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Hidden_status();  //����״̬������
		setContentView(R.layout.page_bookmark); // ����listview����
		init(); // ����listview
	    SysApplication.getInstance().addActivity(this);     //��ֵ�ر�����activity
	}
	
	/*����״̬������*/
	public void Hidden_status()
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE); // ���ر�����
		// ����״̬��
		// ����ȫ������
		int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		// ��õ�ǰ�������
		Window window = Bookmark.this.getWindow();
		// ���õ�ǰ����Ϊȫ����ʾ
		window.setFlags(flag, flag);
	}

	/* ����listview */
	public void init() {
		sqliteHelper = new SQLiteHelper(getApplicationContext()); // ʵ��sql
		history_listview = (ListView) findViewById(R.id.history_list); // ��listview

		SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), // ���û�ͼlistview
				get_History(), R.layout.history_display_style, new String[] { "��ҳ", "��ַ" },
				new int[] { R.id.website_name, R.id.website_url });
		history_listview.setAdapter(adapter); // ���»���

		// ����ListView����Ŀ�����¼�����
		history_listview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View v, int position, long id) { // ����
				MainActivity._url = history_data_list.get(position).get("��ַ") // ��ȡ��ַ����
						.toString();
				MainActivity.if_main_activity +=1;
				// ����mainactivity
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), MainActivity.class);
				startActivity(intent);
				finish();  //�ر���ǩ
			}
		});

		// ����ListView����Ŀ�������¼�����
		history_listview.setOnItemLongClickListener(new ListItemLongClick());
		history_listview.setOnCreateContextMenuListener(new ListonCreate());

	}

	// �������������¼���
	private class ListonCreate implements OnCreateContextMenuListener {

		public void onCreateContextMenu(ContextMenu menu, View arg1, ContextMenuInfo arg2) {
			menu.add(0, 0, 0, "ɾ��");
		}

	}

	// �����¼���
	private class ListItemLongClick implements OnItemLongClickListener {

		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long arg3) {
			Log.e("tag", history_data_list.get(position).get("��ҳ").toString());
			operaString = history_data_list.get(position).get("��ҳ").toString();
			return false;
		}

	}

	/* ɾ������ */
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			// ɾ������
			Log.e("opera", operaString);
			sqliteHelper.delete_single_record(operaString);
			
			SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), // ���û�ͼlistview
					get_History(), R.layout.history_display_style, new String[] { "��ҳ", "��ַ" },
					new int[] { R.id.website_name, R.id.website_url });
			history_listview.setAdapter(adapter); // ���»���*/
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);

	}

	/* ȡ�����ݿ��е���ǩ */
	public ArrayList<HashMap<String, Object>> get_History() {
		SQLiteDatabase db = sqliteHelper.getWritableDatabase();
		myCursor = db.query(sqliteHelper.TB__HISTORY_NAME, new String[] { "name", "url" }, "isbookmark=1", null, null,
				null, "time" + " DESC");
		int url = myCursor.getColumnIndex("url");
		int name = myCursor.getColumnIndex("name");
		history_data_list.clear();
		if (myCursor.moveToFirst()) {
			do {
				HashMap<String, Object> item = new HashMap<String, Object>();
				item.put("��ҳ", myCursor.getString(name));
				item.put("��ַ", myCursor.getString(url));
				history_data_list.add(item);
			} while (myCursor.moveToNext());
		}
		myCursor.close();
		return history_data_list;
	}

}
