package com.tale.rxrepositorymosby;

import android.os.Bundle;
import android.view.View;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.delegate.BaseMvpViewStateDelegateCallback;
import com.hannesdorfmann.mosby.mvp.delegate.FragmentMvpDelegate;
import com.hannesdorfmann.mosby.mvp.delegate.FragmentMvpViewStateDelegateImpl;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;

/**
 * A {@link BaseMvpLceFragment} with {@link ViewState} support.
 *
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public abstract class BaseMvpLceViewStateFragment<CV extends View, M, V extends MvpLceView<M>, P extends MvpPresenter<V>>
    extends BaseMvpLceFragment<CV, M, V, P>
    implements MvpLcemView<M>, BaseMvpViewStateDelegateCallback<V, P> {

  /**
   * The viewstate will be instantiated by calling {@link #createViewState()} in {@link
   * #onViewCreated(View, Bundle)}. Don't instantiate it by hand.
   */
  protected LceViewState<M, V> viewState;

  /**
   * A flag that indicates if the viewstate tires to restore the view right now.
   */
  private boolean restoringViewState = false;

  /**
   * Create the view state object of this class
   */
  public abstract LceViewState<M, V> createViewState();

  @Override protected FragmentMvpDelegate<V, P> getMvpDelegate() {
    if (mvpDelegate == null) {
      mvpDelegate = new FragmentMvpViewStateDelegateImpl<V, P>(this);
    }

    return mvpDelegate;
  }

  @Override public ViewState getViewState() {
    return viewState;
  }

  @Override public void setViewState(ViewState<V> viewState) {
    this.viewState = (LceViewState<M, V>) viewState;
  }

  @Override public void showContent() {
    super.showContent();
    viewState.setStateShowContent(getData());
  }

  @Override public void showError(Throwable e, boolean pullToRefresh) {
    super.showError(e, pullToRefresh);
    viewState.setStateShowError(e, pullToRefresh);
  }

  @Override public void showLoading(boolean pullToRefresh) {
    super.showLoading(pullToRefresh);
    viewState.setStateShowLoading(pullToRefresh);
  }

  @Override public void setRestoringViewState(boolean restoringViewState) {
    this.restoringViewState = restoringViewState;
  }

  @Override public boolean isRestoringViewState() {
    return restoringViewState;
  }

  @Override public void onViewStateInstanceRestored(boolean instanceStateRetained) {
    // Not needed in general. override it in subclass if you need this callback
  }

  @Override public void onNewViewStateInstance() {
    loadData(false);
  }

  @Override protected void showLightError(String msg) {
    if (isRestoringViewState()) {
      return; // Do not display toast again while restoring viewstate
    }

    super.showLightError(msg);
  }
}
