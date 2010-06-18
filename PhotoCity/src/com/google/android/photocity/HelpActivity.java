package com.google.android.photocity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class HelpActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		WebView webview = (WebView)findViewById(R.id.help_web);
		webview.loadUrl("file:///android_asset/howto.html");
	}
}
