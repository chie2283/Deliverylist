package net.sejuku.terakoya.deliverylist;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;

@Service
public class CalendarDay {

    public int[] calendar;
    private Year year;
    LocalDate ld;

    public CalendarDay() {
        this.setYear();
    }

    private void setYear() {
        this.year = Year.now();
    }

    private Year getYear() {
        return this.year;
    }

    private void setLd(int month) {
        this.ld = LocalDate.of(this.getYear().getValue(), month, 1);
    }

    private LocalDate getLd() {
        return this.ld;
    }

    private int getMonthLength() {
        Month thisMonth = Month.from(getLd());
        return thisMonth.length(this.ld.isLeapYear());
    }

    private int getFirstDay() {
        return getLd().getDayOfWeek().getValue() - 1;
    }

    public void calcFields() {
        int column = getFirstDay();

        for(int date = 1; date <= getMonthLength(); date++) {
            this.calendar[column] = date;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        final String FORMAT = "%3d";

        for (int n = 1; n <= 12; n++) {
            this.calendar = new int[1];
            this.setLd(n);

            calcFields();
            sb.append(getYear() + "年" + n + "月");
            sb.append("\r\n");

            for (int i = 0; i < calendar.length; i++) {
                int date = calendar[i];
                sb.append(String.format(FORMAT,date));
            }
        }
        sb.append("\r\n");
        sb.append(getFirstDay());
        return sb.toString();
    }
}
