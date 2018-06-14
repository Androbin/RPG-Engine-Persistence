package de.androbin.rpg.persist.util;

import static de.androbin.io.DynamicClassLoader.*;
import de.androbin.io.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class FileUtil {
  private FileUtil() {
  }
  
  public static boolean delete( final String path ) {
    return delete( DynamicClassLoader.getPath( path ) );
  }
  
  public static boolean delete( final Path path ) {
    try {
      Files.delete( path );
      return true;
    } catch ( final IOException e ) {
      e.printStackTrace();
      return false;
    }
  }
  
  public static boolean deleteRecursively( final String path ) {
    try {
      Files.walk( getPath( path ) )
          .sorted( Comparator.reverseOrder() )
          .forEach( FileUtil::delete );
      return true;
    } catch ( final IOException e ) {
      e.printStackTrace();
      return false;
    }
  }
}