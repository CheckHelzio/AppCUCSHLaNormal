
package com.example.check.agendacucshlanormal;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.Interpolator;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.MeasureSpec.makeMeasureSpec;

/**
 * A transition between a FAB & another surface using a circular reveal moving along an arc.
 * <p>
 * See: https://www.google.com/design/spec/motion/transforming-material.html#transforming-material-radial-transformation
 */
public class FabTransition extends Transition {

    private static final String TAG = FabTransition.class.getSimpleName();
    private static final String EXTRA_FAB_COLOR = "EXTRA_FAB_COLOR";
    private static final String EXTRA_FAB_ICON_RES_ID = "EXTRA_FAB_ICON_RES_ID";
    private static final String PROP_BOUNDS = "fabTransform:bounds";
    private static final String[] TRANSITION_PROPERTIES = {
            PROP_BOUNDS
    };

    private final int color;
    private final int icon;

    private FabTransition(@ColorInt int fabColor, @DrawableRes int fabIconResId) {
        Log.d(TAG, "FabTransition: ");
        color = fabColor;
        icon = fabIconResId;
        setPathMotion(new GravityArcMotion());
        long DEFAULT_DURATION = 390L;
        setDuration(DEFAULT_DURATION);
    }

    /**
     * Configure {@code intent} with the extras needed to initialize this transition.
     */
    static void addExtras(@NonNull Intent intent, @ColorInt int fabColor,
                          @DrawableRes int fabIconResId) {
        Log.d(TAG, "addExtras:  ");
        intent.putExtra(EXTRA_FAB_COLOR, fabColor);
        intent.putExtra(EXTRA_FAB_ICON_RES_ID, fabIconResId);
    }

    /**
     * Create a {@link FabTransition} from the supplied {@code activity} extras and set as its
     * shared element enter/return transition.
     */
    static void setup(@NonNull Activity activity, @Nullable View target) {
        Log.d(TAG, "setup: ");
        final Intent intent = activity.getIntent();
        if (!intent.hasExtra(EXTRA_FAB_COLOR) || !intent.hasExtra(EXTRA_FAB_ICON_RES_ID)) {
            return;
        }

        final int color = intent.getIntExtra(EXTRA_FAB_COLOR, Color.TRANSPARENT);
        final int icon = intent.getIntExtra(EXTRA_FAB_ICON_RES_ID, -1);
        final FabTransition sharedEnter = new FabTransition(color, icon);
        if (target != null) {
            sharedEnter.addTarget(target);
        }
        activity.getWindow().setSharedElementEnterTransition(sharedEnter);
    }

    @Override
    public String[] getTransitionProperties() {
        return TRANSITION_PROPERTIES;
    }

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    private void captureValues(TransitionValues transitionValues) {
        final View view = transitionValues.view;
        if (view == null || view.getWidth() <= 0 || view.getHeight() <= 0) return;

        transitionValues.values.put(PROP_BOUNDS, new Rect(view.getLeft(), view.getTop(),
                view.getRight(), view.getBottom()));
    }

