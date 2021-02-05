package com.rspl.sf.msfa.edittext;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;


import java.lang.reflect.Field;


public class EdittextMenu extends androidx.appcompat.widget.AppCompatEditText {
    private final Context mContext;
    public EdittextMenu(Context context) {
        super(context);
        this.mContext=context;
        blockContextMenu();
    }

    public EdittextMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext=context;
        blockContextMenu();
    }

    public EdittextMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext=context;
        blockContextMenu();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                // setInsertionDisabled when user touches the view
                this.setInsertionDisabled();
                break;
        }

        return super.onTouchEvent(event);
    }

    private void setInsertionDisabled() {


        try {
            Field editorField = TextView.class.getDeclaredField("mEditor");
            editorField.setAccessible(true);
            Object editorObject = editorField.get(this);

            Class editorClass = Class.forName("android.widget.Editor");
            Field mInsertionControllerEnabledField = editorClass.getDeclaredField("mInsertionControllerEnabled");
            mInsertionControllerEnabledField.setAccessible(true);
            mInsertionControllerEnabledField.set(editorObject, false);
        }
        catch (Exception ignored) {
            // ignore exception here
        }


    }
    @Override
    public boolean isSuggestionsEnabled() {
        return false;
    }
    private void blockContextMenu() {

        this.setCustomSelectionActionModeCallback(new BlockedActionModeCallback());
        this.setLongClickable(false);
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                EdittextMenu.this.clearFocus();
                return false;
            }
        });

    }

    private class BlockedActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    }
}
