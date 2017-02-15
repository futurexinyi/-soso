package com.Lightweight.soso;

import com.Lightweight.soso.R;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.widget.Button;

public class MainActivity extends Activity implements OnEditorActionListener {

	private EditText editText_title; // 网址栏
	private WebView wView; // 网页显示
	private Button Button; // 更多
	private Button Button_http; // 转到
	private long exitTime = 0; // 返回按钮判断
	private ProgressBar bar; // 进度条
	private SQLiteHelper sql; // 存储收藏网址数据库
	private Button bookmarks; // 书签
	private InputMethodManager imm; // 输入法隐藏
	static String _url = ""; // 书签网站链接
	static int if_main_activity = 0; // 判断是否回到主activity
	private static boolean _if = true;     //判断是否开启html5缓存

	/* 视频全屏播放 */
	private FrameLayout video_fullView; // FrameLayout布局
	private View xCustomView; // 保存点击全屏后回调方法传入的view
	private CustomViewCallback xCustomViewCallback; // 保存传出的view回调
	private myWebChromeClient xwebchromeclient; // web浏览器客户端

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Hidden_status(); // 隐藏状态标题栏
		setContentView(R.layout.activity_main);

		bind(); // 绑定控件
		Set_go(); // 加载实例化
		StartHtml5(); // 开启html5缓存加速
		Start_pr(); // 加载进度条
		DoWeb();   //打开网页
		Start_Button(); // 加载按钮等功能
		go_Html(); /* 打开书签链接*/	
		SysApplication.getInstance().addActivity(this); // 赋值关闭所有activity

	}
	
	/*打开网页*/
	public void DoWeb()
	{
		wView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH); // 提高渲染级别
		wView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.startsWith("http:") || url.startsWith("https:")) {
					return false; // 取消重定向
				} else {
					return true;
				}
			}
			
			/*延迟加载图片*/
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                    wView.getSettings().setBlockNetworkImage(true);
            }

            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                    wView.getSettings().setBlockNetworkImage(false);
            }
		});
	}

	/* 开启html5缓存加速 */
	public void StartHtml5() {
		if (_if) {
			WebSettings settings_item_hcon = wView.getSettings(); // 设置webview对象
			settings_item_hcon.setAppCacheEnabled(true); // 开启htm5缓存
			settings_item_hcon.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); // 只要本地有，无论是否过期，都使用缓存中的数据
			Toast.makeText(getApplicationContext(), "极速模式启动", Toast.LENGTH_SHORT).show();
			_if = false;
		}
	}

	/* 隐藏状态标题栏 */
	public void Hidden_status() {
		/* 隐藏状态栏 */
		// 隐藏标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 隐藏状态栏
		// 定义全屏参数
		int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		// 获得当前窗体对象
		Window window = MainActivity.this.getWindow();
		// 设置当前窗体为全屏显示
		window.setFlags(flag, flag);
	}

	/* 绑定控件 */
	public void bind() {
		editText_title = (EditText) findViewById(R.id.editText_title);
		Button_http = (Button) findViewById(R.id.Button_http);
		Button = (Button) findViewById(R.id.Button);
		bar = (ProgressBar) findViewById(R.id.myProgressBar);
		wView = (WebView) findViewById(R.id.wView);
		bookmarks = (Button) findViewById(R.id.bookmarks);
		video_fullView = (FrameLayout) findViewById(R.id.video_fullView);

		/* edittext搜索和转到监听 */
		editText_title.setOnEditorActionListener(this);
	}

	/* 打开书签链接 */
	public void go_Html() {

		/* 判断是否打开网页，打开则读取网页打开 */
		Intent intent = getIntent();
		String action = intent.getAction();
		if (intent.ACTION_VIEW.equals(action)) {
			wView.loadUrl(intent.getDataString());
		} 
		else
		{
			wView.loadUrl(_url);
		}
		_url="";
	}

	/* 加载进度条 */
	public void Start_pr() {
		wView.setWebChromeClient(new myWebChromeClient() {
			/* 加载进度条 */
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 100) {
					bar.setVisibility(View.INVISIBLE);
				} else {
					if (View.INVISIBLE == bar.getVisibility()) {
						bar.setVisibility(View.VISIBLE);
					}
					bar.setProgress(newProgress);
				}
				super.onProgressChanged(view, newProgress);
			}
		});

	}

	/* 加载按钮 */
	public void Start_Button() {
		/* 搜索和转到网页 */
		Button_http.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (editText_title.getText().toString().contains("http://")&&!
						(editText_title.getText().toString().contains("https://"))) {
					wView.loadUrl(editText_title.getText().toString());
				}
				else if (editText_title.getText().toString().contains("https://")) {
					wView.loadUrl(editText_title.getText().toString());
				}
				else if (editText_title.getText().toString().contains("www.")) {
					wView.loadUrl("http://" + editText_title.getText().toString());
				} 
				else
				{
					wView.loadUrl("http://www.baidu.com/s?wd=" + editText_title.getText().toString());
				}
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); // 隐藏输入法
				wView.requestFocus();  //webview获取焦点
			}
		});
		
		
		/* 点击文本框自动选中所有内容 */
		editText_title.setSelectAllOnFocus(true);
		

		/* 启动书签 */
		bookmarks.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent_bookmark = new Intent();
				intent_bookmark.setClass(getApplicationContext(), Bookmark.class); // 启动
				startActivity(intent_bookmark);
			}
		});

		wView.getSettings().setLoadsImagesAutomatically(false); // 设置自动加载图片
		/* 注册菜单 */
		registerForContextMenu(Button);
	}

	/* 加载实例化 */
	public void Set_go() {
		/* 输入法隐藏 */
		imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		sql = new SQLiteHelper(this); // 实例化sql类

		/* 视频全屏播放 */
		xwebchromeclient = new myWebChromeClient(); // 实例化web浏览器客户端
		wView.setWebChromeClient(xwebchromeclient); // 设置web浏览器客户端
	}

	/* 重写菜单 */
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		MenuInflater inflator = new MenuInflater(this); // 实例化菜单对象
		inflator.inflate(R.menu.menu_xml, menu); // 加载菜单布局
		super.onCreateContextMenu(menu, v, menuInfo);

	}

	/* EditText监听 */
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		doWhichOperation(actionId); // 当点击传入actionId判断
		return true;
	}

	/* 判断操作输入法 */
	private void doWhichOperation(int actionId) {
		switch (actionId) {
		case EditorInfo.IME_ACTION_SEARCH:
			if (editText_title.getText().toString().contains("http://")&&!
					(editText_title.getText().toString().contains("https://"))) {
				wView.loadUrl(editText_title.getText().toString());
			}
			else if (editText_title.getText().toString().contains("https://")) {
				wView.loadUrl(editText_title.getText().toString());
			}
			else if (editText_title.getText().toString().contains("www.")) {
				wView.loadUrl("http://" + editText_title.getText().toString());
			} else {
				wView.loadUrl("http://www.baidu.com/s?wd=" + editText_title.getText().toString());
			}
			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); // 隐藏输入法
			wView.requestFocus();  //webview获取焦点
			break;
		default:
			break;
		}
	}

	/* 点击触发 */
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		/* 开启下载 */
		case R.id.item_get:
			wView.setDownloadListener(new DownloadListener() {
				@Override
				public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
						long contentLength) {
					if (url != null && url.startsWith("http://"))
						startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
				}
			});
			Toast.makeText(getApplicationContext(), "开启文件下载!开启后将持续监听无法停止!", Toast.LENGTH_SHORT).show();
			break;

		/* 开启图片 */
		case R.id.item_imageon:
			wView.getSettings().setLoadsImagesAutomatically(true); // 设置自动加载图片
			Toast.makeText(getApplicationContext(), "开启图片!", Toast.LENGTH_SHORT).show();
			break;
		/* 隐藏图片 */
		case R.id.item_imageoff:
			wView.getSettings().setLoadsImagesAutomatically(false); // 设置自动加载图片
			Toast.makeText(getApplicationContext(), "关闭图片!", Toast.LENGTH_SHORT).show();
			break;

		/* 开启脚本 */
		case R.id.item_json:
			wView.getSettings().setJavaScriptEnabled(true);
			Toast.makeText(getApplicationContext(), "开启脚本,请重新进入页面!", Toast.LENGTH_SHORT).show();
			break;
		/* 停用脚本 */
		case R.id.item_jsoff:
			wView.getSettings().setJavaScriptEnabled(false);
			Toast.makeText(getApplicationContext(), "停用脚本!", Toast.LENGTH_SHORT).show();
			break;

		/* 开启缩放 */
		case R.id.item_sfon:
			WebSettings settings_item_sfon = wView.getSettings(); // 设置webview对象
			settings_item_sfon.setUseWideViewPort(true);// 设定支持viewport
			settings_item_sfon.setLoadWithOverviewMode(true); // 自适应屏幕
			settings_item_sfon.setBuiltInZoomControls(true);
			settings_item_sfon.setDisplayZoomControls(false);
			settings_item_sfon.setSupportZoom(true);// 设定支持缩放
			settings_item_sfon.setDisplayZoomControls(false); // 取消缩放控件
			Toast.makeText(getApplicationContext(), "缩放开启!", Toast.LENGTH_SHORT).show();
			break;

		/* 关闭缩放 */
		case R.id.item_sfoff:
			WebSettings settings_item_sfoff = wView.getSettings(); // 设置webview对象
			settings_item_sfoff.setUseWideViewPort(false);// 设定支持viewport
			settings_item_sfoff.setLoadWithOverviewMode(false); // 自适应屏幕
			settings_item_sfoff.setBuiltInZoomControls(false);
			settings_item_sfoff.setDisplayZoomControls(false);
			settings_item_sfoff.setSupportZoom(false);// 设定支持缩放
			Toast.makeText(getApplicationContext(), "缩放关闭!", Toast.LENGTH_SHORT).show();
			break;

		/* 复制链接 */
		case R.id.item_geturl:
			ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
			cm.setText(wView.getUrl());
			Toast.makeText(getApplicationContext(), "链接已复制!", Toast.LENGTH_SHORT).show();
			break;

		/* 开启html5网页支持 */
		case R.id.item_html0n:
			WebSettings settings_item_html0n = wView.getSettings(); // 设置webview对象
			settings_item_html0n.setDatabaseEnabled(true); // 开启数据库
			settings_item_html0n.setDomStorageEnabled(true);
			Toast.makeText(getApplicationContext(), "开启html5网页支持!", Toast.LENGTH_SHORT).show();
			break;

		/* 停用html5网页支持 */
		case R.id.item_html0ff:
			WebSettings settings_item_html0ff = wView.getSettings(); // 设置webview对象
			settings_item_html0ff.setDatabaseEnabled(false);
			settings_item_html0ff.setDomStorageEnabled(false);
			Toast.makeText(getApplicationContext(), "停用html5网页支持!", Toast.LENGTH_SHORT).show();
			break;
		/* 收藏 */
		case R.id.item_bookmarks:
			sql.add_history(getApplicationContext(), wView.getTitle(), wView.getUrl(), 1); // 添加url到数据库
			Toast.makeText(getApplicationContext(), "已加入收藏!", Toast.LENGTH_SHORT).show();
			break;
		/* 退出 */
		case R.id.item_exit:
			wView.clearCache(true); // 清空缓存
			finish(); // 结束activity
			// 关闭整个程序
			SysApplication.getInstance().exit();
			break;
		/* 刷新 */
		case R.id.item_reload:
			wView.reload();
			Toast.makeText(getApplicationContext(), "成功刷新!", Toast.LENGTH_SHORT).show();
			break;
		}
		return true;
	}

	/* 视频全屏播放 */
	public class myWebChromeClient extends WebChromeClient {

		// 播放网络视频时全屏会被调用的方法
		public void onShowCustomView(View view, CustomViewCallback callback) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // 设置
			wView.setVisibility(View.INVISIBLE);
			// 如果一个视图已经存在，那么立刻终止并新建一个
			if (xCustomView != null) { // 如果不是空就把他变为空
				callback.onCustomViewHidden(); // 隐藏视图
				return;
			}
			video_fullView.addView(view); // 将全屏后获取到的视图添加到全屏控件去
			xCustomView = view; // 自定义视图赋值
			xCustomViewCallback = callback; // 保存自定义视图回调

			/* 隐藏控件 */
			editText_title.setVisibility(View.GONE);
			Button_http.setVisibility(View.GONE);
			bookmarks.setVisibility(View.GONE);
			Button.setVisibility(View.GONE);

			video_fullView.setVisibility(View.VISIBLE); // 加载视图
		}

		// 视频播放退出全屏会被调用的
		@Override
		public void onHideCustomView() {
			if (xCustomView == null) // 不是全屏播放状态
				return;

			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			xCustomView.setVisibility(View.GONE); // 隐藏视图回调
			video_fullView.removeView(xCustomView); // 删除存储的视图回调
			xCustomView = null; // 置空
			video_fullView.setVisibility(View.GONE); // 隐藏视图
			xCustomViewCallback.onCustomViewHidden();

			/* 显示控件 */
			editText_title.setVisibility(View.VISIBLE);
			Button_http.setVisibility(View.VISIBLE);
			bookmarks.setVisibility(View.VISIBLE);
			Button.setVisibility(View.VISIBLE);

			wView.setVisibility(View.VISIBLE);
		}

	}

	/**
	 * 全屏时按返加键执行退出全屏方法
	 */
	public void hideCustomView() {
		xwebchromeclient.onHideCustomView();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@Override
	protected void onResume() { // 在oncreat后执行
		super.onResume();
		super.onResume();
		wView.onResume();
		wView.resumeTimers();

		/**
		 * 设置为横屏
		 */
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) { // 横屏竖屏之间切换
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

	@Override
	protected void onPause() { // 按下暂停
		super.onPause();
		wView.onPause();
		wView.pauseTimers();
	}

	@Override
	public void onBackPressed() {
		if (wView.canGoBack()) {
			wView.goBack();
		} else {
			if (if_main_activity <= 0) { // 判断是否是主activity
				if ((System.currentTimeMillis() - exitTime) > 2000) {
					Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
					exitTime = System.currentTimeMillis();
				} else {
					finish(); // 结束activity
				}
			} else {
				if_main_activity -= 1; // 此时返回主activity
				finish(); // 结束activity
			}

		}
	}

}
