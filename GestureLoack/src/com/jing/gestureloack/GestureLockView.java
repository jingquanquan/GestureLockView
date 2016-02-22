package com.jing.gestureloack;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * 手势解锁控件,支持3x3和4x4矩阵,默认3x3，生成无规律密码
 * 
 * @author 景圈圈
 * 
 */
@SuppressLint("DrawAllocation")
public class GestureLockView extends LinearLayout {
	private Context context;
	private boolean hasMeasured = false;
	private boolean failed = false;
	private boolean actionUp = false;
	private int layout_width, layout_height;
	private int point_space;
	private Point[][] point_matrix;
	private ArrayList<Point> checked_points;
	private Paint paint;
	private float radios;
	private int grid_size;
	private int left, right, top;
	private float touch_x, touch_y;
	public static final int TYPE_3X3 = 3;
	public static final int TYPE_4X4 = 4;
	private int grid_type = 3;
	private String StringPass = "";
	private GestureResultListener gestureResultListener;

	public GestureResultListener getGestureResultListener() {
		return gestureResultListener;
	}

	public void setGestureResultListener(
			GestureResultListener gestureResultListener) {
		this.gestureResultListener = gestureResultListener;
	}

	public int getGrid_type() {
		return grid_type;
	}

	public void setGrid_type(int grid_type) {
		this.grid_type = grid_type;
		initViews(context);
		initSize();
		invalidate();
	}

