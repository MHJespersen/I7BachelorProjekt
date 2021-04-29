package mmm.i7bachelor_smartsale.app;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import mmm.i7bachelor_smartsale.app.Activities.CreateSaleActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static bolts.Task.delay;

//@RunWith(AndroidJUnit4.class)
public class ActionsUiTest {

    //Perform UI click test.
    // Find view
    // Perform an action
    // Inspect result

    @Rule
    public ActivityTestRule<CreateSaleActivity> createSaleActiviy =
            new ActivityTestRule<CreateSaleActivity>(CreateSaleActivity.class);


    @Test
    public void clickable_buttons_test(){
        //Check if buttons are clickable
        onView(withId(R.id.createSaleBtnGetLocation))
                .check(matches(isClickable()));
        onView(withId(R.id.createSaleSpinner))
                .check(matches(isClickable()));
        onView(withId(R.id.btnPublish))
                .check(matches(isClickable()));
        onView(withId(R.id.btnTakePhoto))
                .check(matches(isClickable()));
    }

    @Test
    public void Text_input_test(){
        //Check field does not contain input.
        onView(withId(R.id.editTxtEnterDescription)).check(matches((withText(""))));

        //Input text and check whether it holds the text value
        delay(1000);
        onView(withId(R.id.editTxtEnterDescription))
                .perform(typeText("test"));
        onView(withId(R.id.editTxtEnterDescription))
                .check(matches(withText("test")));

    }
}
