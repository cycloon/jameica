/**********************************************************************
 * $Source: /cvsroot/jameica/jameica/src/de/willuhn/jameica/gui/parts/FormTextPart.java,v $
 * $Revision: 1.1 $
 * $Date: 2004/04/12 19:15:58 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.willuhn.jameica.gui.parts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.rmi.RemoteException;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.HyperlinkSettings;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;

import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.util.Style;

/**
 * Freiformatierbarer Text.
 */
public class FormTextPart implements Part {

	private StringBuffer content 				= new StringBuffer();
	private ScrolledComposite container = null;
	private FormText text								= null;
	private ArrayList listener					= new ArrayList();

	/**
	 * ct.
	 */
	public FormTextPart()
	{
	}

	/**
	 * ct.
	 * @param text der anzuzeigenden Text.
	 */
	public FormTextPart(String text)
	{
		setText(text);
	}
  
	/**
	 * ct.
	 * @param text die PlainText-Datei.
	 * @throws IOException Wenn beim Lesen der Datei Fehler auftreten.
	 */
	public FormTextPart(Reader text) throws IOException
	{
		setText(text);
	}

	/**
	 * Fuegt einen Listener hinzu, der beim Klick auf einen Link ausgeloest wird.
   * @param l der Listener. Ihm wird der Wert des Links mitgegeben.
   */
  public void addHyperlinkListener(Listener l)
	{
		listener.add(l);
	}

	/**
	 * Zeigt den Text aus der uebergebenen Datei an.
   * @param text anzuzeigender Text.
   * @throws IOException
   */
  public void setText(Reader text) throws IOException
	{
		BufferedReader br =  null;
		
		try {
			br = new BufferedReader(text);

			String thisLine = null;
			StringBuffer buffer = new StringBuffer();
			while ((thisLine =  br.readLine()) != null)
			{
				if (thisLine.length() == 0) // Leerzeile
				{
					buffer.append("\n\n");
					continue;
				}
				buffer.append(thisLine.trim() + " "); // Leerzeichen am Ende einfuegen.


			}

			content = buffer; // machen wir erst wenn die gesamte Datei gelesen werden konnte
			refresh();
		}
		catch (IOException e)
		{
			throw e;
		}
		finally
		{
			try {
				br.close();
			}
			catch (Exception e) {}
		}
	}

	/**
	 * Zeigt den uebergebenen Hilfe-Text an.
	 * @param s anzuzeigender Hilfe-Text.
	 */
	public void setText(String s)
	{
		content = new StringBuffer(s);
		refresh();
	}


	/**
   * Aktualisiert die Anzeige.
   */
  private void refresh()
	{
		if (text == null || content == null)
			return;
		String s = content.toString();
		s = (s == null ? "" : s);
		boolean b = s.startsWith("<form>");
		text.setText(s,b,b);
		resize();
	}

	/**
   * Passt die Groesse des Textes an die Umgebung an.
   */
  private void resize()
	{
		if (text == null || container == null)
			return;
		text.setSize(text.computeSize(text.getParent().getClientArea().width,SWT.DEFAULT));
	}

  /**
   * @see de.willuhn.jameica.gui.parts.Part#paint(org.eclipse.swt.widgets.Composite)
   */
  public void paint(Composite parent) throws RemoteException {

		container = new ScrolledComposite(parent,SWT.H_SCROLL | SWT.V_SCROLL);
		container.setBackground(Style.COLOR_BG);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		container.setLayout(new FillLayout());
		container.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event event) {
				resize();
			}
		});

		text = new FormText(container,SWT.WRAP);

		text.setBackground(Style.COLOR_BG);
		text.setForeground(Style.COLOR_FG);

		text.setColor("header",Style.COLOR_COMMENT);
		text.setFont("header", Style.FONT_H1);

		container.setContent(text);
	
		HyperlinkSettings hs = new HyperlinkSettings(GUI.getDisplay());
		hs.setBackground(Style.COLOR_WHITE);
		hs.setForeground(Style.COLOR_LINK);
		hs.setActiveBackground(Style.COLOR_WHITE);
		hs.setActiveForeground(Style.COLOR_LINK_ACTIVE);

		text.setHyperlinkSettings(hs);

		text.addHyperlinkListener(new HyperlinkAdapter() {
      public void linkActivated(HyperlinkEvent e) {
      	for (int i=0;i<listener.size();++i)
      	{
      		Event ev = new Event();
      		ev.data = e.getHref();
      		Listener l = (Listener) listener.get(i);
      		l.handleEvent(ev);
      	}
      }
    });

		refresh();
  }
}


/**********************************************************************
 * $Log: FormTextPart.java,v $
 * Revision 1.1  2004/04/12 19:15:58  willuhn
 * @C refactoring
 * @N forms
 *
 **********************************************************************/