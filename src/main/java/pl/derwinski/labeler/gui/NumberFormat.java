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

/**
 *
 * @author Dominik Derwiński
 */
public final class NumberFormat {

  private final StringBuilder sb = new StringBuilder();

  public NumberFormat() {

  }

  public String intToString(int d) {
    return Integer.toString(d);
  }

  public int stringToInt(String s) {
    if (s == null) {
      return 0;
    }
    sb.setLength(0);
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if ((c == '-') && (sb.length() == 0)) {
        sb.append('-');
      } else if (c >= '0' && c <= '9') {
        sb.append(c);
      }
    }
    if (sb.length() == 0) {
      return 0;
    }
    try {
      return Integer.parseInt(sb.toString());
    } catch (NumberFormatException ex) {
      return 0;
    }
  }

  public String doubleToString(double d) {
    return Double.toString(d);
  }

  public double stringToDouble(String s) {
    if (s == null) {
      return 0d;
    }
    sb.setLength(0);
    s = s.replace(',', '.');
    int lastDot = s.lastIndexOf('.');
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if ((c == '-') && (sb.length() == 0)) {
        sb.append('-');
      } else if ((c == '.') && (i == lastDot)) {
        sb.append('.');
      } else if (c >= '0' && c <= '9') {
        sb.append(c);
      }
    }
    if (sb.length() == 0) {
      return 0d;
    }
    try {
      return Double.parseDouble(sb.toString());
    } catch (NumberFormatException ex) {
      return 0d;
    }
  }

}
