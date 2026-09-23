package persistencia;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import modelo.Cliente;
import modelo.Producto;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;


/**
 * ================================================================
 * GESTOR DE FICHEROS
 * ================================================================
 *
 * Esta clase concentra la lógica relacionada con la persistencia
 * de nuestros objetos en ficheros.
 *
 * La idea es separar responsabilidades:
 *
 *      INTERFAZ GRÁFICA
 *             |
 *             v
 *         SERVICIO
 *             |
 *             v
 *      GestorFicheros
 *             |
 *             v
 *          FICHEROS
 *
 *
 * En esta fase estamos trabajando con dos formatos:
 *
 *      TXT
 *      CSV
 *
 *
 * Posteriormente incorporaremos:
 *
 *      XML
 *      JSON
 *
 *
 * IMPORTANTE:
 *
 * Para TXT y CSV estamos realizando nosotros manualmente
 * la transformación entre objetos Java y texto.
 *
 * Más adelante, con XML y JSON, utilizaremos Jackson para
 * realizar gran parte de ese trabajo automáticamente.
 */
public class GestorFicheros {


    // ============================================================
    // TXT
    // ============================================================


    /**
     * ============================================================
     * EXPORTAR CLIENTES A TXT
     * ============================================================
     *
     * Transforma una lista de objetos Cliente en un fichero
     * de texto.
     *
     * Hemos decidido utilizar ";" como separador.
     *
     * Ejemplo:
     *
     *      Cliente
     *      -------
     *      id       = 1
     *      nombre   = Ana
     *      email    = ana@email.com
     *      telefono = 600123456
     *
     * se guardará como:
     *
     *      1;Ana;ana@email.com;600123456
     *
     */
    public static void exportarClientesTxt(
            Path ruta,
            List<Cliente> clientes
    ) throws IOException {


        /*
         * Files.newBufferedWriter(...)
         *
         * abre un fichero para escritura.
         *
         * Utilizamos UTF-8 para que caracteres como:
         *
         *      á
         *      é
         *      ñ
         *      €
         *
         * se almacenen correctamente.
         *
         *
         * El try-with-resources:
         *
         *      try (...) {
         *
         *      }
         *
         * garantiza que Java cerrará automáticamente
         * el BufferedWriter al terminar.
         */
        try (BufferedWriter bw =
                     Files.newBufferedWriter(
                             ruta,
                             StandardCharsets.UTF_8
                     )) {


            /*
             * Recorremos todos los clientes.
             *
             * En cada iteración:
             *
             *      c
             *
             * representa un Cliente diferente.
             */
            for (Cliente c : clientes) {


                /*
                 * Construimos manualmente la representación
                 * textual del Cliente.
                 *
                 * Por ejemplo:
                 *
                 *      1;Ana;ana@email.com;600123456
                 *
                 *
                 * Estamos realizando una SERIALIZACIÓN MANUAL:
                 *
                 *      Cliente
                 *         ↓
                 *      String
                 *         ↓
                 *      fichero
                 */
                bw.write(
                        c.getId()
                                + ";"
                                + c.getNombre()
                                + ";"
                                + c.getEmail()
                                + ";"
                                + c.getTelefono()
                );


                /*
                 * Cada Cliente ocupará una línea.
                 */
                bw.newLine();
            }
        }
    }


