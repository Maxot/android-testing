package com.testing.user.debug;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.testing.rules.RxImmediateSchedulerRule;
import com.testing.user.rx.NameRepository;
import io.reactivex.subjects.PublishSubject;
import java.util.logging.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class UserPresenterTest {

  static final int TIMEOUT_SEC = 2;
  static final String NAME = "Sasha";

  @Rule public final MockitoRule rule = MockitoJUnit.rule();
  @Rule public final RxImmediateSchedulerRule timeoutRule = new RxImmediateSchedulerRule();

  @Mock UserPresenter.Listener listener;
  @Mock NameRepository nameRepository;
  @Mock Logger logger;
  PublishSubject<String> nameObservable = PublishSubject.create();
  UserPresenter presenter;

  @Before
  public void setUp() {
    when(nameRepository.getName()).thenReturn(nameObservable.firstOrError());
    presenter = new UserPresenter(listener, nameRepository, logger);
  }

  @Test
  public void getUserName() {
    presenter.getUserName();
    timeoutRule.getTestScheduler().advanceTimeBy(TIMEOUT_SEC - 1, SECONDS);
    nameObservable.onNext(NAME);
    verify(listener).onUserNameLoaded(NAME);
  }

  @Test
  public void getUserName_timeout() {
    presenter.getUserName();
    timeoutRule.getTestScheduler().advanceTimeBy(TIMEOUT_SEC + 1, SECONDS);
    nameObservable.onNext(NAME);
    verify(listener).onGettingUserNameError(any());
  }
}
