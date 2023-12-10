package pl.pollub.android.myapplication;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class PasswordValidationTest {
    private PasswordValidation pv;

   // @Before
    //public void SetUp(){
    //     pv = new PasswordValidation("P@ssw0rdd");
    //}
    @Test
    public void testIsPasswordValid() {
        PasswordValidation passwordValidation = new PasswordValidation();

        // Pozytywny przypadek testowy, hasło spełnia warunki
        assertTrue(passwordValidation.isPasswordValid("Abc1234!"));

        // Negatywny przypadek testowy, hasło nie spełnia warunków
        assertFalse(passwordValidation.isPasswordValid("abc123"));
    }
    @Test
    public void testIsLengthValid() {
        PasswordValidation passwordValidation = new PasswordValidation();

        // Pozytywny przypadek testowy, hasło ma co najmniej 8 znaków
        assertTrue(passwordValidation.isLengthValid("Abc1234!"));

        // Negatywny przypadek testowy, hasło ma mniej niż 8 znaków
        assertFalse(passwordValidation.isLengthValid("Abc123"));
    }

    @Test
    public void testHasUpperCaseLetter() {
        PasswordValidation passwordValidation = new PasswordValidation();

        // Pozytywny przypadek testowy, hasło zawiera wielką literę
        assertTrue(passwordValidation.hasUpperCaseLetter("Abc123!"));

        // Negatywny przypadek testowy, hasło nie zawiera wielkiej litery
        assertFalse(passwordValidation.hasUpperCaseLetter("abc123!"));
    }

    @Test
    public void testHasDigit() {
        PasswordValidation passwordValidation = new PasswordValidation();

        // Pozytywny przypadek testowy, hasło zawiera cyfrę
        assertTrue(passwordValidation.hasDigit("Abc123!"));

        // Negatywny przypadek testowy, hasło nie zawiera cyfry
        assertFalse(passwordValidation.hasDigit("Abcdef!"));
    }

    @Test
    public void testHasSpecialChar() {
        PasswordValidation passwordValidation = new PasswordValidation();

        // Pozytywny przypadek testowy, hasło zawiera znak specjalny
        assertTrue(passwordValidation.hasSpecialChar("Abc123!"));

        // Negatywny przypadek testowy, hasło nie zawiera znaku specjalnego
        assertFalse(passwordValidation.hasSpecialChar("Abc123"));
    }
}
