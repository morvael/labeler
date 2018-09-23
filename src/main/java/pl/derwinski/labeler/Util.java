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
package pl.derwinski.labeler;

import java.io.File;
import java.util.Collection;

/**
 *
 * @author Dominik Derwiński
 */
public final class Util {

  public static final double MM_PER_INCH = 25.4d;

  private Util() {
    throw new UnsupportedOperationException();
  }

  public static double mmToDots(double mm, double dpi) {
    return (mm * dpi) / MM_PER_INCH;
  }

  public static double dpiToDpmm(double dpi) {
    return dpi / MM_PER_INCH;
  }

  public static void formatCollection(StringBuilder sb, Collection<?> c) {
    if (c != null) {
      sb.append("[");
      for (Object o : c) {
        sb.append(o);
        sb.append(", ");
      }
      if (c.isEmpty() == false) {
        sb.setLength(sb.length() - 2);
      }
      sb.append("]");
    } else {
      sb.append((Object) null);
    }
  }

  public static String getFileName(File f) {
    String name = f.getName();
    int i = name.lastIndexOf('.');
    return i == -1 ? name : name.substring(0, i);
  }

}