    /**
     * ============================================================
     * IMPORTAR CLIENTES DESDE TXT
     * ============================================================
     *
     * Hace el proceso contrario:
     *
     *      fichero
     *         ↓
     *      String
     *         ↓
     *      Cliente
     *
     */
    public static List<Cliente> importarClientesTxt(
            Path ruta
    ) throws IOException {


        /*
         * Aquí iremos guardando todos los clientes
         * que consigamos reconstruir correctamente.
         */
        List<Cliente> resultado = new ArrayList<>();


        /*
         * Abrimos el fichero para lectura.
         */
        try (BufferedReader br =
                     Files.newBufferedReader(
                             ruta,
                             StandardCharsets.UTF_8
                     )) {


            /*
             * Variable que almacenará temporalmente
             * cada línea del fichero.
             */
            String linea;


            /*
             * readLine() devuelve:
             *
             *      una línea
             *
             * o:
             *
             *      null
             *
             * cuando llegamos al final del fichero.
             */
            while ((linea = br.readLine()) != null) {


                /*
                 * Dividimos la línea utilizando ";".
                 *
                 * Ejemplo:
                 *
                 *      1;Ana;ana@email.com;600123456
                 *
                 * se convierte en:
                 *
                 *      p[0] -> "1"
                 *      p[1] -> "Ana"
                 *      p[2] -> "ana@email.com"
                 *      p[3] -> "600123456"
                 */
                String[] p = linea.split(";");


                /*
                 * Nuestro Cliente necesita exactamente
                 * cuatro campos.
                 *
                 * Si no tenemos cuatro, descartamos la línea.
                 */
                if (p.length != 4) {
                    continue;
                }


                try {

                    /*
                     * El fichero solamente contiene texto.
                     *
                     * Por tanto:
                     *
                     *      p[0]
                     *
                     * es un String.
                     *
                     * Tenemos que convertirlo:
                     *
                     *      "123" -> 123
                     */
                    int id = Integer.parseInt(p[0]);


                    /*
                     * Estos campos ya son String.
                     */
                    String nombre = p[1];
                    String email = p[2];
                    String telefono = p[3];


                    /*
                     * Reconstruimos el objeto Cliente.
                     */
                    Cliente cliente =
                            new Cliente(
                                    id,
                                    nombre,
                                    email,
                                    telefono
                            );


                    /*
                     * Lo añadimos a la lista.
                     */
                    resultado.add(cliente);


                } catch (NumberFormatException ex) {

                    /*
                     * Si encontramos:
                     *
                     *      ABC;Ana;ana@email.com;600123456
                     *
                     * Integer.parseInt("ABC")
                     *
                     * provocará NumberFormatException.
                     */
                    System.err.println(
                            "Cliente incorrecto: " + linea
                    );
                }
            }
        }


        return resultado;
    }



    // ============================================================
    // CSV
    // ============================================================


    /*
     * Ahora aparece un problema nuevo.
     *
     * CSV utiliza normalmente una coma como separador.
     *
     * Podríamos tener:
     *
     *      1,Ana,ana@email.com,600123456
     *
     *
     * Pero ¿qué sucede si el propio dato contiene una coma?
     *
     * Por ejemplo:
     *
     *      Pérez, Juan
     *
     *
     * No podemos escribir:
     *
     *      1,Pérez, Juan,email,telefono
     *
     * porque parecería que Pérez y Juan son campos distintos.
     *
     *
     * CSV permite utilizar comillas:
     *
     *      1,"Pérez, Juan",email,telefono
     *
     *
     * Por tanto necesitamos implementar dos procesos:
     *
     *
     * ESCRITURA:
     *
     *      String
     *        ↓
     *      csv()
     *        ↓
     *      campo correctamente escapado
     *
     *
     * LECTURA:
     *
     *      línea CSV
     *        ↓
     *      parseCsv()
     *        ↓
     *      campos individuales
     */



