package com.rspl.sf.msfa.ui.toggleButton;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.ConstantsUtils;

import java.util.ArrayList;
import java.util.List;

public class MultiStateToggleButton extends ToggleButton {

    private static final String TAG = MultiStateToggleButton.class.getSimpleName();

    private static final String KEY_BUTTON_STATES = "button_states";
    private static final String KEY_INSTANCE_STATE = "instance_state";

    /**
     * A list of rendered buttons. Used to get state, among others
     */
    List<View> buttons;

    /**
     * The specified texts
     */
    CharSequence[] texts;

    /**
     * If true, multiple buttons can be pressed at the same time
     */
    boolean mMultipleChoice = false;

    /**
     * The layout containing all buttons
     */
    private LinearLayout mainLayout;
    private LinearLayout llViews;

    public MultiStateToggleButton(Context context) {
        super(context, null);
    }

    public MultiStateToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MultiStateToggleButton, 0, 0);
        try {
            CharSequence[] texts = a.getTextArray(R.styleable.MultiStateToggleButton_values);
            colorPressed = a.getColor(R.styleable.MultiStateToggleButton_mstbPrimaryColor, 0);
            colorNotPressed = a.getColor(R.styleable.MultiStateToggleButton_mstbSecondaryColor, 0);
            colorPressedText = a.getColor(R.styleable.MultiStateToggleButton_mstbColorPressedText, 0);
            colorPressedBackground = a.getColor(R.styleable.MultiStateToggleButton_mstbColorPressedBackground, 0);
            pressedBackgroundResource = a.getResourceId(R.styleable.MultiStateToggleButton_mstbColorPressedBackgroundResource, 0);
            colorNotPressedText = a.getColor(R.styleable.MultiStateToggleButton_mstbColorNotPressedText, 0);
            colorNotPressedBackground = a.getColor(R.styleable.MultiStateToggleButton_mstbColorNotPressedBackground, 0);
            notPressedBackgroundResource = a.getResourceId(R.styleable.MultiStateToggleButton_mstbColorNotPressedBackgroundResource, 0);

            int length = 0;
            if (texts != null) {
                length = texts.length;
            }
            setElements(texts, null, new boolean[length],2);
        } finally {
            a.recycle();
        }
    }

    /**
     * If multiple choice is enabled, the user can select multiple
     * values simultaneously.
     *
     * @param enable
     */
    public void enableMultipleChoice(boolean enable) {
        this.mMultipleChoice = enable;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putBooleanArray(KEY_BUTTON_STATES, getStates());
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            setStates(bundle.getBooleanArray(KEY_BUTTON_STATES));
            state = bundle.getParcelable(KEY_INSTANCE_STATE);
        }
        super.onRestoreInstanceState(state);
    }

    /**
     * Set the enabled state of this MultiStateToggleButton, including all of its child buttons.
     *
     * @param enabled True if this view is enabled, false otherwise.
     */
    @Override
    public void setEnabled(boolean enabled) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.setEnabled(enabled);
        }
    }

    /**
     * Set multiple buttons with the specified texts and default
     * initial values. Initial states are allowed, but both
     * arrays must be of the same size.
     *
     * @param texts            An array of CharSequences for the buttons
     * @param imageResourceIds an optional icon to show, either text, icon or both needs to be set.
     * @param selected         The default value for the buttons
     */
    public void setElements(@Nullable CharSequence[] texts, int[] imageResourceIds, boolean[] selected, int numberOfClm) {
        this.texts = texts;
        final int textCount = texts != null ? texts.length : 0;
        final int iconCount = imageResourceIds != null ? imageResourceIds.length : 0;
        final int elementCount = Math.max(textCount, iconCount);
        if (elementCount == 0) {
            return;
        }

        boolean enableDefaultSelection = true;
        if (selected == null || elementCount != selected.length) {
            enableDefaultSelection = false;
        }

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (mainLayout == null) {
            mainLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.view_multi_state_toggle_button, this, true);
        }
        llViews = (LinearLayout) mainLayout.findViewById(R.id.llViews);
        llViews.removeAllViews();

        this.buttons = new ArrayList<>(elementCount);
        LinearLayout llView = null;
        LinearLayout llView1 = null;
        LayoutParams filterParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        filterParams.setMargins(16, 8, 16, 8);
        filterParams.weight = 1;
        filterParams.gravity = Gravity.CENTER;
        for (int i = 0; i < elementCount; i++) {
            if (numberOfClm == 2) {
                if (i % 2 == 0) {
                    llView1 = new LinearLayout(context);
                    llView1.setOrientation(LinearLayout.VERTICAL);
                    LayoutParams linearLayout1 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                    linearLayout1.gravity = Gravity.CENTER;
                    llView1.setLayoutParams(linearLayout1);

                    llView = new LinearLayout(context);
                    llView.setOrientation(LinearLayout.HORIZONTAL);
                    LayoutParams linearLayout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                    linearLayout.gravity = Gravity.CENTER;
                    llView.setLayoutParams(linearLayout);
                    llView.setWeightSum(2);

                    llView1.addView(llView);
                    llViews.addView(llView1);
                }
            } else {
                llView = llViews;
            }
            Button b = new AppCompatButton(context);//(Button) inflater.inflate(R.layout.view_single_toggle_button, null, true);
            b.setLayoutParams(filterParams);
            b.setPadding(ConstantsUtils.dpToPx(16, context), ConstantsUtils.dpToPx(4, context), ConstantsUtils.dpToPx(16, context), ConstantsUtils.dpToPx(4, context));
            b.setText(texts != null ? texts[i] : "");
            if (imageResourceIds != null && imageResourceIds[i] != 0) {
                b.setCompoundDrawablesWithIntrinsicBounds(imageResourceIds[i], 0, 0, 0);
            }
            final int position = i;
            b.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    setValue(position);
                }

            });
            if (llView != null)
                llView.addView(b);
            if (enableDefaultSelection) {
                setButtonState(b, selected[i]);
            }
            this.buttons.add(b);
        }
