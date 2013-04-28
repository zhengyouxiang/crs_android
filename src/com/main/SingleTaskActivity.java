package com.main;

import com.example.client.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class SingleTaskActivity extends Activity {
	
	View back;
	
	Button pictureTask, verifyTask, addTask, reviewTask, nearbyTask;
	
	class MyOnClickListener implements View.OnClickListener {
		
		String type = "";
		
		public MyOnClickListener(String str) {
			type = str;
		}

		@Override
		public void onClick(View v) {
			if(type.compareTo("nearby") == 0) {
				startActivity(new Intent(SingleTaskActivity.this, NearbyTaskActivity.class));
				return;
			}
			Intent next = new Intent(SingleTaskActivity.this, TaskListActivity.class);
			next.putExtra("type", type);
			startActivity(next);
		}
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_single_task);
		
		back = findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		pictureTask = (Button)findViewById(R.id.pictureTask);
		verifyTask = (Button)findViewById(R.id.verifyTask);
		addTask= (Button)findViewById(R.id.addTask);
//		reviewTask = (Button)findViewById(R.id.reviewTask);
		nearbyTask = (Button)findViewById(R.id.nearbyTask);
		
		pictureTask.setOnClickListener(new MyOnClickListener("picture"));
		verifyTask.setOnClickListener(new MyOnClickListener("verify"));
		addTask.setOnClickListener(new MyOnClickListener("add"));
//		reviewTask.setOnClickListener(new MyOnClickListener("review"));
		nearbyTask.setOnClickListener(new MyOnClickListener("nearby"));
		
	}

}
