package group20tup.matchingengine.model.utilidades;

import group20tup.matchingengine.model.estructuras.lineales.ColaNodosCamino;
import group20tup.matchingengine.model.estructuras.nolineales.GrafoDirigido;
import group20tup.matchingengine.model.recursos.Nodo;
import group20tup.matchingengine.model.recursos.NodoCamino;

import java.util.ArrayList;
import java.util.List;

public class BuscadorCaminos {
    private static final double INFINITO = Double.POSITIVE_INFINITY;

    public int[] calcularDijkstra(GrafoDirigido grafo, int origen, int destino) {
        int n = grafo.getOrden();

        double[] distancias = new double[n];
        int[] padres = new int[n];
        boolean[] visitados = new boolean[n];

        for (int i = 0; i < n; i++) {
            distancias[i] = INFINITO;
            padres[i] = -1;
            visitados[i] = false;
        }

        distancias[origen] = 0.0;

        ColaNodosCamino colaPrioridad = new ColaNodosCamino();
        colaPrioridad.meter(new NodoCamino(origen, 0.0));

        while (!colaPrioridad.estaVacia()) {
            NodoCamino actual = (NodoCamino) colaPrioridad.sacar();
            int u = actual.getIdFilaArray();

            if (u == destino) {
                break;
            }

            if (visitados[u]) {
                continue;
            }
            visitados[u] = true;

            for (int v = 0; v < n; v++) {
                if (!visitados[v]) {
                    double pesoArista = (Double) grafo.getMatrizCosto().devolver(u, v);
                    if (pesoArista < INFINITO) {
                        double nuevaDistancia = distancias[u] + pesoArista;

                        if (nuevaDistancia < distancias[v]) {
                            distancias[v] = nuevaDistancia;
                            padres[v] = u;
                            colaPrioridad.meter(new NodoCamino(v, nuevaDistancia));
                        }
                    }
                }
            }
        }

        if (distancias[destino] == INFINITO) {
            return new int[0];
        }

        return reconstruirRuta(padres, origen, destino);
    }

    private int[] reconstruirRuta(int[] padres, int origen, int destino) {
        List<Integer> rutaInversa = new ArrayList<>();
        int actual = destino;

        while (actual != -1) {
            rutaInversa.add(actual);
            actual = padres[actual];
        }

        int[] rutaFinal = new int[rutaInversa.size()];
        int posicion = 0;
        for (int i = rutaInversa.size() - 1; i >= 0; i--) {
            rutaFinal[posicion++] = rutaInversa.get(i);
        }

        return rutaFinal;
    }
}
