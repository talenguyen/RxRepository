package com.tale.rxrepositorymosby;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.hannesdorfmann.mosby.mvp.MvpFragment;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.lce.LceAnimator;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;

/**
 * A {@link MvpFragment} that implements {@link MvpLceView} which gives you 3 options:
 * <ul>
 * <li>Display a loading view: A view with <b>R.id.loadingView</b> must be specified in your
 * inflated xml layout</li>
 * <li>Display a error view: A <b>TextView</b> with <b>R.id.errorView</b> must be declared in your
 * inflated xml layout</li>
 * <li>Display content view: A view witjh <b>R.id.contentView</b> must be specified in your
 * inflated
 * xml layout</li>
 * </ul>
 *
 * @param <CV> The type of the content view with the id = R.id.contentView. Can be any kind of
 * android view widget like ListView, RecyclerView, ScrollView or a simple layout like Framelayout
 * etc. (everything that extends from android.view.View)
 * @param <M> The underlying data model that will be displayed with this view
 * @param <V> The View interface that must be implemented by this view. You can use {@link
 * MvpLceView}, but if you want to add more methods you have to provide your own view interface
 * that
 * extends from {@link MvpLceView}
 * @param <P> The type of the Presenter. Must extend from {@link MvpPresenter}
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public abstract class BaseMvpLceFragment<CV extends View, M, V extends MvpLceView<M>, P extends MvpPresenter<V>>
    extends MvpFragment<V, P> implements MvpLceView<M> {

  protected View loadingView;
  protected CV contentView;
  protected View errorView;

  @CallSuper @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    loadingView = view.findViewById(R.id.loadingView);
    contentView = (CV) view.findViewById(R.id.contentView);
    errorView = view.findViewById(R.id.errorView);

    if (loadingView == null) {
      throw new NullPointerException(
          "Loading view is null! Have you specified a loading view in your layout xml file?"
              + " You have to give your loading View the id R.id.loadingView");
    }

    if (contentView == null) {
      throw new NullPointerException(
          "Content view is null! Have you specified a content view in your layout xml file?"
              + " You have to give your content View the id R.id.contentView");
    }

    if (errorView == null) {
      throw new NullPointerException(
          "Error view is null! Have you specified a content view in your layout xml file?"
              + " You have to give your error View the id R.id.errorView");
    }

    errorView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        onErrorViewClicked();
      }
    });
  }

  @Override public void showLoading(boolean pullToRefresh) {

    if (!pullToRefresh) {
      animateLoadingViewIn();
    }

    // otherwise the pull to refresh widget will already display a loading animation
  }

  /**
   * Override this method if you want to provide your own animation for showing the loading view
   */
  protected void animateLoadingViewIn() {
    LceAnimator.showLoading(loadingView, contentView, errorView);
  }

  @Override public void showContent() {
    animateContentViewIn();
  }

  /**
   * Called to animate from loading view to content view
   */
  protected void animateContentViewIn() {
    LceAnimator.showContent(loadingView, contentView, errorView);
  }

  /**
   * Get the error message for a certain Exception that will be shown on {@link
   * #showError(Throwable, boolean)}
   */
  protected abstract String getErrorMessage(Throwable e, boolean pullToRefresh);

  /**
   * The default behaviour is to display a toast message as light error (i.e. pull-to-refresh
   * error).
   * Override this method if you want to display the light error in another way (like crouton).
   */
  protected void showLightError(String msg) {
    if (getActivity() != null) {
      Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
  }

  /**
   * Called if the error view has been clicked. To disable clicking on the errorView use
   * <code>errorView.setClickable(false)</code>
   */
  protected void onErrorViewClicked() {
    loadData(false);
  }

  @Override public void showError(Throwable e, boolean pullToRefresh) {

    String errorMsg = getErrorMessage(e, pullToRefresh);

    if (pullToRefresh) {

      showLightError(errorMsg);
    } else {
      if (errorView instanceof TextView) {
        ((TextView) errorView).setText(errorMsg);
      }
      animateErrorViewIn();
    }
  }

  /**
   * Animates the error view in (instead of displaying content view / loading view)
   */
  protected void animateErrorViewIn() {
    contentView.setVisibility(View.GONE);

    // Not visible yet, so animate the view in
    AnimatorSet set = new AnimatorSet();
    ObjectAnimator in = ObjectAnimator.ofFloat(errorView, "alpha", 1f);
    ObjectAnimator loadingOut = ObjectAnimator.ofFloat(loadingView, "alpha", 0f);

    set.playTogether(in, loadingOut);
    set.setDuration(200);

    set.addListener(new AnimatorListenerAdapter() {

      @Override public void onAnimationStart(Animator animation) {
        super.onAnimationStart(animation);
        errorView.setVisibility(View.VISIBLE);
      }

      @Override public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        loadingView.setVisibility(View.GONE);
        loadingView.setAlpha(1f); // For future showLoading calls
      }
    });

    set.start();
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    loadingView = null;
    contentView = null;
    errorView = null;
  }
}
