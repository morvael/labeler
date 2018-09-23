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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import pl.derwinski.labeler.data.LabelSet;

/**
 *
 * @author Dominik Derwiński
 */
public final class LabelRenderer {

  public static void initGraphics(Graphics2D g) {
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
  }

  private double dpi;
  private double topDiameter;
  private double bottomDiameter;
  private double height;
  private double coveredAngle;
  private double insetsTop;
  private double insetsLeft;
  private double insetsBottom;
  private double insetsRight;
  private Font font;
  private double fontSize;
  private double fontReductionStep;
  private double horizontalMargin;
  private double verticalMargin;
  private Paint backgroundPaint;
  private Paint textPaint;

  private boolean happy;
  private double shorterRadius;
  private double longerRadius;
  private double angle;
  private double startAngle;
  private double endAngle;

  private double heightAdjustment;
  private double textLength;

  public LabelRenderer() {

  }

  private GlyphVector getAdjustedGlyphVector(Graphics2D g, double maxWidth, double maxHeight, String text) {
    FontRenderContext frc = g.getFontRenderContext();
    int n = 0;
    double fs = fontSize;
    maxWidth -= 2d * horizontalMargin;
    maxHeight -= 2d * verticalMargin;
    while (fs > 0d) {
      fs = fontSize - ((double) n * fontReductionStep);
      Font f = font.deriveFont((float) fs);
      GlyphVector gv = f.createGlyphVector(frc, text);
      Rectangle2D bounds = gv.getVisualBounds();
      GlyphVector bpgv = f.createGlyphVector(frc, "bp");
      Rectangle2D bpbounds = bpgv.getVisualBounds();
      double w = Math.max(bounds.getWidth(), bpbounds.getHeight());
      double h = Math.max(bounds.getHeight(), bpbounds.getHeight());
      if ((w <= maxWidth) && (h < maxHeight)) {
        heightAdjustment = -bpbounds.getY() - bpbounds.getHeight() / 2d;
        return gv;
      }
      n++;
    }
    throw new IllegalArgumentException(String.format("Text \"%s\" is too large", text));
  }

  private void renderText(Graphics2D g, String text, double maxWidth, double maxHeight, GlyphTransformer glyphTransformer) {
    if (text == null) {
      return;
    }

    text = text.trim();

    if (text.isEmpty()) {
      return;
    }

    GlyphVector gv = getAdjustedGlyphVector(g, maxWidth, maxHeight, text);

    Rectangle2D firstBounds = gv.getGlyphVisualBounds(0).getBounds2D();
    Rectangle2D lastBounds = gv.getGlyphVisualBounds(gv.getNumGlyphs() - 1).getBounds2D();

    double firstCenter = firstBounds.getWidth() / 2d;
    double lastCenter = lastBounds.getX() - firstBounds.getX() + lastBounds.getWidth() / 2d;

    textLength = lastCenter - firstCenter;

    int gvl = gv.getNumGlyphs();
    for (int i = 0; i < gvl; i++) {
      Rectangle2D glyphBounds = gv.getGlyphVisualBounds(i).getBounds2D();

      if (glyphBounds.getWidth() == 0d) {
        continue;
      }

      double thisCenter = glyphBounds.getX() - firstBounds.getX() + glyphBounds.getWidth() / 2d - firstCenter;
      double progress = thisCenter / textLength;

      AffineTransform at = glyphTransformer.getTransform(glyphBounds, progress);

      g.fill(at.createTransformedShape(gv.getGlyphOutline(i)));
    }
  }

  private void renderCone(int x, int y, Graphics2D g, String text) {
    calculateArc();

    Dimension bounds = getBounds(createUpperArc(0d, 0d), createLowerArc(0d, 0d));
    double dx = x + bounds.width / 2;
    double dy = happy ? y + bounds.height - longerRadius : y + longerRadius;

    Arc2D.Double upperArc = createUpperArc(dx, dy);
    Arc2D.Double lowerArc = createLowerArc(dx, dy);
    Line2D.Double rightLine = new Line2D.Double(upperArc.getEndPoint(), lowerArc.getStartPoint());
    Line2D.Double leftLine = new Line2D.Double(lowerArc.getEndPoint(), upperArc.getStartPoint());

    Path2D.Double label = new Path2D.Double();
    label.append(upperArc, true);
    label.append(rightLine, true);
    label.append(lowerArc, true);
    label.append(leftLine, true);

    g.setPaint(backgroundPaint);
    g.fill(label);

    double centerRadius = (longerRadius + shorterRadius) / 2d;
    double maxWidth = (angle * 2d * Math.PI * centerRadius) / 360d;
    double maxHeight = longerRadius - shorterRadius;

    g.setPaint(textPaint);
    renderText(g, text, maxWidth, maxHeight, (glyphBounds, progress) -> {

      double textAngle = (textLength * angle) / maxWidth;
      double textStartAngle = happy ? 270d - (textAngle / 2d) : 90d + (textAngle / 2d);
      double thisAngle = happy ? textStartAngle + (textAngle * progress) : textStartAngle - (textAngle * progress);
      double thisX = dx - glyphBounds.getX() - (glyphBounds.getWidth() / 2d);
      double thisY = dy + heightAdjustment + (happy ? centerRadius : -centerRadius);

      AffineTransform at = AffineTransform.getRotateInstance(Math.toRadians(happy ? 270d - thisAngle : 90d - thisAngle), dx, dy);
      at.translate(thisX, thisY);
      return at;
    });
  }

