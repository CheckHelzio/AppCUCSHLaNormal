package com.example.rangedatepicker;

import java.util.Date;

public interface CalendarCellDecorator {
    void decorate(CalendarCellView cellView, Date date);
}
