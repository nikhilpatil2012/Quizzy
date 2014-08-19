package tronbox.arena;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

@SuppressLint("ViewConstructor")
public class FinalGraph extends View {

	private int H, W;
	private Paint outlinePaint, textPaint, firstScorerPaint, secondScorerPaint,
			dashStylePaint;
	private int[] scoreOfFirstPlayer,scoreOfSecondPlayer;
/*	public FinalGraph(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

	}

	public FinalGraph(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
*/
	public FinalGraph(Context context,int[] scoreOfFirstPlayer,int[] scoreOfSecondPlayer) {
		super(context);
		this.scoreOfFirstPlayer = scoreOfFirstPlayer;
		this.scoreOfSecondPlayer  = scoreOfSecondPlayer;
	}

	private void init() {
		outlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		outlinePaint.setStrokeWidth(3);
		outlinePaint.setPathEffect(null);
		outlinePaint.setColor(Color.WHITE);
		outlinePaint.setStyle(Paint.Style.STROKE);

		textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setColor(Color.WHITE);

		firstScorerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		firstScorerPaint.setStrokeWidth(3);
		firstScorerPaint.setPathEffect(null);
		firstScorerPaint.setColor(Color.GREEN);
		firstScorerPaint.setStyle(Paint.Style.STROKE);

		secondScorerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		secondScorerPaint.setStrokeWidth(3);
		secondScorerPaint.setPathEffect(null);
		secondScorerPaint.setColor(Color.RED);
		secondScorerPaint.setStyle(Paint.Style.STROKE);

		dashStylePaint = new Paint();
		dashStylePaint.setColor(Color.WHITE);
		dashStylePaint.setStyle(Paint.Style.STROKE);
		//dashStylePaint.setPathEffect(new DashPathEffect(new float[] { 5, 5,5, 5 }, 5));
	}

	@Override
	protected void onDraw(Canvas canvas) {

		H = getHeight();
		W = getWidth();

		init();

		canvas.drawColor(Color.TRANSPARENT);

		drawOutline(canvas);
		drawDashPath(canvas);
		drawScaleY(canvas);
		drawScaleX(canvas);
		drawFirstScore(canvas, scoreOfFirstPlayer);
		drawSecondScore(canvas, scoreOfSecondPlayer);
	}

	private void drawDashPath(Canvas canvas) {
		float dy = (H - ((H * 20) / 100)) / 6f;
		float dx = (W - ((W * 20) / 100)) / 10f;

		Path p = new Path();
		p.moveTo((W * 10) / 100, (H * 10) / 100);
		p.lineTo(W - ((W * 10) / 100), (H * 10) / 100);
		p.close();

		Path p1 = new Path();
		p1.moveTo((W * 10) / 100, (H * 10) / 100 + dy);
		p1.lineTo(W - ((W * 10) / 100), (H * 10) / 100 + dy);
		p1.close();

		Path p2 = new Path();
		p2.moveTo((W * 10) / 100, (H * 10) / 100 + dy * 2);
		p2.lineTo(W - ((W * 10) / 100), (H * 10) / 100 + dy * 2);
		p2.close();

		Path p3 = new Path();
		p3.moveTo((W * 10) / 100, (H * 10) / 100 + dy * 3);
		p3.lineTo(W - ((W * 10) / 100), (H * 10) / 100 + dy * 3);
		p3.close();

		Path p4 = new Path();
		p4.moveTo((W * 10) / 100, (H * 10) / 100 + dy * 4);
		p4.lineTo(W - ((W * 10) / 100), (H * 10) / 100 + dy * 4);
		p4.close();

		Path p5 = new Path();
		p5.moveTo((W * 10) / 100, (H * 10) / 100 + dy * 5);
		p5.lineTo(W - ((W * 10) / 100), (H * 10) / 100 + dy * 5);
		p5.close();

		Path p6 = new Path();
		p6.moveTo((W * 10) / 100 + dx, H - (H * 7) / 100);
		p6.lineTo((W * 10) / 100 + dx, (H * 10) / 100);
		p6.close();

		Path p7 = new Path();
		p7.moveTo((W * 10) / 100 + dx * 2, H - (H * 7) / 100);
		p7.lineTo((W * 10) / 100 + dx * 2, (H * 10) / 100);
		p7.close();

		Path p8 = new Path();
		p8.moveTo((W * 10) / 100 + dx * 3, H - (H * 7) / 100);
		p8.lineTo((W * 10) / 100 + dx * 3, (H * 10) / 100);
		p8.close();

		Path p9 = new Path();
		p9.moveTo((W * 10) / 100 + dx * 4, H - (H * 7) / 100);
		p9.lineTo((W * 10) / 100 + dx * 4, (H * 10) / 100);
		p9.close();

		Path p16 = new Path();
		p16.moveTo((W * 10) / 100 + dx * 4, H - (H * 7) / 100);
		p16.lineTo((W * 10) / 100 + dx * 4, (H * 10) / 100);
		p16.close();

		Path p10 = new Path();
		p10.moveTo((W * 10) / 100 + dx * 5, H - (H * 7) / 100);
		p10.lineTo((W * 10) / 100 + dx * 5, (H * 10) / 100);
		p10.close();

		Path p11 = new Path();
		p11.moveTo((W * 10) / 100 + dx * 6, H - (H * 7) / 100);
		p11.lineTo((W * 10) / 100 + dx * 6, (H * 10) / 100);
		p11.close();

		Path p12 = new Path();
		p12.moveTo((W * 10) / 100 + dx * 7, H - (H * 7) / 100);
		p12.lineTo((W * 10) / 100 + dx * 7, (H * 10) / 100);
		p12.close();

		Path p13 = new Path();
		p13.moveTo((W * 10) / 100 + dx * 8, H - (H * 7) / 100);
		p13.lineTo((W * 10) / 100 + dx * 8, (H * 10) / 100);
		p13.close();

		Path p14 = new Path();
		p14.moveTo((W * 10) / 100 + dx * 9, H - (H * 7) / 100);
		p14.lineTo((W * 10) / 100 + dx * 9, (H * 10) / 100);
		p14.close();

		Path p15 = new Path();
		p15.moveTo((W * 10) / 100 + dx * 10, H - (H * 7) / 100);
		p15.lineTo((W * 10) / 100 + dx * 10, (H * 10) / 100);
		p15.close();

		canvas.drawPath(p, dashStylePaint);
		canvas.drawPath(p1, dashStylePaint);
		canvas.drawPath(p2, dashStylePaint);
		canvas.drawPath(p3, dashStylePaint);
		canvas.drawPath(p4, dashStylePaint);
		canvas.drawPath(p5, dashStylePaint);
		canvas.drawPath(p6, dashStylePaint);
		canvas.drawPath(p7, dashStylePaint);
		canvas.drawPath(p8, dashStylePaint);
		canvas.drawPath(p9, dashStylePaint);
		canvas.drawPath(p10, dashStylePaint);
		canvas.drawPath(p11, dashStylePaint);
		canvas.drawPath(p12, dashStylePaint);
		canvas.drawPath(p13, dashStylePaint);
		canvas.drawPath(p14, dashStylePaint);
		canvas.drawPath(p15, dashStylePaint);
		canvas.drawPath(p16, dashStylePaint);
	}

