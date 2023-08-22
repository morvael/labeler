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

import java.awt.Component;
import java.io.File;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import pl.derwinski.labeler.PageRenderer;
import pl.derwinski.labeler.Util;
import pl.derwinski.labeler.data.LabelFile;
import pl.derwinski.labeler.data.LabelSet;

/**
 *
 * @author Dominik Derwiński
 */
public final class Editor {

    private final LinkedHashSet<EditorListener> listeners;
    private final NumberFormat format;

    private LabelFile labelFile;
    private LabelSet labelSet;
    private int index;

    private File dataFile;
    private File imageFile;

    public Editor() {
        this.listeners = new LinkedHashSet<>();
        this.format = new NumberFormat();
        newLabelFile();
    }

    public NumberFormat getFormat() {
        return format;
    }

    public LabelFile getLabelFile() {
        return labelFile;
    }

    public LabelSet getLabelSet() {
        return labelSet;
    }

    public int getIndex() {
        return index;
    }

    public File getDataFile() {
        return dataFile;
    }

    public File getImageFile() {
        return imageFile;
    }

    public boolean newLabelFile() {
        labelFile = new LabelFile();
        dataFile = null;
        imageFile = null;
        newLabelSet();
        return true;
    }

    public boolean openLabelFile(Component parent) {
        Objects.requireNonNull(parent);
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(false);
        fc.setFileFilter(ExtensionFileFilter.XML);
        if (dataFile != null) {
            fc.setSelectedFile(dataFile);
        }
        if (fc.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            try {
                labelFile = LabelFile.load(fc.getSelectedFile());
                if (labelFile.getLabelSets().isEmpty() == false) {
                    index = 0;
                    labelSet = labelFile.getLabelSets().get(index);
                    triggerChanged();
                } else {
                    newLabelSet();
                }
                dataFile = fc.getSelectedFile();
                imageFile = new File(dataFile.getParentFile(), String.format("%s.png", Util.getFileName(dataFile)));
                return true;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Failed to load file.", "Load", JOptionPane.ERROR_MESSAGE);
            }
        }
        return false;
    }

    public boolean saveLabelFile(Component parent) {
        Objects.requireNonNull(parent);
        if (dataFile != null) {
            try {
                labelFile.save(dataFile);
                return true;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Failed to save file.", "Save", JOptionPane.ERROR_MESSAGE);
            }
            return false;
        } else {
            return saveAsLabelFile(parent);
        }
    }

    public boolean saveAsLabelFile(Component parent) {
        Objects.requireNonNull(parent);
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(false);
        fc.setFileFilter(ExtensionFileFilter.XML);
        if (dataFile != null) {
            fc.setSelectedFile(dataFile);
        }
        if (fc.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            try {
                File f = new File(fc.getSelectedFile().getParent(), String.format("%s.xml", Util.getFileName(fc.getSelectedFile())));
                labelFile.save(f);
                dataFile = f;
                imageFile = new File(f.getParentFile(), String.format("%s.png", Util.getFileName(dataFile)));
                return true;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Failed to save file.", "Save As", JOptionPane.ERROR_MESSAGE);
            }
        }
        return false;
    }

    public boolean generateImageFile(Component parent) {
        Objects.requireNonNull(parent);
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(false);
        fc.setFileFilter(ExtensionFileFilter.PNG);
        if (imageFile != null) {
            fc.setSelectedFile(imageFile);
        }
        if (fc.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            try {
                File f = new File(fc.getSelectedFile().getParent(), String.format("%s.png", Util.getFileName(fc.getSelectedFile())));
                new PageRenderer().render(labelFile, f);
                imageFile = f;
                return true;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Failed to generate image.", "Generate", JOptionPane.ERROR_MESSAGE);
            }
        }
        return false;
    }

    public boolean newLabelSet() {
        labelSet = new LabelSet();
        labelSet.getLabels().add("Example");
        labelFile.getLabelSets().add(labelSet);
        index = labelFile.getLabelSets().size() - 1;
        triggerChanged();
        return true;
    }

    public boolean gotoFirstLabelSet() {
        if (index != 0) {
            index = 0;
            labelSet = labelFile.getLabelSets().get(index);
            triggerChanged();
            return true;
        }
        return false;
    }

    public boolean gotoPreviousLabelSet() {
        if (index > 0) {
            index--;
            labelSet = labelFile.getLabelSets().get(index);
            triggerChanged();
            return true;
        }
        return false;
    }

    public String getPosition() {
        return String.format("%d of %d", index + 1, labelFile.getLabelSets().size());
    }

    public boolean gotoNextLabelSet() {
        if (index + 1 < labelFile.getLabelSets().size()) {
            index++;
            labelSet = labelFile.getLabelSets().get(index);
            triggerChanged();
            return true;
        }
        return false;
    }

    public boolean gotoLastLabelSet() {
        if (index != labelFile.getLabelSets().size() - 1) {
            index = labelFile.getLabelSets().size() - 1;
            labelSet = labelFile.getLabelSets().get(index);
            triggerChanged();
            return true;
        }
        return false;
    }

    public boolean deleteLabelSet() {
        if (labelFile.getLabelSets().size() > 1) {
            labelFile.getLabelSets().remove(index);
            index = Math.min(index, labelFile.getLabelSets().size() - 1);
            labelSet = labelFile.getLabelSets().get(index);
            triggerChanged();
            return true;
        }
        return false;
    }

    public void addListener(EditorListener listener) {
        listeners.add(listener);
    }

    public void removeListener(EditorListener listener) {
        listeners.remove(listener);
    }

    public void triggerChanged() {
        for (EditorListener listener : listeners) {
            listener.changed(labelFile, labelSet);
        }
    }

    public void triggerAlteredFile() {
        for (EditorListener listener : listeners) {
            listener.altered(labelFile);
        }
    }

    public void triggerAlteredSet() {
        for (EditorListener listener : listeners) {
            listener.altered(labelSet);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Editor [labelFile=");
        sb.append(labelFile);
        sb.append(", labelSet=");
        sb.append(labelSet);
        sb.append(", index=");
        sb.append(index);
        sb.append(", dataFile=");
        sb.append(dataFile);
        sb.append(", imageFile=");
        sb.append(imageFile);
        sb.append("]");
        return sb.toString();
    }

    public void setInt(Object o, JTextField txt, Consumer<Integer> target, Supplier<Integer> backSource) {
        try {
            target.accept(format.stringToInt(txt.getText()));
            if (o == labelFile) {
                triggerAlteredFile();
            } else if (o == labelSet) {
                triggerAlteredSet();
            }
        } catch (Exception ex) {

        } finally {
            txt.setText(format.intToString(backSource.get()));
        }
    }

    public void setDouble(Object o, JTextField txt, Consumer<Double> target, Supplier<Double> backSource) {
        try {
            target.accept(format.stringToDouble(txt.getText()));
            if (o == labelFile) {
                triggerAlteredFile();
            } else if (o == labelSet) {
                triggerAlteredSet();
            }
        } catch (Exception ex) {

        } finally {
            txt.setText(format.doubleToString(backSource.get()));
        }
    }

}
