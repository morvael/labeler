/*
 * Copyright (C) 2018 Dominik Derwiński
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
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
package pl.derwinski.labeler.gui;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Dominik Derwiński
 */
public final class ExtensionFileFilter extends FileFilter {

  public static final ExtensionFileFilter XML = new ExtensionFileFilter("xml");
  public static final ExtensionFileFilter PNG = new ExtensionFileFilter("png");

  private final String extenstion;
  private final String description;

  private ExtensionFileFilter(String extension) {
    this.extenstion = String.format(".%s", extension.toLowerCase());
    this.description = String.format("%s files", extension.toUpperCase());
  }

  @Override
  public boolean accept(File f) {
    if (f.isDirectory()) {
      return true;
    } else {
      return (f.isFile()) && (f.getName().toLowerCase().endsWith(extenstion));
    }
  }

  @Override
  public String getDescription() {
    return description;
  }

}
