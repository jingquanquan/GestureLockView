package com.jing.gestureloack;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.util.AttributeSet;
import android.widget.LinearLayout;

@SuppressLint("DrawAllocation")
public class GesturePointView extends LinearLayout {
	private ArrayList<Point> checked_points = new ArrayList<Point>();
	private Point[][] point_matrix;
	private int layout_width, layout_height;
	private int point_space;
	private Paint paint;
	private float radios;
	private int grid_size;
	private int left, right, top;
	public static final int TYPE_3X3 = 3;
	public static final int TYPE_4X4 = 4;
	private int grid_type = 3;
	private Context context;

	public ArrayList<Point> getChecked_points() {
		return checked_points;
	}

	public void setChecked_points(ArrayList<Point> checked_points) {
		this.checked_points = checked_points;
		invalidate();
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

	public GesturePointView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initViews(context);
	}

	public GesturePointView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViews(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		initSize();
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

	public int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
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
		paint.setStrokeWidth(dip2px(context, 1));
		setBackgroundColor(Color.TRANSPARENT);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		for (int i = 0; i < grid_size; i++)
			for (int j = 0; j < grid_size; j++) {
				Point point = new Point(i, j);
				if (!checked_points.contains(point)) {
					paint.setStyle(Style.STROKE);
					paint.setColor(Color.rgb(0x1c, 0x86, 0xee));
				} else {
					paint.setStyle(Style.FILL);
					paint.setColor(Color.WHITE);
				}
				canvas.drawCircle(point_matrix[i][j].x, point_matrix[i][j].y,
						radios, paint);
			}
	}

	public void resetPoints() {
		checked_points.clear();
		invalidate();
	}

}
