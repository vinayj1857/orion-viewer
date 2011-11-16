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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

/**
* User: mike
* Date: 19.10.11
* Time: 13:02
*/
public class FileChooser extends ArrayAdapter {

    private List currentList;

    private File parentFile = new File("..");

    private File currentFolder;

    private FilenameFilter filter = new FilenameFilter() {
        public boolean accept(File dir, String filename) {
            return new File(dir, filename).isDirectory() || filename.toLowerCase().endsWith(".pdf");
        }
    };

    public FileChooser(Context context, File folder) {
        super(context, R.layout.file, R.id.fileName);
        currentList = new ArrayList();
        changeFolder(folder);
    }

    public void changeFolder(File file) {
        changeFolderInner(file);
        this.notifyDataSetChanged();
    }

    private void changeFolderInner(File file) {
        currentList.clear();

        if (file == parentFile) {
            file = currentFolder.getParentFile();
        }

        currentFolder = file;
        if (file.getParent() != null) {
            currentList.add(parentFile);
        }

        currentList.addAll(Arrays.asList(file.listFiles(filter)));
        Collections.sort(currentList, new Comparator<File>() {
            public int compare(File f1, File f2) {
                if (f1.isDirectory() && !f2.isDirectory() || !f1.isDirectory() && f2.isDirectory()) {
                    return f1.isDirectory() ? -1 : 1;
                }
                return f1.getName().compareTo(f2.getName());
            }
        });
    }

    public int getCount() {
        return currentList.size();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
           convertView = super.getView(position, convertView, parent);

           File data = (File)getItem(position);
           if (data != null) {
               ImageView fileIcon = (ImageView) convertView.findViewById(R.id.fileImage);
               fileIcon.setImageResource(data.isDirectory() ? R.drawable.folder : R.drawable.book);
               TextView fileName = (TextView) convertView.findViewById(R.id.fileName);
               fileName.setText(data.getName());
           }
           return convertView;
    }

    public Object getItem(int position) {
        return currentList.get(position);
    }
}