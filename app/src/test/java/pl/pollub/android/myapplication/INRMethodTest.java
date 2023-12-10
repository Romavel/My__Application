package pl.pollub.android.myapplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.firebase.Timestamp;

import org.junit.Before;
import org.junit.Test;

import pl.pollub.android.myapplication.ui.measurements.InrMeasurement;

public class INRMethodTest {
    private InrMeasurement inrm;

    @Before
    public void SetUp(){
        inrm = new InrMeasurement();
        inrm.setValue(4.4);
        inrm.setDocument_id("dokumenttest");
        inrm.setTime(Timestamp.now());
    }
    @Test
    public void getINR(){
        assertEquals(4.4, inrm.getValue(), 0.1);
    }
    @Test
    public void getDocument_id(){
        assertEquals("dokumenttest", inrm.getDocument_id());
    }
    @Test
    public void getTime(){
        assertEquals(Timestamp.now(), inrm.getTime());
    }
}
