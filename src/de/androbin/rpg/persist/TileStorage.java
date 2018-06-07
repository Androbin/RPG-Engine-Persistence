package de.androbin.rpg.persist;

import de.androbin.io.util.*;
import de.androbin.rpg.*;
import de.androbin.rpg.tile.*;
import de.androbin.rpg.world.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public final class TileStorage {
  private TileStorage() {
  }
  
  public static void loadTiles( final File dir, final TileLayer layer )
      throws FileNotFoundException {
    final Dimension size = layer.size;
    
    int pointer = 0;
    
    try ( final Scanner scanner = new Scanner( new FileReader( new File( dir, "tiles" ) ) ) ) {
      while ( scanner.hasNextInt() ) {
        final int count = scanner.nextInt();
        
        if ( scanner.hasNextInt() ) {
          pointer += count;
          scanner.nextInt();
          continue;
        }
        
        final Ident type = Ident.fromSerial( scanner.next() );
        final TileData data = Tiles.getData( type );
        
        for ( int i = 0; i < count; i++ ) {
          final int x = pointer % size.width;
          final int y = pointer / size.width;
          
          final Point pos = new Point( x, y );
          final Tile tile = Tiles.create( data );
          layer.set( pos, tile );
          
          pointer++;
        }
      }
    }
    
    loadTileEvents( dir, layer );
  }
  
  public static void loadTileEvents( final File dir, final TileLayer layer )
      throws FileNotFoundException {
    EventStorage.loadEvents( new File( dir, "tile_enter_events" ), layer.size, layer::get,
        ( tile, event ) -> tile.enterEvent = event );
  }
  
  public static void saveTiles( final File dir, final TileLayer layer ) {
    final Dimension size = layer.size;
    
    FileWriterUtil.writeFile( new File( dir, "tiles" ), writer -> {
      int lastCount = 0;
      Ident lastType = null;
      
      for ( int y = 0; y < size.height; y++ ) {
        for ( int x = 0; x < size.width; x++ ) {
          final Point pos = new Point( x, y );
          final Tile tile = layer.get( pos );
          
          if ( tile == null ) {
            if ( lastType != null ) {
              CoreStorage.writeRow( writer, lastCount, lastType );
              
              lastCount = 0;
              lastType = null;
            }
            
            lastCount++;
            continue;
          }
          
          final Ident type = tile.getData().type;
          
          if ( !type.equals( lastType ) ) {
            CoreStorage.writeRow( writer, lastCount, lastType );
            
            lastCount = 0;
            lastType = type;
          }
          
          lastCount++;
        }
      }
      
      CoreStorage.writeRow( writer, lastCount, lastType );
    } );
    
    saveTileEvents( dir, layer );
  }
  
  public static void saveTileEvents( final File dir, final TileLayer layer ) {
    EventStorage.saveEvents( new File( dir, "tile_enter_events" ), layer.size, layer::get,
        tile -> tile.enterEvent );
  }
}