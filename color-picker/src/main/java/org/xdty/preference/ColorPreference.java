package org.xdty.preference;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.xdty.preference.colorpicker.ColorPickerDialog;
import org.xdty.preference.colorpicker.ColorPickerSwatch;
import org.xdty.preference.colorpicker.R;

/**
 * A preference showing a {@link ColorPickerDialog} to allow the user to select a color to save as {@link Preference}.
 */
public class ColorPreference extends Preference implements ColorPickerSwatch
        .OnColorSelectedListener {

    private static final int DEFAULT_VALUE = Color.BLACK;

    private int mTitle = R.string.color_picker_default_title;
    private int mCurrentValue;
    private int[] mColors;
    private int mColumns;
    private boolean mMaterial;

    private View mColorView;

    public ColorPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ColorPreference, 0, 0);
        try {
            int id = a.getResourceId(R.styleable.ColorPreference_colorDialogColors, R.array.default_rainbow);
            if (id != 0) {
                mColors = getContext().getResources().getIntArray(id);
            }
            id = a.getResourceId(R.styleable.ColorPreference_colorDialogTitle, 0);
            if (id != 0) {
                mTitle = a.getResourceId(R.styleable.ColorPreference_colorDialogTitle, R.string.color_picker_default_title);
            }
            mColumns = a.getInt(R.styleable.ColorPreference_colorDialogColumns, 5);
            mMaterial = a.getBoolean(R.styleable.ColorPreference_colorDialogMaterial, true);
        } finally {
            a.recycle();
        }
    }

    @Override protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInteger(index, DEFAULT_VALUE);
    }

    @Override protected View onCreateView(ViewGroup parent) {
        View s = super.onCreateView(parent);
        mColorView = new View(getContext());
        int size = (int) dpToPx(32);
        mColorView.setLayoutParams(new ViewGroup.LayoutParams(size, size));
        updateShownColor();
        ViewGroup w = (ViewGroup) s.findViewById(android.R.id.widget_frame);
        w.setVisibility(View.VISIBLE);
        w.addView(mColorView);
        return s;
    }

    @Override protected void onBindView(View view) {
        super.onBindView(view);
        if (mMaterial) {
            TextView textTitle = (TextView) view.findViewById(android.R.id.title);
            TextView textSummary = (TextView) view.findViewById(android.R.id.summary);

            textTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            textSummary.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            textTitle.setTextColor(getColor(android.R.attr.textColorPrimary));
            textSummary.setTextColor(getColor(android.R.attr.textColorSecondary));

            View parent = (View) textSummary.getParent().getParent();
            parent.setPadding((int) dpToPx(16), 0, (int) dpToPx(16), 0);
        }
    }

    @Override protected void onClick() {
        int[] colors = mColors.length != 0 ? mColors : new int[]{
                Color.BLACK, Color.WHITE, Color
                .RED, Color.GREEN, Color.BLUE
        };
        ColorPickerDialog d = ColorPickerDialog.newInstance(mTitle,
                colors, mCurrentValue, mColumns,
                ColorPickerDialog.SIZE_SMALL);
        d.setOnColorSelectedListener(this);
        d.show(((AppCompatActivity) getContext()).getSupportFragmentManager(), "ColorPickerDialog");
    }

    @Override protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            // Restore existing state
            mCurrentValue = this.getPersistedInt(DEFAULT_VALUE);
        } else {
            // Set default state from the XML attribute
            mCurrentValue = (Integer) defaultValue;
            persistInt(mCurrentValue);
        }
    }

    @Override protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        // Check whether this Preference is persistent (continually saved)
        if (isPersistent()) {
            // No need to save instance state since it's persistent,
            // use superclass state
            return superState;
        }

        // Create instance of custom BaseSavedState
        final SavedState myState = new SavedState(superState);
        // Set the state's value with the class member that holds current
        // setting value
        myState.current = mCurrentValue;
        myState.colors = mColors;
        myState.columns = mColumns;
        return myState;
    }

    @Override protected void onRestoreInstanceState(Parcelable state) {
        // Check whether we saved the state in onSaveInstanceState
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didn't save the state, so call superclass
            super.onRestoreInstanceState(state);
            return;
        }

        // Cast state to custom BaseSavedState and pass to superclass
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());

        // Update own values
        mCurrentValue = myState.current;
        mColors = myState.colors;
        mColumns = myState.columns;

        // Update shown color
        updateShownColor();

        // Set this Preference's widget to reflect the restored state
        //mNumberPicker.setValue(myState.value);
    }

    @Override public void onColorSelected(int color) {
        persistInt(color);
        mCurrentValue = color;
        updateShownColor();
    }

    private void updateShownColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mColorView.setBackground(new ShapeDrawable(new OvalShape()));
            ((ShapeDrawable) mColorView.getBackground()).getPaint().setColor(mCurrentValue);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mColorView.setBackground(new ColorCircleDrawable(mCurrentValue));
        } else {
            mColorView.setBackground(new ColorCircleDrawable(mCurrentValue));
        }
        mColorView.invalidate();
    }

    /**
     * Convert a dp size to pixel. Useful for specifying view sizes in code.
     *
     * @param dp
     *         The size in density-independent pixels.
     * @return {@code px} - The size in generic pixels (density-dependent).
     */
    private float dpToPx(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getContext().getResources().getDisplayMetrics());
    }

    private int getColor(int attrId) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getContext().getTheme();
        theme.resolveAttribute(attrId, typedValue, true);
        TypedArray arr = getContext().obtainStyledAttributes(typedValue.data, new int[]{attrId});
        int color = arr.getColor(0, -1);
        arr.recycle();
        return color;
    }

    private static class SavedState extends BaseSavedState {
        // Standard creator object using an instance of this class
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {

                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
        // Member that holds the preference's values
        int current;
        int[] colors;
        int columns;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source) {
            super(source);
            // Get the current preference's values
            current = source.readInt();
            source.readIntArray(colors);
            columns = source.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            // Write the preference's values
            dest.writeInt(current);
            dest.writeIntArray(colors);
            dest.writeInt(columns);
        }
    }
}