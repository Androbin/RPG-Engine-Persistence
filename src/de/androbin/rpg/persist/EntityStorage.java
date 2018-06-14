package de.androbin.rpg.persist;

import de.androbin.io.util.*;
import de.androbin.rpg.*;
import de.androbin.rpg.entity.*;
import de.androbin.rpg.world.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class EntityStorage {
  private EntityStorage() {
  }
  
  public static void loadEntities( final Path dir, final EntityLayer layer )
      throws IOException {
    loadEntities( dir, layer, true );
    loadEntities( dir, layer, false );
    
    for ( final Entity entity : layer.list() ) {
      loadEntityDetails( dir, entity );
    }
  }
  
  private static void loadEntities( final Path dir, final EntityLayer layer, final boolean solid )
      throws IOException {
    final Dimension size = layer.getSize();
    
    try ( final Scanner scanner = FileReaderUtil.scanFile( dir.resolve( "entities-" + solid ) ) ) {
      int pointer = 0;
      
      while ( scanner.hasNext() ) {
        final int count;
        final Ident type;
        final int id;
        
        if ( scanner.hasNextInt() ) {
          count = scanner.nextInt();
          
          if ( scanner.hasNextInt() ) {
            pointer += count;
            scanner.nextInt();
            continue;
          } else {
            type = Ident.fromSerial( scanner.next() );
            id = 0;
          }
        } else {
          count = 1;
          type = Ident.fromSerial( scanner.next() );
          id = scanner.nextInt();
        }
        
        final EntityData data = Entities.getData( type );
        
        for ( int i = 0; i < count; i++ ) {
          final int x = pointer % size.width;
          final int y = pointer / size.width;
          
          final Point pos = new Point( x, y );
          final Entity entity = Entities.create( data, id );
          layer.add( entity, pos );
          
          pointer += data.size.width;
        }
      }
    }
  }
  
  public static void loadEntityDetails( final Path dir, final Entity entity )
      throws IOException {
    if ( entity.id == 0 ) {
      return;
    }
    
    final Path file = dir.resolve( "entity_details/" + entity.id );
    DetailStorage.loadDetails( file, entity );
  }
  
  public static void saveEntities( final Path dir, final EntityLayer layer ) throws IOException {
    saveEntities( dir, layer, true );
    saveEntities( dir, layer, false );
    
    if ( CoreStorage.velvet ) {
      return;
    }
    
    for ( final Entity entity : layer.list() ) {
      saveEntityDetails( dir, entity );
    }
  }
  
  private static void saveEntities( final Path dir, final EntityLayer layer, final boolean solid )
      throws IOException {
    final Dimension size = layer.getSize();
    
    FileWriterUtil.writeFile( dir.resolve( "entities-" + solid ), writer -> {
      int lastCount = 0;
      Ident lastType = null;
      
      for ( int y = 0; y < size.height; y++ ) {
        for ( int x = 0; x < size.width; x++ ) {
          final Point pos = new Point( x, y );
          final Entity entity = layer.get( solid, pos );
          
          if ( entity == null || !entity.getSpot().hasPos( pos ) ) {
            if ( lastType != null ) {
              CoreStorage.writeRow( writer, lastCount, lastType );
              
              lastCount = 0;
              lastType = null;
            }
            
            lastCount++;
            continue;
          }
          
          final EntityData data = entity.getData();
          final Ident type = data.type;
          
          if ( entity.id == 0 ) {
            if ( !type.equals( lastType ) ) {
              CoreStorage.writeRow( writer, lastCount, lastType );
              
              lastCount = 0;
              lastType = type;
            }
            
            lastCount++;
          } else {
            CoreStorage.writeRow( writer, lastCount, lastType );
            
            lastCount = 0;
            lastType = null;
            
            writer.write( type.toString() );
            writer.write( ' ' );
            writer.write( String.valueOf( entity.id ) );
            writer.write( ' ' );
          }
          
          x += data.size.width - 1;
        }
      }
      
      CoreStorage.writeRow( writer, lastCount, lastType );
    } );
  }
  
  public static void saveEntityDetails( final Path dir, final Entity entity ) throws IOException {
    if ( entity.id == 0 ) {
      return;
    }
    
    final Path file = dir.resolve( "entity_details/" + entity.id );
    DetailStorage.saveDetails( file, entity );
  }
}