package javaapplication6;

import java.util.Random;

public class JavaApplication6 {

    class Perceptron {

        private double[] pesos;
        private double sesgo;
        private double tasaAprendizaje;

        public Perceptron(int numEntradas, double tasaAprendizaje) {
            this.tasaAprendizaje = tasaAprendizaje;
            this.pesos = new double[numEntradas];
            this.sesgo = Math.random() * 2 - 1; // Entre -1 y 1

            // Inicializar pesos aleatoriamente
            Random rand = new Random();
            for (int i = 0; i < numEntradas; i++) {
                pesos[i] = rand.nextGaussian() * 0.1;
            }
        }

        // Función de activación sigmoide
        private double sigmoide(double x) {
            return 1.0 / (1.0 + Math.exp(-x));
        }

        // Derivada de la sigmoide
        private double derivadaSigmoide(double x) {
            return x * (1 - x);
        }

        public double predecir(double[] entradas) {
            double suma = sesgo;
            for (int i = 0; i < entradas.length; i++) {
                suma += entradas[i] * pesos[i];
            }
            return sigmoide(suma);
        }

        // Entrenar usando la regla delta
        public void entrenar(double[] entradas, double salidaDeseada) {
            double prediccion = predecir(entradas);
            double error = salidaDeseada - prediccion;

            // Ajustar pesos
            for (int i = 0; i < pesos.length; i++) {
                pesos[i] += tasaAprendizaje * error * entradas[i] * derivadaSigmoide(prediccion);
            }
            sesgo += tasaAprendizaje * error * derivadaSigmoide(prediccion);
        }
    }

// Clase para la Red Neuronal Multicapa
    class RedNeuronal {

        private int[] arquitectura;
        private double[][][] pesos; // [capa][neurona_salida][neurona_entrada]
        private double[][] sesgos;  // [capa][neurona]
        private double[][] activaciones; // [capa][neurona]
        private double tasaAprendizaje;

        public RedNeuronal(int[] arquitectura, double tasaAprendizaje) {
            this.arquitectura = arquitectura.clone();
            this.tasaAprendizaje = tasaAprendizaje;

            inicializarRed();
        }

        private void inicializarRed() {
            int numCapas = arquitectura.length - 1;
            pesos = new double[numCapas][][];
            sesgos = new double[numCapas][];
            activaciones = new double[arquitectura.length][];

            Random rand = new Random();

            for (int capa = 0; capa < numCapas; capa++) {
                int neuronasSalida = arquitectura[capa + 1];
                int neuronasEntrada = arquitectura[capa];

                pesos[capa] = new double[neuronasSalida][neuronasEntrada];
                sesgos[capa] = new double[neuronasSalida];

                // Inicialización Xavier
                double limite = Math.sqrt(6.0 / (neuronasEntrada + neuronasSalida));

                for (int j = 0; j < neuronasSalida; j++) {
                    sesgos[capa][j] = rand.nextGaussian() * 0.1;
                    for (int i = 0; i < neuronasEntrada; i++) {
                        pesos[capa][j][i] = (rand.nextDouble() * 2 - 1) * limite;
                    }
                }
            }

            // Inicializar arrays de activación
            for (int capa = 0; capa < arquitectura.length; capa++) {
                activaciones[capa] = new double[arquitectura[capa]];
            }
        }

        // Función ReLU
        private double relu(double x) {
            return Math.max(0, x);
        }

        // Derivada de ReLU
        private double derivadaRelu(double x) {
            return x > 0 ? 1 : 0;
        }

        // Función sigmoide
        private double sigmoide(double x) {
            return 1.0 / (1.0 + Math.exp(-Math.max(-500, Math.min(500, x))));
        }

        // Derivada sigmoide
        private double derivadaSigmoide(double x) {
            return x * (1 - x);
        }

        // Forward propagation
        public double[] predecir(double[] entrada) {
            // Copiar entrada a la primera capa
            System.arraycopy(entrada, 0, activaciones[0], 0, entrada.length);

            // Propagar hacia adelante
            for (int capa = 0; capa < pesos.length; capa++) {
                for (int neurona = 0; neurona < arquitectura[capa + 1]; neurona++) {
                    double suma = sesgos[capa][neurona];

                    for (int entrada_idx = 0; entrada_idx < arquitectura[capa]; entrada_idx++) {
                        suma += activaciones[capa][entrada_idx] * pesos[capa][neurona][entrada_idx];
                    }

                    // Usar ReLU para capas ocultas, sigmoide para salida
                    if (capa < pesos.length - 1) {
                        activaciones[capa + 1][neurona] = relu(suma);
                    } else {
                        activaciones[capa + 1][neurona] = sigmoide(suma);
                    }
                }
            }

            return activaciones[activaciones.length - 1].clone();
        }