  private void renderQuadrangleImpl(double topWidth, double bottomWidth, double height, int x, int y, Graphics2D g, String text) {
    double dx = x;
    double dy = y;
    double width = Math.max(topWidth, bottomWidth);
    double topMargin = (width - topWidth) / 2d;
    double bottomMargin = (width - bottomWidth) / 2d;

    Line2D.Double topLine = new Line2D.Double(dx + topMargin, dy, dx + topMargin + topWidth, dy);
    Line2D.Double bottomLine = new Line2D.Double(dx + bottomMargin + bottomWidth, dy + height, dx + bottomMargin, dy + height);
    Line2D.Double rightLine = new Line2D.Double(topLine.getX2(), topLine.getY2(), bottomLine.getX1(), bottomLine.getY1());
    Line2D.Double leftLine = new Line2D.Double(bottomLine.getX2(), bottomLine.getY2(), topLine.getX1(), topLine.getY1());

    Path2D.Double label = new Path2D.Double();
    label.append(topLine, true);
    label.append(rightLine, true);
    label.append(bottomLine, true);
    label.append(leftLine, true);

    g.setPaint(backgroundPaint);
    g.fill(label);

    double maxLength = Math.min(topWidth, bottomWidth);
    double centerX = dx + width / 2d;
    double centerY = dy + height / 2d;

    g.setPaint(textPaint);
    renderText(g, text, maxLength, height, (glyphBounds, progress) -> {

      double thisX = centerX - (textLength / 2d) + (textLength * progress) - glyphBounds.getX() - (glyphBounds.getWidth() / 2d);
      double thisY = centerY + heightAdjustment;

      return AffineTransform.getTranslateInstance(thisX, thisY);
    });
  }

  private void renderCylinder(int x, int y, Graphics2D g, String text) {
    double w = ((topDiameter * Math.PI * coveredAngle) / 360d) - insetsLeft - insetsRight;
    double h = height - insetsTop - insetsBottom;
    renderQuadrangleImpl(w, w, h, x, y, g, text);
  }

  private void renderQuadrangle(int x, int y, Graphics2D g, String text) {
    double wTop = topDiameter - insetsLeft - insetsRight;
    double wBottom = bottomDiameter - insetsLeft - insetsRight;
    double h = height - insetsTop - insetsBottom;
    renderQuadrangleImpl(wTop, wBottom, h, x, y, g, text);
  }

  public void render(int x, int y, Graphics2D g, String text) {
    if (coveredAngle == 0d) {
      renderQuadrangle(x, y, g, text);
    } else {
      if (topDiameter == bottomDiameter) {
        renderCylinder(x, y, g, text);
      } else {
        renderCone(x, y, g, text);
      }
    }
  }

  private Dimension getConeBounds() {
    calculateArc();
    return getBounds(createUpperArc(0d, 0d), createLowerArc(0d, 0d));
  }

  private Dimension getCylinderBounds() {
    double w = ((topDiameter * Math.PI * coveredAngle) / 360d) - insetsLeft - insetsRight;
    double h = height - insetsTop - insetsBottom;
    return new Dimension((int) Math.ceil(w), (int) Math.ceil(h));
  }

  private Dimension getQuadrangleBounds() {
    double w = Math.max(topDiameter, bottomDiameter) - insetsLeft - insetsRight;
    double h = height - insetsTop - insetsBottom;
    return new Dimension((int) Math.ceil(w), (int) Math.ceil(h));
  }

  public Dimension getBounds() {
    if (coveredAngle == 0d) {
      return getQuadrangleBounds();
    } else {
      if (topDiameter == bottomDiameter) {
        return getCylinderBounds();
      } else {
        return getConeBounds();
      }
    }
  }

  public void validate() {
    if (dpi <= 0d) {
      throw new IllegalStateException("DPI must be greater than 0.");
    }
    if (topDiameter <= 0d) {
      throw new IllegalStateException("Top diameter must be greater than 0.");
    }
    if (bottomDiameter <= 0d) {
      throw new IllegalStateException("Bottom diameter must be greater than 0.");
    }
    if (height < 0d) {
      throw new IllegalStateException("Height must be greater or equal 0.");
    }
    if ((height == 0d) && (coveredAngle <= 0d)) {
      throw new IllegalStateException("Covered angle must be greater than 0.");
    } else if (coveredAngle < 0d) {
      throw new IllegalStateException("Covered angle must be greater or equal 0.");
    }
    if (coveredAngle > 360d) {
      throw new IllegalStateException("Covered angle must be less or equal 360.");
    }
    if (insetsTop < 0d || insetsLeft < 0d || insetsBottom < 0d || insetsRight < 0d) {
      throw new IllegalStateException("Insets must be greater or equal 0.");
    }
    if (fontSize <= 0d) {
      throw new IllegalStateException("Font size must be greater than 0.");
    }
    if (fontReductionStep <= 0d) {
      throw new IllegalStateException("Font reduction step must be greater than 0.");
    }
    if (horizontalMargin < 0d) {
      throw new IllegalStateException("Horizontal margin must be greater or equal 0.");
    }
    if (verticalMargin < 0d) {
      throw new IllegalStateException("Vertical margin must be greater or equal 0.");
    }
  }

