package com.Lightweight.soso;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {

	public static String DB_NAME = "History.db";// ���ݿ����ƣ�����Ϊ�����еĶ���Ϊ��¼�����bookmark�ı�ǩ��Ϊtrueʱ��Ϊ��ǩ������������ͨ����ʷ��¼
	public String TB__HISTORY_NAME = "allHistory";// ����-��ʷ��¼����ǩ
	private static SQLiteHelper instance = null;   //���ݿ����
	private Cursor temp_cursor;   //��ѯ

	public static SQLiteHelper getInstance(Context context) {   //���������Ķ�����ʵ��
		if (instance == null) {   //���Ϊ��
			instance = new SQLiteHelper(context);    //Ĭ�ϸ�ֵ
		}
		return instance;
	}

	public SQLiteHelper(Context context) {   //���췽��,����������
		super(context, DB_NAME, null, 1);    //��������,������,���ݿ�汾
	}

	@Override
	public void onCreate(SQLiteDatabase db) {    //�������ݿ����ʱ���Զ�����÷���
		// ����Ĭ�ϵ�allHistory��   ��ʷ��¼
		db.execSQL("create table " + TB__HISTORY_NAME + " ( "    //ִ������
				+ "name" + " varchar, " + "url"
				+ " varchar, " + "isbookmark" + " integer, "
				+ "time" + " integer)");
	}

	/*
	 * public void createTable(SQLiteDatabase db, String table) {
	 * db.execSQL("create table " + table + " ( " + HistoryBean.NAME +
	 * " varchar, " + HistoryBean.URL + " varchar, " + HistoryBean.ISBOOKMARK +
	 * " integer, " + HistoryBean.TIME + " integer)"); }
	 */

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		/**
		37          * 1����һ�δ������ݿ��ʱ���������������
		38          * 2��������ݺ��ٴ�����(�൱�ڵ�һ�δ���)�������������
		39          * 3�����ݿ��Ѿ����ڣ����Ұ汾���ߵ�ʱ����������Ż����
		40          */
	}

	// �����ʷ��¼--isbookmark=0��ʾ����ǩ����ͨ��ʷ��¼��bookmark=1��ʾ��ǩ
	public void add_history(Context context, String name, String url,
			int isbookmark) {
		String SQL = null;
		String TIP = null;
		int time = (int) Math.floor(System.currentTimeMillis() / 1000);  //ȡ�õ�ǰʱ��
		SQLiteDatabase db = this.getWritableDatabase();   //����������ݿ�
		temp_cursor = db.rawQuery("select * from " + TB__HISTORY_NAME    //��ѯ
				+ " where name=" + "'" + name + "';", null);
		if (temp_cursor.moveToFirst()) {   //�ж��Ƿ�Ϊ��
			SQL = "update " + TB__HISTORY_NAME + " set " + "time"   //����ֵ
					+ "=" + time + "," + "isbookmark" + "="
					+ isbookmark + " where name=" + "'" + name + "';";
			TIP = "update";
		} else {
			// ���ʣ�����������������
			SQL = "insert into  " + TB__HISTORY_NAME + "(" + "time"    //����
					+ "," + "name" + "," + "url" + ","
					+ "isbookmark" + ")" + "values(" + time + ",'"
					+ name + "','" + url + "'," + isbookmark + ");";
			TIP = "insert";
		}
		try {
			db.execSQL(SQL);      //ִ������
			//Toast.makeText(context, TIP + "�˼�¼", Toast.LENGTH_LONG).show();
			Log.e("sqlite", TIP + "�˼�¼");
		} catch (SQLException e) {
			//Toast.makeText(context, TIP + "��¼����", Toast.LENGTH_LONG).show();
			Log.e("splite", TIP + "�˼�¼");
			return;
		}
	}
public void delete_single_record(String name){    //ɾ��
	String SQL = "delete from " + TB__HISTORY_NAME + " where name=" +"'" + name + "'";
	SQLiteDatabase dbHelper = this.getWritableDatabase();  //����������ݿ�
	try{
		dbHelper.execSQL(SQL);   //ִ������
		Log.e("delete_single_record", "success");
	}catch(Exception e){
		Log.e("delete_single_record", "failed");
	}
}


}
