/*
 * Project:  NextGIS Mobile
 * Purpose:  Mobile GIS for Android.
 * Authors:  Stanislav Petriakov
 * *****************************************************************************
 * Copyright (c) 2015 NextGIS, info@nextgis.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.nextgis.maplibui.api;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import com.nextgis.maplib.datasource.GeoPoint;
import com.nextgis.maplib.map.MapDrawable;


public abstract class Overlay
{
    private boolean mIsVisible = true;


    public abstract void draw(
            Canvas canvas,
            MapDrawable mapDrawable);

    public abstract void drawOnPanning(
            Canvas canvas,
            PointF mCurrentMouseOffset);

    public abstract void drawOnZooming(
            Canvas canvas,
            PointF mCurrentFocusLocation,
            float scale);


    protected void draw(
            Canvas canvas,
            OverlayItem overlayItem)
    {
        canvas.drawBitmap(overlayItem.getMarker(), overlayItem.getScreenX(),
                          overlayItem.getScreenY(), null);
    }


    public void drawOnPanning(
            Canvas canvas,
            PointF currentMouseOffset,
            OverlayItem overlayItem)
    {
        canvas.drawBitmap(overlayItem.getMarker(), overlayItem.getScreenX() - currentMouseOffset.x,
                          overlayItem.getScreenY() - currentMouseOffset.y, null);
    }


    protected void drawOnZooming(
            Canvas canvas,
            PointF currentFocusLocation,
            float scale,
            OverlayItem overlayItem,
            boolean scaleMarker)
    {
        GeoPoint offset = getScaledOffset(currentFocusLocation, overlayItem, scale, scaleMarker);
        float zoomedX = (float) (overlayItem.getScreenX() - offset.getX());
        float zoomedY = (float) (overlayItem.getScreenY() - offset.getY());

        Matrix matrix = new Matrix();

        if (scaleMarker) {
            matrix.postScale(scale, scale);
        }

        matrix.postTranslate(zoomedX, zoomedY);
        canvas.drawBitmap(overlayItem.getMarker(), matrix, null);
    }


    public GeoPoint getScaledOffset(
            PointF currentFocusLocation,
            OverlayItem overlayItem,
            float scale,
            boolean scaleMarker)
    {
        double x = overlayItem.getScreenCoordinates().x;
        double y = overlayItem.getScreenCoordinates().y;
        int markerWidth = overlayItem.getMarker().getWidth();
        int markerHeight = overlayItem.getMarker().getHeight();

        double dx = x + markerWidth / 2 + currentFocusLocation.x;
        double dy = y + markerHeight / 2 + currentFocusLocation.y;

        GeoPoint offset = new GeoPoint();

        if (!scaleMarker) {
            markerHeight = markerWidth = 0;
        }

        float scaledWidth = markerWidth * scale;
        float scaledHeight = markerHeight * scale;

        offset.setX((scaledWidth - markerWidth) / 2 + (1 - scale) * dx);
        offset.setY((scaledHeight - markerHeight) / 2 + (1 - scale) * dy);

        return offset;
    }


    public void setVisibility(boolean isVisible)
    {
        mIsVisible = isVisible;
    }


    public boolean isVisible()
    {
        return mIsVisible;
    }

}