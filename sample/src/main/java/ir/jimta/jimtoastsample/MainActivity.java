package ir.jimta.jimtoastsample;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import static ir.jimta.jimtoast.JimToast.*;

public class MainActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    int dp16 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());

    ScrollView scroll = new ScrollView(this);
    scroll.setPadding(0, dp16, 0, dp16);

    LinearLayout container = new LinearLayout(this);
    container.setOrientation(LinearLayout.VERTICAL);
    container.setGravity(Gravity.CENTER_HORIZONTAL);
    container.setPadding(dp16, 0, dp16, 0);

    container.addView(createBtn("Default", ir.jimta.jimtoast.R.drawable.jim_shape_default,
      () -> show(this, "This is a toast message.", DURATION_SHORT, TYPE_DEFAULT, ir.jimta.jimtoast.R.drawable.jim_vec_default)));

    container.addView(createBtn("Setup", ir.jimta.jimtoast.R.drawable.jim_shape_setup,
      () -> show(this, "This is a toast message.", DURATION_SHORT, TYPE_SETUP)));

    container.addView(createBtn("Success", ir.jimta.jimtoast.R.drawable.jim_shape_success,
      () -> showUrgent(this, "This is a toast message.", DURATION_SHORT, TYPE_SUCCESS, ir.jimta.jimtoast.R.drawable.jim_vec_success)));

    container.addView(createBtn("Warning", ir.jimta.jimtoast.R.drawable.jim_shape_warning,
      () -> showUrgent(this, "This is a toast message.", DURATION_SHORT, TYPE_WARNING)));

    container.addView(createBtn("Error", ir.jimta.jimtoast.R.drawable.jim_shape_error,
      () -> showUrgent(this, "This is a toast message.", DURATION_SHORT, TYPE_ERROR)));

    container.addView(createBtn("Help", ir.jimta.jimtoast.R.drawable.jim_shape_help,
      () -> showUrgent(this, "This is a toast message.", DURATION_SHORT, TYPE_HELP)));

    container.addView(createBtn("Info", ir.jimta.jimtoast.R.drawable.jim_shape_info,
      () -> showUrgent(this, "This is a toast message.", DURATION_SHORT, TYPE_INFO)));

    container.addView(createBtn("Pending", ir.jimta.jimtoast.R.drawable.jim_shape_pending,
      () -> showUrgent(this, "This is a toast message.", DURATION_SHORT, TYPE_PENDING)));

    container.addView(createBtn("Star", ir.jimta.jimtoast.R.drawable.jim_shape_star,
      () -> showUrgent(this, "This is a toast message.", DURATION_LONG, TYPE_STAR)));

    container.addView(createBtn("Bolt", ir.jimta.jimtoast.R.drawable.jim_shape_bolt,
      () -> showUrgent(this, "This is a toast message.", DURATION_LONG, TYPE_BOLT)));

    scroll.addView(container);
    setContentView(scroll);
  }

  private Button createBtn(String text, int bgRes, Runnable onClick) {
    Button btn = new Button(this);
    btn.setText(text);
    btn.setTextColor(Color.WHITE);
    btn.setTextSize(14);
    btn.setGravity(Gravity.CENTER);
    btn.setBackgroundResource(bgRes);

    int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 140, getResources().getDisplayMetrics());
    int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36, getResources().getDisplayMetrics());
    int marginBottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());

    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
    params.bottomMargin = marginBottom;
    btn.setLayoutParams(params);

    btn.setShadowLayer(1f, 1f, 1f, 0x80000000);
    btn.setOnClickListener(v -> onClick.run());

    return btn;
  }
}
