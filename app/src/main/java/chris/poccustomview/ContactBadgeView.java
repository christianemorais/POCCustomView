package chris.poccustomview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

public class ContactBadgeView extends View {

    private String initial;
    private Drawable imageProfile;
    private TextPaint textConfiguration;
    private float initialSize;

    private int textWidth;
    private int textHeight;

    private int paddingLeft = getPaddingLeft();
    private int paddingTop = getPaddingTop();
    private int paddingRight = getPaddingRight();
    private int paddingBottom = getPaddingBottom();

    public ContactBadgeView(Context context) {
        this(context, null);
    }

    public ContactBadgeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContactBadgeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setSaveEnabled(true);

        getAttributes(attrs, defStyle);

        configInit();
    }

    private void getAttributes(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.ContactBadgeView, defStyle, 0);

        if (a.hasValue(R.styleable.ContactBadgeView_imageProfile)) {
            imageProfile = a.getDrawable(R.styleable.ContactBadgeView_imageProfile);

            if (imageProfile != null) {
                imageProfile.setCallback(this);
            }
        } else if (a.hasValue(R.styleable.ContactBadgeView_initials)) {
            initial = a.getString(R.styleable.ContactBadgeView_initials);

            if (a.hasValue(R.styleable.ContactBadgeView_initialsSize)) {
                initialSize = a.getDimension(R.styleable.ContactBadgeView_initialsSize, 0);
            }
        }

        a.recycle();
    }

    private void configInit() {
        if (initial != null) {

            // Set up a default TextPaint object
            textConfiguration = new TextPaint();
            textConfiguration.setFlags(Paint.ANTI_ALIAS_FLAG);
            textConfiguration.setTextAlign(Paint.Align.CENTER);

            Typeface bold = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
            textConfiguration.setTypeface(bold);

            invalidateTextPaintAndMeasurements();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        if (imageProfile != null) {
            imageProfile.setBounds(0, 0,
                    paddingLeft + contentWidth, paddingTop + contentHeight);
            imageProfile.draw(canvas);
        } else if (initial != null) {
            // Draw the text.
            canvas.drawText(initial,
                    paddingLeft + (contentWidth - (textWidth / 2)) / 2,
                    paddingTop + (contentHeight + (textHeight * 2)) / 2,
                    textConfiguration);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minWidth = paddingLeft + paddingRight + getSuggestedMinimumWidth();
        int width = resolveSizeAndState(minWidth, widthMeasureSpec, 1);

        int minHeight = MeasureSpec.getSize(width) - textWidth + paddingBottom + paddingTop;
        int height = resolveSizeAndState(minHeight, heightMeasureSpec, 0);

        setMeasuredDimension(width, height);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();

        bundle.putParcelable("superState", super.onSaveInstanceState());

        bundle.putString("initial", initial);
        bundle.putFloat("initialSize", initialSize);

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            this.initial = bundle.getString("initial");
            this.initialSize = bundle.getFloat("initialSize");

            state = bundle.getParcelable("superState");
        }

        super.onRestoreInstanceState(state);
    }

    private void invalidateTextPaintAndMeasurements() {
        textConfiguration.setTextSize(initialSize);
        textConfiguration.setColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark,
                getContext().getTheme()));
        textWidth = (int) textConfiguration.measureText(initial);

        Paint.FontMetrics fontMetrics = textConfiguration.getFontMetrics();
        textHeight = (int) fontMetrics.bottom;

        invalidate();
    }

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
        invalidateTextPaintAndMeasurements();
    }

    public Drawable getImageProfile() {
        return imageProfile;
    }

    public void setImageProfile(Drawable imageProfile) {
        this.imageProfile = imageProfile;
    }

    public float getInitialSize() {
        return initialSize;
    }

    public void setInitialSize(float initialSize) {
        this.initialSize = initialSize;
        invalidateTextPaintAndMeasurements();
    }
}
