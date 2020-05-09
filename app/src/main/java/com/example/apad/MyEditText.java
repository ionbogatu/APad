package com.example.apad;

import android.content.Context;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ToggleButton;

public class MyEditText extends androidx.appcompat.widget.AppCompatEditText {
    private static final int STYLE_BOLD = 0;
    private static final int STYLE_ITALIC = 1;
    private static final int STYLE_UNDERLINED = 2;

    private ToggleButton boldToggle;
    private ToggleButton italicsToggle;
    private ToggleButton underlineToggle;
    private String textSize = "12dp";

    public MyEditText(Context context) {
        super(context);
        initialize();
    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    private void initialize() {
        this.addTextChangedListener(new MyEditText.EditTextWatcher());
    }

    public void setBoldToggleButton(ToggleButton button) {
        boldToggle = button;

        boldToggle.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                toggleStyle(STYLE_BOLD);
            }
        });
    }

    public void setItalicsToggleButton(ToggleButton button) {
        italicsToggle = button;

        italicsToggle.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                toggleStyle(STYLE_ITALIC);
            }
        });
    }

    public void setUnderlineToggleButton(ToggleButton button) {
        underlineToggle = button;

        underlineToggle.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                toggleStyle(STYLE_UNDERLINED);
            }
        });
    }

    public void setTextSize(String textSize) {
        this.textSize = textSize;
    }

    private void toggleStyle(int style) {
        int selectionStart = this.getSelectionStart();
        int selectionEnd = this.getSelectionEnd();

        if (selectionStart > selectionEnd) {
            int temp = selectionEnd;
            selectionEnd = selectionStart;
            selectionStart = temp;
        }

        if (selectionEnd > selectionStart) {
            Spannable str = this.getText();
            boolean exists = false;
            StyleSpan[] styleSpans;

            switch (style) {
                case STYLE_BOLD:
                    styleSpans = str.getSpans(selectionStart, selectionEnd, StyleSpan.class);

                    for (int i = 0; i < styleSpans.length; i++) {
                        if (styleSpans[i].getStyle() == android.graphics.Typeface.BOLD) {
                            str.removeSpan(styleSpans[i]);
                            exists = true;
                        }
                    }

                    if (!exists) {
                        str.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), selectionStart, selectionEnd,
                                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    }

                    this.setSelection(selectionStart, selectionEnd);
                    break;
                case STYLE_ITALIC:
                    styleSpans = str.getSpans(selectionStart, selectionEnd, StyleSpan.class);

                    for (int i = 0; i < styleSpans.length; i++) {
                        if (styleSpans[i].getStyle() == android.graphics.Typeface.ITALIC) {
                            str.removeSpan(styleSpans[i]);
                            exists = true;
                        }
                    }

                    if (!exists) {
                        str.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), selectionStart, selectionEnd,
                                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    }

                    this.setSelection(selectionStart, selectionEnd);
                    break;
                case STYLE_UNDERLINED:
                    UnderlineSpan[] underSpan = str.getSpans(selectionStart, selectionEnd, UnderlineSpan.class);

                    for (int i = 0; i < underSpan.length; i++) {
                        str.removeSpan(underSpan[i]);
                        exists = true;
                    }

                    if (!exists) {
                        str.setSpan(new UnderlineSpan(), selectionStart, selectionEnd, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    }

                    this.setSelection(selectionStart, selectionEnd);
                    break;
            }
        }
    }

    private class EditTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            int position = Selection.getSelectionStart(MyEditText.this.getText());
            if (position < 0) {
                position = 0;
            }

            if (position > 0) {
                CharacterStyle[] appliedStyles = editable.getSpans(position - 1, position, CharacterStyle.class);

                StyleSpan currentBoldSpan = null;
                StyleSpan currentItalicSpan = null;
                UnderlineSpan currentUnderlineSpan = null;

                for (int i = 0; i < appliedStyles.length; i++) {
                    if (appliedStyles[i] instanceof StyleSpan) {
                        if (((StyleSpan) appliedStyles[i]).getStyle() == android.graphics.Typeface.BOLD) {
                            currentBoldSpan = (StyleSpan) appliedStyles[i];
                        } else if (((StyleSpan) appliedStyles[i]).getStyle() == android.graphics.Typeface.ITALIC) {
                            currentItalicSpan = (StyleSpan) appliedStyles[i];
                        }
                    } else if (appliedStyles[i] instanceof UnderlineSpan) {
                        currentUnderlineSpan = (UnderlineSpan) appliedStyles[i];
                    }
                }

                StyleSpan boldStyleSpan = new StyleSpan(android.graphics.Typeface.BOLD);
                if (boldToggle != null && boldToggle.isChecked() && currentBoldSpan == null) {
                    editable.setSpan(boldStyleSpan, position - 1, position,
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                } else if (boldToggle != null && !boldToggle.isChecked() && currentBoldSpan != null) {
                    int boldStart = editable.getSpanStart(currentBoldSpan);
                    int boldEnd = editable.getSpanEnd(currentBoldSpan);

                    editable.removeSpan(currentBoldSpan);
                    if (boldStart <= (position - 1)) {
                        editable.setSpan(boldStyleSpan, boldStart, position - 1,
                                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    }

                    if (boldEnd > position) {
                        editable.setSpan(boldStyleSpan, position, boldEnd,
                                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    }
                }

                StyleSpan italicStyleSpan = new StyleSpan(android.graphics.Typeface.ITALIC);
                if (italicsToggle != null && italicsToggle.isChecked() && currentItalicSpan == null) {
                    editable.setSpan(italicStyleSpan, position - 1, position,
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                } else if (italicsToggle != null && !italicsToggle.isChecked() && currentItalicSpan != null) {
                    int italicStart = editable.getSpanStart(currentItalicSpan);
                    int italicEnd = editable.getSpanEnd(currentItalicSpan);

                    editable.removeSpan(currentItalicSpan);
                    if (italicStart <= (position - 1)) {
                        editable.setSpan(italicStyleSpan, italicStart, position - 1,
                                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    }

                    if (italicEnd > position) {
                        editable.setSpan(italicStyleSpan, position, italicEnd,
                                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    }
                }

                UnderlineSpan underlineSpan = new UnderlineSpan();
                if (underlineToggle != null && underlineToggle.isChecked() && currentUnderlineSpan == null) {
                    editable.setSpan(underlineSpan, position - 1, position, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                } else if (underlineToggle != null && !underlineToggle.isChecked() && currentUnderlineSpan != null) {
                    int underLineStart = editable.getSpanStart(currentUnderlineSpan);
                    int underLineEnd = editable.getSpanEnd(currentUnderlineSpan);

                    editable.removeSpan(currentUnderlineSpan);
                    if (underLineStart <= (position - 1)) {
                        editable.setSpan(underlineSpan, underLineStart, position - 1,
                                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    }

                    if (underLineEnd > position) {
                        editable.setSpan(underlineSpan, position, underLineEnd,
                                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    }
                }

                AbsoluteSizeSpan absoluteSizeSpan = new AbsoluteSizeSpan(convertDpToPx(Integer.parseInt(MyEditText.this.textSize.replace("dp", ""))));
                editable.setSpan(absoluteSizeSpan, position - 1, position, SpannableString.SPAN_EXCLUSIVE_INCLUSIVE);
            }
        }

        private int convertDpToPx(int dp) {
            return Math.round(dp * (getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
        }
    }
}