	public GestureLockView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initViews(context);
	}

	public GestureLockView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initViews(context);
	}

	private void initViews(Context context) {
		this.context = context;
		switch (grid_type) {
		case TYPE_3X3:
			grid_size = 3;
			break;
		case TYPE_4X4:
			grid_size = 4;
			break;
		default:
			grid_size = 3;
			break;
		}
		point_matrix = new Point[grid_size][grid_size];
		checked_points = new ArrayList<Point>();
		paint = new Paint();
		paint.setAntiAlias(true);
		setBackgroundColor(Color.TRANSPARENT);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		for (int i = 0; i < grid_size; i++)
			for (int j = 0; j < grid_size; j++) {
				Point point = new Point(i, j);
				if (!checked_points.contains(point))
					if (!failed)
						paint.setColor(Color.WHITE);
					else
						paint.setColor(Color.RED);
				else {
					if (failed)
						paint.setColor(Color.RED);
					else
						paint.setColor(Color.rgb(0x1c, 0x86, 0xee));
					paint.setStyle(Style.FILL);
					canvas.drawCircle(point_matrix[i][j].x,
							point_matrix[i][j].y, (float) (radios * 0.4), paint);
					if (checked_points.size() > 1) {
						paint.setStrokeWidth(dip2px(context, 5));
						for (int k = 1; k < checked_points.size(); k++) {
							canvas.drawLine(
									point_matrix[checked_points.get(k - 1).x][checked_points
											.get(k - 1).y].x,
									point_matrix[checked_points.get(k - 1).x][checked_points
											.get(k - 1).y].y,
									point_matrix[checked_points.get(k).x][checked_points
											.get(k).y].x,
									point_matrix[checked_points.get(k).x][checked_points
											.get(k).y].y, paint);
						}

					}
				}

				paint.setStyle(Style.STROKE);
				paint.setStrokeWidth(dip2px(context, 3));
				canvas.drawCircle(point_matrix[i][j].x, point_matrix[i][j].y,
						radios, paint);
				if (checked_points.size() > 0 && !failed && !actionUp) {
					paint.setStrokeWidth(dip2px(context, 5));
					paint.setColor(Color.rgb(0x1c, 0x86, 0xee));
					canvas.drawLine(point_matrix[checked_points
							.get(checked_points.size() - 1).x][checked_points
							.get(checked_points.size() - 1).y].x,
							point_matrix[checked_points.get(checked_points
									.size() - 1).x][checked_points
									.get(checked_points.size() - 1).y].y,
							touch_x, touch_y, paint);

				}
			}
	}

	public int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (!hasMeasured) {
			initSize();
			hasMeasured = !hasMeasured;
		}
	}

	private void initSize() {
		layout_height = getMeasuredHeight();
		layout_width = getMeasuredWidth();
		if (layout_height >= layout_width) {
			left = 0;
			right = layout_width;
			top = (layout_height - layout_width) / 2;
		} else {
			top = 0;
			left = (layout_width - layout_height) / 2;
			right = layout_width - left;
		}
		point_space = (right - left) / grid_size;
		radios = (float) (point_space / 2 * 0.7);
		for (int i = 0; i < grid_size; i++) {
			int x = (int) (left + (i + 0.5) * point_space);
			for (int j = 0; j < grid_size; j++) {

				point_matrix[i][j] = new Point(x, (int) (top + (j + 0.5)
						* point_space));
			}
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			addPoints(event.getX(), event.getY());
			break;

		case MotionEvent.ACTION_MOVE:
			addPoints(event.getX(), event.getY());
			break;
		case MotionEvent.ACTION_UP:
			actionUp = true;
			invalidate();
			if (gestureResultListener != null)
				gestureResultListener.getCodedPass(getCodedPassString());
			break;
		}
		return true;
	}

	private void addPoints(float touch_x, float touch_y) {
		this.touch_x = touch_x;
		this.touch_y = touch_y;

		for (int i = 0; i < grid_size; i++)
			for (int j = 0; j < grid_size; j++) {
				Point point = new Point(i, j);
				if (Math.sqrt(Math.pow(touch_x - point_matrix[i][j].x, 2)
						+ Math.pow(touch_y - point_matrix[i][j].y, 2)) < radios * 0.7
						&& !checked_points.contains(point)) {
					if (checked_points.size() > 0) {
						switch (this.grid_type) {
						case TYPE_3X3:
							addMiddlePoint3x3(
									checked_points
											.get(checked_points.size() - 1).x,
									checked_points.get(checked_points.size() - 1).y,
									i, j);
							break;
						case TYPE_4X4:
							addMiddlePoint3x3(
									checked_points
											.get(checked_points.size() - 1).x,
									checked_points.get(checked_points.size() - 1).y,
									i, j);
							addMiddlePoint4x4(
									checked_points
											.get(checked_points.size() - 1).x,
									checked_points.get(checked_points.size() - 1).y,
									i, j);
							break;
						default:
							addMiddlePoint3x3(
									checked_points
											.get(checked_points.size() - 1).x,
									checked_points.get(checked_points.size() - 1).y,
									i, j);
							break;
						}

					}
					addPoints(point);
				}
			}
		invalidate();
	}

	private void addMiddlePoint3x3(int ox, int oy, int x, int y) {
		if (Math.abs(ox - x) == 2 && Math.abs(oy - y) == 2) {
			Point point = new Point(ox < x ? ox + 1 : x + 1, oy < y ? oy + 1
					: y + 1);
			if (!checked_points.contains(point))
				addPoints(point);
		} else if (Math.abs(ox - x) == 2 && Math.abs(oy - y) == 0) {
			Point point = new Point(ox < x ? ox + 1 : x + 1, y);
			if (!checked_points.contains(point))
				addPoints(point);
		} else if (Math.abs(ox - x) == 0 && Math.abs(oy - y) == 2) {
			Point point = new Point(x, oy < y ? oy + 1 : y + 1);
			if (!checked_points.contains(point))
				addPoints(point);
		}

	}

	private void addMiddlePoint4x4(int ox, int oy, int x, int y) {
		if (Math.abs(ox - x) == 3 && Math.abs(oy - y) == 3 && x != y
				&& ox != oy) {
			if (ox < oy) {
				Point point1 = new Point(ox + 1, oy - 1);
				if (!checked_points.contains(point1))
					addPoints(point1);
				Point point2 = new Point(x - 1, y + 1);
				if (!checked_points.contains(point2))
					addPoints(point2);
			} else {
				Point point1 = new Point(ox - 1, oy + 1);
				if (!checked_points.contains(point1))
					addPoints(point1);
				Point point2 = new Point(x + 1, y - 1);
				if (!checked_points.contains(point2))
					addPoints(point2);
			}

		} else if (Math.abs(ox - x) == 3 && Math.abs(oy - y) == 3 && x == y
				&& ox == oy) {
			Point point1 = new Point(ox < x ? ox + 1 : x + 1, oy < y ? oy + 1
					: y + 1);
			if (!checked_points.contains(point1))
				addPoints(point1);
			Point point2 = new Point(ox > x ? ox - 1 : x - 1, oy > y ? oy - 1
					: y - 1);
			if (!checked_points.contains(point2))
				addPoints(point2);
		} else if (Math.abs(ox - x) == 3 && Math.abs(oy - y) == 0) {
			Point point1 = new Point(ox < x ? ox + 1 : x + 1, y);
			if (!checked_points.contains(point1))
				addPoints(point1);
			Point point2 = new Point(ox > x ? ox - 1 : x - 1, y);
			if (!checked_points.contains(point2))
				addPoints(point2);
		} else if (Math.abs(ox - x) == 0 && Math.abs(oy - y) == 3) {
			Point point1 = new Point(x, oy < y ? oy + 1 : y + 1);
			if (!checked_points.contains(point1))
				addPoints(point1);
			Point point2 = new Point(x, oy > y ? oy - 1 : y - 1);
			if (!checked_points.contains(point2))
				addPoints(point2);
		}
	}

	public void addPoints(Point point) {
		checked_points.add(point);
		addPassString(point);
		if (gestureResultListener != null)
			gestureResultListener.getPointMatrix(checked_points);
	}

	public void resetGestureView() {
		checked_points.clear();
		failed = false;
		actionUp = false;
		invalidate();
		StringPass = "";

	}

	private String addPassString(Point point) {
		StringPass += (toCode(point.x) + toCode(point.y));
		return StringPass;
	}

	public String getCodedPassString() {
		return Base64.encodeToString(StringPass.getBytes(), Base64.DEFAULT);
	}

	private String toCode(int n) {
		String c = "";
		switch (n) {
		case 0:
			c = "K";
			break;

		case 1:
			c = "O";
			break;
		case 2:
			c = "C";
			break;
		case 3:
			c = "Y";
			break;
		}
		return c;
	}

	public abstract static interface GestureResultListener {
		public void getCodedPass(String pass);

		public void getPointMatrix(ArrayList<Point> pointList);
	}

	public void setAuthenticationFailed() {
		failed = true;
		invalidate();
	}

}