  public void configure(LabelSet set, double dpi) {
    this.dpi = dpi;
    topDiameter = Util.mmToDots(set.getTopDiameter(), dpi);
    bottomDiameter = Util.mmToDots(set.getBottomDiameter(), dpi);
    height = Util.mmToDots(set.getHeight(), dpi);
    coveredAngle = set.getCoveredAngle();
    if (set.getInsets() != null) {
      insetsTop = Util.mmToDots(set.getInsets().getTop(), dpi);
      insetsLeft = Util.mmToDots(set.getInsets().getLeft(), dpi);
      insetsBottom = Util.mmToDots(set.getInsets().getBottom(), dpi);
      insetsRight = Util.mmToDots(set.getInsets().getRight(), dpi);
    } else {
      insetsTop = 0d;
      insetsLeft = 0d;
      insetsBottom = 0d;
      insetsRight = 0d;
    }
    font = set.getFont().createFont();
    fontSize = Util.mmToDots(set.getFont().getSize(), dpi);
    fontReductionStep = Util.mmToDots(set.getFont().getReductionStep(), dpi);
    horizontalMargin = Util.mmToDots(set.getFont().getHorizontalMargin(), dpi);
    verticalMargin = Util.mmToDots(set.getFont().getVerticalMargin(), dpi);
    backgroundPaint = set.getBackgroundPaint().createPaint();
    textPaint = set.getTextPaint().createPaint();
  }

  @FunctionalInterface
  private interface GlyphTransformer {

    AffineTransform getTransform(Rectangle2D glyphBounds, double progress);

  }

  private void calculateArc() {
    happy = topDiameter < bottomDiameter;
    shorterRadius = (happy ? topDiameter : bottomDiameter) / 2d;
    longerRadius = (happy ? bottomDiameter : topDiameter) / 2d;
    angle = coveredAngle;

    if (height > 0d) {
      double radiusDiff = longerRadius - shorterRadius;
      double scaleRatio = shorterRadius / radiusDiff;
      double h = Math.sqrt((height * height) + (radiusDiff * radiusDiff));
      shorterRadius = h * scaleRatio;
      longerRadius = h + shorterRadius;
      double fullArc = ((happy ? topDiameter : bottomDiameter) * 360d) / (shorterRadius * 2d);
      angle = (angle * fullArc) / 360d;
    }

    if (insetsTop > 0d || insetsBottom > 0d) {
      shorterRadius += happy ? insetsTop : insetsBottom;
      longerRadius -= happy ? insetsBottom : insetsTop;
    }

    if (insetsLeft > 0d || insetsRight > 0d) {
      double centerRadius = (shorterRadius + longerRadius) / 2d;
      double centerLength = (angle * 2d * Math.PI * centerRadius) / 360d;
      double adjustedLength = centerLength - insetsLeft - insetsRight;
      angle = (angle * adjustedLength) / centerLength;
    }

    double halfAngle = angle / 2d;
    startAngle = happy ? 270d - halfAngle : 90d + halfAngle;
    endAngle = happy ? 270d + halfAngle : 90d - halfAngle;
  }

  private Arc2D.Double createUpperArc(double x, double y) {
    Arc2D.Double upperArc = new Arc2D.Double();
    upperArc.setArcByCenter(x, y, happy ? shorterRadius : longerRadius, startAngle, happy ? angle : -angle, Arc2D.OPEN);
    return upperArc;
  }

  private Arc2D.Double createLowerArc(double x, double y) {
    Arc2D.Double lowerArc = new Arc2D.Double();
    lowerArc.setArcByCenter(x, y, happy ? longerRadius : shorterRadius, endAngle, happy ? -angle : angle, Arc2D.OPEN);
    return lowerArc;
  }

  private Dimension getBounds(Arc2D.Double upperArc, Arc2D.Double lowerArc) {
    Rectangle2D upperArcBounds = upperArc.getBounds2D();
    Rectangle2D lowerArcBounds = lowerArc.getBounds2D();

    int minX = (int) Math.floor(Math.min(upperArcBounds.getMinX(), lowerArcBounds.getMinX()));
    int minY = (int) Math.floor(Math.min(upperArcBounds.getMinY(), lowerArcBounds.getMinY()));
    int maxX = (int) Math.ceil(Math.max(upperArcBounds.getMaxX(), lowerArcBounds.getMaxX()));
    int maxY = (int) Math.ceil(Math.max(upperArcBounds.getMaxY(), lowerArcBounds.getMaxY()));

    int w = maxX - minX;
    if (w % 2 == 1) {
      w++;
    }

    int h = maxY - minY;
    if (h % 2 == 1) {
      h++;
    }

    return new Dimension(w, h);
  }

}
