package de.androbin.rpg.persist.tree;

import de.androbin.rpg.*;
import de.androbin.rpg.persist.util.*;
import java.util.*;
import javax.swing.tree.*;

public class DataTree<D extends Data> extends AtomTree<D> {
  public DataTree() {
    model.setAsksAllowsChildren( true );
    createContainer( root );
  }
  
  @ Override
  protected DefaultMutableTreeNode addFold( final DefaultMutableTreeNode parent,
      final String name ) {
    final DefaultMutableTreeNode existing = TreeUtil.findChild( parent, name );
    
    if ( existing != null ) {
      return existing;
    }
    
    final DefaultMutableTreeNode child = new DefaultMutableTreeNode( name, true );
    model.insertNodeInto( child, parent, parent.getChildCount() - 1 );
    
    createContainer( child );
    return child;
  }
  
  @ Override
  public void addItem( final D data ) {
    getContainer( data ).add( data );
  }
  
  private void createContainer( final DefaultMutableTreeNode parent ) {
    final DefaultMutableTreeNode child = new DefaultMutableTreeNode( new ArrayList<>(), false );
    model.insertNodeInto( child, parent, 0 );
  }
  
  private List<D> getContainer( final D data ) {
    final DefaultMutableTreeNode parent = getParent( data.type );
    final DefaultMutableTreeNode child = (DefaultMutableTreeNode) parent.getLastChild();
    
    @ SuppressWarnings( "unchecked" )
    final List<D> container = (List<D>) child.getUserObject();
    return container;
  }
  
  @ Override
  public void removeItem( final D data ) {
    getContainer( data ).remove( data );
  }
}