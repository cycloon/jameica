/**********************************************************************
 * $Source: /cvsroot/jameica/jameica/src/de/willuhn/jameica/gui/parts/TreePart.java,v $
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

import java.rmi.RemoteException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBObjectNode;
import de.willuhn.jameica.gui.controller.*;
import de.willuhn.jameica.gui.util.Style;

/**
 * Erzeugt einen Baum.
 * Dabei werden alle Kind-Objekte rekursiv dargestellt.
 * @author willuhn
 */
public class TreePart implements Part
{

  private AbstractControl controller;
  private Composite composite;
  private DBObjectNode object = null;
  private DBIterator list = null;
  private org.eclipse.swt.widgets.Tree tree = null;
    
	/**
   * Erzeugt einen neuen Tree basierend auf dem uebergebenen Objekt.
   * @param object Das Objekt, fuer das der Baum erzeugt werden soll. 
   * @param controller der AbstractControl, der bei der Auswahl eines Elements
   * aufgerufen werden soll.
   */
  public TreePart(DBObjectNode object, AbstractControl controller)
	{
    this.controller = controller;
    this.object = object;
	}

  /**
   * Erzeugt einen neuen Tree basierend auf der uebergebenen Liste
   * von Objekten des Typs DBObjectNode. Enthaelt der
   * Iterator Objekte, die <b>nicht</b> von DBObjectNode
   * abgeleitet sind, wird er eine ClassCastException werfen.
   * @param list Liste mit Objekten, fuer die der Baum erzeugt werden soll.
   * @param controller der AbstractControl, der bei der Auswahl eines Elements
   * aufgerufen werden soll.
   */
  public TreePart(DBIterator list, AbstractControl controller)
  {
    this.controller = controller;
    this.list = list;
  }


  /**
   * Malt den Baum in das uebergebene Composite.
   * @param parent das Composite.
   * @throws RemoteException
   */
  public void paint(Composite parent) throws RemoteException
  {
    if (this.object == null && this.list == null)
      throw new RemoteException("Keine darstellbaren Objekte �bergeben.");

    this.composite = parent;
    
    if (this.object != null)
    {
      final Item root = new Item(null,object);
      root.expandChilds();
    }
    else
    {
      while (list.hasNext())
      {
        final Item root = new Item(null,(DBObjectNode) list.next());
        root.expandChilds();
      }
    }
  }

	/**
   * Behandelt das Event "Ordner auf".
   * @param event das ausgeloeste Event.
   */
  private void handleFolderOpen(Event event)
	{
		Widget widget = event.item;
		if (!(widget instanceof TreeItem))
			return;
		TreeItem item = (TreeItem) widget;
		item.setImage(Style.getImage("folderopen.gif"));
	}

	/**
	 * Behandelt das Event "Ordner zu".
	 * @param event das ausgeloeste Event.
	 */
	private void handleFolderClose(Event event)
	{
		Widget widget = event.item;
		if (!(widget instanceof TreeItem))
			return;
		TreeItem item = (TreeItem) widget;
  	item.setImage(Style.getImage("folder.gif"));
	}

	/**
	 * Behandelt das Event "action". 
	 * @param event das ausgeloeste Event.
	 */
	private void handleSelect(MouseEvent event)
	{
		Widget widget = tree.getItem(new Point(event.x,event.y));
		if (!(widget instanceof TreeItem))
			return;
		TreeItem item = (TreeItem) widget;

    Object o = item.getData();
    if (o == null)
      return;
    this.controller.handleOpen(o);
	}

