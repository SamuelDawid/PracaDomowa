package org.yellowflash.catalog;

import org.apache.commons.lang3.Validate;

import java.util.LinkedHashMap;
import java.util.Optional;

public class Catalog<T> {
    LinkedHashMap<Integer, T> catalog;

    public Catalog() {
        this.catalog = new LinkedHashMap<>();
    }
    public void add(int id, T element){
        Validate.notNull(element,"Element field can't be empty or null");
        Validate.isTrue(!catalog.containsKey(id),"Element with this ID already exist");
        catalog.put(id,element);
    }
    public Optional<T> find(int id){
        return Optional.ofNullable(catalog.get(id));
    }
    public boolean contains(int id){
        return catalog.containsKey(id);
    }
    public int size(){
        return catalog.size();
    }
}
