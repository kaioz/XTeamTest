package com.jakewharton.u2020.ui;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.cocosw.xteam.R;
import com.cocosw.xteam.ui.MainActivity;
import com.squareup.spoon.Spoon;

import static android.os.SystemClock.sleep;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public final class DummyTest {
  @Rule public final ActivityTestRule<MainActivity> main =
      new ActivityTestRule<>(MainActivity.class);

  @Test public void noneOfTheThings() {
    Spoon.screenshot(main.getActivity(), "initial_state");
    sleep(SECONDS.toMillis(5)); // Long enough to see some data from mock mode.
    assertTrue(true);
    Spoon.screenshot(main.getActivity(), "displayed_data");
  }

  @Test public void testList() {
    sleep(SECONDS.toMillis(3));
    onView(withId(R.id.trending_list))
            .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    onView(withId(R.id.face)).check(matches(withText("( .-. )")));
    onView(withId(R.id.stock)).check(matches(withText("BUY NOW!"))).perform(click());
    pressBack();
    onView(withId(R.id.trending_list))
            .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
    onView(withId(R.id.face)).check(matches(withText("( .o.)")));
    onView(withId(R.id.stock)).check(matches(not(isEnabled())));
  }

}
