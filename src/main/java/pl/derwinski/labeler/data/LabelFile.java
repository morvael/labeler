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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.NullPermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;
import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.apache.commons.lang3.Validate;
import static pl.derwinski.labeler.Util.formatCollection;

/**
 *
 * @author Dominik Derwiński
 */
@XStreamAlias("labelFile")
public final class LabelFile implements Serializable, Iterable<LabelSet> {

  private static final long serialVersionUID = 1L;

  private static XStream createXStream() {
    XStream xs = new XStream(new DomDriver());
    xs.addPermission(NoTypePermission.NONE);
    xs.addPermission(NullPermission.NULL);
    xs.addPermission(PrimitiveTypePermission.PRIMITIVES);
    xs.allowTypeHierarchy(Collection.class);
    xs.allowTypesByWildcard(new String[]{
      LabelFile.class.getPackage().getName() + ".*"
    });
    xs.ignoreUnknownElements();
    xs.setMode(XStream.NO_REFERENCES);
    xs.processAnnotations(Margins.class);
    xs.processAnnotations(LabelPaint.class);
    xs.processAnnotations(ColorPaint.class);
    xs.processAnnotations(LabelFont.class);
    xs.processAnnotations(LabelSet.class);
    xs.processAnnotations(LabelFile.class);
    return xs;
  }

  public static LabelFile load(InputStream is) throws IOException {
    try (InputStreamReader reader = new InputStreamReader(is, "utf-8")) {
      XStream xs = createXStream();
      return (LabelFile) xs.fromXML(reader);
    }
  }

  public static LabelFile load(File f) throws IOException {
    try (FileInputStream fis = new FileInputStream(f);
            BufferedInputStream bis = new BufferedInputStream(fis)) {
      return LabelFile.load(bis);
    }
  }

  @XStreamAsAttribute
  private double dpi = 600d;
  @XStreamAsAttribute
  private double pageWidth = 210d;
  @XStreamAsAttribute
  private double pageHeight = 297d;
  @XStreamAsAttribute
  private double columnSpacing = 2d;
  @XStreamAsAttribute
  private double rowSpacing = 2d;
  private Margins pageMargins = new Margins(20d, 20d, 20d, 20d);
  private LabelPaint pagePaint = new ColorPaint(Color.WHITE);
  @XStreamImplicit
  private ArrayList<LabelSet> labelSets = new ArrayList<>();

  public LabelFile() {

  }

  public LabelFile(double dpi, double pageWidth, double pageHeight, double columnSpacing, double rowSpacing, Margins pageMargins, LabelPaint pagePaint, ArrayList<LabelSet> labelSets) {
    setDpi(dpi);
    setPageWidth(pageWidth);
    setPageHeight(pageHeight);
    setRowSpacing(rowSpacing);
    setColumnSpacing(columnSpacing);
    setPageMargins(pageMargins);
    setPagePaint(pagePaint);
    setLabelSets(labelSets);
  }

  public void save(OutputStream os) throws IOException {
    try (OutputStreamWriter osw = new OutputStreamWriter(os, "utf-8")) {
      osw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      osw.write(System.getProperty("line.separator"));
      XStream xs = createXStream();
      xs.toXML(this, osw);
      osw.flush();
    }
  }

  public void save(File f) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(f, false);
            BufferedOutputStream bos = new BufferedOutputStream(fos)) {
      save(bos);
    }
  }

  public double getDpi() {
    return dpi;
  }

  public void setDpi(double dpi) {
    Validate.isTrue(dpi > 0d);
    this.dpi = dpi;
  }

  public double getPageWidth() {
    return pageWidth;
  }

  public void setPageWidth(double pageWidth) {
    Validate.isTrue(pageWidth > 0d);
    this.pageWidth = pageWidth;
  }

  public double getPageHeight() {
    return pageHeight;
  }

  public void setPageHeight(double pageHeight) {
    Validate.isTrue(pageHeight > 0d);
    this.pageHeight = pageHeight;
  }

  public double getColumnSpacing() {
    return columnSpacing;
  }

  public void setColumnSpacing(double columnSpacing) {
    Validate.isTrue(columnSpacing >= 0d);
    this.columnSpacing = columnSpacing;
  }

  public double getRowSpacing() {
    return rowSpacing;
  }

  public void setRowSpacing(double rowSpacing) {
    Validate.isTrue(rowSpacing >= 0d);
    this.rowSpacing = rowSpacing;
  }

  public Margins getPageMargins() {
    return pageMargins;
  }

  public void setPageMargins(Margins pageMargins) {
    Validate.notNull(pageMargins);
    this.pageMargins = pageMargins;
  }

  public LabelPaint getPagePaint() {
    return pagePaint;
  }

  public void setPagePaint(LabelPaint pagePaint) {
    Validate.notNull(pagePaint);
    this.pagePaint = pagePaint;
  }

  public ArrayList<LabelSet> getLabelSets() {
    return labelSets;
  }

  public void setLabelSets(ArrayList<LabelSet> labelSets) {
    Validate.notNull(labelSets);
    this.labelSets = labelSets;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("LabelFile [dpi=");
    sb.append(dpi);
    sb.append(", pageWidth=");
    sb.append(pageWidth);
    sb.append(", pageHeight=");
    sb.append(pageHeight);
    sb.append(", columnSpacing=");
    sb.append(columnSpacing);
    sb.append(", rowSpacing=");
    sb.append(rowSpacing);
    sb.append(", pageMargins=");
    sb.append(pageMargins);
    sb.append(", pagePaint=");
    sb.append(pagePaint);
    sb.append(", labelSets=");
    formatCollection(sb, labelSets);
    sb.append("]");
    return sb.toString();
  }

  @Override
  public Iterator<LabelSet> iterator() {
    return labelSets.iterator();
  }

}
