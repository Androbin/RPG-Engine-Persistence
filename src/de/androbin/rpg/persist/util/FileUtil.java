package de.androbin.rpg.persist.util;

import static de.androbin.io.DynamicClassLoader.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class FileUtil {
  private FileUtil() {
  }
  
  public static boolean delete( final String path ) {
    return getFile( path ).delete();
  }
  
  public static boolean deleteRecursively( final String path ) {
    try {
      Files.walk( getFile( path ).toPath() )
          .map( Path::toFile )
          .sorted( Comparator.reverseOrder() )
          .forEach( File::delete );
      return true;
    } catch ( final IOException e ) {
      e.printStackTrace();
      return false;
    }
  }
}