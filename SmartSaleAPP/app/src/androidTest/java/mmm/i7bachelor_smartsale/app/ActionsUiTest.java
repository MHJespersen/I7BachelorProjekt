package mmm.i7bachelor_smartsale.app;

import androidx.test.rule.ActivityTestRule;

import com.android21buttons.fragmenttestrule.FragmentTestRule;

import org.junit.Rule;
import org.junit.Test;

import mmm.i7bachelor_smartsale.app.Activities.CreateSaleActivity;
import mmm.i7bachelor_smartsale.app.Activities.LoginActivity;

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
    public FragmentTestRule<?, CreateSaleActivity> fragmentTestRule =
            FragmentTestRule.create(CreateSaleActivity.class);
    //public ActivityTestRule<CreateSaleActivity> CreateSaleActivity =
      //      new ActivityTestRule<CreateSaleActivity>(CreateSaleActivity.class);


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
