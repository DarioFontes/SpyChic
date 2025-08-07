package spy.chic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

// ==================== CLASE CARTA ====================
class Carta {

    private String categoria; //Categoria de la carta
    private String nombre; // nombre de la carta
    private int valor; //asiganamos un valor a la carta para jugar
    private boolean esEspia; // expresion booleana para identificar al espia

    // constructor que inica todo los atributos de la carta
    public Carta(String categoria, String nombre, int valor, boolean esEspia) {
        this.categoria = categoria;
        this.nombre = nombre;
        this.valor = valor;
        this.esEspia = esEspia;
    }

    // Getters y Setters para acceder a los atributos privados
    public String getCategoria() {
        return categoria;
    }

    public String getNombre() {
        return nombre;
    }

    public int getValor() {
        return valor;
    }

    public boolean esEspia() {
        return esEspia;
    }

    public void setEspia(boolean espia) {
        this.esEspia = espia;
    }

    // sobre escribimos la carta espia
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

    // creamos todas la cartas del juego siempre asiganando false => eso indique que ninguno es espia
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

    //Metodo para seleccionar carta en una ronda
    public Carta[] seleccionarCartasParaJuego(int cantidad) {
        Collections.shuffle(cartas); //mezclamos las cartas aleatoriamente
        Carta[] cartasJuego = new Carta[cantidad];

        // 1. Selecciona la categoría principal (para las cartas normales)
        String categoriaPrincipal = cartas.get(0).getCategoria();
        List<Carta> cartasMismaCategoria = new ArrayList<>();
        List<Carta> cartasOtrasCategorias = new ArrayList<>();
        for (Carta carta : cartas) {
            if (carta.getCategoria().equals(categoriaPrincipal)) {
                cartasMismaCategoria.add(carta);
            } else {
                cartasOtrasCategorias.add(carta);
            }
        }

        // 2. Selecciona una carta espía de OTRA categoría
        Carta cartaEspiaOriginal = cartasOtrasCategorias.get(random.nextInt(cartasOtrasCategorias.size()));
        Carta cartaEspia = new Carta(cartaEspiaOriginal.getCategoria(), cartaEspiaOriginal.getNombre(), cartaEspiaOriginal.getValor(), true);

        // 3. Selecciona (cantidad-1) cartas DIFERENTES de la categoría principal
        Collections.shuffle(cartasMismaCategoria);
        List<Carta> cartasSeleccionadas = new ArrayList<>();
        
        // Asegurar que no se repitan cartas
        for (int i = 0; i < Math.min(cantidad - 1, cartasMismaCategoria.size()); i++) {
            Carta original = cartasMismaCategoria.get(i);
            cartasSeleccionadas.add(new Carta(original.getCategoria(), original.getNombre(), original.getValor(), false));
        }

        // 4. Crear array temporal con todas las cartas (sin el espía aún)
        List<Carta> todasLasCartas = new ArrayList<>(cartasSeleccionadas);
        
        // 5. Agregar el espía a la lista
        todasLasCartas.add(cartaEspia);
        
        // 6. Mezclar todas las cartas para posición aleatoria
        Collections.shuffle(todasLasCartas);
        
        // 7. Copiar al array final
        for (int i = 0; i < cantidad && i < todasLasCartas.size(); i++) {
            cartasJuego[i] = todasLasCartas.get(i);
        }

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
    public String getNombre() {
        return nombre;
    }

    public int getPuntos() {
        return puntos;
    }

    public boolean esIA() {
        return esIA;
    }

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
        switch (dificultad) {
            case 1: // Fácil - completamente aleatorio
                return random.nextInt(cartas.length);

            case 2: // Medio - busca categoría diferente con probabilidad del 70%
                return elegirCartaMedio(cartas);

            case 3: // Difícil - casi siempre encuentra al espía
                return elegirCartaDificil(cartas);

            default:
                return random.nextInt(cartas.length);
        }
    }

    private int elegirCartaMedio(Carta[] cartas) {
        // Estrategia: 70% de probabilidad de encontrar la categoría diferente
        if (random.nextDouble() < 0.7) {
            return encontrarCartaDiferenteCategoria(cartas);
        } else {
            return random.nextInt(cartas.length);
        }
    }

    private int elegirCartaDificil(Carta[] cartas) {
        // Estrategia: 90% de probabilidad de encontrar la categoría diferente
        if (random.nextDouble() < 0.9) {
            return encontrarCartaDiferenteCategoria(cartas);
        } else {
            return random.nextInt(cartas.length);
        }
    }

