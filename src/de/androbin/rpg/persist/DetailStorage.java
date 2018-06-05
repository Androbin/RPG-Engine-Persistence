package de.androbin.rpg.persist;

import de.androbin.io.util.*;
import de.androbin.json.*;
import de.androbin.rpg.*;
import java.io.*;
import java.util.*;

public final class DetailStorage {
  private DetailStorage() {
  }
  
  public static void loadDetails( final File file, final Detailed obj ) {
    obj.load( loadDetails( file ) );
  }
  
  public static XObject loadDetails( final File file ) {
    return XUtil.readJSON( file ).get().asObject();
  }
  
  public static void saveDetails( final File file, final Detailed obj ) {
    final Map<String, Object> details = new HashMap<>();
    obj.save( details );
    saveDetails( file, details );
  }
  
  public static void saveDetails( final File file, final Map<String, Object> details ) {
    FileWriterUtil.writeFile( file, XObject.toString( details ) );
  }
}