        // Backpropagation
        public void entrenar(double[] entrada, double[] salidaDeseada) {
            // Forward pass
            predecir(entrada);

            int numCapas = pesos.length;
            double[][] errores = new double[numCapas][];

            // Inicializar arrays de errores
            for (int capa = 0; capa < numCapas; capa++) {
                errores[capa] = new double[arquitectura[capa + 1]];
            }

            // Calcular error de la capa de salida
            int capaSalida = numCapas - 1;
            for (int neurona = 0; neurona < arquitectura[capaSalida + 1]; neurona++) {
                double salida = activaciones[capaSalida + 1][neurona];
                double error = salidaDeseada[neurona] - salida;
                errores[capaSalida][neurona] = error * derivadaSigmoide(salida);
            }

            // Backpropagation de errores
            for (int capa = numCapas - 2; capa >= 0; capa--) {
                for (int neurona = 0; neurona < arquitectura[capa + 1]; neurona++) {
                    double error = 0.0;

                    for (int siguiente = 0; siguiente < arquitectura[capa + 2]; siguiente++) {
                        error += errores[capa + 1][siguiente] * pesos[capa + 1][siguiente][neurona];
                    }

                    double activacion = activaciones[capa + 1][neurona];
                    errores[capa][neurona] = error * derivadaRelu(activacion);
                }
            }

            // Actualizar pesos y sesgos
            for (int capa = 0; capa < numCapas; capa++) {
                for (int neurona = 0; neurona < arquitectura[capa + 1]; neurona++) {
                    // Actualizar sesgo
                    sesgos[capa][neurona] += tasaAprendizaje * errores[capa][neurona];

                    // Actualizar pesos
                    for (int entrada_idx = 0; entrada_idx < arquitectura[capa]; entrada_idx++) {
                        double delta = tasaAprendizaje * errores[capa][neurona] * activaciones[capa][entrada_idx];
                        pesos[capa][neurona][entrada_idx] += delta;
                    }
                }
            }
        }

        // Entrenar con conjunto de datos
        public void entrenar(double[][] datosEntrada, double[][] salidaDeseada, int epocas) {
            for (int epoca = 0; epoca < epocas; epoca++) {
                double errorTotal = 0.0;

                for (int ejemplo = 0; ejemplo < datosEntrada.length; ejemplo++) {
                    entrenar(datosEntrada[ejemplo], salidaDeseada[ejemplo]);

                    // Calcular MSE para monitoreo
                    double[] prediccion = predecir(datosEntrada[ejemplo]);
                    for (int i = 0; i < salidaDeseada[ejemplo].length; i++) {
                        double error = salidaDeseada[ejemplo][i] - prediccion[i];
                        errorTotal += error * error;
                    }
                }

                if (epoca % 100 == 0) {
                    double mse = errorTotal / (datosEntrada.length * salidaDeseada[0].length);
                    System.out.printf("Época %d, MSE: %.6f%n", epoca, mse);
                }
            }
        }
    }

// Clase para representar puntos geométricos
    class Punto {

        public double x, y;

        public Punto(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double distancia(Punto otro) {
            return Math.sqrt((x - otro.x) * (x - otro.x) + (y - otro.y) * (y - otro.y));
        }

        @Override
        public String toString() {
            return String.format("(%.2f, %.2f)", x, y);
        }
    }

// Clase para representar obstáculos lineales
    class Obstaculo {

        public Punto inicio, fin;

        public Obstaculo(Punto inicio, Punto fin) {
            this.inicio = inicio;
            this.fin = fin;
        }

        // Verificar si un punto está en el segmento
        public boolean intersecta(Punto p1, Punto p2) {
            return interseccionSegmentos(p1, p2, inicio, fin);
        }

        // Algoritmo para detectar intersección entre segmentos
        private boolean interseccionSegmentos(Punto p1, Punto q1, Punto p2, Punto q2) {
            int o1 = orientacion(p1, q1, p2);
            int o2 = orientacion(p1, q1, q2);
            int o3 = orientacion(p2, q2, p1);
            int o4 = orientacion(p2, q2, q1);

            // Caso general
            if (o1 != o2 && o3 != o4) {
                return true;
            }

            // Casos especiales colineales
            if (o1 == 0 && enSegmento(p1, p2, q1)) {
                return true;
            }
            if (o2 == 0 && enSegmento(p1, q2, q1)) {
                return true;
            }
            if (o3 == 0 && enSegmento(p2, p1, q2)) {
                return true;
            }
            if (o4 == 0 && enSegmento(p2, q1, q2)) {
                return true;
            }

            return false;
        }

