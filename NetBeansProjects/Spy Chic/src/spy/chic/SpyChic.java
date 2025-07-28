
package spy.chic;


public class SpyChic {
    
        //Metodo para saludar
    public static void saludarUsuario(){
        System.out.println("Hola usuario! Bienvenido al sistema");
    }
            //Metodo Suma enteros
    public static int sumDosNum(int a, int b){
        return a + b;
    }
        /**
         * Sintaxis "print F" 
         * (con la cadena de formato y los valores separados por comas)
         * es exclusiva de System.out.printf()
         */
    
    public static void mostrarTabla(int n){
        for (int i = 0; i <= 10; i++) {
            System.out.printf("%d x %d = %d%n", n, i, n*i);
        }
    }
            //Metodo para numero PAR
    public static boolean esPar(int n){
        return n % 2 == 0;
    }
                    //como funciona?
        //(condición) ? valor_si_verdadero : valor_si_falso;
    public static int maximos(int a, int b){
        return (a > b) ? a : b;
    }
    
   
    public static void main(String[] args) {
       
        //Ej1
        saludarUsuario();
        
        //Ej2
        int resultado = sumDosNum(2,2);
        System.out.println(resultado);
        
        //Ej3
        mostrarTabla(2);
        
        //Ej4
        boolean numPar = esPar(3);
        System.out.println(numPar);
        
        //Ej5
        int maXimos = maximos(12, 24);
        System.out.println(maXimos);
        
        
    }
    
}