    @Override
    public Animator createAnimator(final ViewGroup sceneRoot,
                                   final TransitionValues startValues,
                                   final TransitionValues endValues) {
        if (startValues == null || endValues == null) return null;

        final Rect startBounds = (Rect) startValues.values.get(PROP_BOUNDS);
        final Rect endBounds = (Rect) endValues.values.get(PROP_BOUNDS);

        final boolean fromFab = endBounds.width() > startBounds.width();
        final View view = endValues.view;
        final Rect dialogBounds = fromFab ? endBounds : startBounds;
        final Rect fabBounds = fromFab ? startBounds : endBounds;
        final Interpolator fastOutSlowInInterpolator =
                AnimUtils.getFastOutSlowInInterpolator(sceneRoot.getContext());
        final long duration = getDuration();
        final long halfDuration = duration / 2;
        final long twoThirdsDuration = duration * 2 / 3;

        if (!fromFab) {
            // Force measure / layout the dialog back to it's original bounds
            view.measure(
                    makeMeasureSpec(startBounds.width(), View.MeasureSpec.EXACTLY),
                    makeMeasureSpec(startBounds.height(), View.MeasureSpec.EXACTLY));
            view.layout(startBounds.left, startBounds.top, startBounds.right, startBounds.bottom);
        }

        final int translationX = startBounds.centerX() - endBounds.centerX();
        final int translationY = startBounds.centerY() - endBounds.centerY();
        if (fromFab) {
            view.setTranslationX(translationX);
            view.setTranslationY(translationY);
        }

        // Add a color overlay to fake appearance of the FAB
        final ColorDrawable fabColor = new ColorDrawable(color);
        fabColor.setBounds(0, 0, dialogBounds.width(), dialogBounds.height());
        if (!fromFab) fabColor.setAlpha(0);
        view.getOverlay().add(fabColor);

        // Add an icon overlay again to fake the appearance of the FAB
        final Drawable fabIcon =
                ContextCompat.getDrawable(sceneRoot.getContext(), icon).mutate();
        final int iconLeft = (dialogBounds.width() - fabIcon.getIntrinsicWidth()) / 2;
        final int iconTop = (dialogBounds.height() - fabIcon.getIntrinsicHeight()) / 2;
        fabIcon.setBounds(iconLeft, iconTop,
                iconLeft + fabIcon.getIntrinsicWidth(),
                iconTop + fabIcon.getIntrinsicHeight());
        if (!fromFab) fabIcon.setAlpha(0);
        view.getOverlay().add(fabIcon);

        // Circular clip from/to the FAB size
        final Animator circularReveal;
        if (fromFab) {
            circularReveal = ViewAnimationUtils.createCircularReveal(view,
                    view.getWidth() / 2,
                    view.getHeight() / 2,
                    startBounds.width() / 2,
                    (float) Math.hypot(endBounds.width() / 2, endBounds.height() / 2));
            circularReveal.setInterpolator(AnimUtils.getFastOutLinearInInterpolator(sceneRoot.getContext()));
            //circularReveal.setInterpolator(new AnimationUtils().loadInterpolator(sceneRoot.getContext(), android.R.interpolator.fast_out_slow_in));
        } else {
            circularReveal = ViewAnimationUtils.createCircularReveal(view,
                    view.getWidth() / 2,
                    view.getHeight() / 2,
                    (float) Math.hypot(startBounds.width() / 2, startBounds.height() / 2),
                    endBounds.width() / 2);
            circularReveal.setInterpolator(
                    AnimUtils.getLinearOutSlowInInterpolator(sceneRoot.getContext()));

            // Persist the end clip i.e. stay at FAB size after the reveal has run
            circularReveal.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setOutlineProvider(new ViewOutlineProvider() {
                        @Override
                        public void getOutline(View view, Outline outline) {
                            final int left = (view.getWidth() - fabBounds.width()) / 2;
                            final int top = (view.getHeight() - fabBounds.height()) / 2;
                            outline.setOval(
                                    left, top, left + fabBounds.width(), top + fabBounds.height());
                            view.setClipToOutline(true);
                        }
                    });
                }
            });
        }
        circularReveal.setDuration(duration);

        // Translate to end position along an arc
        final Animator translate = ObjectAnimator.ofFloat(
                view,
                View.TRANSLATION_X,
                View.TRANSLATION_Y,
                fromFab ? getPathMotion().getPath(translationX, translationY, 0, 0)
                        : getPathMotion().getPath(0, 0, -translationX, -translationY));
        translate.setDuration(duration);
        translate.setInterpolator(fastOutSlowInInterpolator);

        // Fade contents of non-FAB view in/out
        List<Animator> fadeContents = null;
        if (view instanceof ViewGroup) {
            final ViewGroup vg = ((ViewGroup) view);
            fadeContents = new ArrayList<>(vg.getChildCount());
            for (int i = vg.getChildCount() - 1; i >= 0; i--) {
                final View child = vg.getChildAt(i);
                final Animator fade =
                        ObjectAnimator.ofFloat(child, View.ALPHA, fromFab ? 1f : 0f);
                if (fromFab) {
                    child.setAlpha(0f);
                }
                fade.setDuration(twoThirdsDuration);
                fade.setInterpolator(fastOutSlowInInterpolator);
                fadeContents.add(fade);
            }
        }

        // Fade in/out the fab color & icon overlays
        final Animator colorFade = ObjectAnimator.ofInt(fabColor, "alpha", fromFab ? 0 : 255);
        final Animator iconFade = ObjectAnimator.ofInt(fabIcon, "alpha", fromFab ? 0 : 255);
        if (!fromFab) {
            colorFade.setStartDelay(halfDuration);
            iconFade.setStartDelay(halfDuration);
        }
        colorFade.setDuration(halfDuration);
        iconFade.setDuration(halfDuration);
        colorFade.setInterpolator(fastOutSlowInInterpolator);
        iconFade.setInterpolator(fastOutSlowInInterpolator);

        // Work around issue with elevation shadows. At the end of the return transition the shared
        // element's shadow is drawn twice (by each activity) which is jarring. This workaround
        // still causes the shadow to snap, but it's better than seeing it double drawn.
        Animator elevation = null;
        if (!fromFab) {
            elevation = ObjectAnimator.ofFloat(view, View.TRANSLATION_Z, -view.getElevation());
            elevation.setDuration(duration);
            elevation.setInterpolator(fastOutSlowInInterpolator);
        }

        // Run all animations together
        final AnimatorSet transition = new AnimatorSet();
        transition.playTogether(circularReveal, translate, colorFade, iconFade);
        transition.playTogether(fadeContents);
        if (elevation != null) transition.play(elevation);
        if (fromFab) {
            transition.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    // Clean up
                    view.getOverlay().clear();
                }
            });
        }
        return new AnimUtils.NoPauseAnimator(transition);
    }

}