        private int orientacion(Punto p, Punto q, Punto r) {
            double val = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y);
            if (Math.abs(val) < 1e-10) {
                return 0; // Colinear
            }
            return (val > 0) ? 1 : 2; // Horario o antihorario
        }

        private boolean enSegmento(Punto p, Punto q, Punto r) {
            return q.x <= Math.max(p.x, r.x) && q.x >= Math.min(p.x, r.x)
                    && q.y <= Math.max(p.y, r.y) && q.y >= Math.min(p.y, r.y);
        }
    }

// Generador de datos sintéticos
    class GeneradorDatos {

        private Random random;

        public GeneradorDatos() {
            this.random = new Random(42); // Semilla fija para reproducibilidad
        }

        // Generar un ejemplo de entrenamiento
        public EjemploEntrenamiento generarEjemplo() {
            // Generar puntos A y B aleatorios
            Punto A = new Punto(random.nextDouble() * 10, random.nextDouble() * 10);
            Punto B = new Punto(random.nextDouble() * 10, random.nextDouble() * 10);

            // Generar obstáculo de longitud 2
            double centroX = 2 + random.nextDouble() * 6;
            double centroY = 2 + random.nextDouble() * 6;
            double angulo = random.nextDouble() * 2 * Math.PI;

            Punto inicioObst = new Punto(
                    centroX - Math.cos(angulo),
                    centroY - Math.sin(angulo)
            );
            Punto finObst = new Punto(
                    centroX + Math.cos(angulo),
                    centroY + Math.sin(angulo)
            );

            Obstaculo obstaculo = new Obstaculo(inicioObst, finObst);

            // Calcular puntos de desviación P1 y P2
            Punto[] puntosDesviacion = calcularDesviacion(A, B, obstaculo);

            return new EjemploEntrenamiento(A, B, obstaculo, puntosDesviacion[0], puntosDesviacion[1]);
        }

        // Algoritmo heurístico para calcular puntos de desviación
        private Punto[] calcularDesviacion(Punto A, Punto B, Obstaculo obst) {
            // Vector perpendicular al obstáculo
            double dx = obst.fin.x - obst.inicio.x;
            double dy = obst.fin.y - obst.inicio.y;
            double perpX = -dy;
            double perpY = dx;

            // Normalizar
            double longitud = Math.sqrt(perpX * perpX + perpY * perpY);
            perpX /= longitud;
            perpY /= longitud;

            // Punto medio del obstáculo
            double medioX = (obst.inicio.x + obst.fin.x) / 2;
            double medioY = (obst.inicio.y + obst.fin.y) / 2;

            // Desviación de 1 unidad perpendicular
            double distDesviacion = 1.5;

            Punto P1 = new Punto(
                    medioX + perpX * distDesviacion,
                    medioY + perpY * distDesviacion
            );

            Punto P2 = new Punto(
                    medioX - perpX * distDesviacion,
                    medioY - perpY * distDesviacion
            );

            // Elegir el par que mejor evita el obstáculo
            if (A.distancia(P1) + P1.distancia(P2) + P2.distancia(B)
                    < A.distancia(P2) + P2.distancia(P1) + P1.distancia(B)) {
                return new Punto[]{P1, P2};
            } else {
                return new Punto[]{P2, P1};
            }
        }

        // Generar conjunto de datos
        public EjemploEntrenamiento[] generarConjuntoDatos(int numEjemplos) {
            EjemploEntrenamiento[] ejemplos = new EjemploEntrenamiento[numEjemplos];

            System.out.println("Generando " + numEjemplos + " ejemplos de entrenamiento...");
            for (int i = 0; i < numEjemplos; i++) {
                ejemplos[i] = generarEjemplo();

                if ((i + 1) % 1000 == 0) {
                    System.out.println("Generados: " + (i + 1) + "/" + numEjemplos);
                }
            }

            return ejemplos;
        }
    }

// Clase para almacenar ejemplos de entrenamiento
    class EjemploEntrenamiento {

        public Punto A, B, P1, P2;
        public Obstaculo obstaculo;

        public EjemploEntrenamiento(Punto A, Punto B, Obstaculo obst, Punto P1, Punto P2) {
            this.A = A;
            this.B = B;
            this.obstaculo = obst;
            this.P1 = P1;
            this.P2 = P2;
        }

        // Convertir a array de entrada normalizado [0,1]
        public double[] toEntrada() {
            return new double[]{
                A.x / 10.0, A.y / 10.0,
                B.x / 10.0, B.y / 10.0,
                obstaculo.inicio.x / 10.0, obstaculo.inicio.y / 10.0,
                obstaculo.fin.x / 10.0, obstaculo.fin.y / 10.0
            };
        }

