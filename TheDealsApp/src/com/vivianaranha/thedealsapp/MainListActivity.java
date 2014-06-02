package com.vivianaranha.thedealsapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainListActivity extends ListActivity {

	private static final String KEY_TITLE = "shortAnnouncementTitle";
	private static final String KEY_DESC = "announcementTitle";

	public static final String TAG = MainListActivity.class.getSimpleName();
	protected JSONObject mDealData;
	protected ProgressBar mProgressBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_list);
		
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
		
		if(isNetworkAvailable()){
			mProgressBar.setVisibility(View.VISIBLE);
			GetDealsTask getDealsTask = new GetDealsTask();
			getDealsTask.execute();
		} else {
			Toast.makeText(this, "Network UnAvailable!!", Toast.LENGTH_LONG).show();
		}
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		boolean isAvailable = false;
		if (networkInfo != null && networkInfo.isConnected()) {
			isAvailable = true;
		}
		return isAvailable;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		try {
			JSONArray jsonDeals = mDealData.getJSONArray("deals");
			JSONObject jsonDeal = jsonDeals.getJSONObject(position);
			String dealURL = jsonDeal.getString("dealUrl");
			
			Intent intent = new Intent(this, DealActivity.class);
			intent.setData(Uri.parse(dealURL));
			startActivity(intent);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void handleDealsResponse() {
		mProgressBar.setVisibility(View.INVISIBLE);
		if(mDealData == null){
			updateDisplayForError();
		} else {
			JSONArray jsonDeals;
			try {
				jsonDeals = mDealData.getJSONArray("deals");
				ArrayList<HashMap<String, String>> dealArray = new ArrayList<HashMap<String, String>>();
				for(int i = 0; i<jsonDeals.length();i++){
					JSONObject deal = jsonDeals.getJSONObject(i);
					String title = deal.getString(KEY_TITLE);
					title = Html.fromHtml(title).toString();
					
					String desc = deal.getString(KEY_DESC);
					desc = Html.fromHtml(desc).toString();
					
					HashMap<String, String> dealValue = new HashMap<String, String>();
					dealValue.put(KEY_TITLE, title);
					dealValue.put(KEY_DESC, desc);
					
					dealArray.add(dealValue);

				}
				
				String[] keys = {KEY_TITLE, KEY_DESC};
				int[] ids = {android.R.id.text1, android.R.id.text2};
				SimpleAdapter adapter  = new SimpleAdapter(this, dealArray, android.R.layout.simple_list_item_2, keys, ids);
				setListAdapter(adapter);
				
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	private void updateDisplayForError() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.error_title));
		builder.setMessage(getString(R.string.error_message));
		builder.setPositiveButton(android.R.string.ok, null);
		
		AlertDialog dialog = builder.create();
		dialog.show();
		
		TextView emptyTextView = (TextView) getListView().getEmptyView();
		emptyTextView.setText(getString(R.string.no_items));
	}
	
	private class GetDealsTask extends AsyncTask<Object, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Object... params) {
			// TODO Auto-generated method stub
			int responseCode = -1;
			JSONObject jsonResponse = null;
			
			try {
				URL grouponDealsURL = new URL("http://api.groupon.com/v2/deals?client_id=8d1f63c7a85319c3d7671b108964fbfc0ea72d86");
				
				HttpURLConnection connection = (HttpURLConnection) grouponDealsURL.openConnection();
				connection.connect();
				System.out.println("HELLO WORLD");
				System.out.println(connection.getResponseMessage());

				
				responseCode = connection.getResponseCode();
				if(responseCode == HttpURLConnection.HTTP_OK) {
					InputStream inputStream = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
					String responseData = "", data="";
					while ((data = reader.readLine()) != null){
						responseData += data + "\n";
					}
					jsonResponse = new JSONObject(responseData);
					
				} else {
					//Not Success
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return jsonResponse;
		}
		
		@Override
		protected void onPostExecute(JSONObject result) {
			mDealData = result;
			handleDealsResponse();
		}
		
	}

	

}
