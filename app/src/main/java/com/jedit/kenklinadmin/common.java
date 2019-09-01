package com.jedit.kenklinadmin;

import android.support.design.widget.Snackbar;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class common {

    public static final String STATE_ACTIVE = "Active";
    public static final String STATE_PENDING = "Pending";
    public static final String STATE_COMPLETED = "Completed";
    public static final String STATE_CANCELLED = "Cancelled";

    public static String time_to_date(String timeMillis){

        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(Long.parseLong(timeMillis));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());

        return  simpleDateFormat.format(calendar.getTime());
    }

    public static Snackbar Mysnackbar(View parent_view, String message, int lenght) {

        final Snackbar snackbar = Snackbar.make(parent_view, message, lenght);
        snackbar.setActionTextColor(parent_view.getContext().getResources().getColor(R.color.colorPrimary));
        snackbar.setAction("Close", v -> snackbar.dismiss());

        return snackbar;
    }
}
