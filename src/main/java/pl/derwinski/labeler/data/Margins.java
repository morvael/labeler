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
package pl.derwinski.labeler.data;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import java.io.Serializable;
import org.apache.commons.lang3.Validate;

/**
 *
 * @author Dominik Derwiński
 */
@XStreamAlias("margins")
public final class Margins implements Serializable {

  private static final long serialVersionUID = 1L;

  @XStreamAsAttribute
  private double top = 0d;
  @XStreamAsAttribute
  private double left = 0d;
  @XStreamAsAttribute
  private double bottom = 0d;
  @XStreamAsAttribute
  private double right = 0d;

  public Margins() {

  }

  public Margins(double top, double left, double bottom, double right) {
    setTop(top);
    setLeft(left);
    setBottom(bottom);
    setRight(right);
  }

  public double getTop() {
    return top;
  }

  public void setTop(double top) {
    Validate.isTrue(top >= 0d);
    this.top = top;
  }

  public double getLeft() {
    return left;
  }

  public void setLeft(double left) {
    Validate.isTrue(left >= 0d);
    this.left = left;
  }

  public double getBottom() {
    return bottom;
  }

  public void setBottom(double bottom) {
    Validate.isTrue(bottom >= 0d);
    this.bottom = bottom;
  }

  public double getRight() {
    return right;
  }

  public void setRight(double right) {
    Validate.isTrue(right >= 0d);
    this.right = right;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Margins [top=");
    sb.append(top);
    sb.append(", left=");
    sb.append(left);
    sb.append(", bottom=");
    sb.append(bottom);
    sb.append(", right=");
    sb.append(right);
    sb.append("]");
    return sb.toString();
  }

}
