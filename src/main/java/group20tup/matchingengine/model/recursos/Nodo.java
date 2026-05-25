/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package group20tup.matchingengine.model.recursos;

/**
 *
 * @author arc
 */
public class Nodo {
    private Object nodoInfo;
    private Nodo nextNodo;

    public Nodo(Object nodoInfo){
        this(nodoInfo,null);
    }
    public Nodo(Object nodoInfo,Nodo nextNodo){
        this.nodoInfo = nodoInfo;
        this.nextNodo = nextNodo;
    }

    public void setNodoInfo(Object nodoInfo){
        this.nodoInfo = nodoInfo;
    }
    public void setNextNodo(Nodo nextNodo){
        this.nextNodo = nextNodo;
    }
    public Object getNodoInfo(){
        return this.nodoInfo;
    }
    public Nodo getNextNodo(){
        return this.nextNodo;
    }
}