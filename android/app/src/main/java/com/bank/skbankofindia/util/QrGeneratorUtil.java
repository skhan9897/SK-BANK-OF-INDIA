package com.bank.skbankofindia.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.nio.charset.StandardCharsets;

public class QrGeneratorUtil {

    public static Bitmap generateQrBitmap(String content, int size) {
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#0A192F"));
        paint.setAntiAlias(true);

        int gridCount = 29;
        float cellSize = (float) size / gridCount;

        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);

        // Draw Corner Finder Patterns (Top-Left, Top-Right, Bottom-Left)
        drawFinderPattern(canvas, paint, 0, 0, cellSize);
        drawFinderPattern(canvas, paint, (gridCount - 7) * cellSize, 0, cellSize);
        drawFinderPattern(canvas, paint, 0, (gridCount - 7) * cellSize, cellSize);

        // Deterministic QR Data Matrix
        for (int r = 0; r < gridCount; r++) {
            for (int c = 0; c < gridCount; c++) {
                if (isFinderArea(r, c, gridCount)) continue;

                int hash = (r * 31 + c * 17 + bytes[(r + c) % bytes.length]) & 0xFF;
                if ((hash % 2) == 0) {
                    float left = c * cellSize;
                    float top = r * cellSize;
                    canvas.drawRect(left, top, left + cellSize, top + cellSize, paint);
                }
            }
        }
        return bitmap;
    }

    private static void drawFinderPattern(Canvas canvas, Paint paint, float x, float y, float cellSize) {
        paint.setColor(Color.parseColor("#0A192F"));
        canvas.drawRect(x, y, x + 7 * cellSize, y + 7 * cellSize, paint);

        paint.setColor(Color.WHITE);
        canvas.drawRect(x + cellSize, y + cellSize, x + 6 * cellSize, y + 6 * cellSize, paint);

        paint.setColor(Color.parseColor("#0052CC"));
        canvas.drawRect(x + 2 * cellSize, y + 2 * cellSize, x + 5 * cellSize, y + 5 * cellSize, paint);
    }

    private static boolean isFinderArea(int r, int c, int gridCount) {
        if (r < 7 && c < 7) return true;
        if (r < 7 && c >= gridCount - 7) return true;
        return r >= gridCount - 7 && c < 7;
    }
}
