package com.testing.user.awaitility;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;
import com.testing.MainActivity;
import com.testing.rules.CreateFileRule;
import com.testing.rules.FragmentTestRule;
import java.io.File;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UserFragmentTest {

  @Rule
  public final RuleChain rules =
      RuleChain.outerRule(new CreateFileRule(getTestFile(), "{name : Sasha}"))
          .around(new FragmentTestRule<>(MainActivity.class, new UserFragment()));

  @Test
  public void nameDisplayed() {
    await()
        .atMost(5, SECONDS)
        .ignoreExceptions()
        .untilAsserted(() -> onView(ViewMatchers.withText("Sasha")).check(matches(isDisplayed())));
  }

  private static File getTestFile() {
    return new File(
        InstrumentationRegistry.getTargetContext().getFilesDir().getAbsoluteFile()
            + File.separator
            + "test_file");
  }
}
