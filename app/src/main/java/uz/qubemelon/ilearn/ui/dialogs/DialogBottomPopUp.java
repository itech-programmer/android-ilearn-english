package uz.qubemelon.ilearn.ui.dialogs;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import uz.qubemelon.ilearn.R;

public class DialogBottomPopUp {

    public PopupWindow popup_window;

    public PopupWindow get_popup_window() {
        return popup_window;
    }

    private Context context;

    @SuppressWarnings("deprecation")
    public DialogBottomPopUp(Context context, View view) {
        context = context;
        popup_window = new PopupWindow(context);
        popup_window.setBackgroundDrawable(new BitmapDrawable());
        popup_window.setWidth(WindowManager.LayoutParams.FILL_PARENT);
        popup_window.setHeight(WindowManager.LayoutParams.FILL_PARENT);
        popup_window.setTouchable(true);
        popup_window.setFocusable(true);
        popup_window.setOutsideTouchable(true);
        popup_window.setAnimationStyle(R.style.bottom_animation);
        popup_window.setContentView(view);

        popup_window.getContentView().setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popup_window.setFocusable(false);
                popup_window.dismiss();
                return true;
            }
        });

    }

    public void dismiss() {
        if (popup_window != null && popup_window.isShowing()) {
            popup_window.dismiss();
        }
    }

    public void showPopup(View rootView) {
        popup_window.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
    }
}

