package group20tup.matchingengine;

import group20tup.matchingengine.model.recursos.simulacion.Usuario;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    @Test
    @DisplayName("Se construye con y sin destino, y compara por id")
    void testUsuarioConstructores() {
        Usuario u1 = new Usuario(1, 10);
        assertEquals(1, u1.getId());
        assertEquals(10, u1.getNodoOrigen());
        assertEquals(-1, u1.getNodoDestino());

        Usuario u2 = new Usuario(2, 20, 30);
        assertEquals(30, u2.getNodoDestino());

        u2.setNodoDestino(99);
        assertEquals(99, u2.getNodoDestino());

        Usuario u1c = new Usuario(1, 99);
        assertEquals(u1, u1c);
        assertNotEquals(u1, u2);
    }

    @Test
    @DisplayName("equals y hashCode son consistentes")
    void testUsuarioEqualsHashCode() {
        Usuario a = new Usuario(42, 100);
        Usuario b = new Usuario(42, 200);
        Usuario c = new Usuario(99, 100);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
    }
}
