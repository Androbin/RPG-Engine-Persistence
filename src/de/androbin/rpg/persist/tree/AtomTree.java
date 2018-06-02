package de.androbin.rpg.persist.tree;

import de.androbin.rpg.*;
import java.util.*;
import javax.swing.tree.*;

public abstract class AtomTree<D> {
  public final DefaultMutableTreeNode root;
  public final DefaultTreeModel model;
  
  public AtomTree() {
    root = new DefaultMutableTreeNode( null );
    model = new DefaultTreeModel( root );
  }
  
  protected abstract DefaultMutableTreeNode addFold( DefaultMutableTreeNode parent, String name );
  
  public abstract void addItem( D data );
  
  protected DefaultMutableTreeNode getParent( final Ident id ) {
    DefaultMutableTreeNode parent = (DefaultMutableTreeNode) model.getRoot();
    
    for ( final Iterator<String> iter = id.iterator(); iter.hasNext(); ) {
      final String element = iter.next();
      
      if ( iter.hasNext() ) {
        parent = addFold( parent, element );
      }
    }
    
    return parent;
  }
  
  public abstract void removeItem( D data );
}