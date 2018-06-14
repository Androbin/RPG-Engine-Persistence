package de.androbin.rpg.persist;

import de.androbin.io.util.*;
import de.androbin.json.*;
import de.androbin.rpg.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class PoolStorage {
  private PoolStorage() {
  }
  
  public static void loadDetails( final Path file, final Pooled obj ) {
    obj.load( loadDetails( file ) );
  }
  
  public static XArray loadDetails( final Path file ) {
    return XUtil.readJSON( file ).get().asArray();
  }
  
  public static void saveDetails( final Path file, final Pooled obj ) throws IOException {
    saveDetails( file, obj.save() );
  }
  
  public static void saveDetails( final Path file, final List<Object> pool ) throws IOException {
    FileWriterUtil.writeFile( file, XArray.toString( pool ) );
  }
}