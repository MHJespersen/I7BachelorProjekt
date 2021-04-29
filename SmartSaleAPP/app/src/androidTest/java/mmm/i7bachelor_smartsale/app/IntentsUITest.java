package mmm.i7bachelor_smartsale.app;

import android.content.ComponentName;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import mmm.i7bachelor_smartsale.app.Activities.CreateSaleActivity;
import mmm.i7bachelor_smartsale.app.Activities.LoginActivity;
import mmm.i7bachelor_smartsale.app.Activities.MainActivity;
import mmm.i7bachelor_smartsale.app.Activities.MarketsActivity;

import static android.support.test.InstrumentationRegistry.getTargetContext;
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
        onView(withId(R.id.CreateASale)).perform(click());

        intended(hasComponent(CreateSaleActivity.class.getName()));
        Intents.release();
    }
}
