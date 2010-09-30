/* Copyright (C) 2010 0xlab.org
 * Authored by: Kan-Ru Chen <kanru@0xlab.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cerqueira.util.tscal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import android.util.Log;

public class TSCalibrationView extends View {

    final private static String TAG = "TSCalibration";

    private class TargetPoint {
        public int x;
        public int y;
        public int calx;
        public int caly;
        public TargetPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private int mStep = 0;
    private TargetPoint mTargetPoints[];
    private TSCalibration mContext;

    public TSCalibrationView(TSCalibration context, int h, int w) {
        super(context);

        mContext = context;
        mTargetPoints = new TargetPoint[5];
        mTargetPoints[0] = new TargetPoint(50, 50);
        mTargetPoints[1] = new TargetPoint(w - 50, 50);
        mTargetPoints[2] = new TargetPoint(w - 50, h - 50);
        mTargetPoints[3] = new TargetPoint(50, h - 50);
        mTargetPoints[4] = new TargetPoint(w/2, h/2);
    }

    public void reset() {
        mStep = 0;
    }

    public boolean isFinished() {
        return mStep >= 5;
    }

    public void dumpCalData(File file) {
        StringBuilder sb = new StringBuilder();
        for (TargetPoint point : mTargetPoints) {
            sb.append(point.calx);
            sb.append(" ");
            sb.append(point.caly);
            sb.append(" ");
        }
        for (TargetPoint point : mTargetPoints) {
            sb.append(point.x);
            sb.append(" ");
            sb.append(point.y);
            sb.append(" ");
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(sb.toString().getBytes());
            fos.flush();
            fos.getFD().sync();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Cannot open file " + file);
        } catch (IOException e) {
            Log.e(TAG, "Cannot write file " + file);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isFinished())
            return true;
        if (ev.getAction() != MotionEvent.ACTION_UP)
            return true;
        mTargetPoints[mStep].calx = (int)ev.getRawX();
        mTargetPoints[mStep].caly = (int)ev.getRawY();
        mStep++;
        mContext.onCalTouchEvent(ev);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isFinished())
            return;
        canvas.drawColor(Color.BLACK);
        drawTarget(canvas, mTargetPoints[mStep].x, mTargetPoints[mStep].y);
    }

    private void drawTarget(Canvas c, int x, int y) {
        Paint white = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint red = new Paint(Paint.ANTI_ALIAS_FLAG);
        white.setColor(Color.WHITE);
        red.setColor(Color.RED);
        c.drawCircle(x, y, 25, red);
        c.drawCircle(x, y, 21, white);
        c.drawCircle(x, y, 17, red);
        c.drawCircle(x, y, 13, white);
        c.drawCircle(x, y, 9, red);
        c.drawCircle(x, y, 5, white);
        c.drawCircle(x, y, 1, red);
    }
}
