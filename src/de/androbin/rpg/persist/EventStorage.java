package de.androbin.rpg.persist;

import de.androbin.io.util.*;
import de.androbin.mixin.dim.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.function.*;

public final class EventStorage {
  private EventStorage() {
  }
  
  public static <T> void loadEvents( final File file, final Dimension size,
      final Function<Point, T> layer, final BiConsumer<T, String> prop )
      throws FileNotFoundException {
    int pointer = 0;
    
    try ( final Scanner scanner = new Scanner( new FileReader( file ) ) ) {
      while ( scanner.hasNextLine() ) {
        final String line = scanner.nextLine();
        
        if ( !scanner.hasNextLine() ) {
          continue;
        }
        
        final int x = pointer % size.width;
        final int y = pointer / size.width;
        
        final Point pos = new Point( x, y );
        final T atom = layer.apply( pos );
        
        if ( atom == null ) {
          continue;
        }
        
        final String event = line.isEmpty() ? null : line;
        prop.accept( atom, event );
        pointer++;
      }
    }
  }
  
  public static <T> void saveEvents( final File file, final Dimension size,
      final Function<Point, T> layer, final Function<T, String> prop ) {
    FileWriterUtil.writeFile( file, writer -> {
      LoopUtil.forEach( size, pos -> {
        final T atom = layer.apply( pos );
        
        if ( atom == null ) {
          return;
        }
        
        final String event = prop.apply( atom );
        
        try {
          if ( event != null ) {
            writer.write( event );
          }
          
          writer.write( '\n' );
        } catch ( final IOException e ) {
          e.printStackTrace();
        }
      } );
    } );
  }
}