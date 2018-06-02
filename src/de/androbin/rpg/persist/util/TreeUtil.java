package de.androbin.rpg.persist.util;

import java.util.function.*;
import javax.swing.tree.*;

public final class TreeUtil {
  private TreeUtil() {
  }
  
  public static DefaultMutableTreeNode findChild( final DefaultMutableTreeNode parent,
      final String name ) {
    return forEachChild( parent, child -> child.toString().equals( name ) );
  }
  
  public static DefaultMutableTreeNode forEachChild( final DefaultMutableTreeNode parent,
      final Predicate<DefaultMutableTreeNode> c ) {
    final int count = parent.getChildCount();
    
    for ( int index = 0; index < count; index++ ) {
      final DefaultMutableTreeNode child = (DefaultMutableTreeNode) parent.getChildAt( index );
      
      if ( c.test( child ) ) {
        return child;
      }
    }
    
    return null;
  }
}