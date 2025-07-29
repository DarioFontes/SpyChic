package spy.chic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

// ==================== CLASE CARTA ====================
class Carta {
    private String categoria;
    private String nombre;
    private int valor;
    private boolean esEspia;
    
    public Carta(String categoria, String nombre, int valor, boolean esEspia) {
        this.categoria = categoria;
        this.nombre = nombre;
        this.valor = valor;
        this.esEspia = esEspia;
    }
    
    // Getters y Setters
    public String getCategoria() { return categoria; }
    public String getNombre() { return nombre; }
    public int getValor() { return valor; }
    public boolean esEspia() { return esEspia; }
    
    public void setEspia(boolean espia) { this.esEspia = espia; }
    
    @Override
    public String toString() {
        return nombre + " (" + categoria + ") - Valor: " + valor;
    }
}

// ==================== CLASE MAZO ====================
class Mazo {
    private List<Carta> cartas;
    private Random random;
    
    public Mazo() {
        this.cartas = new ArrayList<>();
        this.random = new Random();
        inicializarCartas();
    }
    
    private void inicializarCartas() {
        // Animales
        cartas.add(new Carta("Animal", "León", 8, false));
        cartas.add(new Carta("Animal", "Tigre", 9, false));
        cartas.add(new Carta("Animal", "Elefante", 7, false));
        cartas.add(new Carta("Animal", "Águila", 6, false));
        cartas.add(new Carta("Animal", "Lobo", 7, false));
        
        // Profesiones
        cartas.add(new Carta("Profesión", "Doctor", 9, false));
        cartas.add(new Carta("Profesión", "Maestro", 7, false));
        cartas.add(new Carta("Profesión", "Ingeniero", 8, false));
        cartas.add(new Carta("Profesión", "Chef", 6, false));
        cartas.add(new Carta("Profesión", "Artista", 5, false));
        
        // Objetos
        cartas.add(new Carta("Objeto", "Espada", 8, false));
        cartas.add(new Carta("Objeto", "Escudo", 6, false));
        cartas.add(new Carta("Objeto", "Poción", 5, false));
        cartas.add(new Carta("Objeto", "Llave", 4, false));
        cartas.add(new Carta("Objeto", "Libro", 3, false));
        
        // Lugares
        cartas.add(new Carta("Lugar", "Bosque", 7, false));
        cartas.add(new Carta("Lugar", "Montaña", 8, false));
        cartas.add(new Carta("Lugar", "Playa", 6, false));
        cartas.add(new Carta("Lugar", "Ciudad", 9, false));
        cartas.add(new Carta("Lugar", "Desierto", 5, false));
    }
    
    public Carta[] seleccionarCartasParaJuego(int cantidad) {
        Collections.shuffle(cartas);
        Carta[] cartasJuego = new Carta[cantidad];
        
        // Seleccionar cartas de la misma categoría
        String categoriaSeleccionada = cartas.get(0).getCategoria();
        int cartasEncontradas = 0;
        
        for (Carta carta : cartas) {
            if (carta.getCategoria().equals(categoriaSeleccionada) && cartasEncontradas < cantidad) {
                cartasJuego[cartasEncontradas] = new Carta(
                    carta.getCategoria(), 
                    carta.getNombre(), 
                    carta.getValor(), 
                    false
                );
                cartasEncontradas++;
            }
        }
        
        // Designar una carta como espía (diferente)
        int indiceEspia = random.nextInt(cantidad);
        cartasJuego[indiceEspia].setEspia(true);
        
        return cartasJuego;
    }
    
    public int getTotalCartas() {
        return cartas.size();
    }
}

// ==================== CLASE JUGADOR ====================
class Jugador {
    private String nombre;
    private int puntos;
    private boolean esIA;
    
    public Jugador(String nombre, boolean esIA) {
        this.nombre = nombre;
        this.puntos = 0;
        this.esIA = esIA;
    }
    
    // Getters y Setters
    public String getNombre() { return nombre; }
    public int getPuntos() { return puntos; }
    public boolean esIA() { return esIA; }
    
    public void sumarPuntos(int puntos) {
        this.puntos += puntos;
    }
    
    public void restarPuntos(int puntos) {
        this.puntos = Math.max(0, this.puntos - puntos);
    }
}

// ==================== CLASE IA ====================
class IA {
    private Random random;
    
