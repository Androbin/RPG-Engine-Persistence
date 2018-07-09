package de.androbin.rpg.persist;

import de.androbin.io.util.*;
import de.androbin.rpg.*;
import de.androbin.rpg.entity.*;
import de.androbin.rpg.persist.tree.*;
import de.androbin.rpg.tile.*;
import de.androbin.rpg.world.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import javax.swing.tree.*;

public final class ProjectStorage {
  private ProjectStorage() {
  }
  
  public static Path getWorldFile( final Path dir, final Ident id ) {
    return dir.resolve( "worlds/" + id + "/.world" );
  }
  
  public static Project loadProject( final Path dir ) {
    final Project project = FileReaderUtil.readFileDirty( dir.resolve( ".project" ), reader -> {
      final String name = reader.readLine();
      return new Project( name );
    } );
    
    if ( project == null ) {
      return null;
    }
    
    project.assocDir = dir;
    project.load();
    
    final Path worldDir = dir.resolve( "worlds" );
    
    try {
      loadWorlds( worldDir, null, project.worlds::addItem );
    } catch ( final IOException e ) {
      e.printStackTrace();
    }
    
    try {
      loadTiles( dir.resolve( "json/tile" ), null, project.tiles::addItem );
    } catch ( final IOException e ) {
      e.printStackTrace();
    }
    
    try {
      loadEntities( dir.resolve( "json/entity" ), null, project.entities::addItem );
    } catch ( final IOException e ) {
      e.printStackTrace();
    }
    
    return project;
  }
  
  public static void loadEntities( final Path dir, final String prefix,
      final Consumer<EntityData> tree ) throws IOException {
    for ( final Iterator<Path> iter = Files.list( dir ).iterator(); iter.hasNext(); ) {
      final Path file = iter.next();
      final String name = file.getFileName().toString();
      
      if ( name.equals( "package.json" ) ) {
        continue;
      }
      
      if ( Files.isDirectory( file ) ) {
        final String nextPrefix = prefix == null ? name : prefix + "/" + name;
        loadEntities( file, nextPrefix, tree );
      } else {
        final String simpleName = name.substring( 0, name.length() - 5 );
        final String serial = prefix == null ? simpleName : prefix + "/" + simpleName;
        tree.accept( Entities.getData( Ident.parse( serial ) ) );
      }
    }
  }
  
  public static void loadTiles( final Path dir, final String prefix,
      final Consumer<TileData> tree ) throws IOException {
    for ( final Iterator<Path> iter = Files.list( dir ).iterator(); iter.hasNext(); ) {
      final Path file = iter.next();
      final String name = file.getFileName().toString();
      
      if ( name.equals( "package.json" ) ) {
        continue;
      }
      
      if ( Files.isDirectory( file ) ) {
        final String nextPrefix = prefix == null ? name : prefix + "/" + name;
        loadTiles( file, nextPrefix, tree );
      } else {
        final String simpleName = name.substring( 0, name.length() - 5 );
        final String serial = prefix == null ? simpleName : prefix + "/" + simpleName;
        tree.accept( Tiles.getData( Ident.parse( serial ) ) );
      }
    }
  }
  
  public static World loadWorld( final Path dir0, final Ident id )
      throws IOException {
    final Path dir = getWorldFile( dir0, id );
    return CoreStorage.loadWorld( dir, id );
  }
  
  public static void loadWorlds( final Path dir, final String prefix,
      final Consumer<World> tree ) throws IOException {
    for ( final Iterator<Path> iter = Files.list( dir ).iterator(); iter.hasNext(); ) {
      final Path file = iter.next();
      final String name = file.getFileName().toString();
      
      if ( name.equals( ".world" ) ) {
        final Ident id = Ident.parse( prefix );
        final World world = CoreStorage.loadWorld( file, id );
        tree.accept( world );
      } else {
        final String nextPrefix = prefix == null ? name : prefix + "/" + name;
        loadWorlds( file, nextPrefix, tree );
      }
    }
  }
  
  public static void saveProject( final Project project ) throws IOException {
    if ( project == null ) {
      return;
    }
    
    final Path dir = project.assocDir;
    
    FileWriterUtil.writeFile( dir.resolve( ".project" ), writer -> {
      writer.write( project.name );
      writer.write( '\n' );
    } );
    
    final AtomTree<World> worlds = project.worlds;
    final DefaultMutableTreeNode root = worlds.root;
    saveWorlds( dir, root );
  }
  
  public static void saveWorld( final Path dir0, final World world ) throws IOException {
    final Path dir = getWorldFile( dir0, world.id );
    CoreStorage.saveWorld( dir, world );
  }
  
  private static void saveWorlds( final Path dir0, final DefaultMutableTreeNode parent )
      throws IOException {
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