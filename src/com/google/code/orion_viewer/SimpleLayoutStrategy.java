package com.google.code.orion_viewer;

/*Orion Viewer is a pdf viewer for Nook Classic based on mupdf

Copyright (C) 2011  Michael Bogdanov

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

import android.graphics.Point;
import android.util.Log;

/**
 * User: mike
 * Date: 15.10.11
 * Time: 13:18
 */
public class SimpleLayoutStrategy implements LayoutStrategy {

    public static final int WIDTH = 600;

    public static final int HEIGHT = 760;

    public static final int OVERLAP = 15;

    private DocumentWrapper doc;

    private int leftMargin, topMargin, rightMargin, bottomMargin;

    private int zoom;

    private int rotation;

    public SimpleLayoutStrategy(DocumentWrapper doc) {
        this.doc = doc;
    }

    public void nextPage(LayoutPosition info) {
        if (info.cellX < info.maxX) {
            info.cellX += 1;
        } else if (info.cellY < info.maxY) {
            info.cellX = 0;
            info.cellY += 1;
        } else {
            if (info.pageNumber < doc.getPageCount() - 1) {
                reset(info, info.pageNumber + 1);
            }
        }
        Log.d(Common.LOGTAG, "new cellX = " + info.cellX + " cellY =" + info.cellY);
    }

    public void prevPage(LayoutPosition info) {
        if (info.cellX > 0) {
            info.cellX -= 1;
        } else if (info.cellY > 0) {
            info.cellX = info.maxX;
            info.cellY -= 1;
        } else {
            if (info.pageNumber > 0) {
                reset(info, info.pageNumber - 1);
                info.cellX = info.maxX;
                info.cellY = info.maxY;
            }
        }


        Log.d(Common.LOGTAG, "new cellX = " + info.cellX + " maxX =" + info.cellY);
    }
    public boolean changeRotation(int rotation) {
        if (this.rotation != rotation) {
            this.rotation = rotation;
            return true;
        }
        return false;
    }

    public void reset(LayoutPosition info, int pageNum) {
        info.pageNumber = pageNum;
        //original width and height without cropped margins
        PageInfo pinfo = doc.getPageInfo(pageNum);
        info.pageWidth = pinfo.width - leftMargin - rightMargin;
        info.pageHeight = pinfo.height - topMargin - bottomMargin;

        info.pieceWidth = rotation == 0 ? WIDTH : HEIGHT;
        info.pieceHeight = rotation == 0 ? HEIGHT : WIDTH;

        //calc zoom
        if (zoom <= 0) {
            //zoom by width
            info.docZoom = 1.0f * info.pieceWidth / info.pageWidth;
        } else {
            info.docZoom = 0.01f * zoom;
        }

        //zoomed with and height
        info.pageWidth = (int)(info.docZoom * info.pageWidth);
        info.pageHeight = (int) (info.docZoom * info.pageHeight);

        info.maxX = (info.pageWidth - OVERLAP) / (info.pieceWidth - OVERLAP) + ((info.pageWidth - OVERLAP) % (info.pieceWidth - OVERLAP) == 0 ? 0 : 1)  - 1;
        info.maxY = (info.pageHeight - OVERLAP) / (info.pieceHeight - OVERLAP) + ((info.pageHeight - OVERLAP) % (info.pieceHeight - OVERLAP) == 0 ?  0: 1) -1;

        info.cellX = 0;
        info.cellY = 0;
    }

    public boolean changeZoom(int zoom) {
        if (this.zoom != zoom) {
            this.zoom = zoom;
            return true;
        }
        return false;
    }

    public int getZoom() {
        return zoom;
    }

    public boolean changeMargins(int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        if (leftMargin != this.leftMargin || rightMargin != this.rightMargin || topMargin != this.topMargin || bottomMargin != this.bottomMargin) {
            this.leftMargin = leftMargin;
            this.rightMargin = rightMargin;
            this.topMargin = topMargin;
            this.bottomMargin = bottomMargin;
            return true;
        }
        return false;
    }

    public int getRotation() {
        return rotation;
    }


    public void init(LastPageInfo info) {
        changeMargins(info.leftMargin, info.topMargin, info.rightMargin, info.bottomMargin);
        changeRotation(info.rotation);
        changeZoom(info.zoom);
    }

    public void serialize(LastPageInfo info) {
        info.leftMargin = leftMargin;
        info.rightMargin = rightMargin;
        info.topMargin = topMargin;
        info.bottomMargin = bottomMargin;
        info.rotation = rotation;
        info.zoom = zoom;
    }

    public Point convertToPoint(LayoutPosition pos) {
        return new Point((int)(pos.docZoom * leftMargin + pos.cellX * (pos.pieceWidth - OVERLAP)),
                (int) (pos.docZoom * topMargin + pos.cellY * (pos.pieceHeight - OVERLAP)));
    }
}