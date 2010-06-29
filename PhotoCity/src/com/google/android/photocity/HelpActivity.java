package com.google.android.photocity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;

public class HelpActivity extends Activity {
	public static final String URL = "url";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		String url = "file:///android_asset/howto.html";
		if (getIntent() != null && getIntent().hasExtra(URL)) {
			url = getIntent().getExtras().getString(URL);
		}
		WebView webview = (WebView)findViewById(R.id.help_web);
		webview.loadUrl(url);
		
		Button ok = (Button)findViewById(R.id.done);
		ok.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}
}