    public IA() {
        this.random = new Random();
    }
    
    public int elegirCarta(Carta[] cartas, int dificultad) {
        switch(dificultad) {
            case 1: // Fácil - completamente aleatorio
                return random.nextInt(cartas.length);
                
            case 2: // Medio - analiza valores
                return elegirCartaMedio(cartas);
                
            case 3: // Difícil - estrategia avanzada
                return elegirCartaDificil(cartas);
                
            default:
                return random.nextInt(cartas.length);
        }
    }
    
    private int elegirCartaMedio(Carta[] cartas) {
        // Busca la carta con valor más diferente al promedio
        double promedio = 0;
        for (Carta carta : cartas) {
            promedio += carta.getValor();
        }
        promedio /= cartas.length;
        
        int mejorIndice = 0;
        double mayorDiferencia = 0;
        
        for (int i = 0; i < cartas.length; i++) {
            double diferencia = Math.abs(cartas[i].getValor() - promedio);
            if (diferencia > mayorDiferencia) {
                mayorDiferencia = diferencia;
                mejorIndice = i;
            }
        }
        
        return mejorIndice;
    }
    
    private int elegirCartaDificil(Carta[] cartas) {
        // Análisis más sofisticado: busca patrones
        int[] valores = new int[cartas.length];
        for (int i = 0; i < cartas.length; i++) {
            valores[i] = cartas[i].getValor();
        }
        
        // Encuentra el valor menos común
        int valorMenosComun = valores[0];
        int menorFrecuencia = Integer.MAX_VALUE;
        
        for (int valor : valores) {
            int frecuencia = 0;
            for (int otroValor : valores) {
                if (valor == otroValor) frecuencia++;
            }
            
            if (frecuencia < menorFrecuencia) {
                menorFrecuencia = frecuencia;
                valorMenosComun = valor;
            }
        }
        
        // Retorna el índice de la carta con el valor menos común
        for (int i = 0; i < cartas.length; i++) {
            if (cartas[i].getValor() == valorMenosComun) {
                return i;
            }
        }
        
        return random.nextInt(cartas.length);
    }
}

// ==================== CLASE PRINCIPAL ====================
public class SpyChic {
    private Scanner scanner;
    private Mazo mazo;
    private IA ia;
    private Jugador jugadorHumano;
    private Jugador jugadorIA;
    private int dificultad;
    private int rondasJugadas;
    
    public SpyChic() {
        this.scanner = new Scanner(System.in);
        this.mazo = new Mazo();
        this.ia = new IA();
        this.dificultad = 2; // Medio por defecto
        this.rondasJugadas = 0;
    }
    
    public void iniciar() {
        mostrarBienvenida();
        
        while (true) {
            mostrarMenuPrincipal();
            int opcion = leerOpcion();
            
            switch (opcion) {
                case 1:
                    configurarJuego();
                    jugar();
                    break;
                case 2:
                    mostrarInstrucciones();
                    break;
                case 3:
                    mostrarEstadisticas();
                    break;
                case 4:
                    configurarDificultad();
                    break;
                case 5:
                    System.out.println("\n¡Gracias por jugar! ¡Hasta pronto!");
                    return;
                default:
                    System.out.println("Opción inválida. Intenta de nuevo.");
            }
        }
    }
    
    private void mostrarBienvenida() {
        System.out.println("===============================================");
        System.out.println("    BIENVENIDO AL JUEGO DEL ESPÍA");
        System.out.println("===============================================");
        System.out.println("¡Encuentra la carta diferente entre todas!");
        System.out.println();
    }
    
    private void mostrarMenuPrincipal() {
        System.out.println("\n========== MENÚ PRINCIPAL ==========");
        System.out.println("1. Iniciar Juego");
        System.out.println("2. Ver Instrucciones");
        System.out.println("3. Estadísticas");
        System.out.println("4. Configurar Dificultad (Actual: " + getNombreDificultad() + ")");
        System.out.println("5. Salir");
        System.out.print("\nSelecciona una opción: ");
    }
    
    private String getNombreDificultad() {
        switch(dificultad) {
            case 1: return "Fácil";
            case 2: return "Medio";
            case 3: return "Difícil";
            default: return "Medio";
        }
    }
    
