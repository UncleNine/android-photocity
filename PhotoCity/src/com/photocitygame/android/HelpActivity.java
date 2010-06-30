package com.photocitygame.android;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class HelpActivity extends Activity {
	public static final String URL = "url";

    private Handler handler = new Handler();
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		String url = "file:///android_asset/howto.html";
		if (getIntent() != null && getIntent().hasExtra(URL)) {
			url = getIntent().getExtras().getString(URL);
		}
		WebView webview = (WebView)findViewById(R.id.help_web);
		
		WebSettings webSettings = webview.getSettings();
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);

        webview.addJavascriptInterface(new HelpJavaScriptInterface(), "help");

		webview.loadUrl(url);
	}
	
	 final class HelpJavaScriptInterface {
	        HelpJavaScriptInterface() {
	        }

	        /**
	         * This is not called on the UI thread. Post a runnable to invoke
	         * finish on the UI thread.
	         */
	        public void clickOnDone() {
	            handler.post(new Runnable() {
	                public void run() {
	                    finish();
	                }
	            });

	        }
	    }
}
