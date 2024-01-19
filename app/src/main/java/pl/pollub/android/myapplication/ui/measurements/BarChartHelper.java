package pl.pollub.android.myapplication.ui.measurements;

import android.content.Context;
import android.graphics.Color;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.pollub.android.myapplication.R;

public class BarChartHelper {

    private final BarChart barChart;

    public BarChartHelper(BarChart barChart) {
        this.barChart = barChart;
    }

    public void displayBarChart(List<Float> values, List<String> dates, double lowerThreshold, double upperThreshold) {
        // Odwróć listy dat
        List<String> reversedDates = new ArrayList<>(dates);
        Collections.reverse(reversedDates);

        // Odwróć listę wartości
        List<Float> reversedValues = new ArrayList<>(values);
        Collections.reverse(reversedValues);

        List<Integer> colors = new ArrayList<>();
        List<BarEntry> barEntries = new ArrayList<>();

        for (int i = 0; i < reversedValues.size(); i++) {
            float value = reversedValues.get(i);

            // Ustaw kolor słupka na podstawie przedziałów
            int color = getColorForValue(value, lowerThreshold, upperThreshold);
            colors.add(color);

            barEntries.add(new BarEntry(i, value));
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "Wartości INR");
        barDataSet.setColors(colors);
        barDataSet.setValueTextColor(ContextCompat.getColor(barChart.getContext(), android.R.color.black));
        barDataSet.setValueTextSize(12f);
        barDataSet.setValueFormatter(new MyValueFormatter()); // Set custom ValueFormatter

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barChart.getDescription().setText("");

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                // Sprawdź, czy indeks jest w granicach listy dat
                int index = (int) value;
                if (index >= 0 && index < reversedDates.size()) {
                    return reversedDates.get(index);
                } else {
                    return "";
                }
            }
        });

        barChart.invalidate();
    }

    private int getColorForValue(float value, double lowerThreshold, double upperThreshold) {

        if (value >= lowerThreshold && value <= upperThreshold) {
            return ContextCompat.getColor(barChart.getContext(), R.color.green); // Zielony kolor dla wartości w przedziale
        } else if ((value >= lowerThreshold - 0.25 * lowerThreshold && value < lowerThreshold) ||
                (value > upperThreshold && value <= upperThreshold + 0.25 * upperThreshold)) {
            return ContextCompat.getColor(barChart.getContext(), R.color.yellow); // Żółty kolor dla wartości w przedziałach żółtych
        } else {
            return ContextCompat.getColor(barChart.getContext(), R.color.red); // Czerwony kolor dla pozostałych wartości
        }
    }

    // Custom ValueFormatter to limit decimal places
    private static class MyValueFormatter extends ValueFormatter {
        private final DecimalFormat format;

        public MyValueFormatter() {
            format = new DecimalFormat("0.00"); // Format to two decimal places
        }

        @Override
        public String getBarLabel(BarEntry barEntry) {
            return format.format(barEntry.getY());
        }
    }
}
