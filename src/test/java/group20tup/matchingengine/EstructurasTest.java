package group20tup.matchingengine;

import group20tup.matchingengine.model.estructuras.lineales.colas.ColaPrioridadMonticulo;
import group20tup.matchingengine.model.estructuras.lineales.listas.ListaDoubleLinkedL;
import group20tup.matchingengine.model.estructuras.lineales.matrices.MatrizArr;
import group20tup.matchingengine.model.estructuras.lineales.matrices.MatrizGrafo;
import group20tup.matchingengine.model.estructuras.nolineales.arboles.MonticuloBinario;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EstructurasTest {

    @Test
    @DisplayName("ListaDoubleLinkedL: insercion, eliminacion, busqueda")
    void testListaDoubleLinkedL() {
        ListaDoubleLinkedL lista = new ListaDoubleLinkedL();

        assertTrue(lista.estaVacia(), "Lista nueva debe estar vacia");
        assertEquals(0, lista.tamanio());

        lista.insertar("A", 0);
        lista.insertar("B", 1);
        lista.insertar("C", 2);
        assertEquals(3, lista.tamanio());
        assertEquals("A", lista.devolver(0));
        assertEquals("B", lista.devolver(1));
        assertEquals("C", lista.devolver(2));

        lista.reemplazar("X", 1);
        assertEquals("X", lista.devolver(1));

        assertEquals(0, lista.buscar("A"));
        assertEquals(1, lista.buscar("X"));
        assertEquals(-1, lista.buscar("Z"));

        assertTrue(lista.iguales("A", "A"));
        assertFalse(lista.iguales("A", "B"));

        lista.eliminar(1);
        assertEquals(2, lista.tamanio());
        assertEquals("C", lista.devolver(1));

        assertThrows(IndexOutOfBoundsException.class, () -> lista.devolver(99));
        assertThrows(IndexOutOfBoundsException.class, () -> lista.insertar("Z", 99));
    }

    @Test
    @DisplayName("ListaDoubleLinkedL: insertar al inicio")
    void testListaInsertarAlInicio() {
        ListaDoubleLinkedL lista = new ListaDoubleLinkedL();
        lista.insertar("B", 0);
        lista.insertar("A", 0);
        assertEquals("A", lista.devolver(0));
        assertEquals("B", lista.devolver(1));
        assertEquals(2, lista.tamanio());
    }

    @Test
    @DisplayName("MatrizArr: dimensiones, actualizar, limpiar, limites")
    void testMatrizArr() {
        MatrizArr m = new MatrizArr(3, 4);

        assertEquals(3, m.getNroFilas());
        assertEquals(4, m.getNroColumnas());

        m.actualizar(5.0, 1, 2);
        assertEquals(5.0, m.devolver(1, 2), 1e-12);

        m.limpiaMatriz();
        assertEquals(0.0, m.devolver(1, 2), 1e-12);

        assertThrows(IndexOutOfBoundsException.class, () -> m.actualizar(1.0, 5, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> m.devolver(-1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> m.devolver(0, 10));
    }

    @Test
    @DisplayName("MatrizArr: limpiar mantiene dimensiones")
    void testMatrizArrLimpiarMantieneDimensiones() {
        MatrizArr m = new MatrizArr(3, 4);
        m.actualizar(1.0, 2, 3);
        m.limpiaMatriz();
        assertEquals(3, m.getNroFilas());
        assertEquals(4, m.getNroColumnas());
    }

    @Test
    @DisplayName("MatrizGrafo: areConnected detecta conexiones y limites")
    void testMatrizGrafo() {
        MatrizGrafo mg = new MatrizGrafo(5);

        mg.actualizar(3.0, 1, 2);
        assertTrue(mg.areConnected(1, 2));

        assertFalse(mg.areConnected(1, 3));

        assertFalse(mg.areConnected(-1, 0));
        assertFalse(mg.areConnected(0, 99));
    }

    @Test
    @DisplayName("MonticuloBinario: basico vacio y con elementos")
    void testMonticuloBinarioBasico() {
        MonticuloBinario heap = new MonticuloBinario(2);

        assertTrue(heap.estaVacia());
        assertEquals(0, heap.tamanio());
        assertEquals(-1, heap.extraerMin());

        heap.insertar(5, 3.0);
        heap.insertar(3, 1.0);
        heap.insertar(7, 2.0);

        assertFalse(heap.estaVacia());
        assertEquals(3, heap.tamanio());

        assertEquals(3, heap.extraerMin());
        assertEquals(7, heap.extraerMin());
        assertEquals(5, heap.extraerMin());
        assertTrue(heap.estaVacia());
    }

    @Test
    @DisplayName("ColaPrioridadMonticulo: extrae en orden ascendente")
    void testColaPrioridadMonticulo() {
        ColaPrioridadMonticulo cola = new ColaPrioridadMonticulo(4);

        cola.insertar(3, 10.0);
        cola.insertar(1, 5.0);
        cola.insertar(2, 15.0);
        cola.insertar(0, 1.0);

        assertEquals(0, cola.extraerMin());
        assertEquals(1, cola.extraerMin());
        assertEquals(3, cola.extraerMin());
        assertEquals(2, cola.extraerMin());
        assertTrue(cola.estaVacia());
    }
}
