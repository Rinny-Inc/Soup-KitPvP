package io.noks.kitpvp.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LightList<E> implements Iterable<E>{
    private final List<E> list = new ArrayList<>();
    
    public void add(E element) {
        list.add(element);
    }
    
    public void remove(E element) {
        list.remove(element);
    }
    
    public int size() {
        return list.size();
    }
    
    @Override
    public Iterator<E> iterator() {
        return list.iterator();
    }
    
    public void clear() {
    	list.clear();
    }
}