    /**
     * ============================================================
     * MÉTODO csv()
     * ============================================================
     *
     * Recibe UN campo y devuelve ese campo preparado
     * para poder introducirlo correctamente en nuestro CSV.
     *
     * IMPORTANTE:
     *
     * Este método NO escribe en el fichero.
     *
     * Solamente transforma un String.
     *
     *
     * Ejemplos:
     *
     *      csv("Ana")
     *
     * devuelve:
     *
     *      Ana
     *
     *
     * Pero:
     *
     *      csv("Pérez, Juan")
     *
     * devuelve:
     *
     *      "Pérez, Juan"
     *
     */
    private static String csv(String valor) {


        /*
         * PRIMER CASO:
         *
         * El valor recibido es null.
         *
         * No podemos hacer:
         *
         *      valor.contains(...)
         *
         * sobre null porque provocaría:
         *
         *      NullPointerException
         *
         * Hemos decidido representar null mediante
         * una cadena vacía.
         */
        if (valor == null) {
            return "";
        }


        /*
         * Comprobamos si el contenido tiene alguno
         * de los caracteres que necesitan tratamiento
         * especial:
         *
         *      ,
         *      "
         *      salto de línea
         *
         *
         * || significa OR lógico.
         *
         * Basta con que UNA de las condiciones sea true
         * para entrar en el if.
         */
        if (
                valor.contains(",")
                        || valor.contains("\"")
                        || valor.contains("\n")
        ) {


            /*
             * Aquí hacemos DOS operaciones diferentes.
             *
             *
             * OPERACIÓN 1
             * -----------
             *
             * Escapar las comillas interiores.
             *
             * En CSV una comilla interior se puede representar
             * duplicándola.
             *
             *
             * Tenemos:
             *
             *      Tienda "Pepe"
             *
             * Después de:
             *
             *      valor.replace("\"", "\"\"")
             *
             * tendremos:
             *
             *      Tienda ""Pepe""
             *
             *
             * IMPORTANTE:
             *
             * \" es simplemente la manera de representar
             * el carácter " dentro de un String Java.
             *
             *
             * OPERACIÓN 2
             * -----------
             *
             * Rodeamos todo el campo con comillas.
             *
             *
             * Resultado final:
             *
             *      "Tienda ""Pepe"""
             *
             *
             * Otro ejemplo:
             *
             *      Pérez, Juan
             *
             * se convierte en:
             *
             *      "Pérez, Juan"
             *
             *
             * IMPORTANTE:
             *
             * Este replace NO elimina saltos de línea.
             *
             * Solamente sustituye:
             *
             *      "
             *
             * por:
             *
             *      ""
             */
            return "\""
                    + valor.replace("\"", "\"\"")
                    + "\"";
        }


        /*
         * Si el valor no tiene:
         *
         *      comas
         *      comillas
         *      saltos de línea
         *
         * no necesitamos modificarlo.
         */
        return valor;
    }



