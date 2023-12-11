package pl.pollub.android.myapplication.ui.measurements;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.pollub.android.myapplication.R;

public class LineChartHelper {

    private final LineChart lineChart;

    public LineChartHelper(LineChart lineChart) {
        this.lineChart = lineChart;
    }

    public void displayLineChart(List<Integer> systolicValues, List<Integer> diastolicValues, List<Integer> pulseValues, List<String> dates, Context context) {
        // Odwróć listy dat
        List<String> reversedDates = new ArrayList<>(dates);
        Collections.reverse(reversedDates);

        // Odwróć listy wartości
        List<Integer> reversedSystolicValues = new ArrayList<>(systolicValues);
        Collections.reverse(reversedSystolicValues);

        List<Integer> reversedDiastolicValues = new ArrayList<>(diastolicValues);
        Collections.reverse(reversedDiastolicValues);

        List<Integer> reversedPulseValues = new ArrayList<>(pulseValues);
        Collections.reverse(reversedPulseValues);

        LineDataSet systolicDataSet = new LineDataSet(getLineData(reversedSystolicValues), "Ciśnienie skurczowe");
        systolicDataSet.setColor(ContextCompat.getColor(context, R.color.colorSystolic)); // Kolor dla ciśnienia skurczowego
        systolicDataSet.setCircleColor(ContextCompat.getColor(context, R.color.colorSystolic));
        systolicDataSet.setLineWidth(2f);
        systolicDataSet.setCircleRadius(5f);

        LineDataSet diastolicDataSet = new LineDataSet(getLineData(reversedDiastolicValues), "Ciśnienie rozkurczowe");
        diastolicDataSet.setColor(ContextCompat.getColor(context, R.color.colorDiastolic)); // Kolor dla ciśnienia rozkurczowego
        diastolicDataSet.setCircleColor(ContextCompat.getColor(context, R.color.colorDiastolic));
        diastolicDataSet.setLineWidth(2f);
        diastolicDataSet.setCircleRadius(5f);

        LineDataSet pulseDataSet = new LineDataSet(getLineData(reversedPulseValues), "Puls");
        pulseDataSet.setColor(ContextCompat.getColor(context, R.color.colorPulse)); // Kolor dla pulsu
        pulseDataSet.setCircleColor(ContextCompat.getColor(context, R.color.colorPulse));
        pulseDataSet.setLineWidth(2f);
        pulseDataSet.setCircleRadius(5f);

        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(systolicDataSet);
        dataSets.add(diastolicDataSet);
        dataSets.add(pulseDataSet);

        LineData lineData = new LineData(dataSets);
        lineChart.setData(lineData);
        lineChart.getDescription().setText("");

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                int index = (int) value;
                return index >= 0 && index < dates.size() ? dates.get(index) : "";

            }
        });

        // Dodaj marginesy na początku i na końcu osi X
        xAxis.setAxisMinimum(-0.5f);  // Początkowy margines
        xAxis.setAxisMaximum(dates.size() - 0.5f);  // Końcowy margines



        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f); // Możesz dostosować minimalną wartość na osi Y
        leftAxis.setGranularity(1f);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        Legend legend = lineChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(10f);
        legend.setTextColor(Color.BLACK);

        lineChart.invalidate();
    }

    private List<Entry> getLineData(List<Integer> values) {
        List<Entry> data = new ArrayList<>();

        for (int i = 0; i < values.size(); i++) {
            data.add(new Entry(i, values.get(i)));
        }

        return data;
    }
}
