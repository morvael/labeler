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
import java.awt.Font;
import java.io.Serializable;
import org.apache.commons.lang3.Validate;

/**
 *
 * @author Dominik Derwiński
 */
@XStreamAlias("font")
public final class LabelFont implements Serializable {

  private static final long serialVersionUID = 1L;

  @XStreamAsAttribute
  private String name = Font.SERIF;
  @XStreamAsAttribute
  private boolean bold = false;
  @XStreamAsAttribute
  private boolean italic = false;
  @XStreamAsAttribute
  private double size = 3d;
  @XStreamAsAttribute
  private double reductionStep = 0.125d;
  @XStreamAsAttribute
  private double horizontalMargin = 0.5d;
  @XStreamAsAttribute
  private double verticalMargin = 0.5d;

  public LabelFont() {

  }

  public LabelFont(String name, boolean bold, boolean italic, double size, double reductionStep, double horizontalMargin, double verticalMargin) {
    this.name = name;
    this.bold = bold;
    this.italic = italic;
    this.size = size;
    this.reductionStep = reductionStep;
    this.horizontalMargin = horizontalMargin;
    this.verticalMargin = verticalMargin;
  }

  public Font createFont() {
    return new Font(name, (bold ? Font.BOLD : 0) | (italic ? Font.ITALIC : 0), 12);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    Validate.notBlank(name);
    this.name = name;
  }

  public boolean isBold() {
    return bold;
  }

  public void setBold(boolean bold) {
    this.bold = bold;
  }

  public boolean isItalic() {
    return italic;
  }

  public void setItalic(boolean italic) {
    this.italic = italic;
  }

  public double getSize() {
    return size;
  }

  public void setSize(double size) {
    Validate.isTrue(size > 0d);
    this.size = size;
  }

  public double getReductionStep() {
    return reductionStep;
  }

  public void setReductionStep(double reductionStep) {
    Validate.isTrue(reductionStep > 0d);
    this.reductionStep = reductionStep;
  }

  public double getHorizontalMargin() {
    return horizontalMargin;
  }

  public void setHorizontalMargin(double horizontalMargin) {
    Validate.isTrue(horizontalMargin >= 0d);
    this.horizontalMargin = horizontalMargin;
  }

  public double getVerticalMargin() {
    return verticalMargin;
  }

  public void setVerticalMargin(double verticalMargin) {
    Validate.isTrue(verticalMargin >= 0d);
    this.verticalMargin = verticalMargin;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("LabelFont [name=");
    sb.append(name);
    sb.append(", bold=");
    sb.append(bold);
    sb.append(", italic=");
    sb.append(italic);
    sb.append(", size=");
    sb.append(size);
    sb.append(", reductionStep=");
    sb.append(reductionStep);
    sb.append(", horizontalMargin=");
    sb.append(horizontalMargin);
    sb.append(", verticalMargin=");
    sb.append(verticalMargin);
    sb.append("]");
    return sb.toString();
  }

}