    /**
     * ============================================================
     * parseCsv()
     * ============================================================
     *
     * Este método hace aproximadamente el proceso contrario
     * de csv().
     *
     *
     * Recibe UNA LÍNEA completa:
     *
     *      3,"Pérez, ""Juan""",Ourense,Pepa
     *
     *
     * y debe obtener los diferentes campos.
     *
     *
     * NO podemos utilizar:
     *
     *      linea.split(",")
     *
     * porque la coma puede aparecer dentro de un campo:
     *
     *              ↓
     *      "Pérez, Juan"
     *
     *
     * Esa coma NO separa columnas.
     *
     *
     * Por eso necesitamos analizar la línea
     * CARÁCTER A CARÁCTER.
     */
    private static List<String> parseCsv(String linea) {


        /*
         * Lista donde almacenaremos los campos
         * que vayamos encontrando.
         *
         * Por ejemplo, finalmente podríamos tener:
         *
         *      campos[0] -> "3"
         *      campos[1] -> "Pérez, \"Juan\""
         *      campos[2] -> "Ourense"
         *      campos[3] -> "Pepa"
         */
        List<String> campos = new ArrayList<>();


        /*
         * StringBuilder
         * ========================================================
         *
         * Necesitamos construir cada campo poco a poco.
         *
         * Como vamos a recorrer caracteres individualmente,
         * utilizamos StringBuilder.
         *
         *
         * Por ejemplo:
         *
         * leemos:
         *
         *      P
         *      é
         *      r
         *      e
         *      z
         *
         * y hacemos:
         *
         *      actual.append('P');
         *      actual.append('é');
         *      ...
         *
         * hasta obtener:
         *
         *      "Pérez"
         *
         *
         * StringBuilder es especialmente apropiado cuando
         * construimos texto mediante muchas modificaciones.
         */
        StringBuilder actual = new StringBuilder();


        /*
         * Esta variable es fundamental.
         *
         * Nos dice si actualmente estamos:
         *
         *      FUERA de un campo entrecomillado
         *
         * o:
         *
         *      DENTRO de un campo entrecomillado.
         *
         *
         * false:
         *
         *      estamos fuera
         *
         * true:
         *
         *      estamos dentro
         *
         *
         * Inicialmente estamos fuera.
         */
        boolean entreComillas = false;


        /*
         * Ejemplo que queremos interpretar:
         *
         *      3,"Pérez, ""Juan""",Ourense,Pepa
         *
         *
         * Recorreremos:
         *
         *      3
         *      ,
         *      "
         *      P
         *      é
         *      r
         *      e
         *      z
         *      ,
         *      ...
         *
         * carácter por carácter.
         */
        for (int i = 0; i < linea.length(); i++) {


            /*
             * charAt(i) devuelve el carácter que ocupa
             * la posición i.
             *
             * Si:
             *
             *      linea = "Ana"
             *
             * tendremos:
             *
             *      charAt(0) -> 'A'
             *      charAt(1) -> 'n'
             *      charAt(2) -> 'a'
             */
            char c = linea.charAt(i);



            /*
             * ====================================================
             * CASO 1: ENCONTRAMOS UNA COMILLA
             * ====================================================
             */
            if (c == '"') {


                /*
                 * Tenemos que distinguir dos situaciones.
                 *
                 *
                 * SITUACIÓN A:
                 *
                 * Estamos dentro de un campo entrecomillado
                 * Y la siguiente posición también contiene ".
                 *
                 *
                 * Es decir:
                 *
                 *      ""
                 *
                 *
                 * Eso representa una comilla REAL que forma
                 * parte del contenido.
                 *
                 *
                 * Ejemplo CSV:
                 *
                 *      "Pérez, ""Juan"""
                 *
                 *
                 * Las comillas dobles alrededor de Juan
                 * representan:
                 *
                 *      Pérez, "Juan"
                 */
                if (
                        entreComillas

                                /*
                                 * Comprobamos primero que exista
                                 * una posición siguiente.
                                 *
                                 * Esto evita intentar acceder
                                 * fuera del String.
                                 */
                                && i + 1 < linea.length()

                                /*
                                 * Comprobamos si el siguiente
                                 * carácter también es ".
                                 */
                                && linea.charAt(i + 1) == '"'
                ) {


                    /*
                     * Hemos encontrado:
                     *
                     *      ""
                     *
                     * dentro de un campo.
                     *
                     * Eso representa UNA comilla real.
                     *
                     * Añadimos:
                     *
                     *      "
                     *
                     * al contenido actual.
                     */
                    actual.append('"');


                    /*
                     * MUY IMPORTANTE:
                     *
                     * Hemos consumido DOS caracteres:
                     *
                     *      ""
                     *
                     * pero queremos interpretarlos como uno:
                     *
                     *      "
                     *
                     *
                     * Por eso avanzamos manualmente i.
                     *
                     * Así evitamos procesar la segunda
                     * comilla otra vez.
                     */
                    i++;


                } else {


                    /*
                     * Si no estamos ante "", entonces esta
                     * comilla abre o cierra un campo.
                     *
                     *
                     * Utilizamos:
                     *
                     *      !entreComillas
                     *
                     * para invertir el boolean.
                     *
                     *
                     * false -> true
                     *
                     * significa:
                     *
                     *      acabamos de ENTRAR en comillas.
                     *
                     *
                     * true -> false
                     *
                     * significa:
                     *
                     *      acabamos de SALIR de comillas.
                     */
                    entreComillas = !entreComillas;
                }



                /*
                 * ====================================================
                 * CASO 2: ENCONTRAMOS UNA COMA
                 * ====================================================
                 *
                 * Una coma solamente funciona como separador
                 * cuando estamos FUERA de las comillas.
                 */
            } else if (
                    c == ',' && !entreComillas
            ) {


                /*
                 * Hemos terminado un campo.
                 *
                 * Todo lo que hemos acumulado en:
                 *
                 *      actual
                 *
                 * pertenece a ese campo.
                 *
                 * Lo convertimos a String y lo añadimos.
                 */
                campos.add(actual.toString());


                /*
                 * Ahora necesitamos empezar a construir
                 * el siguiente campo.
                 *
                 * Podríamos crear otro StringBuilder,
                 * pero reutilizamos el mismo.
                 *
                 * setLength(0)
                 *
                 * lo vacía.
                 *
                 *
                 * Antes:
                 *
                 *      actual = "Pérez, Juan"
                 *
                 * Después:
                 *
                 *      actual = ""
                 */
                actual.setLength(0);


            } else {


                /*
                 * =================================================
                 * CASO 3: CARÁCTER NORMAL
                 * =================================================
                 *
                 * Si no es una comilla especial ni una coma
                 * separadora, forma parte del contenido.
                 *
                 * Lo añadimos al campo actual.
                 */
                actual.append(c);
            }
        }


        /*
         * ========================================================
         * ¿POR QUÉ HAY QUE AÑADIR UN CAMPO AL FINAL?
         * ========================================================
         *
         * Nosotros añadimos un campo cuando encontramos ",".
         *
         * Pero el último campo NO termina con coma.
         *
         *
         * Ejemplo:
         *
         *      1,Ana,Madrid
         *
         *
         * Encontramos:
         *
         *      1,
         *
         * añadimos "1".
         *
         * Después:
         *
         *      Ana,
         *
         * añadimos "Ana".
         *
         * Finalmente leemos:
         *
         *      Madrid
         *
         * pero no aparece otra coma.
         *
         * Por eso, al terminar el for, tenemos que añadir
         * manualmente el contenido que queda.
         */
        campos.add(actual.toString());


        /*
         * Devolvemos todos los campos encontrados.
         */
        return campos;
    }



