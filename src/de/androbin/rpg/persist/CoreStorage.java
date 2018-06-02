package de.androbin.rpg.persist;

import de.androbin.io.util.*;
import de.androbin.rpg.*;
import de.androbin.rpg.world.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public final class CoreStorage {
  private CoreStorage() {
  }
  
  public static World loadWorld( final File dir, final Ident id )
      throws FileNotFoundException {
    final World world;
    
    try ( final Scanner scanner = new Scanner( new FileReader( new File( dir, "world" ) ) ) ) {
      final int width = scanner.nextInt();
      final int height = scanner.nextInt();
      
      final Dimension size = new Dimension( width, height );
      world = new World( id, size );
    }
    
    TileStorage.loadTiles( dir, world.tiles );
    EntityStorage.loadEntities( dir, world.entities );
    return world;
  }
  
  public static void saveWorld( final File dir, final World world ) {
    final Dimension size = world.size;
    
    FileWriterUtil.writeFile( new File( dir, "world" ), writer -> {
      try {
        writer.write( String.valueOf( size.width ) );
        writer.write( ' ' );
        writer.write( String.valueOf( size.height ) );
      } catch ( final IOException e ) {
        e.printStackTrace();
      }
    } );
    
    TileStorage.saveTiles( dir, world.tiles );
    EntityStorage.saveEntities( dir, world.entities );
  }
  
  public static void writeRow( final Writer writer, final int count, final Ident type ) {
    if ( count == 0 ) {
      return;
    }
    
    try {
      writer.write( String.valueOf( count ) );
      writer.write( ' ' );
      
      if ( type == null ) {
        writer.write( '0' );
      } else {
        writer.write( type.toString() );
      }
      
      writer.write( ' ' );
    } catch ( final IOException e ) {
      e.printStackTrace();
    }
  }
}