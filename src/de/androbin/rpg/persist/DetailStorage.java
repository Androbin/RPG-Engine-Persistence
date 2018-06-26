package de.androbin.rpg.persist;

import de.androbin.io.util.*;
import de.androbin.json.*;
import de.androbin.rpg.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class DetailStorage {
  private DetailStorage() {
  }
  
  public static void loadDetails( final Path file, final Detailed obj ) {
    obj.load( loadDetails( file ) );
  }
  
  public static XObject loadDetails( final Path file ) {
    return XUtil.readJSON( file )
        .map( XValue::asObject )
        .orElseGet( XObject::new );
  }
  
  public static void saveDetails( final Path file, final Detailed obj ) throws IOException {
    saveDetails( file, obj.save() );
  }
  
  public static void saveDetails( final Path file, final Map<String, Object> details )
      throws IOException {
    FileWriterUtil.writeFile( file, XObject.toString( details, false ) );
  }
}