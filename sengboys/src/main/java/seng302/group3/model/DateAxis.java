/**
 The MIT License (MIT)

 Copyright (c) 2014 Christian Schudt

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.

 https://bitbucket.org/sco0ter/extfx
 */

package seng302.group3.model;

import com.sun.javafx.charts.ChartLayoutAnimator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.chart.Axis;
import javafx.util.Duration;
import javafx.util.StringConverter;

public final class DateAxis extends Axis<Date> {
    private final LongProperty currentLowerBound;
    private final LongProperty currentUpperBound;
    private final ObjectProperty<StringConverter<Date>> tickLabelFormatter;
    private Date minDate;
    private Date maxDate;
    private ObjectProperty<Date> lowerBound;
    private ObjectProperty<Date> upperBound;
    private ChartLayoutAnimator animator;
    private Object currentAnimationID;
    private DateAxis.Interval actualInterval;

    public DateAxis() {
        this.currentLowerBound = new SimpleLongProperty(this, "currentLowerBound");
        this.currentUpperBound = new SimpleLongProperty(this, "currentUpperBound");
        this.tickLabelFormatter = new ObjectPropertyBase() {
            protected void invalidated() {
                if(!DateAxis.this.isAutoRanging()) {
                    DateAxis.this.invalidateRange();
                    DateAxis.this.requestAxisLayout();
                }

            }

            public Object getBean() {
                return DateAxis.this;
            }

            public String getName() {
                return "tickLabelFormatter";
            }
        };
        this.lowerBound = new ObjectPropertyBase() {
            protected void invalidated() {
                if(!DateAxis.this.isAutoRanging()) {
                    DateAxis.this.invalidateRange();
                    DateAxis.this.requestAxisLayout();
                }

            }

            public Object getBean() {
                return DateAxis.this;
            }

            public String getName() {
                return "lowerBound";
            }
        };
        this.upperBound = new ObjectPropertyBase() {
            protected void invalidated() {
                if(!DateAxis.this.isAutoRanging()) {
                    DateAxis.this.invalidateRange();
                    DateAxis.this.requestAxisLayout();
                }

            }

            public Object getBean() {
                return DateAxis.this;
            }

            public String getName() {
                return "upperBound";
            }
        };
        this.animator = new ChartLayoutAnimator(this);
        this.actualInterval = DateAxis.Interval.DECADE;
    }

    public DateAxis(Date lowerBound, Date upperBound) {
        this();
        this.setAutoRanging(false);
        this.setLowerBound(lowerBound);
        this.setUpperBound(upperBound);
    }

    public DateAxis(String axisLabel, Date lowerBound, Date upperBound) {
        this(lowerBound, upperBound);
        this.setLabel(axisLabel);
    }

    public void invalidateRange(List<Date> list) {
        super.invalidateRange(list);
        Collections.sort(list);
        if(list.isEmpty()) {
            this.minDate = this.maxDate = new Date();
        } else if(list.size() == 1) {
            this.minDate = this.maxDate = (Date)list.get(0);
        } else if(list.size() > 1) {
            this.minDate = (Date)list.get(0);
            this.maxDate = (Date)list.get(list.size() - 1);
        }

    }

    protected Object autoRange(double length) {
        if(this.isAutoRanging()) {
            return new Object[]{this.minDate, this.maxDate};
        } else if(this.getLowerBound() != null && this.getUpperBound() != null) {
            return this.getRange();
        } else {
            throw new IllegalArgumentException("If autoRanging is false, a lower and upper bound must be set.");
        }
    }

    protected void setRange(Object range, boolean animating) {
        Object[] r = (Object[])((Object[])range);
        Date oldLowerBound = this.getLowerBound();
        Date oldUpperBound = this.getUpperBound();
        Date lower = (Date)r[0];
        Date upper = (Date)r[1];
        this.setLowerBound(lower);
        this.setUpperBound(upper);
        if(animating) {
            this.animator.stop(this.currentAnimationID);
            this.currentAnimationID = this.animator.animate(new KeyFrame[]{new KeyFrame(Duration.ZERO, new KeyValue[]{new KeyValue(this.currentLowerBound, Long.valueOf(oldLowerBound.getTime())), new KeyValue(this.currentUpperBound, Long.valueOf(oldUpperBound.getTime()))}), new KeyFrame(Duration.millis(700.0D), new KeyValue[]{new KeyValue(this.currentLowerBound, Long.valueOf(lower.getTime())), new KeyValue(this.currentUpperBound, Long.valueOf(upper.getTime()))})});
        } else {
            this.currentLowerBound.set(this.getLowerBound().getTime());
            this.currentUpperBound.set(this.getUpperBound().getTime());
        }

    }