        // Convertir salida esperada normalizada [0,1]
        public double[] toSalida() {
            return new double[]{
                P1.x / 10.0, P1.y / 10.0,
                P2.x / 10.0, P2.y / 10.0
            };
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Sistema de Navegación con Redes Neuronales ===\n");

        // 1. Generar datos
        System.out.println("1. Generación de datos sintéticos");
        GeneradorDatos generador = new JavaApplication6().new GeneradorDatos();
        EjemploEntrenamiento[] ejemplos = generador.generarConjuntoDatos(10000);

        // 2. Preparar datos para entrenamiento
        System.out.println("\n2. Preparación de datos para entrenamiento");
        double[][] entradas = new double[ejemplos.length][];
        double[][] salidas = new double[ejemplos.length][];

        for (int i = 0; i < ejemplos.length; i++) {
            entradas[i] = ejemplos[i].toEntrada();
            salidas[i] = ejemplos[i].toSalida();
        }

        // 3. Crear y entrenar red neuronal
        System.out.println("\n3. Entrenamiento de la red neuronal");
        System.out.println("Arquitectura: 8 → 16 → 16 → 4");

        RedNeuronal red = new JavaApplication6().new RedNeuronal(new int[]{8, 16, 16, 4}, 0.01);
        red.entrenar(entradas, salidas, 1000);

        // 4. Evaluación
        System.out.println("\n4. Evaluación del modelo");
        new JavaApplication6().evaluarModelo(red, ejemplos);

        // 5. Caso de ejemplo
        System.out.println("\n5. Caso de ejemplo");
        new JavaApplication6().probarEjemplo(red);
    }
    
    private static void evaluarModelo(RedNeuronal red, EjemploEntrenamiento[] ejemplos) {
        double errorTotal = 0;
        int prediccionesCorrectas = 0;
        int numPruebas = 1000;
        
        for (int i = 0; i < numPruebas; i++) {
            EjemploEntrenamiento ejemplo = ejemplos[i];
            double[] entrada = ejemplo.toEntrada();
            double[] salidaEsperada = ejemplo.toSalida();
            double[] prediccion = red.predecir(entrada);
            
            // Calcular error
            double error = 0;
            for (int j = 0; j < prediccion.length; j++) {
                double diff = (prediccion[j] - salidaEsperada[j]) * 10; // Desnormalizar
                error += Math.abs(diff);
            }
            errorTotal += error;
            
            // Considerar correcta si el error es menor a 0.5 unidades
            if (error / 4 < 0.5) {
                prediccionesCorrectas++;
            }
        }
        
        double mae = errorTotal / (numPruebas * 4);
        double precision = (double) prediccionesCorrectas / numPruebas * 100;
        
        System.out.printf("Error Medio Absoluto (MAE): %.3f unidades%n", mae);
        System.out.printf("Precisión: %.1f%% (predicciones con error < 0.5 unidades)%n", precision);
    }
    
    private static void probarEjemplo(RedNeuronal red) {
        // Caso del documento
        Punto A = new Punto(1.0, 1.0);
        Punto B = new Punto(9.0, 9.0);
        Obstaculo obst = new Obstaculo(new Punto(4.0, 4.0), new Punto(6.0, 6.0));
        
        double[] entrada = {
            A.x / 10.0, A.y / 10.0,
            B.x / 10.0, B.y / 10.0,
            obst.inicio.x / 10.0, obst.inicio.y / 10.0,
            obst.fin.x / 10.0, obst.fin.y / 10.0
        };


        
        double[] prediccion = red.predecir(entrada);
        
        // Desnormalizar
        Punto P1_pred = new Punto(prediccion[0] * 10, prediccion[1] * 10);
        Punto P2_pred = new Punto(prediccion[2] * 10, prediccion[3] * 10);
        
        System.out.println("Entrada:");
        System.out.println("  A: " + A);
        System.out.println("  B: " + B);
        System.out.println("  Obstáculo: " + obst.inicio + " → " + obst.fin);
        System.out.println();
        System.out.println("Predicción de la red:");
        System.out.println("  P1: " + P1_pred);
        System.out.println("  P2: " + P2_pred);
        System.out.println();
        System.out.printf("Ruta total: A → P1 → P2 → B%n");
        System.out.printf("Distancia total: %.2f unidades%n", 
            A.distancia(P1_pred) + P1_pred.distancia(P2_pred) + P2_pred.distancia(B));
    }
}    
    
    
