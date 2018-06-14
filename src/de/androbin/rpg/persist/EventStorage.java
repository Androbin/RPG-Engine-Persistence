package de.androbin.rpg.persist;

import de.androbin.io.util.*;
import de.androbin.mixin.dim.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;

public final class EventStorage {
  private EventStorage() {
  }
  
  public static <T> void loadEvents( final Path file, final Dimension size,
      final Function<Point, T> layer, final BiConsumer<T, String> prop )
      throws IOException {
    int pointer = 0;
    
    try ( final Scanner scanner = FileReaderUtil.scanFile( file ) ) {
      while ( scanner.hasNextLine() ) {
        final String line = scanner.nextLine();
        
        if ( !scanner.hasNextLine() ) {
          continue;
        }
        
        final int x = pointer % size.width;
        final int y = pointer / size.width;
        
        final Point pos = new Point( x, y );
        final T atom = layer.apply( pos );
        
        if ( atom != null ) {
          final String event = line.isEmpty() ? null : line;
          prop.accept( atom, event );
        }
        
        pointer++;
      }
    }
  }
  
  public static <T> void saveEvents( final Path file, final Dimension size,
      final Function<Point, T> layer, final Function<T, String> prop ) throws IOException {
    FileWriterUtil.writeFile( file, writer -> {
      LoopUtil.forEach( size, pos -> {
        final T atom = layer.apply( pos );
        
        try {
          if ( atom != null ) {
            final String event = prop.apply( atom );
            
            if ( event != null ) {
              writer.write( event );
            }
          }
          
          writer.write( '\n' );
        } catch ( final IOException e ) {
          e.printStackTrace();
        }
      } );
    } );
  }
}