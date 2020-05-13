package es.esteban.pruebas;

public class Prueba
{

    public static void main(String[] args)
    {
        String nif = "F92566173";
        System.out.println(nif + " -> " + isNif(nif));
    }

    public static boolean isNif (String nif) {
        if (nif == null) {
            return false;
        }
        
        try {
            nif = nif.toUpperCase();
            
            // Método personas físicas
            try {
                String secuenciaLetrasNIF = "TRWAGMYFPDXBNJZSQVHLCKE"; 
        
                //Posición inicial: 0 (primero en la cadena de texto).
                //Longitud: cadena de texto menos última posición. Así obtenemos solo el número.
                String numeroNIF = nif.substring(0, nif.length()-1);
        
                //Si es un NIE reemplazamos letra inicial por su valor numérico.
                numeroNIF = numeroNIF.replace("X", "0").replace("Y", "1").replace("Z", "2");
        
                //Obtenemos la letra con un char que nos servirá también para el índice de las secuenciaLetrasNIF
                char letraNIF = nif.charAt(8);
                int i = Integer.parseInt(numeroNIF) % 23;
                if (letraNIF == secuenciaLetrasNIF.charAt(i)) {
                    return true;
                }
            } catch (Exception e) {
                System.out.println("El NIF \"" + nif + "\" no se pudo validar como DNI o NIE");
            }
            
            //Método personas jurídicas
            String posicionesCentrales = nif.substring(1, nif.length() - 1);
            
            // A = Suma de los dígitos pares
            int sumatorioA = 0;
            for (int par = 1;par <= 6; par += 2) {
                String caracterPar = posicionesCentrales.substring(par, par + 1);
                sumatorioA += Integer.parseInt(caracterPar);
            }
            
            // B = Suma del doble de los dígitos impares (Si la suma sale > 10, se suman los dígitos de dicho resultado)
            int sumatorioB = 0;
            for (int impar = 0;impar <= 6; impar += 2) {
                String caracterImpar = posicionesCentrales.substring(impar, impar + 1);
                // El dígito se usa multiplicado por dos
                int dobleDigitoImpar = Integer.parseInt(caracterImpar) * 2;
                
                // Si el resultado tiene 2 dígitos, se suman (e.g.: 36 -> 3+6 -> 9)
                if (dobleDigitoImpar >= 10) {
                    dobleDigitoImpar = (dobleDigitoImpar / 10) + (dobleDigitoImpar % 10);
                }
                
                sumatorioB += dobleDigitoImpar;
            }
            
            // C = Suma de A + B
            int sumatorioC = sumatorioA + sumatorioB;
            
            // D = 10 menos el último dígito de C (Si el último dígito de C es 0, D también es 0)
            int sumatorioD = 0;
            int restoC = sumatorioC % 10;
            if (restoC > 0) {
                sumatorioD = 10 - restoC;
            }
            
            // Ahora se comprueba si el dígito de control es igual a sumatorioD, o, si es una letra de control,
            // se corresponde con la equivalencia A=1, B=2, C=3, D=3, E=5, F=6, G=7, H=8, I=9, J=0
            
            String digitoControl = nif.substring(nif.length() - 1);
            if (digitoControl.matches("^[JABCDEFGHI]$")) {
                if (sumatorioD == "JABCDEFGHI".indexOf(digitoControl)) {
                    return true;
                }
            } else if (digitoControl.matches("^\\d$")) {
                if (sumatorioD == Integer.parseInt(digitoControl)) {
                    return true;
                }
            }
            
        } catch (Exception e) {
            System.out.println("El NIF \"" + nif + "\" no se pudo validar");
        }
        
        return false;
    }
}
