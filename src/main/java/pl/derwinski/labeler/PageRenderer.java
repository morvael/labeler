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
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import pl.derwinski.labeler.data.LabelFile;
import pl.derwinski.labeler.data.LabelSet;
import pl.derwinski.labeler.data.Margins;

/**
 *
 * @author Dominik Derwiński
 */
public final class PageRenderer {

  public PageRenderer() {

  }

  public void render(LabelFile labelFile, File file) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(file, false);
            BufferedOutputStream bos = new BufferedOutputStream(fos)) {
      render(labelFile, bos);
    }
  }

  public void render(LabelFile labelFile, OutputStream os) throws IOException {
    int pageWidth = (int) Math.ceil(Util.mmToDots(labelFile.getPageWidth(), labelFile.getDpi()));
    int pageHeight = (int) Math.ceil(Util.mmToDots(labelFile.getPageHeight(), labelFile.getDpi()));
    int startX = 0;
    int x = 0;
    int y = 0;
    int availableWidth = pageWidth;
    int availableHeight = pageHeight;
    Margins pageMargins = labelFile.getPageMargins();
    if (pageMargins != null) {
      startX = x = (int) Math.ceil(Util.mmToDots(pageMargins.getLeft(), labelFile.getDpi()));
      y = (int) Math.ceil(Util.mmToDots(pageMargins.getTop(), labelFile.getDpi()));
      availableWidth -= x;
      availableWidth -= (int) Math.ceil(Util.mmToDots(pageMargins.getRight(), labelFile.getDpi()));
      availableHeight -= y;
      availableHeight -= (int) Math.ceil(Util.mmToDots(pageMargins.getBottom(), labelFile.getDpi()));
    }
    int spaceX = (int) Math.ceil(Util.mmToDots(labelFile.getColumnSpacing(), labelFile.getDpi()));
    int spaceY = (int) Math.ceil(Util.mmToDots(labelFile.getRowSpacing(), labelFile.getDpi()));
    BufferedImage pageImage = new BufferedImage(pageWidth, pageHeight, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = pageImage.createGraphics();
    LabelRenderer.initGraphics(g);
    g.setPaint(labelFile.getPagePaint().createPaint());
    g.fillRect(0, 0, pageWidth, pageHeight);
    g.setClip(x, y, availableWidth, availableHeight);
    LabelRenderer renderer = new LabelRenderer();
    try {
      for (LabelSet labelSet : labelFile.getLabelSets()) {
        renderer.configure(labelSet, labelFile.getDpi());
        renderer.validate();
        Dimension bounds = renderer.getBounds();
        bounds.width += spaceX;
        bounds.height += spaceY;
        for (String label : labelSet.getLabels()) {
          if (x + bounds.width > availableWidth) {
            x = startX;
            y += bounds.height;
          }
          if (y + bounds.height > availableHeight) {
            return;
          }
          renderer.render(x, y, g, label);
          x += bounds.width;
        }
        x = startX;
        y += bounds.height;
      }
    } finally {
      g.dispose();
      saveGridImage(pageImage, os, labelFile.getDpi());
    }
  }

  private void saveGridImage(BufferedImage pageImage, OutputStream output, double dpi) throws IOException {
    // https://stackoverflow.com/questions/321736/how-to-set-dpi-information-in-an-image
    Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName("png");
    while (it.hasNext()) {
      ImageWriter writer = it.next();
      ImageWriteParam writeParam = writer.getDefaultWriteParam();
      ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
      IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
      if ((metadata.isReadOnly()) || (metadata.isStandardMetadataFormatSupported() == false)) {
        continue;
      }
      setDPI(metadata, dpi);

      try (ImageOutputStream stream = ImageIO.createImageOutputStream(output)) {
        writer.setOutput(stream);
        writer.write(metadata, new IIOImage(pageImage, null, metadata), writeParam);
      }
      return;
    }
  }

  private void setDPI(IIOMetadata metadata, double dpi) throws IIOInvalidTreeException {
    double dotsPerMilli = Util.dpiToDpmm(dpi);

    IIOMetadataNode horizontal = new IIOMetadataNode("HorizontalPixelSize");
    horizontal.setAttribute("value", Double.toString(dotsPerMilli));

    IIOMetadataNode vertical = new IIOMetadataNode("VerticalPixelSize");
    vertical.setAttribute("value", Double.toString(dotsPerMilli));

    IIOMetadataNode dimension = new IIOMetadataNode("Dimension");
    dimension.appendChild(horizontal);
    dimension.appendChild(vertical);

    IIOMetadataNode root = new IIOMetadataNode("javax_imageio_1.0");
    root.appendChild(dimension);

    metadata.mergeTree("javax_imageio_1.0", root);
  }

}
