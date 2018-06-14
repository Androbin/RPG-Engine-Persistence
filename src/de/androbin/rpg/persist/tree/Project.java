package de.androbin.rpg.persist.tree;

import de.androbin.io.*;
import de.androbin.rpg.entity.*;
import de.androbin.rpg.tile.*;
import java.net.*;
import java.nio.file.*;

public final class Project {
  public final String name;
  
  public final TileTree tiles;
  public final EntityTree entities;
  public final WorldTree worlds;
  
  public Path assocDir;
  
  public Project( final String name ) {
    this.name = name;
    
    this.tiles = new TileTree();
    this.entities = new EntityTree();
    this.worlds = new WorldTree();
  }
  
  private URL getURL() {
    try {
      return assocDir.toUri().toURL();
    } catch ( final MalformedURLException e ) {
      e.printStackTrace();
      return null;
    }
  }
  
  public void load() {
    DynamicClassLoader.addDynamicURL( getURL() );
  }
  
  public void unload() {
    DynamicClassLoader.removeDynamicURL( getURL() );
    
    Entities.invalidate();
    Tiles.invalidate();
  }
}