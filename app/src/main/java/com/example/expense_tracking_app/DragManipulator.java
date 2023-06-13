package com.example.expense_tracking_app;

import android.content.ClipData;
import android.content.ClipDescription;
import android.view.DragEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class DragManipulator {

    private final View draggable;
    private final float margins;
    private final float threshold;
    private float dragStartX;
    private float dragStartY;

    protected DragManipulator(View view, float threshold, float margins) {
        this.threshold = threshold;
        this.margins = margins;

        this.draggable = view;
        view.setOnLongClickListener(this::onLongClick);
        view.getRootView().setOnDragListener(this::onDrag);
    }

    public static DragManipulator manipulate(View view, float threshold, float margins) {
        return new DragManipulator(view, threshold, margins);
    }

    private boolean onLongClick(View view) {
        View.DragShadowBuilder dragShadowBuilder = new View.DragShadowBuilder(view);
        return view.startDragAndDrop(new ClipData(new ClipDescription("", new String[0]), new ClipData.Item("")), dragShadowBuilder, null, View.DRAG_FLAG_GLOBAL);
//        return true;
    }

    private boolean onDrag(View view, DragEvent dragEvent) {
        switch (dragEvent.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED: {
                dragStartX = draggable.getX();
                dragStartY = draggable.getY();
                break;
            }
            case DragEvent.ACTION_DRAG_LOCATION: {
                float x = dragEvent.getX();
                float y = dragEvent.getY();

                draggable.setX(x - draggable.getWidth() / 2f);
                draggable.setY(y - draggable.getHeight() / 2f);
                break;
            }
            case DragEvent.ACTION_DRAG_ENDED: {
                int width = view.getWidth();
                int height = view.getHeight();

                float x = draggable.getX();
                float y = draggable.getY();

                float dx = x - dragStartX;
                float dy = y - dragStartY;

                float anchorX, anchorY;
                if (Math.abs(dx) >= threshold && Math.abs(dy) >= threshold) {
                    anchorX = dx > 0 ? width - draggable.getWidth() - margins : margins;
                    anchorY = dy > 0 ? height - draggable.getHeight() - margins : margins;
                } else if (Math.abs(dx) >= threshold && Math.abs(dy) < threshold) {
                    anchorX = dx > 0 ? width - draggable.getWidth() - margins : margins;
                    anchorY = height / 2f - y < 0 ? height - draggable.getHeight() - margins : margins;
                } else if (Math.abs(dx) < threshold && Math.abs(dy) >= threshold) {
                    anchorX = width / 2f - x < 0 ? width - draggable.getWidth() - margins : margins;
                    anchorY = dy > 0 ? height - draggable.getHeight() - margins : margins;
                } else {
                    anchorX = width / 2f - x < 0 ? width - draggable.getWidth() - margins : margins;
                    anchorY = height / 2f - y < 0 ? height - draggable.getHeight() - margins : margins;
                }

                draggable.animate()
                        .setDuration(300)
                        .setInterpolator(new DecelerateInterpolator())
                        .x(anchorX)
                        .y(anchorY)
                        .start();
                break;
            }
        }
        return true;
    }
}
