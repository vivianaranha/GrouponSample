package com.vivianaranha.thedealsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class DealActivity extends ActionBarActivity {
	
	protected String mURL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deal);

		Intent intent = getIntent();
		Uri dealUri = intent.getData();
		mURL = dealUri.toString();
		
		WebView webView = (WebView) findViewById(R.id.webView1);
		webView.setWebViewClient(new WebViewClient());
		webView.loadUrl(mURL);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.deal, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_share) {
			
			Intent shareIntent = new Intent(Intent.ACTION_SEND);
			shareIntent.setType("text/plain");
			shareIntent.putExtra(Intent.EXTRA_TEXT, mURL);
			startActivity(Intent.createChooser(shareIntent, getString(R.string.share_chooser_style)));
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
