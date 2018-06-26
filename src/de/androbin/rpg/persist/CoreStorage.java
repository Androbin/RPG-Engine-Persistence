package de.androbin.rpg.persist;

import de.androbin.io.util.*;
import de.androbin.rpg.*;
import de.androbin.rpg.world.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class CoreStorage {
  private CoreStorage() {
  }
  
  public static World loadWorld( final Path dir, final Ident id )
      throws IOException {
    final World world;
    
    try ( final Scanner scanner = FileReaderUtil.scanFile( dir.resolve( "world" ) ) ) {
      final int width = scanner.nextInt();
      final int height = scanner.nextInt();
      
      final Dimension size = new Dimension( width, height );
      world = new World( id, size );
    }
    
    TileStorage.loadTiles( dir, world.tiles );
    EntityStorage.loadEntities( dir, world.entities );
    return world;
  }
  
  public static void saveWorld( final Path dir, final World world ) throws IOException {
    final Dimension size = world.size;
    
    FileWriterUtil.writeFile( dir.resolve( "world" ), writer -> {
      writer.write( String.valueOf( size.width ) );
      writer.write( ' ' );
      writer.write( String.valueOf( size.height ) );
    } );
    
    TileStorage.saveTiles( dir, world.tiles );
    EntityStorage.saveEntities( dir, world.entities );
  }
  
  protected static void writeRow( final Writer writer, final int count, final Ident type )
      throws IOException {
    if ( count == 0 ) {
      return;
    }
    
    writer.write( String.valueOf( count ) );
    writer.write( ' ' );
    
    if ( type == null ) {
      writer.write( '0' );
    } else {
      writer.write( type.toString() );
    }
    
    writer.write( ' ' );
  }
}