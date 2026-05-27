package group20tup.matchingengine.model.utilidades;

import group20tup.matchingengine.model.estructuras.lineales.ListaDoubleLinkedL;
import group20tup.matchingengine.model.estructuras.nolineales.GrafoDirigido;
import group20tup.matchingengine.model.recursos.MetadataNodo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.nio.charset.StandardCharsets;

public class CargadorDatos {
    private ListaDoubleLinkedL listaEsquinas;
    private long[] mapeoIndicesAIdOSM;

    public CargadorDatos() {
        this.listaEsquinas = new ListaDoubleLinkedL();
        this.mapeoIndicesAIdOSM = new long[0]; // Will be resized in cargarMetadatos
    }

    public void cargarMetadatos(String pathResourceMetadata) throws IllegalArgumentException {
        InputStream is = getClass().getResourceAsStream(pathResourceMetadata);
        if (is == null) {
            throw new IllegalArgumentException("No se encontro el archivo de metadatos en: " + pathResourceMetadata);
        }

        // Read all lines into a list first to avoid stream closing issues
        List<String> lineas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            br.readLine(); // Skip header
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    lineas.add(linea);
                }
            }
        } catch (Exception ex) {
            System.err.println("No se pudo encontrar el archivo para cargar los metadatos: " + ex.getMessage());
            throw new IllegalArgumentException("Error cargando metadatos", ex);
        }
        
        // Resize the array to actual count
        this.mapeoIndicesAIdOSM = new long[lineas.size()];
        
        // Process the lines
        int contadorSecuencial = 0;
        for (String linea : lineas) {
            String[] campos = linea.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            if (campos.length >= 6) {
                long idOSM = Long.parseLong(campos[0].trim());
                double latitud = Double.parseDouble(campos[1].trim());
                double longitud = Double.parseDouble(campos[2].trim());
                String calleA = campos[3].replace("\"", "").trim();
                String calleB = campos[4].replace("\"", "").trim();
                String nombreEsquina = campos[5].replace("\"", "").trim();
                MetadataNodo nodoMeta = new MetadataNodo(contadorSecuencial, idOSM, latitud, longitud, calleA, calleB, nombreEsquina);
                this.listaEsquinas.insertar(nodoMeta, contadorSecuencial);
                this.mapeoIndicesAIdOSM[contadorSecuencial] = idOSM;
                ++contadorSecuencial;
            }
        }
        
        System.out.println("[Éxito] Se cargaron " + contadorSecuencial + " esquinas en la Lista Enlazada.");
    }

    public void cargarMatrizVial(String pathResourceMatriz, GrafoDirigido grafo) throws IllegalArgumentException {
        InputStream is = getClass().getResourceAsStream(pathResourceMatriz);
        if (is == null) {
            throw new IllegalArgumentException("No se encontro el archivo de matriz en: " + pathResourceMatriz);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            br.readLine();
            String linea;
            int filaInterna = 0;
            final double VELOCIDAD_METROS_POR_SEGUNDO = 25.0 / 3.6;

            grafo.setOrdenGrafo(this.listaEsquinas.tamanio());

            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] valores = linea.split(",");
                for (int columnaInterna = 0; columnaInterna < grafo.getOrden(); columnaInterna++) {
                    if (columnaInterna + 1 >= valores.length) {
                        break;
                    }

                    int valorCelda = Integer.parseInt(valores[columnaInterna + 1].trim());

                    if (valorCelda == 1 && filaInterna != columnaInterna) {
                        MetadataNodo nodoOrigen = (MetadataNodo) this.listaEsquinas.devolver(filaInterna);
                        MetadataNodo nodoDestino = (MetadataNodo) this.listaEsquinas.devolver(columnaInterna);

                        double distanciaMetros = calcularHaversine(
                                nodoOrigen.getLatitud(), nodoOrigen.getLongitud(),
                                nodoDestino.getLatitud(), nodoDestino.getLongitud()
                                );

                        double etaSegundos = distanciaMetros / VELOCIDAD_METROS_POR_SEGUNDO;

                        grafo.getMatrizCosto().actualizar(etaSegundos, filaInterna, columnaInterna);
                    } else {
                        grafo.getMatrizCosto().actualizar(Double.POSITIVE_INFINITY, filaInterna, columnaInterna);
                    }
                }
                ++filaInterna;
            }
            System.out.println("[Éxito] Matriz de adyacencia de " + filaInterna + "x" + filaInterna + " convertida a pesos ETA (segundos).");
        } catch (Exception ex) {
            System.err.println("No se pudo encontrar el archivo para cargar la matriz: " + ex.getMessage());
            throw new IllegalArgumentException("Error cargando matriz vial", ex);
        }
    }

    private double calcularHaversine(double lat1, double lon1, double lat2, double lon2) {
        final double RADIO_TIERRA_METROS = 6371000.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return RADIO_TIERRA_METROS * c;
    }

    public ListaDoubleLinkedL getListaEsquinas() {
        return listaEsquinas;
    }

    public long[] getMapeoIndicesAIdOSM() {
        return mapeoIndicesAIdOSM;
    }
}
