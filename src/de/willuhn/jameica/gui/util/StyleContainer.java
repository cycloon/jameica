/**********************************************************************
 * $Source: /cvsroot/jameica/jameica/src/de/willuhn/jameica/gui/util/Attic/StyleContainer.java,v $
 * $Revision: 1.1 $
 * $Date: 2004/03/11 08:56:56 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.gui.util;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

import de.willuhn.jameica.gui.GUI;

/**
 * Kleine Hilfsklasse, die verschiedene Styling-Informationen in sich vereint.
 * Will man ein Control formatieren, dann muss Schriftart, Vordergrund- und
 * Hintergrundfarbe definiert werden. Das sind drei Eigenschaften, die nacheinander
 * gesetzt werden muessen. Hier sind sie einfach zusammengefasst und koennen
 * den Jameica-Komponenten en bloc uebergeben werden.
 * TODO: Das Ding irgendwo mal verwenden - z.Bsp. um negative Betraege rot zu machen
 */
public class StyleContainer {

  private Color foreground;
  private Color background;
  private Font font;

  /**
   * ct.
   */
  public StyleContainer() {
    font = new Font(GUI.getDisplay(),new FontData());
  }
  
  /**
   * Liefert die zu verwendende Vordergrundfarbe.
   * Die Funktion kann durchaus <code>null</code> 
   * @return Vordergrundfarbe.
   */
  public Color getForeground()
  {
    return foreground;
  }

  /**
   * Liefert die zu verwendende Hintergrundfarbe.
   * @return Hintergrundfarbe.
   */
  public Color getBackground()
  {
    return background;
  }

  /**
   * Liefert die zu verwendende Schriftart und -groesse.
   * @return Schrift.
   */
  public Font getFont()
  {
    return font;
  }

  /**
   * Speichert die Hintergrundfarbe.
   * @param c die Farbe.
   */
  public void setBackground(Color c)
  {
    this.background = c;
  }

  /**
   * Speichert die Vordergrundfarbe.
   * @param c die Farbe.
   */
  public void setForeground(Color c)
  {
    this.foreground = c;
  }

  /**
   * Speichert die Schriftart und -farbe.
   * @param f die Schrift.
   */
  public void setFont(Font f)
  {
    this.font = f;
  }
}


/**********************************************************************
 * $Log: StyleContainer.java,v $
 * Revision 1.1  2004/03/11 08:56:56  willuhn
 * @C some refactoring
 *
 **********************************************************************/