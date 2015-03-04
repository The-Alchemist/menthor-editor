/**
 * Copyright(C) 2011-2014 by John Guerson, Tiago Prince, Antognoni Albuquerque
 *
 * This file is part of OLED (OntoUML Lightweight BaseEditor).
 * OLED is based on TinyUML and so is distributed under the same
 * license terms.
 *
 * OLED is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * OLED is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OLED; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.menthor.editor.umldraw.structure;

import java.awt.Color;
import java.awt.geom.GeneralPath;

import net.menthor.editor.draw.AbstractCompositeNode;
import net.menthor.editor.draw.Defaults;
import net.menthor.editor.draw.DrawingContext;
import net.menthor.editor.draw.Label;
import net.menthor.editor.draw.LabelSource;
import net.menthor.editor.draw.MultiLineLabel;
import net.menthor.editor.model.RelationEndType;
import net.menthor.editor.model.RelationType;
import net.menthor.editor.umldraw.shared.UmlNode;
import RefOntoUML.NamedElement;

/**
 * This class represents a Note element in the UML diagram. This is in general
 * a multiline element which is flexible in resizing.
 *
 * @author Wei-ju Wu
 */
public final class NoteElement extends AbstractCompositeNode
implements UmlNode, LabelSource {

  private static final long serialVersionUID = -4403380938254940682L;
  private static final int CORNER_SIZE = 10;
  private static final double MARGIN_TOP = CORNER_SIZE + 2;
  private static final Color FILL_COLOR = new Color(249, 249, 145);
  private String content;
  private Label label = new MultiLineLabel();
  private static NoteElement prototype = new NoteElement();

  /**
   * Returns the prototype instance.
   * @return the prototype instance
   */
  public static NoteElement getPrototype() { return prototype; }

  /**
   * Constructor.
   */
  private NoteElement() {
    setSize(180, 60);
    setLabelText("");
    label.setSource(this);
    label.setParent(this);
    label.setOrigin(Defaults.getInstance().getMarginLeft(), MARGIN_TOP);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object clone() {
    NoteElement cloned = (NoteElement) super.clone();
    cloned.label = (Label) label.clone();
    cloned.label.setSource(cloned);
    cloned.label.setParent(cloned);
    return cloned;
  }

  /**
   * {@inheritDoc}
   */
  public String getLabelText() { return content; }

  /**
   * {@inheritDoc}
   */
  public void setLabelText(String aText) { content = aText; }

  /**
   * {@inheritDoc}
   */
  @Override
  public void invalidate() { label.invalidate(); }

  /**
   * {@inheritDoc}
   */
  public Label getLabelAt(double mx, double my) {
    if (inInnerArea(mx, my)) return label;
    return null;
  }

  /**
   * Returns true if the specified point is in the inner Note area. It keeps
   * the margins from reacting to mouse clicks in order to improve usability.
   * @param mx the mapped mouse x position
   * @param my the mapped mouse y position
   * @return true if in label area, false otherwise
   */
  private boolean inInnerArea(double mx, double my) {
    return mx >= (getAbsoluteX1() + Defaults.getInstance().getMarginLeft()) &&
      mx <= (getAbsoluteX2() - Defaults.getInstance().getMarginRight()) &&
      my >= (getAbsoluteY1() + MARGIN_TOP) &&
      my <= (getAbsoluteY2() - Defaults.getInstance().getMarginBottom());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void draw(DrawingContext drawingContext) {
    double width = getSize().getWidth(), height = getSize().getHeight();
    double marginSide = Defaults.getInstance().getMarginSide();
    double marginBottom = Defaults.getInstance().getMarginBottom();
    if (!label.isValid()) {
      label.setSize(width - marginSide, height);
      label.recalculateSize(drawingContext);
      // Set a new height if it is the old one
      if ((label.getSize().getHeight() + MARGIN_TOP + marginBottom) > height) {
        height = label.getSize().getHeight() + (MARGIN_TOP + marginBottom);
        setSize(width, height);
      }
    }
    double x = getAbsoluteX1(), y = getAbsoluteY1();
    GeneralPath mainShape = new GeneralPath();
    mainShape.moveTo(x, y);
    mainShape.lineTo(x + width - CORNER_SIZE, y);
    mainShape.lineTo(x + width, y + CORNER_SIZE);
    mainShape.lineTo(x + width, y + height);
    mainShape.lineTo(x, y + height);
    mainShape.closePath();
    GeneralPath corner = new GeneralPath();
    corner.moveTo(x + width - CORNER_SIZE, y);
    corner.lineTo(x + width - CORNER_SIZE, y + CORNER_SIZE);
    corner.lineTo(x + width, y + CORNER_SIZE);
    corner.closePath();
    drawingContext.draw(mainShape, FILL_COLOR);
    drawingContext.draw(corner, FILL_COLOR);
    label.draw(drawingContext);
  }

  /**
   * {@inheritDoc}
   */
  public NamedElement getClassifier() { return null; }

  /**
   * {@inheritDoc}
   */
  public boolean acceptsConnection(RelationType associationType,
    RelationEndType as, UmlNode with) {
    return associationType == RelationType.NOTE_CONNECTOR;
  }
}