package de.androbin.rpg.persist.tree;

import de.androbin.rpg.entity.*;
import de.androbin.rpg.persist.util.*;

public final class EntityTree extends DataTree<EntityData> {
  @ Override
  public void removeItem( final EntityData data ) {
    super.removeItem( data );
    
    FileUtil.delete( "gfx/entity/" + data.type + ".png" );
    FileUtil.delete( "json/entity/" + data.type + ".json" );
  }
}