    protected Object getRange() {
        return new Object[]{this.getLowerBound(), this.getUpperBound()};
    }

    public double getZeroPosition() {
        return 0.0D;
    }

    public double getDisplayPosition(Date date) {
        double length = this.getSide().isHorizontal()?this.getWidth():this.getHeight();
        double diff = (double)(this.currentUpperBound.get() - this.currentLowerBound.get());
        double range = length - this.getZeroPosition();
        double d = (double)(date.getTime() - this.currentLowerBound.get()) / diff;
        return this.getSide().isVertical()?this.getHeight() - d * range + this.getZeroPosition():d * range + this.getZeroPosition();
    }

    public Date getValueForDisplay(double displayPosition) {
        double length = this.getSide().isHorizontal()?this.getWidth():this.getHeight();
        double diff = (double)(this.currentUpperBound.get() - this.currentLowerBound.get());
        double range = length - this.getZeroPosition();
        return this.getSide().isVertical()?new Date((long)((displayPosition - this.getZeroPosition() - this.getHeight()) / -range * diff + (double)this.currentLowerBound.get())):new Date((long)((displayPosition - this.getZeroPosition()) / range * diff + (double)this.currentLowerBound.get()));
    }

    public boolean isValueOnAxis(Date date) {
        return date.getTime() > this.currentLowerBound.get() && date.getTime() < this.currentUpperBound.get();
    }

    public double toNumericValue(Date date) {
        return (double)date.getTime();
    }

    public Date toRealValue(double v) {
        return new Date((long)v);
    }

    protected List<Date> calculateTickValues(double v, Object range) {
        Object[] r = (Object[])((Object[])range);
        Date lower = (Date)r[0];
        Date upper = (Date)r[1];
        ArrayList dateList = new ArrayList();
        Calendar calendar = Calendar.getInstance();
        double averageTickGap = 100.0D;
        double averageTicks = v / averageTickGap;
        ArrayList previousDateList = new ArrayList();
        DateAxis.Interval previousInterval = DateAxis.Interval.values()[0];
        DateAxis.Interval[] evenDateList = DateAxis.Interval.values();
        int secondDate = evenDateList.length;

        label45:
        for(int thirdDate = 0; thirdDate < secondDate; ++thirdDate) {
            DateAxis.Interval lastDate = evenDateList[thirdDate];
            calendar.setTime(lower);
            dateList.clear();
            previousDateList.clear();
            this.actualInterval = lastDate;

            while(calendar.getTime().getTime() <= upper.getTime()) {
                dateList.add(calendar.getTime());
                calendar.add(lastDate.interval, lastDate.amount);
            }

            if((double)dateList.size() > averageTicks) {
                calendar.setTime(lower);

                while(true) {
                    if(calendar.getTime().getTime() > upper.getTime()) {
                        break label45;
                    }

                    previousDateList.add(calendar.getTime());
                    calendar.add(previousInterval.interval, previousInterval.amount);
                }
            }

            previousInterval = lastDate;
        }

        if((double)previousDateList.size() - averageTicks > averageTicks - (double)dateList.size()) {
            dateList = previousDateList;
            this.actualInterval = previousInterval;
        }

        dateList.add(upper);
        List var20 = this.makeDatesEven(dateList, calendar);
        if(var20.size() > 2) {
            Date var21 = (Date)var20.get(1);
            Date var22 = (Date)var20.get(2);
            Date var23 = (Date)var20.get(dateList.size() - 2);
            Date previousLastDate = (Date)var20.get(dateList.size() - 3);
            if(var21.getTime() - lower.getTime() < (var22.getTime() - var21.getTime()) / 2L) {
                var20.remove(var21);
            }

            if(upper.getTime() - var23.getTime() < (var23.getTime() - previousLastDate.getTime()) / 2L) {
                var20.remove(var23);
            }
        }

        return var20;
    }

    protected void layoutChildren() {
        if(!this.isAutoRanging()) {
            this.currentLowerBound.set(this.getLowerBound().getTime());
            this.currentUpperBound.set(this.getUpperBound().getTime());
        }

        super.layoutChildren();
    }