    private int encontrarCartaDiferenteCategoria(Carta[] cartas) {
        // Contar frecuencia de cada categoría
        java.util.Map<String, Integer> contadorCategorias = new java.util.HashMap<>();
        java.util.Map<String, java.util.List<Integer>> indicesPorCategoria = new java.util.HashMap<>();
        
        for (int i = 0; i < cartas.length; i++) {
            String categoria = cartas[i].getCategoria();
            contadorCategorias.put(categoria, contadorCategorias.getOrDefault(categoria, 0) + 1);
            
            // Guardar TODOS los índices de cada categoría
            if (!indicesPorCategoria.containsKey(categoria)) {
                indicesPorCategoria.put(categoria, new java.util.ArrayList<>());
            }
            indicesPorCategoria.get(categoria).add(i);
        }

        // Buscar la categoría con menor frecuencia (debería ser 1)
        String categoriaMenosFrecuente = null;
        int menorFrecuencia = Integer.MAX_VALUE;

        for (java.util.Map.Entry<String, Integer> entry : contadorCategorias.entrySet()) {
            if (entry.getValue() < menorFrecuencia) {
                menorFrecuencia = entry.getValue();
                categoriaMenosFrecuente = entry.getKey();
            }
        }

        // Devolver un índice aleatorio de la categoría menos frecuente
        if (categoriaMenosFrecuente != null && indicesPorCategoria.containsKey(categoriaMenosFrecuente)) {
            java.util.List<Integer> indices = indicesPorCategoria.get(categoriaMenosFrecuente);
            return indices.get(random.nextInt(indices.size()));
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
        switch (dificultad) {
            case 1:
                return "Fácil";
            case 2:
                return "Medio";
            case 3:
                return "Difícil";
            default:
                return "Medio";
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
        System.out.println("   Encuentra la carta 'espía' entre todas las cartas mostradas.");
        System.out.println();
        System.out.println("CÓMO JUGAR:");
        System.out.println("   1. Se mostrarán 5 cartas");
        System.out.println("   2. 4 cartas pertenecen a la misma categoría");
        System.out.println("   3. 1 carta (el 'espía') pertenece a una categoría diferente");
        System.out.println("   4. Tu objetivo es identificar cuál es la carta de categoría diferente");
        System.out.println("   5. Compites contra la IA para ver quién encuentra más espías");
        System.out.println();
        System.out.println("CATEGORÍAS:");
        System.out.println("   • Animales: León, Tigre, Elefante, Águila, Lobo");
        System.out.println("   • Profesiones: Doctor, Maestro, Ingeniero, Chef, Artista");
        System.out.println("   • Objetos: Espada, Escudo, Poción, Llave, Libro");
        System.out.println("   • Lugares: Bosque, Montaña, Playa, Ciudad, Desierto");
        System.out.println();
        System.out.println("PUNTUACIÓN:");
        System.out.println("   • Acierto: +10 puntos");
        System.out.println("   • Error: -3 puntos");
        System.out.println();
        System.out.println("CONSEJO:");
        System.out.println("   Observa las categorías de las cartas. El espía será la única carta");
        System.out.println("   que no pertenece al grupo principal.");
        System.out.println();
        System.out.print("Presiona ENTER para volver al menú...");
        scanner.nextLine();
    }

    private void configurarDificultad() {
        System.out.println("\n========== DIFICULTAD ==========");
        System.out.println("1. Fácil   - IA juega completamente al azar");
        System.out.println("2. Medio   - IA encuentra al espía 70% de las veces");
        System.out.println("3. Difícil - IA encuentra al espía 90% de las veces");
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

            // Mostrar pistas útiles
            mostrarPistas(cartasJuego);
            
            // Turno del jugador humano
            System.out.println(jugadorHumano.getNombre() + ", ¿cuál crees que es el espía?");
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
        
        // Encontrar la categoría más común
        java.util.Map<String, Integer> contadorCategorias = new java.util.HashMap<>();
        for (Carta carta : cartas) {
            String categoria = carta.getCategoria();
            contadorCategorias.put(categoria, contadorCategorias.getOrDefault(categoria, 0) + 1);
        }
        
        String categoriaPrincipal = null;
        int maxFrecuencia = 0;
        for (java.util.Map.Entry<String, Integer> entry : contadorCategorias.entrySet()) {
            if (entry.getValue() > maxFrecuencia) {
                maxFrecuencia = entry.getValue();
                categoriaPrincipal = entry.getKey();
            }
        }
        
        System.out.println("Categoría principal: " + categoriaPrincipal + " (aparece " + maxFrecuencia + " veces)");
        System.out.println();

        for (int i = 0; i < cartas.length; i++) {
            System.out.println((i + 1) + ". " + cartas[i].toString());
        }
    }

    private void mostrarPistas(Carta[] cartas) {
        System.out.println("\n🕵️ PISTAS:");
        System.out.println("• Busca la carta que pertenece a una categoría diferente");
        System.out.println("• El espía es la única carta que no encaja con el grupo principal");
        System.out.println("• Las categorías son: Animal, Profesión, Objeto, Lugar");
        System.out.println();
    }

    private void mostrarResultados(Carta[] cartas, int eleccionHumano, int eleccionIA, int espiaReal) {
        System.out.println("\n========================================");
        System.out.println("RESULTADOS DE LA RONDA");
        System.out.println("========================================");

        System.out.println("El espía real era: Carta " + (espiaReal + 1) + " - " + cartas[espiaReal].toString());
        System.out.println("Razón: Es la única carta de categoría '" + cartas[espiaReal].getCategoria() + "'");
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