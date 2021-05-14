package mmm.i7bachelor_smartsale.app;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import mmm.i7bachelor_smartsale.app.Activities.LoginActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static bolts.Task.delay;

public class LoginUiTest {

    @Rule
    public ActivityTestRule<LoginActivity> LoginActivity =
            new ActivityTestRule<LoginActivity>(LoginActivity.class);

    //This test will fail if the emulator is logged in,
    //Remember to run Logout test first if user is logged in.
    @Test
    public void test_user_login_test(){
        onView(withId(R.id.SignInBtn)).perform(click());
        delay(1000);
        onView(withText("Sign in with email"))
                .perform(click());
        onView(withHint("Email"))
                .perform(typeText("test@testmail.com"));
        delay(1000);
        onView(withText("Next"))
                .perform(click());
        onView(withHint("Password"))
                .perform(typeText("testtest"));
        delay(2000);
        onView(withText("SIGN IN"))
                .perform(click());
        delay(2000);
        onView(withId(R.id.SignInBtn)).check(matches(isDisplayed()));
    }

    @Test
    public void test_user_logout_test(){
        onView(withText("Log Out")).perform(click());
        delay(1000);
    }
}
