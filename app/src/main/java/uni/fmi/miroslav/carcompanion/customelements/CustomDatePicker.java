package uni.fmi.miroslav.carcompanion.customelements;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.TimeZone;

public class CustomDatePicker implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    EditText targetField;

    private char separator;

    private int day;
    private int month;
    private int year;

    private final Context ctxt;

    //default constructor for when target field is clickable, but not editable or focusable
    public CustomDatePicker(Context context, @NotNull EditText target)
    {
        targetField = target;
        targetField.setOnClickListener(this);
        ctxt = context;
        separator = '.';
    }

    public char getSeparator() { return separator; }
    public void setSeparator(char sep) { separator = sep; }

    //used when click listener is bound to a button, and target field is present, but not clickable
    @Deprecated
    public CustomDatePicker(Context context, @NotNull EditText editText, @NotNull Button btn){
        this(context, editText);
        btn.setOnClickListener(this);
    }

    //on set date update the target field
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        updateDisplay();
    }

    //on click shows dialog
    @Override
    public void onClick(View v) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        DatePickerDialog dialog = new DatePickerDialog(ctxt, this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    // updates the date in the target field
    private void updateDisplay() {
        targetField.setText(new StringBuilder()
                // Month is 0 based so add 1
                .append(year).append(separator).append(month + 1).append(separator).append(day));
    }
}