//        mainLayout.setBackgroundResource(R.drawable.attendance_button_section_shape);
    }

    /**
     * @return An array of the buttons' text
     */
    public CharSequence[] getTexts() {
        return this.texts;
    }

    /**
     * Set multiple buttons with the specified texts and default
     * initial values. Initial states are allowed, but both
     * arrays must be of the same size.
     *
     * @param buttons  the array of button views to use
     * @param selected The default value for the buttons
     */
    public void setButtons(View[] buttons, boolean[] selected) {
        final int elementCount = buttons.length;
        if (elementCount == 0) {
            return;
        }

        boolean enableDefaultSelection = true;
        if (selected == null || elementCount != selected.length) {
            Log.d(TAG, "Invalid selection array");
            enableDefaultSelection = false;
        }

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (mainLayout == null) {
            mainLayout = (LinearLayout) inflater.inflate(R.layout.view_multi_state_toggle_button, this, true);
        }
        mainLayout.removeAllViews();

        this.buttons = new ArrayList<>();
        for (int i = 0; i < elementCount; i++) {
            View b = buttons[i];
            final int position = i;
            b.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    setValue(position);
                }

            });
            mainLayout.addView(b);
            if (enableDefaultSelection) {
                setButtonState(b, selected[i]);
            }
            this.buttons.add(b);
        }
        mainLayout.setBackgroundResource(R.drawable.attendance_button_section_shape);
    }

    public void setElements(CharSequence[] elements) {
        int size = elements == null ? 0 : elements.length;
        setElements(elements, null, new boolean[size], 2);
    }

    public void setElements(CharSequence[] elements, int selectedPosition) {
        int size = elements == null ? 0 : elements.length;
        boolean[] selected = new boolean[size];
        if (selectedPosition >= 0 && selectedPosition < size) {
            selected[selectedPosition] = true;
        }
        setElements(elements, null, selected, 2);
    }

    public void setElements(CharSequence[] elements, int selectedPosition, int numberOfClm) {
        int size = elements == null ? 0 : elements.length;
        boolean[] selected = new boolean[size];
        if (selectedPosition >= 0 && selectedPosition < size) {
            selected[selectedPosition] = true;
        }
        setElements(elements, null, selected, numberOfClm);
    }

    public void setElements(List<?> elements) {
        int size = elements == null ? 0 : elements.size();
        setElements(elements, new boolean[size]);
    }

    public void setElements(List<?> elements, Object selected) {
        int size = 0;
        int index = -1;
        if (elements != null) {
            size = elements.size();
            index = elements.indexOf(selected);
        }
        boolean[] selectedArray = new boolean[size];
        if (index != -1 && index < size) {
            selectedArray[index] = true;
        }
        setElements(elements, selectedArray);
    }

    public void setElements(List<?> texts, boolean[] selected) {
        try {
            if (texts == null) {
                texts = new ArrayList<>(0);
            }
            int size = texts.size();
            setElements(texts.toArray(new String[size]), null, selected, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setElements(int arrayResourceId, int selectedPosition) {
        // Get resources
        String[] elements = this.getResources().getStringArray(arrayResourceId);

        // Set selected boolean array
        int size = elements == null ? 0 : elements.length;
        boolean[] selected = new boolean[size];
        if (selectedPosition >= 0 && selectedPosition < size) {
            selected[selectedPosition] = true;
        }

        // Super
        setElements(elements, null, selected, 2);
    }

    public void setElements(int arrayResourceId, boolean[] selected) {
        setElements(this.getResources().getStringArray(arrayResourceId), null, selected, 2);
    }

    public void setButtonState(View button, boolean selected) {
        if (button == null) {
            return;
        }
        button.setSelected(selected);
        button.setBackgroundResource(selected ? R.drawable.attendance_button_pressed : R.drawable.attendance_button_not_pressed);
        if (colorPressed != 0 || colorNotPressed != 0) {
            button.setBackgroundColor(selected ? colorPressed : colorNotPressed);
        } else if (colorPressedBackground != 0 || colorNotPressedBackground != 0) {
            button.setBackgroundColor(selected ? colorPressedBackground : colorNotPressedBackground);
        }
        if (button instanceof Button) {
            int style = selected ? R.style.CstmToggleButtonWhiteBoldText : R.style.CstmToggleButtonPrimaryNormalText;
            ((AppCompatButton) button).setTextAppearance(this.getContext(), style);
            if (colorPressed != 0 || colorNotPressed != 0) {
                ((AppCompatButton) button).setTextColor(!selected ? colorPressed : colorNotPressed);
            }
            if (colorPressedText != 0 || colorNotPressedText != 0) {
                ((AppCompatButton) button).setTextColor(selected ? colorPressedText : colorNotPressedText);
            }
            if (pressedBackgroundResource != 0 || notPressedBackgroundResource != 0) {
                button.setBackgroundResource(selected ? pressedBackgroundResource : notPressedBackgroundResource);
            }
        }
    }

    public int getValue() {
        for (int i = 0; i < this.buttons.size(); i++) {
            if (buttons.get(i).isSelected()) {
                return i;
            }
        }
        return -1;
    }

    public void setValue(int position) {
        if(this.buttons!=null) {
            for (int i = 0; i < this.buttons.size(); i++) {
                if (mMultipleChoice) {
                    if (i == position) {
                        View b = buttons.get(i);
                        if (b != null) {
                            setButtonState(b, !b.isSelected());
                        }
                    }
                } else {
                    if (i == position) {
                        setButtonState(buttons.get(i), true);
                    } else if (!mMultipleChoice) {
                        setButtonState(buttons.get(i), false);
                    }
                }
            }
        }
        super.setValue(position);
    }

    public boolean[] getStates() {
        int size = this.buttons == null ? 0 : this.buttons.size();
        boolean[] result = new boolean[size];
        for (int i = 0; i < size; i++) {
            result[i] = this.buttons.get(i).isSelected();
        }
        return result;
    }

    public void setStates(boolean[] selected) {
        if (this.buttons == null || selected == null ||
                this.buttons.size() != selected.length) {
            return;
        }
        int count = 0;
        for (View b : this.buttons) {
            setButtonState(b, selected[count]);
            count++;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setColors(int colorPressed, int colorNotPressed) {
        super.setColors(colorPressed, colorNotPressed);
        refresh();
    }

    private void refresh() {
        boolean[] states = getStates();
        for (int i = 0; i < states.length; i++) {
            setButtonState(buttons.get(i), states[i]);
        }
    }
}
