package ir.medxhub.doctor.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import ir.medxhub.doctor.R;


public class DatePicker extends Activity {
    public int year_min = 1300;
    public int year_max = 1394;
    public int month_min = 1;
    public int month_max = 12;
    public int day_min = 1;
    public int day_max = 31;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_picker);

        Intent intent = getIntent();
        if (intent.hasExtra("title")) {
            ((TextView) findViewById(R.id.date_picker_title)).setText(intent.getStringExtra("title"));
        }
        final EditText year = (EditText) findViewById(R.id.date_year);
        int temp = intent.getIntExtra("year", 1370);
        year.setText((temp == 0 ? 1370 : temp) + "");
        final EditText month = (EditText) findViewById(R.id.date_month);
        temp = intent.getIntExtra("month", 1);
        month.setText((temp == 0 ? 1 : temp) + "");
        final EditText day = (EditText) findViewById(R.id.date_day);
        temp = intent.getIntExtra("day", 1);
        day.setText((temp == 0 ? 1 : temp) + "");

        (findViewById(R.id.inc_year)).setOnClickListener(new Listener(year, year_min, year_max, Listener.Increase));
        (findViewById(R.id.dec_year)).setOnClickListener(new Listener(year, year_min, year_max, Listener.Decrease));
        (findViewById(R.id.inc_month)).setOnClickListener(new Listener(month, month_min, month_max, Listener.Increase));
        (findViewById(R.id.dec_month)).setOnClickListener(new Listener(month, month_min, month_max, Listener.Decrease));
        (findViewById(R.id.inc_day)).setOnClickListener(new Listener(day, day_min, day_max, Listener.Increase));
        (findViewById(R.id.dec_day)).setOnClickListener(new Listener(day, day_min, day_max, Listener.Decrease));

        (findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                setResult(RESULT_CANCELED, returnIntent);
                finish();
            }
        });

        (findViewById(R.id.confirm)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String t;
                t = year.getText().toString();
                int y = t.equals("") ? -1 : Integer.parseInt(t);
                t = month.getText().toString();
                int m = t.equals("") ? -1 : Integer.parseInt(t);
                t = day.getText().toString();
                int d = t.equals("") ? -1 : Integer.parseInt(t);

                if (!(year_min <= y && y <= year_max)) {
                    Tools.makeToast(DatePicker.this, getString(R.string.wrong_birth_year), Toast.LENGTH_SHORT, -1);
                    return;
                }
                if (!(month_min <= m && m <= month_max)) {
                    Tools.makeToast(DatePicker.this, getString(R.string.wrong_birth_month), Toast.LENGTH_SHORT, -1);
                    return;
                }
                if (!(day_min <= d && d <= day_max)) {
                    Tools.makeToast(DatePicker.this, getString(R.string.wrong_birth_day), Toast.LENGTH_SHORT, -1);
                    return;
                }

                // return values if everything has been set up well
                Intent returnIntent = new Intent();
                returnIntent.putExtra("year", y);
                returnIntent.putExtra("month", m);
                returnIntent.putExtra("day", d);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    public class Listener implements View.OnClickListener {
        static final int Increase = 0;
        static final int Decrease = 1;
        EditText num;
        int min;
        int max;
        int type;

        public Listener(EditText num, int min, int max, int type) {
            this.num = num;
            this.min = min;
            this.max = max;
            this.type = type;
        }

        @Override
        public void onClick(View view) {
            String t = num.getText().toString();
            int x = t.equals("") ? min : Integer.parseInt(t);
            switch (type) {
                case Increase:
                    x = x < max ? x + 1 : max;
                    num.setText(x + "");
                    break;

                case Decrease:
                    x = x > min ? x - 1 : min;
                    num.setText(x + "");
                    break;

                default:
                    break;
            }
        }
    }
}
