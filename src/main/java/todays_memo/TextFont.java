package todays_memo;

import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;

import java.util.ArrayList;

class TextFont {
    private TextPaint tp = new TextPaint();
    private Paint p = new Paint();

    public int getCharWidth(char ch, int fontSize) {
        tp.setTextSize(fontSize);
        Rect r = new Rect();
        tp.getTextBounds(String.valueOf(ch), 0, 1, r);
        return r.width();
    }

    public int getHeight(int fontSize) {
        tp.setTextSize(fontSize);
        Rect r = new Rect();
        tp.getTextBounds(String.valueOf('A'), 0, 1, r);
        return r.height();
    }

    public int getStringWidth(String str, int fontSize) {
        p.setTextSize(fontSize);
        tp.setTextSize(fontSize);
        Rect r = new Rect();
        tp.getTextBounds(str, 0, str.length(), r);
        return r.width();//you may also try p.measureText(str);
    }


    public String getLongestSubString(int width, int fontSize, String text) {
        int maxCharPerLine = width / (new TextFont()).getCharWidth('A', fontSize);
        int stringWidth = (new TextFont()).getStringWidth(text, fontSize);
        int posOfSpace = text.lastIndexOf(' ');
        if (stringWidth <= width || maxCharPerLine >= text.length())
            return text;
        String temp = text.substring(0, (posOfSpace == -1) ? (maxCharPerLine > text.length() ? text.length() : maxCharPerLine) : posOfSpace);
        return getLongestSubString(width, fontSize, temp);
    }

    public ArrayList<String> breakTextToLines(int width, int fontSize, String text) {
        if (text == null)
            return null;
        ArrayList<String> lines = new ArrayList<String>();

        text = text.replace("\n", " ");

        int length = -1;
        String tempText = "";

        do {
            if (!tempText.endsWith(" ") && tempText.length() > 0)
                length += tempText.length();
            else
                length += tempText.length() + 1;

            if (length >= text.length())
                break;

            tempText = getLongestSubString(width, fontSize, text.substring(length));
            lines.add(tempText);
        }
        while (length < text.length());

        return lines;
    }
}