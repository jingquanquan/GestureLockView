package com.jing.gestureloack;

import java.util.ArrayList;

import com.jing.gestureloack.GestureLockView.GestureResultListener;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {
	private GestureLockView gestureLockView;
	private GesturePointView gesturePointView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		gestureLockView = (GestureLockView) findViewById(R.id.gesture);
		gesturePointView = (GesturePointView) findViewById(R.id.ges_point);
		gestureLockView.setGestureResultListener(new GestureResultListener() {

			@Override
			public void getPointMatrix(ArrayList<Point> pointList) {
				// TODO Auto-generated method stub
				gesturePointView.setChecked_points(pointList);
			}

			@Override
			public void getCodedPass(String pass) {
				// TODO Auto-generated method stub
				gestureLockView.setAuthenticationFailed();
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						gestureLockView.resetGestureView();
						gesturePointView.resetPoints();
					}
				}, 500);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_3) {
			gestureLockView.setGrid_type(GestureLockView.TYPE_3X3);
			gesturePointView.setGrid_type(GesturePointView.TYPE_3X3);
			return true;
		} else if (id == R.id.action_4) {
			gestureLockView.setGrid_type(GestureLockView.TYPE_4X4);
			gesturePointView.setGrid_type(GesturePointView.TYPE_4X4);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
