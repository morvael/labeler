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
import java.awt.Color;
import java.awt.Paint;
import java.util.Objects;
import org.apache.commons.lang3.Validate;

/**
 *
 * @author Dominik Derwiński
 */
@XStreamAlias("color")
public final class ColorPaint extends LabelPaint {

    private static final long serialVersionUID = 1L;

    @XStreamAsAttribute
    private int alpha = 255;
    @XStreamAsAttribute
    private int red = 255;
    @XStreamAsAttribute
    private int green = 255;
    @XStreamAsAttribute
    private int blue = 255;

    public ColorPaint() {

    }

    public ColorPaint(int alpha, int red, int green, int blue) {
        setAlpha(alpha);
        setRed(red);
        setGreen(green);
        setBlue(blue);
    }

    public ColorPaint(Color color) {
        Objects.requireNonNull(color);
        setAlpha(color.getAlpha());
        setRed(color.getRed());
        setGreen(color.getGreen());
        setBlue(color.getBlue());
    }

    public Color createColor() {
        return new Color(red, green, blue, alpha);
    }

    public Color createReversedColor() {
        return new Color(255 - red, 255 - green, 255 - blue, alpha);
    }

    @Override
    public Paint createPaint() {
        return createColor();
    }

    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        Validate.inclusiveBetween(0, 255, alpha);
        this.alpha = alpha;
    }

    public int getRed() {
        return red;
    }

    public void setRed(int red) {
        Validate.inclusiveBetween(0, 255, red);
        this.red = red;
    }

    public int getGreen() {
        return green;
    }

    public void setGreen(int green) {
        Validate.inclusiveBetween(0, 255, green);
        this.green = green;
    }

    public int getBlue() {
        return blue;
    }

    public void setBlue(int blue) {
        Validate.inclusiveBetween(0, 255, blue);
        this.blue = blue;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ColorPaint [alpha=");
        sb.append(alpha);
        sb.append(", red=");
        sb.append(red);
        sb.append(", green=");
        sb.append(green);
        sb.append(", blue=");
        sb.append(blue);
        sb.append("]");
        return sb.toString();
    }

}
