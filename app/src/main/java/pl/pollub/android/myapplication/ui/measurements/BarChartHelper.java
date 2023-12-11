package pl.pollub.android.myapplication.ui.measurements;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BarChartHelper {

    private final BarChart barChart;

    public BarChartHelper(BarChart barChart) {
        this.barChart = barChart;
    }

    public void displayBarChart(List<Float> values, List<String> dates) {
        // Odwróć listy dat
        List<String> reversedDates = new ArrayList<>(dates);
        Collections.reverse(reversedDates);

        // Odwróć listę wartości
        List<Float> reversedValues = new ArrayList<>(values);
        Collections.reverse(reversedValues);

        BarDataSet barDataSet = new BarDataSet(getData(reversedValues), "Wartości INR");
        barDataSet.setColors(new int[]{android.R.color.holo_blue_light}, ContextCompat.getColor(barChart.getContext(), android.R.color.holo_blue_light));
        barDataSet.setValueTextColor(ContextCompat.getColor(barChart.getContext(), android.R.color.black));
        barDataSet.setValueTextSize(10f);

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

    private List<BarEntry> getData(List<Float> values) {
        List<BarEntry> data = new ArrayList<>();

        for (int i = 0; i < values.size(); i++) {
            data.add(new BarEntry(i, values.get(i)));
        }

        return data;
    }
}