    private int leerOpcion() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private void configurarJuego() {
        System.out.println("\n========== CONFIGURACIÓN ==========");
        System.out.print("Ingresa tu nombre: ");
        String nombre = scanner.nextLine();
        
        jugadorHumano = new Jugador(nombre, false);
        jugadorIA = new Jugador("IA-Espía", true);
        
        System.out.println("¡Perfecto " + nombre + "! Prepárate para el desafío.");
    }
    
    private void mostrarInstrucciones() {
        System.out.println("\n============= INSTRUCCIONES =============");
        System.out.println("OBJETIVO:");
        System.out.println("   Encuentra la carta 'espía' (diferente) entre todas las cartas.");
        System.out.println();
        System.out.println("CÓMO JUGAR:");
        System.out.println("   1. Se mostrarán 5 cartas de la misma categoría");
        System.out.println("   2. Una de ellas es el 'espía' (tiene características diferentes)");
        System.out.println("   3. Analiza las cartas y elige cuál crees que es el espía");
        System.out.println("   4. Compites contra la IA para ver quién encuentra más espías");
        System.out.println();
        System.out.println("PUNTUACIÓN:");
        System.out.println("   • Acierto: +10 puntos");
        System.out.println("   • Error: -3 puntos");
        System.out.println();
        System.out.println("CONSEJO:");
        System.out.println("   Observa los valores y busca patrones. El espía suele destacar.");
        System.out.println();
        System.out.print("Presiona ENTER para volver al menú...");
        scanner.nextLine();
    }
    
    private void configurarDificultad() {
        System.out.println("\n========== DIFICULTAD ==========");
        System.out.println("1. Fácil   - IA juega aleatoriamente");
        System.out.println("2. Medio   - IA analiza valores");
        System.out.println("3. Difícil - IA usa estrategias avanzadas");
        System.out.print("\nSelecciona dificultad: ");
        
        int nuevaDificultad = leerOpcion();
        if (nuevaDificultad >= 1 && nuevaDificultad <= 3) {
            dificultad = nuevaDificultad;
            System.out.println("Dificultad cambiada a: " + getNombreDificultad());
        } else {
            System.out.println("Dificultad inválida. Manteniendo: " + getNombreDificultad());
        }
    }
    
    private void mostrarEstadisticas() {
        System.out.println("\n========== ESTADÍSTICAS ==========");
        if (jugadorHumano != null && jugadorIA != null) {
            System.out.println("Jugador: " + jugadorHumano.getNombre());
            System.out.println("Puntos: " + jugadorHumano.getPuntos());
            System.out.println("Puntos IA: " + jugadorIA.getPuntos());
            System.out.println("Rondas jugadas: " + rondasJugadas);
            
            if (rondasJugadas > 0) {
                if (jugadorHumano.getPuntos() > jugadorIA.getPuntos()) {
                    System.out.println("¡Estás ganando!");
                } else if (jugadorHumano.getPuntos() < jugadorIA.getPuntos()) {
                    System.out.println("La IA está ganando");
                } else {
                    System.out.println("¡Empate perfecto!");
                }
            }
        } else {
            System.out.println("No has jugado ninguna partida aún.");
        }
        
        System.out.print("\nPresiona ENTER para continuar...");
        scanner.nextLine();
    }
    