	private void drawFirstScore(Canvas canvas, int[] score) {

		int startx = (W * 10) / 100;
		float dx = (W - ((W * 20) / 100)) / 10f;
		int starty = H - ((H * 10) / 100);
		int dy = (H - ((H * 20) / 100)) / 6;
		float point = dy / (50f);

		int total = score[0];

		Path p = new Path();

		for (int i = 1; i < 10; i++) {
			p.moveTo(startx + (dx * i), starty - (point * total));
			total = total + score[i];
			p.lineTo(startx + (dx * (i + 1)), starty - (point * total));
		}

		p.close();

		canvas.drawPath(p, firstScorerPaint);

		
	}

	private void drawSecondScore(Canvas canvas, int[] score) {

		int startx = (W * 10) / 100;
		float dx = (W - ((W * 20) / 100)) / 10f;
		int starty = H - ((H * 10) / 100);
		int dy = (H - ((H * 20) / 100)) / 6;
		float point = dy / (50f);

		int total = score[0];

		Path p = new Path();

		for (int i = 1; i < 10; i++) {
			p.moveTo(startx + (dx * i), starty - (point * total));
			total = total + score[i];
			p.lineTo(startx + (dx * (i + 1)), starty - (point * total));
		}

		p.close();

		canvas.drawPath(p, secondScorerPaint);

		
	}

	private void drawOutline(Canvas canvas) {

		Path outline = new Path();

		outline.moveTo((W * 10) / 100, (H * 10) / 100);
		outline.lineTo((W * 10) / 100, H - (H * 7) / 100);
		outline.moveTo((W * 10) / 100, H - (H * 7) / 100);
		outline.lineTo(W - ((W * 10) / 100), H - (H * 7) / 100);

		outline.close();

		canvas.drawPath(outline, outlinePaint);
	}

	private void drawScaleX(Canvas canvas) {

		int X = W - ((W * 20) / 100);
		float dx = X / 10f;
		int total = 10;

		for (int i = 0; i < 10; i++) {
			/*canvas.drawLine(W - ((W * 10) / 100) - dx * i, H - ((H * 9) / 100),
					W - ((W * 10) / 100) - dx * i, H - ((H * 5) / 100),
					outlinePaint);*/
			canvas.drawText("" + total, W - ((W * 10) / 100) - dx * i - 3, H
					- ((H * 1) / 100), textPaint);
			total--;
		}

	}

	private void drawScaleY(Canvas canvas) {
		int Y = H - ((H * 20) / 100);
		float dy = Y / 6f;
		int total = 300;
		for (int i = 0; i < 6; i++) {
/*			canvas.drawLine((W * 9) / 100, (H * 10) / 100 + dy * i,
					(W * 11) / 100, (H * 10) / 100 + dy * i, outlinePaint);
*/			canvas.drawText("" + total, (W * 5) / 100, (H * 10) / 100 + dy * i
					+ 3, textPaint);
			total = total - 50;
		}
	}
/*
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		 ScaleAnimation anim = new ScaleAnimation(0.0f,1.0f,0.0f,1.0f);
		 anim.setDuration(1000);
	     anim.setFillAfter(true);
	     this.startAnimation(anim);
	}*/
}