    /**
     * ============================================================
     * EXPORTAR CLIENTES A CSV
     * ============================================================
     *
     * Ahora utilizamos csv() para preparar correctamente
     * cada uno de los campos.
     */
    public static void exportarClientesCsv(
            Path ruta,
            List<Cliente> clientes
    ) throws IOException {


        /*
         * Abrimos el fichero para escritura.
         */
        try (BufferedWriter bw =
                     Files.newBufferedWriter(
                             ruta,
                             StandardCharsets.UTF_8
                     )) {


            /*
             * ====================================================
             * CABECERA
             * ====================================================
             *
             * A diferencia del TXT anterior, nuestro CSV tendrá
             * una primera línea indicando el significado
             * de cada columna.
             *
             *      id,nombre,email,telefono
             *
             * Esto hace que el fichero sea más descriptivo.
             */
            bw.write("id,nombre,email,telefono");

            bw.newLine();


            /*
             * Recorremos todos los clientes.
             */
            for (Cliente c : clientes) {


                /*
                 * Construimos el registro CSV.
                 *
                 * Fíjate en una diferencia importante.
                 *
                 * Para id:
                 *
                 *      c.getId()
                 *
                 * no utilizamos csv() porque es un número.
                 *
                 *
                 * Para los String sí utilizamos:
                 *
                 *      csv(...)
                 *
                 * porque podrían contener:
                 *
                 *      ,
                 *      "
                 *      salto de línea
                 */
                bw.write(
                        c.getId()
                                + ","
                                + csv(c.getNombre())
                                + ","
                                + csv(c.getEmail())
                                + ","
                                + csv(c.getTelefono())
                );


                /*
                 * Un Cliente = un registro CSV.
                 */
                bw.newLine();
            }
        }
    }