	/**
	 * Fuegt die Listener zum Tree hinzu.
	 * @param tree
	 */
	private void addListener(org.eclipse.swt.widgets.Tree tree) {

		// Listener fuer "Folder auf machen"
		tree.addListener(SWT.Expand, new Listener() {
			public void handleEvent(Event event) {
				handleFolderOpen(event);
			}
		});
		// Listener fuer "Folder auf machen"
		tree.addListener(SWT.Collapse, new Listener() {
			public void handleEvent(Event event) {
				handleFolderClose(event);
			}
		});

		// Listener fuer die Aktionen
    tree.addMouseListener(new MouseAdapter()
    {
      public void mouseDoubleClick(MouseEvent e)
      {
        handleSelect(e);
      }
    });
	}



	/**
   * Bildet ein einzelnes Element des Baumes ab.
   * Es laedt rekursiv alle Kind-Elemente.
   */
  class Item {

		private TreeItem parentItem;
    
    private DBObjectNode element;

		/**
		 * ct. Laed ein neues Element des Baumes.
     * @param parent das Eltern-Element.
     * @param das aktuelle Element.
     * @throws RemoteException
     */
    Item(TreeItem parent, DBObjectNode element) throws RemoteException
		{

			// store parent
			this.parentItem = parent;
      
      // store element
      this.element = element;

			TreeItem item;

			// this is only needed for the first element
      if (tree == null)
      {
        // Tree erzeugen
        tree = new org.eclipse.swt.widgets.Tree(composite, SWT.BORDER);

        // Griddata erzeugen
        // final GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.FILL_VERTICAL);
				final GridData gridData = new GridData(GridData.FILL_VERTICAL | GridData.FILL_HORIZONTAL);
        tree.setLayoutData(gridData);
        
        addListener(tree);
      }

			if (this.parentItem == null) {
        // Root-Element
				item = new TreeItem(tree,SWT.BORDER);
			}
			else {
        // Kind-Element
				item = new TreeItem(this.parentItem,SWT.BORDER);
			}

			// create tree item
			item.setImage(Style.getImage("folder.gif"));
			item.setData(element);

			item.setText(""+(String) element.getField(element.getPrimaryField()));

			// make this item the parent
			this.parentItem = item;

			// load the childs
  		loadChilds();

		}

		/**
     * Laedt alle Kinder dieses Elements.
     */
    void loadChilds() throws RemoteException
    {

			// iterate over childs
      DBIterator list = element.getChilds();
      while(list.hasNext())
			{
				new Item(this.parentItem,(DBObjectNode)list.next());
			}
		}
    
    /**
     * Klappt alle Kind-Elemente auf.
     */
    void expandChilds()
    {
      enumAndExpand(this.parentItem);
    }
    
    private void enumAndExpand(TreeItem treeItem)
    {
      TreeItem[] childItems = treeItem.getItems();
      int count = childItems.length;
      for (int i = 0; i < count; ++i)
      {
        childItems[i].setExpanded(true);
        enumAndExpand(childItems[i]);
      }
      treeItem.setExpanded(true);
    }
    
	}
}


/*********************************************************************
 * $Log: TreePart.java,v $
 * Revision 1.1  2004/04/12 19:15:58  willuhn
 * @C refactoring
 * @N forms
 *
 * Revision 1.4  2004/03/06 18:24:23  willuhn
 * @D javadoc
 *
 * Revision 1.3  2004/02/24 22:46:53  willuhn
 * @N GUI refactoring
 *
 * Revision 1.2  2004/02/22 20:05:21  willuhn
 * @N new Logo panel
 *
 * Revision 1.1  2004/01/28 20:51:24  willuhn
 * @C gui.views.parts moved to gui.parts
 * @C gui.views.util moved to gui.util
 *
 * Revision 1.6  2004/01/23 00:29:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.5  2004/01/08 20:50:32  willuhn
 * @N database stuff separated from jameica
 *
 * Revision 1.4  2003/12/30 03:41:44  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2003/12/29 20:07:19  willuhn
 * @N Formatter
 *
 * Revision 1.2  2003/12/29 16:29:47  willuhn
 * @N javadoc
 *
 * Revision 1.1  2003/12/19 01:43:27  willuhn
 * @N added Tree
 *
 **********************************************************************/