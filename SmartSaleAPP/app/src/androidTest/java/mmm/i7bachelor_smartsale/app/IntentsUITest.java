package mmm.i7bachelor_smartsale.app;

import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import mmm.i7bachelor_smartsale.app.Activities.CreateSaleActivity;
import mmm.i7bachelor_smartsale.app.Activities.FragmentHandler;
import mmm.i7bachelor_smartsale.app.Activities.LoginActivity;
import mmm.i7bachelor_smartsale.app.Activities.MarketsActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class IntentsUITest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<LoginActivity>(
            LoginActivity.class);

    @Test
    public void open_marketsactivity_test() {
        Intents.init();
        onView(withId(R.id.MarketsButton)).perform(click());

        intended(hasComponent(MarketsActivity.class.getName()));
        Intents.release();
    }

    @Test
    public void open_createsaleactivity_test() {
        Intents.init();
        onView(withId(R.id.Fragmentbutton)).perform(click());

        intended(hasComponent(FragmentHandler.class.getName()));
        Intents.release();
    }
}
