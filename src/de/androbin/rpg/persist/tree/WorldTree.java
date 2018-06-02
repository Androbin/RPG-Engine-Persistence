package de.androbin.rpg.persist.tree;

import de.androbin.rpg.persist.util.*;
import de.androbin.rpg.world.*;
import javax.swing.tree.*;

public final class WorldTree extends AtomTree<World> {
  @ Override
  protected DefaultMutableTreeNode addFold( final DefaultMutableTreeNode parent,
      final String name ) {
    final DefaultMutableTreeNode existing = TreeUtil.findChild( parent, name );
    
    if ( existing != null ) {
      return existing;
    }
    
    final DefaultMutableTreeNode child = new DefaultMutableTreeNode( name );
    model.insertNodeInto( child, parent, parent.getChildCount() );
    return child;
  }
  
  @ Override
  public void addItem( final World world ) {
    final DefaultMutableTreeNode parent = getParent( world.id );
    final DefaultMutableTreeNode existing = TreeUtil.findChild( parent, world.id.lastElement() );
    
    final DefaultMutableTreeNode child = new DefaultMutableTreeNode( world ) {
      @ Override
      public String toString() {
        return world.id.lastElement();
      }
    };
    
    if ( existing != null ) {
      model.removeNodeFromParent( existing );
      
      TreeUtil.forEachChild( existing, oldChild -> {
        child.add( oldChild );
        return false;
      } );
    }
    
    model.insertNodeInto( child, parent, parent.getChildCount() );
  }
  
  @ Override
  public void removeItem( final World world ) {
    final DefaultMutableTreeNode parent = getParent( world.id );
    final DefaultMutableTreeNode existing = TreeUtil.findChild( parent, world.id.lastElement() );
    model.removeNodeFromParent( existing );
    
    FileUtil.deleteRecursively( "worlds/" + world.id + "/.world" );
  }
}