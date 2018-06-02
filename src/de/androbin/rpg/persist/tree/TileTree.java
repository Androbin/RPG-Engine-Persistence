package de.androbin.rpg.persist.tree;

import de.androbin.rpg.persist.util.*;
import de.androbin.rpg.tile.*;

public final class TileTree extends DataTree<TileData> {
  @ Override
  public void removeItem( final TileData data ) {
    super.removeItem( data );
    
    FileUtil.delete( "gfx/tile/" + data.type + ".png" );
    FileUtil.delete( "json/tile/" + data.type + ".json" );
  }
}