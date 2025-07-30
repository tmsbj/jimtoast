package ir.jimta.jimtoast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentLinkedQueue;

import ir.jimta.jimtoast.R;

@SuppressWarnings("deprecation")
public class JimToast {

  @Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
  @Retention(RetentionPolicy.CLASS)
  private @interface NonNull {
  }

  @Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
  @Retention(RetentionPolicy.CLASS)
  private @interface DrawableRes {
  }

  @Target({ElementType.ANNOTATION_TYPE})
  @Retention(RetentionPolicy.CLASS)
  private @interface IntDef {
    int[] value();
  }

  public static final int DURATION_SHORT = Toast.LENGTH_SHORT;
  public static final int DURATION_LONG = Toast.LENGTH_LONG;

  @IntDef({DURATION_SHORT, DURATION_LONG})
  @Retention(RetentionPolicy.SOURCE)
  private @interface Duration {
  }

  public static final int TYPE_DEFAULT = 0;
  public static final int TYPE_SETUP = 1;
  public static final int TYPE_SUCCESS = 2;
  public static final int TYPE_WARNING = 3;
  public static final int TYPE_ERROR = 4;
  public static final int TYPE_HELP = 5;
  public static final int TYPE_INFO = 6;
  public static final int TYPE_PENDING = 7;
  public static final int TYPE_STAR = 8;
  public static final int TYPE_BOLT = 9;

  @IntDef({TYPE_DEFAULT, TYPE_SETUP, TYPE_SUCCESS, TYPE_WARNING, TYPE_ERROR, TYPE_HELP, TYPE_INFO, TYPE_PENDING, TYPE_STAR, TYPE_BOLT})
  @Retention(RetentionPolicy.SOURCE)
  private @interface Type {
  }

  private static final int[] TOAST_BACKGROUNDS = {
    R.drawable.jim_shape_default,
    R.drawable.jim_shape_setup,
    R.drawable.jim_shape_success,
    R.drawable.jim_shape_warning,
    R.drawable.jim_shape_error,
    R.drawable.jim_shape_help,
    R.drawable.jim_shape_info,
    R.drawable.jim_shape_pending,
    R.drawable.jim_shape_star,
    R.drawable.jim_shape_bolt
  };

  private static final int[] DEFAULT_TOAST_ICONS = {
    R.drawable.jim_vec_default,
    R.drawable.jim_vec_setup,
    R.drawable.jim_vec_success,
    R.drawable.jim_vec_warning,
    R.drawable.jim_vec_error,
    R.drawable.jim_vec_help,
    R.drawable.jim_vec_info,
    R.drawable.jim_vec_pending,
    R.drawable.jim_vec_star,
    R.drawable.jim_vec_bolt
  };

  private static Toast currentToast;
  private static final ConcurrentLinkedQueue<ToastRequest> toastQueue = new ConcurrentLinkedQueue<>();
  private static final Handler mainHandler = new Handler(Looper.getMainLooper());
  private static volatile boolean isShowingToast = false;

  public static boolean isShowing() {
    return isShowingToast;
  }

  public static void show(@NonNull Context context, @NonNull Object message, @Duration int duration, @Type int type, @DrawableRes int iconRes) {
    CharSequence parsedMessage = parseMessage(context, message);
    if (parsedMessage == null || parsedMessage.length() == 0) return;
    showInternal(context, parsedMessage, duration, type, false, iconRes);
  }

  public static void show(@NonNull Context context, @NonNull Object message, @Duration int duration, @Type int type) {
    CharSequence parsedMessage = parseMessage(context, message);
    if (parsedMessage == null || parsedMessage.length() == 0) return;
    showInternal(context, parsedMessage, duration, type, false, DEFAULT_TOAST_ICONS[type]);
  }

  public static void showUrgent(@NonNull Context context, @NonNull Object message, @Duration int duration, @Type int type, @DrawableRes int iconRes) {
    CharSequence parsedMessage = parseMessage(context, message);
    if (parsedMessage == null || parsedMessage.length() == 0) return;
    showInternal(context, parsedMessage, duration, type, true, iconRes);
  }

  public static void showUrgent(@NonNull Context context, @NonNull Object message, @Duration int duration, @Type int type) {
    CharSequence parsedMessage = parseMessage(context, message);
    if (parsedMessage == null || parsedMessage.length() == 0) return;
    showInternal(context, parsedMessage, duration, type, true, DEFAULT_TOAST_ICONS[type]);
  }

  public static void cancelAllToasts() {
    if (currentToast != null) {
      currentToast.cancel();
      currentToast = null;
    }
    toastQueue.clear();
    isShowingToast = false;
    mainHandler.removeCallbacksAndMessages(null);
  }

  private static void showInternal(@NonNull Context context, @NonNull CharSequence message, @Duration int duration, @Type int type, boolean cancelPrevious, @DrawableRes int iconRes) {
    if (cancelPrevious) {
      cancelAllToasts();
    }

    toastQueue.offer(new ToastRequest(context, message, duration, type, iconRes));

    if (!isShowing()) {
      showNextToast();
    }
  }

  @SuppressLint("InflateParams")
  private static void showNextToast() {
    ToastRequest request = toastQueue.poll();
    if (request == null) {
      isShowingToast = false;
      return;
    }

    Context context = request.contextRef.get();
    if (context == null) {
      showNextToast();
      return;
    }

    isShowingToast = true;

    try {
      Toast toast = new Toast(context);
      toast.setDuration(request.duration);

      View layout = LayoutInflater.from(context).inflate(R.layout.jimtoast_layout, null, false);

      TextView textView = layout.findViewById(R.id.toast_text);
      LinearLayout container = layout.findViewById(R.id.toast_type);
      ImageView iconView = layout.findViewById(R.id.toast_icon);

      textView.setText(request.message);
      container.setBackgroundResource(TOAST_BACKGROUNDS[request.type]);

      if (request.iconRes == -1) {
        iconView.setVisibility(View.INVISIBLE);
      } else if (request.iconRes != 0) {
        iconView.setImageResource(request.iconRes);
        iconView.setVisibility(View.VISIBLE);
      } else {
        iconView.setVisibility(View.GONE);
      }

      toast.setView(layout);
      toast.show();
      currentToast = toast;

      int delayMs = (request.duration == DURATION_LONG) ? 3500 : 2000;
      mainHandler.postDelayed(new Runnable() {
        @Override
        public void run() {
          currentToast = null;
          showNextToast();
        }
      }, delayMs);
    } catch (Exception e) {
      showNextToast();
    }
  }

  private static class ToastRequest {
    final WeakReference<Context> contextRef;
    final CharSequence message;
    final int duration;
    final int type;
    final int iconRes;

    ToastRequest(@NonNull Context context, @NonNull CharSequence message, @Duration int duration, @Type int type, @DrawableRes int iconRes) {
      this.contextRef = new WeakReference<>(context);
      this.message = message;
      this.duration = duration;
      this.type = type;
      this.iconRes = iconRes;
    }
  }

  @NonNull
  private static CharSequence parseMessage(@NonNull Context context, @NonNull Object message) {
    if (message instanceof Integer) {
      try {
        return context.getString((Integer) message);
      } catch (Exception e) {
        return String.valueOf(message);
      }
    }
    return String.valueOf(message);
  }
}