    /**
     * ============================================================
     * IMPORTAR CLIENTES DESDE CSV
     * ============================================================
     *
     * Realiza el proceso contrario:
     *
     *      fichero CSV
     *          ↓
     *      parseCsv()
     *          ↓
     *      List<String>
     *          ↓
     *      Cliente
     */
    public static List<Cliente> importarClientesCsv(
            Path ruta
    ) throws IOException {


        /*
         * Lista donde guardaremos los clientes
         * reconstruidos.
         */
        List<Cliente> resultado = new ArrayList<>();


        /*
         * Abrimos el fichero para lectura.
         */
        try (BufferedReader br =
                     Files.newBufferedReader(
                             ruta,
                             StandardCharsets.UTF_8
                     )) {


            /*
             * ====================================================
             * LEEMOS LA CABECERA
             * ====================================================
             *
             * Nuestro fichero empieza con:
             *
             *      id,nombre,email,telefono
             *
             * No queremos convertir esa línea en Cliente.
             *
             * Por eso hacemos una primera lectura.
             */
            String linea = br.readLine();


            /*
             * IMPORTANTE:
             *
             * El contenido leído anteriormente no se utiliza.
             *
             * El objetivo simplemente es avanzar el lector
             * una línea.
             *
             *
             * Después de:
             *
             *      br.readLine()
             *
             * el BufferedReader queda preparado para leer
             * el primer Cliente.
             */


            /*
             * Recorremos el resto de líneas.
             */
            while ((linea = br.readLine()) != null) {


                /*
                 * NO hacemos:
                 *
                 *      linea.split(",")
                 *
                 * porque ya sabemos que eso fallaría con:
                 *
                 *      "Pérez, Juan"
                 *
                 *
                 * Utilizamos nuestro parser.
                 */
                List<String> c =
                        parseCsv(linea);


                /*
                 * Un Cliente necesita cuatro campos:
                 *
                 *      id
                 *      nombre
                 *      email
                 *      telefono
                 */
                if (c.size() != 4) {

                    /*
                     * Si el registro no tiene la estructura
                     * esperada, lo ignoramos.
                     */
                    continue;
                }


                try {


                    /*
                     * c.get(0) contiene un String.
                     *
                     * Tenemos que convertirlo a int.
                     */
                    int id =
                            Integer.parseInt(c.get(0));


                    /*
                     * Los demás campos ya son String.
                     */
                    String nombre =
                            c.get(1);

                    String email =
                            c.get(2);

                    String telefono =
                            c.get(3);


                    /*
                     * Reconstruimos el Cliente.
                     */
                    Cliente cliente =
                            new Cliente(
                                    id,
                                    nombre,
                                    email,
                                    telefono
                            );


                    /*
                     * Añadimos el cliente.
                     */
                    resultado.add(cliente);


                    /*
                     * También podríamos haberlo hecho directamente,
                     * como aparece en vuestro código original:
                     *
                     * resultado.add(
                     *     new Cliente(
                     *         Integer.parseInt(c.get(0)),
                     *         c.get(1),
                     *         c.get(2),
                     *         c.get(3)
                     *     )
                     * );
                     *
                     * Separarlo en variables ocupa más código,
                     * pero inicialmente es más fácil de explicar.
                     */


                } catch (NumberFormatException e) {


                    /*
                     * Si el id no puede convertirse en int:
                     *
                     *      ABC,Ana,email,telefono
                     *
                     * descartamos el registro e informamos
                     * del problema.
                     */
                    System.err.println(
                            "Cliente erróneo: " + linea
                    );
                }
            }
        }


        /*
         * Devolvemos todos los clientes importados.
         */
        return resultado;
    }


}