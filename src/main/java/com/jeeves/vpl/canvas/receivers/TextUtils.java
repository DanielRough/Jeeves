package com.jeeves.vpl.canvas.receivers;

import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TextUtils {

private static final Text helper;
private static final double DEFAULT_WRAPPING_WIDTH;
private static final double DEFAULT_LINE_SPACING;
private static final String DEFAULT_TEXT;

static {
    helper = new Text();
    DEFAULT_WRAPPING_WIDTH = helper.getWrappingWidth();
    DEFAULT_LINE_SPACING = helper.getLineSpacing();
    DEFAULT_TEXT = helper.getText();
}

public static double computeTextWidth(Font font, String text, double help0) {

    helper.setText(text);
    helper.setFont(font);

    helper.setWrappingWidth(0.0D);
    helper.setLineSpacing(0.0D);
    double d = Math.min(helper.prefWidth(-1.0D), help0);
    helper.setWrappingWidth((int) Math.ceil(d));
    d = Math.ceil(helper.getLayoutBounds().getWidth());

    helper.setWrappingWidth(DEFAULT_WRAPPING_WIDTH);
    helper.setLineSpacing(DEFAULT_LINE_SPACING);
    helper.setText(DEFAULT_TEXT);
    return d;
}
}