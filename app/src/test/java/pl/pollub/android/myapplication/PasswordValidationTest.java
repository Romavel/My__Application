package pl.pollub.android.myapplication;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class PasswordValidationTest {
    private PasswordValidation pv;

    @Before
    public void SetUp(){
         pv = new PasswordValidation("P@ssw0rdd");
    }
    @Test
    public void passwordLength(){
        assertTrue(this.pv.isLengthValid());
    }
    @Test
    public void upperCaseLetter(){
        assertTrue(this.pv.hasUpperCaseLetter());
    }
    @Test
    public void digit(){
        assertTrue(this.pv.hasDigit());
    }
    @Test
    public void hasSpecialChar(){
        assertTrue(this.pv.hasSpecialChar());
    }
    @Test
    public void isPasswordValid(){
        assertTrue(this.pv.isPasswordValid());
    }
}
