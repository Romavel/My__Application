package pl.pollub.android.myapplication;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.idling.CountingIdlingResource;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.assertion.ViewAssertions;

import org.junit.Before;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class RegisterActivityTest {
    private final CountingIdlingResource countingIdlingResource = new CountingIdlingResource("MeasurementsFragment");

    @Before
    public void setUp() {
        // Rejestracja zasobu Idling przed każdym testem
        IdlingRegistry.getInstance().register(countingIdlingResource);
        // Inicjalizacja scenariusza aktywności przed każdym testem
        ActivityScenario.launch(LoginActivity.class);
    }

    @Test
    public void RegisterTest() {
        //Wygenerowanie losowego emaila
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        String generatedString = buffer.toString();
        String email = generatedString + "@email.com";
        String password = "P@ssw0rdddd";
        Espresso.onView(ViewMatchers.withId(R.id.textViewRegister)).perform(ViewActions.click());

        // Wprowadź dane do pól email i hasło, a następnie kliknij przycisk logowania
        Espresso.onView(ViewMatchers.withId(R.id.editTextUsername))
                .perform(ViewActions.typeText(email));
        Espresso.onView(ViewMatchers.withId(R.id.editTextEmail))
                .perform(ViewActions.typeText(email));
        Espresso.onView(ViewMatchers.withId(R.id.editTextPassword))
                .perform(ViewActions.typeText(password));
        Espresso.onView(ViewMatchers.withId(R.id.editTextConfirmPassword))
                .perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.buttonRegister)).perform(ViewActions.click());

        // Odczekaj 3 sekundy, aby poczekać na przejście do kolejnego widoku (opcjonalne)
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Espresso.onView(ViewMatchers.withId(R.id.login_fragment_container)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}