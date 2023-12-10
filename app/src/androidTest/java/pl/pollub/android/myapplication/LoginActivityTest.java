package pl.pollub.android.myapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.idling.CountingIdlingResource;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

public class LoginActivityTest {

    private static final String EMAIL = "jderkacz43@gmail.com";
    private static final String PASSWORD = "123456";
    private final CountingIdlingResource countingIdlingResource = new CountingIdlingResource("MeasurementsFragment");

    @Before
    public void setUp() {
        // Rejestracja zasobu Idling przed każdym testem
        IdlingRegistry.getInstance().register(countingIdlingResource);
        // Inicjalizacja scenariusza aktywności przed każdym testem
        ActivityScenario.launch(LoginActivity.class);
    }

    @Test
    public void loginAndAddInrMeasurement() {
        // Wprowadź dane do pól email i hasło, a następnie kliknij przycisk logowania
        Espresso.onView(ViewMatchers.withId(R.id.editTextEmail)).perform(ViewActions.typeText(EMAIL));
        Espresso.onView(ViewMatchers.withId(R.id.editTextPassword)).perform(ViewActions.typeText(PASSWORD), ViewActions.closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.buttonLogin)).perform(ViewActions.click());

        // Odczekaj 3 sekundy, aby poczekać na przejście do kolejnego widoku (opcjonalne)
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Oczekiwanie na przejście do MeasurementsFragment
        countingIdlingResource.increment();

        // Sprawdź, czy użytkownik został pomyślnie zalogowany i widok MeasurementsFragment jest wyświetlany
        Espresso.onView(ViewMatchers.withId(R.id.frame_layout))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Kliknij przycisk fab
        Espresso.onView(ViewMatchers.withId(R.id.fab)).perform(ViewActions.click());

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Wybierz opcję "Dodaj pomiar INR"
        Espresso.onView(ViewMatchers.withId(R.id.layout_INR)).perform(ViewActions.click());

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Wprowadź wartość pomiaru INR i kliknij przycisk "Dodaj"
        Espresso.onView(ViewMatchers.withId(R.id.INRBox)).perform(ViewActions.typeText("3.9"), ViewActions.closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.btnAddINR)).perform(ViewActions.click());

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Oczekiwanie na zakończenie dodawania pomiaru
        countingIdlingResource.decrement();

        // Kliknij przycisk fab
        Espresso.onView(ViewMatchers.withId(R.id.fab)).perform(ViewActions.click());

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Wybierz opcję "Dodaj pomiar INR"
        Espresso.onView(ViewMatchers.withId(R.id.layout_INR_List)).perform(ViewActions.click());

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Sprawdź, czy pomiar został pomyślnie dodany do listy
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
                .check(ViewAssertions.matches(hasDescendant(withText("3.9"))));
    }
    @Test
    public void loginAndEditInrMeasurement() {
        // Wprowadź dane do pól email i hasło, a następnie kliknij przycisk logowania
        Espresso.onView(ViewMatchers.withId(R.id.editTextEmail)).perform(ViewActions.typeText(EMAIL));
        Espresso.onView(ViewMatchers.withId(R.id.editTextPassword)).perform(ViewActions.typeText(PASSWORD), ViewActions.closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.buttonLogin)).perform(ViewActions.click());

        // Odczekaj 3 sekundy, aby poczekać na przejście do kolejnego widoku (opcjonalne)
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Oczekiwanie na przejście do MeasurementsFragment
        countingIdlingResource.increment();

        // Sprawdź, czy użytkownik został pomyślnie zalogowany i widok MeasurementsFragment jest wyświetlany
        Espresso.onView(ViewMatchers.withId(R.id.frame_layout))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Kliknij przycisk fab
        Espresso.onView(ViewMatchers.withId(R.id.fab)).perform(ViewActions.click());

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Wybierz opcję "Lista pomiarów INR"
        Espresso.onView(ViewMatchers.withId(R.id.layout_INR_List)).perform(ViewActions.click());

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Espresso.onView(withId(R.id.recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, swipeRight())); // Zakładając, że chcesz edytować pierwszy element na liście

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Espresso.onView(ViewMatchers.withId(R.id.editTextValue)).perform(ViewActions.clearText(), ViewActions.closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.editTextValue)).perform(ViewActions.typeText("3.2"), ViewActions.closeSoftKeyboard());

        Espresso.onView(ViewMatchers.withId(R.id.buttonSave)).perform(ViewActions.click());

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Sprawdź, czy pomiar został pomyślnie dodany do listy
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
                .check(ViewAssertions.matches(hasDescendant(withText("3.2"))));
    }

    @Test
    public void forgotPasswordCase() {
        // Wprowadź dane do pól email i hasło, a następnie kliknij przycisk logowania
        Espresso.onView(ViewMatchers.withId(R.id.forgot_password)).perform(ViewActions.click());

        // Odczekaj 3 sekundy, aby poczekać na przejście do kolejnego widoku (opcjonalne)
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Sprawdź, czy użytkownik został pomyślnie zalogowany i widok MeasurementsFragment jest wyświetlany
        Espresso.onView(ViewMatchers.withId(R.id.dialogBox))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Kliknij przycisk fab
        Espresso.onView(ViewMatchers.withId(R.id.emailBox)).perform(ViewActions.typeText(EMAIL));
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Wybierz opcję "Lista pomiarów INR"
        Espresso.onView(ViewMatchers.withId(R.id.btnReset)).perform(ViewActions.click());

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Espresso.onView(ViewMatchers.withId(R.id.login_fragment_container))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}
