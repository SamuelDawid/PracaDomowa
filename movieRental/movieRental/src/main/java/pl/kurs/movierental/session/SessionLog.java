package pl.kurs.movierental.session;

import pl.kurs.movierental.error.RentalStateException;

import java.util.ArrayList;
import java.util.List;

public class SessionLog implements AutoCloseable{
    final List<String> entries= new ArrayList<>();
    boolean closed = false;


    public void log(String line){
        if(closed) throw new RentalStateException("Session is already closed");
        entries.add(line);
        System.out.println(line);
    }
    public int entryCount(){
        return entries.size();
    }
    @Override
    public void close() throws Exception {
            if(closed) return;
            closed = true;
        System.out.println("=== Session ended ===" + "\n" + "Operations: " +entryCount());
    }
}
