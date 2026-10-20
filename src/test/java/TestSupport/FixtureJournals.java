package TestSupport;

import java.nio.file.*;
import java.io.IOException;

public final class FixtureJournals {
  private FixtureJournals(){}
  /** Replace atomically, preserving the prior recovery file if writing is interrupted. */
  public static void write(Path path,Object rows){
    try{
      Files.createDirectories(path.getParent());
      Path temporary=Files.createTempFile(path.getParent(),path.getFileName()+"-",".tmp");
      Files.writeString(temporary,new org.json.JSONArray((java.util.Collection<?>)rows).toString(2));
      for(int attempt=0;;attempt++){
        try{
          try{Files.move(temporary,path,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE);}
          catch(AtomicMoveNotSupportedException unsupported){Files.move(temporary,path,StandardCopyOption.REPLACE_EXISTING);}
          break;
        }catch(AccessDeniedException locked){
          if(attempt==4)throw locked;
          try{Thread.sleep(100L*(attempt+1));}catch(InterruptedException interrupted){Thread.currentThread().interrupt();throw new IOException("Journal replacement interrupted",interrupted);}
        }
      }
    }catch(IOException failure){throw new AssertionError("Cannot persist fixture recovery journal",failure);}
  }
}
