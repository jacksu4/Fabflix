package main.java;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Cart {
    private Map<String, Integer> cart = new HashMap<String, Integer>();
    private String username;
    public Cart(String username){
        cart = new HashMap<String, Integer>();
        this.username = username;
    }
    public Iterator getIterator(){
        return cart.entrySet().iterator();
    }
    public int getCount(String movieId){
        if(cart.containsKey(movieId))
            return (int) cart.get(movieId);
        else
            return -1;
    }
    public void add(String movieId){
        if(cart.containsKey(movieId))
            cart.put(movieId, cart.get(movieId)+1);
        else
            cart.put(movieId, 1);
    }
    public void decrease(String movieId){
        if(cart.containsKey(movieId)){
            cart.put(movieId, cart.get(movieId)-1);
            if(cart.get(movieId)<=0){
                cart.remove(movieId);
            }
        }
    }
}