    protected String getTickMarkLabel(Date date) {
        StringConverter converter = this.getTickLabelFormatter();
        if(converter != null) {
            return converter.toString(date);
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            Object dateFormat;
            if(this.actualInterval.interval == 1 && calendar.get(2) == 0 && calendar.get(5) == 1) {
                dateFormat = new SimpleDateFormat("yyyy");
            } else if(this.actualInterval.interval == 2 && calendar.get(5) == 1) {
                dateFormat = new SimpleDateFormat("MMM yy");
            } else {
                switch(this.actualInterval.interval) {
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    case 11:
                    default:
                        dateFormat = DateFormat.getDateInstance(2);
                        break;
                    case 10:
                    case 12:
                        dateFormat = DateFormat.getTimeInstance(3);
                        break;
                    case 13:
                        dateFormat = DateFormat.getTimeInstance(2);
                        break;
                    case 14:
                        dateFormat = DateFormat.getTimeInstance(0);
                }
            }

            return ((DateFormat)dateFormat).format(date);
        }
    }

    private List<Date> makeDatesEven(List<Date> dates, Calendar calendar) {
        if(dates.size() > 2) {
            ArrayList evenDates = new ArrayList();

            for(int i = 0; i < dates.size(); ++i) {
                calendar.setTime((Date)dates.get(i));
                switch(this.actualInterval.interval) {
                    case 1:
                        if(i != 0 && i != dates.size() - 1) {
                            calendar.set(2, 0);
                            calendar.set(5, 1);
                        }

                        calendar.set(11, 0);
                        calendar.set(12, 0);
                        calendar.set(13, 0);
                        calendar.set(14, 6);
                        break;
                    case 2:
                        if(i != 0 && i != dates.size() - 1) {
                            calendar.set(5, 1);
                        }

                        calendar.set(11, 0);
                        calendar.set(12, 0);
                        calendar.set(13, 0);
                        calendar.set(14, 5);
                        break;
                    case 3:
                        calendar.set(11, 0);
                        calendar.set(12, 0);
                        calendar.set(13, 0);
                        calendar.set(14, 4);
                    case 4:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    case 11:
                    default:
                        break;
                    case 5:
                        calendar.set(11, 0);
                        calendar.set(12, 0);
                        calendar.set(13, 0);
                        calendar.set(14, 3);
                        break;
                    case 10:
                        if(i != 0 && i != dates.size() - 1) {
                            calendar.set(12, 0);
                            calendar.set(13, 0);
                        }

                        calendar.set(14, 2);
                        break;
                    case 12:
                        if(i != 0 && i != dates.size() - 1) {
                            calendar.set(13, 0);
                        }

                        calendar.set(14, 1);
                        break;
                    case 13:
                        calendar.set(14, 0);
                }

                evenDates.add(calendar.getTime());
            }

            return evenDates;
        } else {
            return dates;
        }
    }

    public final ObjectProperty<Date> lowerBoundProperty() {
        return this.lowerBound;
    }

    public final Date getLowerBound() {
        return (Date)this.lowerBound.get();
    }

    public final void setLowerBound(Date date) {
        this.lowerBound.set(date);
    }

    public final ObjectProperty<Date> upperBoundProperty() {
        return this.upperBound;
    }

    public final Date getUpperBound() {
        return (Date)this.upperBound.get();
    }

    public final void setUpperBound(Date date) {
        this.upperBound.set(date);
    }

    public final StringConverter<Date> getTickLabelFormatter() {
        return (StringConverter)this.tickLabelFormatter.getValue();
    }

    public final void setTickLabelFormatter(StringConverter<Date> value) {
        this.tickLabelFormatter.setValue(value);
    }

    public final ObjectProperty<StringConverter<Date>> tickLabelFormatterProperty() {
        return this.tickLabelFormatter;
    }

    private static enum Interval {
        DECADE(1, 10),
        YEAR(1, 1),
        MONTH_6(2, 6),
        MONTH_3(2, 3),
        MONTH_1(2, 1),
        WEEK(3, 1),
        DAY(5, 1),
        HOUR_12(10, 12),
        HOUR_6(10, 6),
        HOUR_3(10, 3),
        HOUR_1(10, 1),
        MINUTE_15(12, 15),
        MINUTE_5(12, 5),
        MINUTE_1(12, 1),
        SECOND_15(13, 15),
        SECOND_5(13, 5),
        SECOND_1(13, 1),
        MILLISECOND(14, 1);

        private final int amount;
        private final int interval;

        private Interval(int interval, int amount) {
            this.interval = interval;
            this.amount = amount;
        }
    }
}
