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

	ArrayList<HashMap<String, Object>> history_data_list = new ArrayList<HashMap<String, Object>>();// 用来显示历史的list
	private SQLiteHelper sqliteHelper;
	private Cursor myCursor;
	private ListView history_listview;
	public String operaString = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Hidden_status();  //隐藏状态标题栏
		setContentView(R.layout.page_bookmark); // 加载listview布局
		init(); // 绘制listview
	    SysApplication.getInstance().addActivity(this);     //赋值关闭所有activity
	}
	
	/*隐藏状态标题栏*/
	public void Hidden_status()
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 隐藏标题栏
		// 隐藏状态栏
		// 定义全屏参数
		int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		// 获得当前窗体对象
		Window window = Bookmark.this.getWindow();
		// 设置当前窗体为全屏显示
		window.setFlags(flag, flag);
	}

	/* 绘制listview */
	public void init() {
		sqliteHelper = new SQLiteHelper(getApplicationContext()); // 实例sql
		history_listview = (ListView) findViewById(R.id.history_list); // 绑定listview

		SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), // 设置绘图listview
				get_History(), R.layout.history_display_style, new String[] { "网页", "网址" },
				new int[] { R.id.website_name, R.id.website_url });
		history_listview.setAdapter(adapter); // 更新绘制

		// 设置ListView的项目按下事件监听
		history_listview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View v, int position, long id) { // 按下
				MainActivity._url = history_data_list.get(position).get("网址") // 获取网址传入
						.toString();
				MainActivity.if_main_activity +=1;
				// 启动mainactivity
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), MainActivity.class);
				startActivity(intent);
				finish();  //关闭书签
			}
		});

		// 设置ListView的项目长按下事件监听
		history_listview.setOnItemLongClickListener(new ListItemLongClick());
		history_listview.setOnCreateContextMenuListener(new ListonCreate());

	}

	// 长按弹出操作事件类
	private class ListonCreate implements OnCreateContextMenuListener {

		public void onCreateContextMenu(ContextMenu menu, View arg1, ContextMenuInfo arg2) {
			menu.add(0, 0, 0, "删除");
		}

	}

	// 长按事件类
	private class ListItemLongClick implements OnItemLongClickListener {

		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long arg3) {
			Log.e("tag", history_data_list.get(position).get("网页").toString());
			operaString = history_data_list.get(position).get("网页").toString();
			return false;
		}

	}

	/* 删除操作 */
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			// 删除操作
			Log.e("opera", operaString);
			sqliteHelper.delete_single_record(operaString);
			
			SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), // 设置绘图listview
					get_History(), R.layout.history_display_style, new String[] { "网页", "网址" },
					new int[] { R.id.website_name, R.id.website_url });
			history_listview.setAdapter(adapter); // 更新绘制*/
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);

	}

	/* 取得数据库中的书签 */
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
				item.put("网页", myCursor.getString(name));
				item.put("网址", myCursor.getString(url));
				history_data_list.add(item);
			} while (myCursor.moveToNext());
		}
		myCursor.close();
		return history_data_list;
	}

}