    private void jugar() {
        if (jugadorHumano == null) {
            System.out.println("Error: Primero debes configurar el juego.");
            return;
        }
        
        boolean continuarJugando = true;
        
        while (continuarJugando) {
            System.out.println("\n==================================================");
            System.out.println("RONDA " + (rondasJugadas + 1));
            System.out.println("==================================================");
            
            // Generar cartas para esta ronda
            Carta[] cartasJuego = mazo.seleccionarCartasParaJuego(5);
            
            // Mostrar cartas al jugador
            mostrarCartas(cartasJuego);
            
            // Turno del jugador humano
            System.out.println("\n" + jugadorHumano.getNombre() + ", ¿cuál crees que es el espía?");
            System.out.print("Ingresa el número de carta (1-5): ");
            int eleccionHumano = leerOpcion() - 1;
            
            // Validar elección
            if (eleccionHumano < 0 || eleccionHumano >= cartasJuego.length) {
                System.out.println("Elección inválida. Perdiste tu turno.");
                eleccionHumano = -1;
            }
            
            // Turno de la IA
            int eleccionIA = ia.elegirCarta(cartasJuego, dificultad);
            
            // Encontrar el espía real
            int indiceEspiaReal = -1;
            for (int i = 0; i < cartasJuego.length; i++) {
                if (cartasJuego[i].esEspia()) {
                    indiceEspiaReal = i;
                    break;
                }
            }
            
            // Mostrar resultados
            mostrarResultados(cartasJuego, eleccionHumano, eleccionIA, indiceEspiaReal);
            
            // Actualizar puntuaciones
            if (eleccionHumano == indiceEspiaReal) {
                jugadorHumano.sumarPuntos(10);
            } else if (eleccionHumano != -1) {
                jugadorHumano.restarPuntos(3);
            }
            
            if (eleccionIA == indiceEspiaReal) {
                jugadorIA.sumarPuntos(10);
            } else {
                jugadorIA.restarPuntos(3);
            }
            
            rondasJugadas++;
            
            // Mostrar puntuaciones actuales
            System.out.println("\nPUNTUACIONES:");
            System.out.println(jugadorHumano.getNombre() + ": " + jugadorHumano.getPuntos() + " puntos");
            System.out.println(jugadorIA.getNombre() + ": " + jugadorIA.getPuntos() + " puntos");
            
            // Preguntar si quiere continuar
            System.out.print("\n¿Quieres jugar otra ronda? (s/n): ");
            String respuesta = scanner.nextLine().toLowerCase();
            continuarJugando = respuesta.equals("s") || respuesta.equals("si");
        }
        
        mostrarResultadoFinal();
    }
    
    private void mostrarCartas(Carta[] cartas) {
        System.out.println("\nCARTAS EN JUEGO:");
        System.out.println("Categoría: " + cartas[0].getCategoria());
        System.out.println();
        
        for (int i = 0; i < cartas.length; i++) {
            System.out.println((i + 1) + ". " + cartas[i].toString());
        }
    }
    
    private void mostrarResultados(Carta[] cartas, int eleccionHumano, int eleccionIA, int espiaReal) {
        System.out.println("\n========================================");
        System.out.println("RESULTADOS DE LA RONDA");
        System.out.println("========================================");
        
        System.out.println("El espía real era: Carta " + (espiaReal + 1) + " - " + cartas[espiaReal].toString());
        System.out.println();
        
        // Resultado del jugador humano
        if (eleccionHumano == -1) {
            System.out.println(jugadorHumano.getNombre() + ": Elección inválida");
        } else if (eleccionHumano == espiaReal) {
            System.out.println(jugadorHumano.getNombre() + ": ¡CORRECTO! (+10 puntos)");
        } else {
            System.out.println(jugadorHumano.getNombre() + ": Incorrecto. Elegiste carta " + (eleccionHumano + 1) + " (-3 puntos)");
        }
        
        // Resultado de la IA
        System.out.println("IA eligió: Carta " + (eleccionIA + 1));
        if (eleccionIA == espiaReal) {
            System.out.println("IA: ¡CORRECTO! (+10 puntos)");
        } else {
            System.out.println("IA: Incorrecto (-3 puntos)");
        }
    }
    
    private void mostrarResultadoFinal() {
        System.out.println("\n==================================================");
        System.out.println("RESULTADO FINAL");
        System.out.println("==================================================");
        
        System.out.println("Rondas jugadas: " + rondasJugadas);
        System.out.println(jugadorHumano.getNombre() + ": " + jugadorHumano.getPuntos() + " puntos");
        System.out.println(jugadorIA.getNombre() + ": " + jugadorIA.getPuntos() + " puntos");
        System.out.println();
        
        if (jugadorHumano.getPuntos() > jugadorIA.getPuntos()) {
            System.out.println("¡FELICITACIONES " + jugadorHumano.getNombre().toUpperCase() + "!");
            System.out.println("¡Has derrotado a la IA!");
        } else if (jugadorHumano.getPuntos() < jugadorIA.getPuntos()) {
            System.out.println("La IA ha ganado esta vez.");
            System.out.println("¡Sigue practicando para mejorar!");
        } else {
            System.out.println("¡EMPATE PERFECTO!");
            System.out.println("Ambos jugadores tienen el mismo puntaje.");
        }
    }
    
    public static void main(String[] args) {
        SpyChic juego = new SpyChic();
        juego.iniciar(); 
    }
}