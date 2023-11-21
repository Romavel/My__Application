package pl.pollub.android.myapplication.ui.pomiary;

import com.github.mikephil.charting.data.BarEntry;

import pl.pollub.android.myapplication.ui.pomiary.InrMeasurement;

public class InrBarEntry extends BarEntry {

    private InrMeasurement inrMeasurement;

    public InrBarEntry(float x, float y, InrMeasurement inrMeasurement) {
        super(x, y);
        this.inrMeasurement = inrMeasurement;
    }

    public InrMeasurement getInrMeasurement() {
        return inrMeasurement;
    }
}
