/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package group20tup.matchingengine.model.estructuras.lineales;


import group20tup.matchingengine.model.recursos.Nodo;
import group20tup.matchingengine.model.recursos.OperacionesCL1;

/**
 *
 * @author arc
 */
public class ColaSLinkedList implements OperacionesCL1 {
    protected Nodo frenteC,finalC;

    public ColaSLinkedList(){
        limpiar();
    }

    @Override
    public void meter(Object elemento){
        if(!estaVacia()){
            this.finalC.setNextNodo(new Nodo(elemento));
            this.finalC = this.finalC.getNextNodo();
        }
        else
            this.frenteC = this.finalC = new Nodo(elemento);
    }
    @Override
    public Object sacar(){
        Object elemento = null;
        if(!estaVacia()){
            elemento = this.frenteC.getNodoInfo();
            this.frenteC = this.frenteC.getNextNodo();
            if(estaVacia())
                this.finalC = null;
        }
        else
            System.out.println("Error. Lista vacia..");
        return elemento;
    }

    @Override
    public boolean estaVacia(){
        return this.frenteC==null;
    }
    @Override
    public void limpiar(){
        this.frenteC = this.finalC = null;
    }

    public Nodo getFrenteC() {
        return frenteC;
    }
    public void setFrenteC(Nodo frenteC) {
        this.frenteC = frenteC;
    }
    public Nodo getFinalC() {
        return finalC;
    }
    public void setFinalC(Nodo finalC) {
        this.finalC = finalC;
    }
}