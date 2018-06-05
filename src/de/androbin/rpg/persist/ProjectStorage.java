package de.androbin.rpg.persist;

import de.androbin.io.util.*;
import de.androbin.rpg.*;
import de.androbin.rpg.entity.*;
import de.androbin.rpg.persist.tree.*;
import de.androbin.rpg.tile.*;
import de.androbin.rpg.world.*;
import java.io.*;
import java.util.function.*;
import javax.swing.tree.*;

public final class ProjectStorage {
  private ProjectStorage() {
  }
  
  public static File getWorldFile( final File dir0, final Ident id ) {
    return new File( dir0, "worlds/" + id + "/.world" );
  }
  
  public static Project loadProject( final File dir0 ) {
    final File projectFile = new File( dir0, ".project" );
    
    final Project project = FileReaderUtil.readFile( projectFile, reader -> {
      try {
        final String name = reader.readLine();
        return new Project( name );
      } catch ( final IOException e ) {
        e.printStackTrace();
        return null;
      }
    } );
    
    if ( project == null ) {
      return null;
    }
    
    project.assocDir = dir0;
    project.load();
    
    final File worldDir = new File( dir0, "worlds" );
    
    if ( worldDir.exists() ) {
      try {
        loadWorlds( worldDir, null, project.worlds::addItem );
      } catch ( final FileNotFoundException e ) {
        e.printStackTrace();
      }
    }
    
    final File tileDir = new File( dir0, "json/tile" );
    final File entityDir = new File( dir0, "json/entity" );
    
    loadTiles( tileDir, null, project.tiles::addItem );
    loadEntities( entityDir, null, project.entities::addItem );
    
    return project;
  }
  
  public static void loadEntities( final File dir, final String prefix,
      final Consumer<EntityData> tree ) {
    for ( final File file : dir.listFiles() ) {
      final String name = file.getName();
      
      if ( name.equals( "package.json" ) ) {
        continue;
      }
      
      if ( file.isDirectory() ) {
        final String nextPrefix = prefix == null ? name : prefix + "/" + name;
        loadEntities( file, nextPrefix, tree );
      } else {
        final String simpleName = name.substring( 0, name.length() - 5 );
        final String serial = prefix == null ? simpleName : prefix + "/" + simpleName;
        tree.accept( Entities.getData( Ident.fromSerial( serial ) ) );
      }
    }
  }
  
  public static void loadTiles( final File dir, final String prefix,
      final Consumer<TileData> tree ) {
    for ( final File file : dir.listFiles() ) {
      final String name = file.getName();
      
      if ( name.equals( "package.json" ) ) {
        continue;
      }
      
      if ( file.isDirectory() ) {
        final String nextPrefix = prefix == null ? name : prefix + "/" + name;
        loadTiles( file, nextPrefix, tree );
      } else {
        final String simpleName = name.substring( 0, name.length() - 5 );
        final String serial = prefix == null ? simpleName : prefix + "/" + simpleName;
        tree.accept( Tiles.getData( Ident.fromSerial( serial ) ) );
      }
    }
  }
  
  public static World loadWorld( final File dir0, final Ident id )
      throws FileNotFoundException {
    final File dir = getWorldFile( dir0, id );
    return CoreStorage.loadWorld( dir, id );
  }
  
  public static void loadWorlds( final File dir, final String prefix,
      final Consumer<World> tree )
      throws FileNotFoundException {
    for ( final File file : dir.listFiles() ) {
      final String name = file.getName();
      
      if ( name.equals( ".world" ) ) {
        final Ident id = Ident.fromSerial( prefix );
        final World world = CoreStorage.loadWorld( file, id );
        tree.accept( world );
      } else {
        final String nextPrefix = prefix == null ? name : prefix + "/" + name;
        loadWorlds( file, nextPrefix, tree );
      }
    }
  }
  
  public static void saveProject( final Project project ) {
    if ( project == null ) {
      return;
    }
    
    final File dir = project.assocDir;
    
    FileWriterUtil.writeFile( new File( dir, ".project" ), writer -> {
      try {
        writer.write( project.name );
        writer.write( '\n' );
      } catch ( final IOException e ) {
        e.printStackTrace();
      }
    } );
    
    final AtomTree<World> worlds = project.worlds;
    final DefaultMutableTreeNode root = worlds.root;
    saveWorlds( dir, root );
  }
  
  public static void saveWorld( final File dir0, final World world ) {
    final File dir = getWorldFile( dir0, world.id );
    CoreStorage.saveWorld( dir, world );
  }
  
  private static void saveWorlds( final File dir0, final DefaultMutableTreeNode parent ) {
    final int count = parent.getChildCount();
    
    for ( int index = 0; index < count; index++ ) {
      final DefaultMutableTreeNode child = (DefaultMutableTreeNode) parent.getChildAt( index );
      
      if ( child.getUserObject() instanceof World ) {
        final World world = (World) child.getUserObject();
        saveWorld( dir0, world );
      }
      
      saveWorlds( dir0, child );
    }
  }
}