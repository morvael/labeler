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
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.awt.Color;
import java.awt.Font;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.commons.lang3.Validate;
import static pl.derwinski.labeler.Util.formatCollection;

/**
 *
 * @author Dominik Derwiński
 */
@XStreamAlias("labelSet")
public final class LabelSet implements Serializable, Iterable<String> {

  private static final long serialVersionUID = 1L;

  @XStreamAsAttribute
  private double topDiameter = 30d;
  @XStreamAsAttribute
  private double bottomDiameter = 35d;
  @XStreamAsAttribute
  private double height = 3d;
  @XStreamAsAttribute
  private double coveredAngle = 90d;
  private Margins insets = new Margins(0.25d, 0.75d, 0.25d, 0.75d);
  private LabelFont font = new LabelFont(Font.SERIF, false, false, 3d, 0.125d, 0.5d, 0.5d);
  private LabelPaint backgroundPaint = new ColorPaint(Color.BLACK);
  private LabelPaint textPaint = new ColorPaint(Color.WHITE);
  @XStreamImplicit(itemFieldName = "label")
  private ArrayList<String> labels = new ArrayList<>();

  public LabelSet() {

  }

  public LabelSet(double topDiameter, double bottomDiameter, double height, double coveredAngle, Margins insets, LabelFont font, LabelPaint backgroundPaint, LabelPaint textPaint, ArrayList<String> labels) {
    setTopDiameter(topDiameter);
    setBottomDiameter(bottomDiameter);
    setHeight(height);
    setCoveredAngle(coveredAngle);
    setInsets(insets);
    setFont(font);
    setBackgroundPaint(backgroundPaint);
    setTextPaint(textPaint);
    setLabels(labels);
  }

  public double getTopDiameter() {
    return topDiameter;
  }

  public void setTopDiameter(double topDiameter) {
    Validate.isTrue(topDiameter > 0d);
    this.topDiameter = topDiameter;
  }

  public double getBottomDiameter() {
    return bottomDiameter;
  }

  public void setBottomDiameter(double bottomDiameter) {
    Validate.isTrue(bottomDiameter > 0d);
    this.bottomDiameter = bottomDiameter;
  }

  public double getHeight() {
    return height;
  }

  public void setHeight(double height) {
    Validate.isTrue(height >= 0d);
    this.height = height;
  }

  public double getCoveredAngle() {
    return coveredAngle;
  }

  public void setCoveredAngle(double coveredAngle) {
    Validate.inclusiveBetween(0d, 360d, coveredAngle);
    this.coveredAngle = coveredAngle;
  }

  public Margins getInsets() {
    return insets;
  }

  public void setInsets(Margins insets) {
    Validate.notNull(insets);
    this.insets = insets;
  }

  public LabelFont getFont() {
    return font;
  }

  public void setFont(LabelFont font) {
    Validate.notNull(font);
    this.font = font;
  }

  public LabelPaint getBackgroundPaint() {
    return backgroundPaint;
  }

  public void setBackgroundPaint(LabelPaint backgroundPaint) {
    Validate.notNull(backgroundPaint);
    this.backgroundPaint = backgroundPaint;
  }

  public LabelPaint getTextPaint() {
    return textPaint;
  }

  public void setTextPaint(LabelPaint textPaint) {
    Validate.notNull(textPaint);
    this.textPaint = textPaint;
  }

  public ArrayList<String> getLabels() {
    return labels;
  }

  public void setLabels(ArrayList<String> labels) {
    Validate.notNull(labels);
    this.labels = labels;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("LabelSet [topDiameter=");
    sb.append(topDiameter);
    sb.append(", bottomDiameter=");
    sb.append(bottomDiameter);
    sb.append(", height=");
    sb.append(height);
    sb.append(", coveredAngle=");
    sb.append(coveredAngle);
    sb.append(", insets=");
    sb.append(insets);
    sb.append(", font=");
    sb.append(font);
    sb.append(", backgroundPaint=");
    sb.append(backgroundPaint);
    sb.append(", textPaint=");
    sb.append(textPaint);
    sb.append(", labels=");
    formatCollection(sb, labels);
    sb.append("]");
    return sb.toString();
  }

  @Override
  public Iterator<String> iterator() {
    return labels.iterator();
  }

}
