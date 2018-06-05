package de.androbin.rpg.persist;

import de.androbin.io.util.*;
import de.androbin.json.*;
import de.androbin.rpg.*;
import java.io.*;
import java.util.*;

public final class PoolStorage {
  private PoolStorage() {
  }
  
  public static void loadDetails( final File file, final Pooled obj ) {
    obj.load( loadDetails( file ) );
  }
  
  public static XArray loadDetails( final File file ) {
    return XUtil.readJSON( file ).get().asArray();
  }
  
  public static void saveDetails( final File file, final Pooled obj ) {
    final List<Object> details = new ArrayList<>();
    obj.save( details );
    saveDetails( file, details );
  }
  
  public static void saveDetails( final File file, final List<Object> pool ) {
    FileWriterUtil.writeFile( file, XArray.toString( pool ) );
  }
}