package mmm.i7bachelor_smartsale.app;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import mmm.i7bachelor_smartsale.app.Activities.SendMessageActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static bolts.Task.delay;

public class ActionsUiTest {

    //Perform UI click test.
    // Find view
    // Perform an action
    // Inspect result

    @Rule
    public ActivityTestRule<SendMessageActivity> SendMessageActivity =
            new ActivityTestRule<SendMessageActivity>(SendMessageActivity.class);


    @Test
    public void clickable_buttons_test(){
        //Check if buttons are clickable
        onView(withId(R.id.sendMessageBtnCancel))
                .check(matches(isClickable()));
        onView(withId(R.id.sendMessageBtnSend))
                .check(matches(isClickable()));
        onView(withId(R.id.sendMessageInputMessage))
                .check(matches(isClickable()));
    }

    @Test
    public void Text_input_test(){
        //Check field does not contain input.
        onView(withId(R.id.sendMessageInputMessage)).check(matches((withText(""))));

        //Input text and check whether it holds the text value
        delay(1000);
        onView(withId(R.id.sendMessageInputMessage))
                .perform(typeText("test"));
        onView(withId(R.id.sendMessageInputMessage))
                .check(matches(withText("test")));

    }
}
