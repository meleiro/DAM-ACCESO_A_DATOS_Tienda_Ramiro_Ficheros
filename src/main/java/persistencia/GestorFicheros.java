package persistencia;


/*
 * ================================================================
 * IMPORTACIONES
 * ================================================================
 *
 * Una importación permite utilizar en esta clase otras clases
 * que se encuentran en paquetes diferentes.
 *
 * Podemos distinguir dos grupos:
 *
 * 1. Clases externas: Jackson.
 * 2. Clases propias del proyecto: Cliente y Producto.
 * 3. Clases incluidas en Java: java.io, java.nio, java.util...
 */


/*
 * Estas cuatro importaciones pertenecen a JACKSON.
 *
 * En esta primera versión, donde solamente estamos trabajando
 * con TXT, TODAVÍA NO LAS NECESITAMOS.
 *
 * Las utilizaremos posteriormente cuando incorporemos
 * JSON y XML.
 *
 * Esto puede ser interesante para explicar a los alumnos
 * que una importación no hace nada por sí misma:
 * simplemente permite utilizar una clase en nuestro código.
 */

// Se utilizará posteriormente para indicar tipos genéricos
// a Jackson, por ejemplo List<Cliente>.
import com.fasterxml.jackson.core.type.TypeReference;

// Clase principal de Jackson para trabajar con JSON.
import com.fasterxml.jackson.databind.ObjectMapper;

// Permite configurar diferentes opciones de serialización,
// por ejemplo generar JSON/XML indentado.
import com.fasterxml.jackson.databind.SerializationFeature;

// Variante de ObjectMapper especializada en XML.
import com.fasterxml.jackson.dataformat.xml.XmlMapper;


/*
 * ================================================================
 * CLASES DE NUESTRO PROYECTO
 * ================================================================
 */

// Representa la entidad Cliente.
import modelo.Cliente;

// Representa la entidad Producto.
// En este código todavía no se utiliza.
import modelo.Producto;


/*
 * ================================================================
 * CLASES DEL JDK
 * ================================================================
 */

/*
 * java.io contiene muchas clases relacionadas con
 * entrada y salida de datos.
 *
 * El * significa:
 *
 *      importa las clases públicas de java.io
 *
 * En nuestro código utilizaremos principalmente:
 *
 *      BufferedWriter
 *      BufferedReader
 *      IOException
 */
import java.io.*;


/*
 * StandardCharsets contiene codificaciones estándar.
 *
 * Nosotros utilizaremos UTF-8 para leer y escribir
 * nuestros ficheros de texto.
 */
import java.nio.charset.StandardCharsets;


/*
 * java.nio.file contiene la API moderna de Java
 * para trabajar con rutas y ficheros.
 *
 * De aquí utilizaremos fundamentalmente:
 *
 *      Path
 *      Files
 */
import java.nio.file.*;


/*
 * ArrayList es una implementación concreta
 * de la interfaz List.
 */
import java.util.ArrayList;


/*
 * List representa una colección ordenada de elementos.
 *
 * Por ejemplo:
 *
 *      List<Cliente>
 *
 * significa:
 *
 *      "una lista que contiene objetos Cliente"
 */
import java.util.List;


/*
 * ================================================================
 * CLASE GestorFicheros
 * ================================================================
 *
 * Esta clase va a concentrar las operaciones relacionadas
 * con la persistencia en ficheros.
 *
 * En lugar de poner la lógica de lectura/escritura dentro
 * de la ventana gráfica, la separamos.
 *
 *
 * Podríamos visualizarlo así:
 *
 *      INTERFAZ
 *          |
 *          v
 *      SERVICIO
 *          |
 *          v
 *   GestorFicheros
 *          |
 *          v
 *     clientes.txt
 *
 *
 * En esta primera versión solamente implementamos TXT.
 */
public class GestorFicheros {


    /*
     * ============================================================
     * EXPORTAR CLIENTES A TXT
     * ============================================================
     *
     * El objetivo de este método es transformar:
     *
     *      List<Cliente>
     *
     * en:
     *
     *      fichero TXT
     *
     *
     * Supongamos que tenemos:
     *
     * Cliente:
     *
     *      id       = 1
     *      nombre   = "Ana"
     *      email    = "ana@email.com"
     *      telefono = "600123456"
     *
     *
     * Nosotros hemos decidido representarlo así:
     *
     *      1;Ana;ana@email.com;600123456
     *
     *
     * Por tanto, estamos creando nuestro propio
     * formato de almacenamiento.
     *
     * El carácter ";" funciona como DELIMITADOR.
     */
    public static void exportarClientesTxt(

            /*
             * Path representa la RUTA donde queremos
             * guardar el fichero.
             *
             * IMPORTANTE:
             *
             * Path NO contiene el fichero.
             * Path representa su ubicación.
             *
             * Ejemplo:
             *
             *      Path.of("clientes.txt")
             */
            Path ruta,


            /*
             * Recibimos también la lista de clientes
             * que queremos guardar.
             */
            List<Cliente> clientes


    ) throws IOException {

        /*
         * throws IOException
         * ------------------
         *
         * Las operaciones con ficheros pueden fallar.
         *
         * Por ejemplo:
         *
         *      - no tenemos permisos de escritura;
         *      - la carpeta no existe;
         *      - el disco no está disponible;
         *      - se produce un problema de entrada/salida.
         *
         * IOException es una CHECKED EXCEPTION.
         *
         * Eso significa que Java nos obliga a:
         *
         *      1. capturarla con try-catch
         *
         * o
         *
         *      2. declararla con throws
         *
         *
         * Aquí hemos elegido:
         *
         *      throws IOException
         *
         * Por tanto, quien llame a este método tendrá
         * que decidir cómo gestionar el posible error.
         */


        /*
         * ========================================================
         * TRY-WITH-RESOURCES
         * ========================================================
         *
         * Esta construcción:
         *
         *      try (recurso) {
         *
         *      }
         *
         * se denomina try-with-resources.
         *
         * Su gran ventaja es que Java cierra automáticamente
         * el recurso cuando terminamos.
         *
         * Por tanto, NO necesitamos escribir:
         *
         *      bw.close();
         *
         * manualmente.
         *
         *
         * Esto es especialmente importante con:
         *
         *      ficheros
         *      conexiones de base de datos
         *      streams
         *      sockets
         *      etc.
         */
        try (
                /*
                 * Files.newBufferedWriter(...)
                 *
                 * abre un fichero para escritura de texto
                 * y devuelve un BufferedWriter.
                 *
                 *
                 * BufferedWriter
                 * --------------
                 *
                 * Es un flujo de escritura de texto que utiliza
                 * un BUFFER.
                 *
                 * Simplificando:
                 *
                 * Programa
                 *    |
                 *    v
                 *  BUFFER
                 *    |
                 *    v
                 * fichero
                 *
                 *
                 * El buffer permite realizar la escritura
                 * de manera eficiente.
                 */
                BufferedWriter bw =
                        Files.newBufferedWriter(

                                /*
                                 * Dónde escribir.
                                 */
                                ruta,

                                /*
                                 * Qué codificación utilizar.
                                 *
                                 * UTF-8 permite representar
                                 * correctamente caracteres como:
                                 *
                                 *      ñ
                                 *      á
                                 *      é
                                 *      €
                                 */
                                StandardCharsets.UTF_8
                        )

        ) {


            /*
             * ====================================================
             * RECORREMOS LOS CLIENTES
             * ====================================================
             *
             * Este es un for-each.
             *
             * Se puede leer:
             *
             *      "Para cada Cliente c
             *       que exista dentro de clientes..."
             */
            for (Cliente c : clientes) {


                /*
                 * =================================================
                 * ESCRIBIMOS UN CLIENTE
                 * =================================================
                 *
                 * bw.write(...) espera texto.
                 *
                 * Por tanto, tenemos que convertir nuestro
                 * objeto Cliente en una representación textual.
                 *
                 *
                 * Estamos haciendo manualmente:
                 *
                 *      Cliente
                 *         |
                 *         v
                 *      String
                 *         |
                 *         v
                 *        TXT
                 *
                 *
                 * A este proceso podemos llamarlo
                 * SERIALIZACIÓN.
                 *
                 * En este caso es una serialización manual,
                 * porque nosotros decidimos exactamente
                 * cómo transformar el objeto.
                 */
                bw.write(

                        /*
                         * Primer campo: id.
                         */
                        c.getId()

                                /*
                                 * Separador.
                                 */
                                + ";"

                                /*
                                 * Segundo campo: nombre.
                                 */
                                + c.getNombre()

                                /*
                                 * Separador.
                                 */
                                + ";"

                                /*
                                 * Tercer campo: email.
                                 */
                                + c.getEmail()

                                /*
                                 * Separador.
                                 */
                                + ";"

                                /*
                                 * Cuarto campo: teléfono.
                                 */
                                + c.getTelefono()
                );


                /*
                 * Después de escribir un Cliente,
                 * añadimos un salto de línea.
                 *
                 * Así conseguimos:
                 *
                 *      un cliente = una línea
                 *
                 *
                 * Por ejemplo:
                 *
                 * 1;Ana;ana@email.com;600111111
                 * 2;Pedro;pedro@email.com;600222222
                 * 3;Lucía;lucia@email.com;600333333
                 */
                bw.newLine();

            }

        }

        /*
         * Al salir del try, Java cierra automáticamente
         * el BufferedWriter.
         */
    }



    /*
     * ============================================================
     * IMPORTAR CLIENTES DESDE TXT
     * ============================================================
     *
     * Ahora realizamos exactamente el proceso contrario.
     *
     *
     * EXPORTAR:
     *
     *      Cliente
     *         |
     *         v
     *       texto
     *         |
     *         v
     *       fichero
     *
     *
     * IMPORTAR:
     *
     *       fichero
     *          |
     *          v
     *        texto
     *          |
     *          v
     *       Cliente
     *
     *
     * El método devuelve:
     *
     *      List<Cliente>
     *
     * porque el fichero puede contener muchos clientes.
     */
    public static List<Cliente> importarClientesTxt(

            /*
             * Ruta del fichero que queremos leer.
             */
            Path ruta

    ) throws IOException {


        /*
         * ========================================================
         * LISTA DE RESULTADOS
         * ========================================================
         *
         * Creamos una lista vacía.
         *
         * Cada vez que consigamos reconstruir correctamente
         * un Cliente desde una línea del fichero,
         * lo añadiremos a esta lista.
         *
         *
         * Aquí es muy interesante explicar:
         *
         *      List<Cliente>      → interfaz
         *
         *      ArrayList<>        → implementación
         *
         *
         * Programamos contra la interfaz:
         */
        List<Cliente> resultado =
                new ArrayList<>();


        /*
         * ========================================================
         * ABRIMOS EL FICHERO PARA LECTURA
         * ========================================================
         */
        try (
                /*
                 * BufferedReader permite leer texto
                 * utilizando un buffer.
                 *
                 * Si BufferedWriter era:
                 *
                 *      programa → fichero
                 *
                 * BufferedReader será:
                 *
                 *      fichero → programa
                 */
                BufferedReader br =
                        Files.newBufferedReader(

                                /*
                                 * Fichero que queremos leer.
                                 */
                                ruta,

                                /*
                                 * Utilizamos la misma codificación
                                 * que utilizamos al escribir.
                                 *
                                 * Si escribimos UTF-8, es lógico
                                 * leer también UTF-8.
                                 */
                                StandardCharsets.UTF_8
                        )

        ) {


            /*
             * Variable temporal.
             *
             * Aquí iremos almacenando cada línea
             * que leamos del fichero.
             */
            String linea;


            /*
             * ====================================================
             * LEEMOS LÍNEA A LÍNEA
             * ====================================================
             *
             * br.readLine()
             *
             * intenta leer una línea.
             *
             * Puede devolver:
             *
             *      "1;Ana;ana@email.com;600123456"
             *
             * o:
             *
             *      null
             *
             * cuando hemos llegado al final del fichero.
             *
             *
             * Por eso:
             *
             * while ((linea = br.readLine()) != null)
             *
             * significa:
             *
             *      "Mientras siga existiendo una línea
             *       que podamos leer..."
             */
            while ((linea = br.readLine()) != null) {


                /*
                 * =================================================
                 * SEPARAMOS LOS CAMPOS
                 * =================================================
                 *
                 * Supongamos:
                 *
                 * linea =
                 *
                 * "1;Ana;ana@email.com;600123456"
                 *
                 *
                 * split(";")
                 *
                 * divide el String utilizando ";".
                 *
                 *
                 * Resultado:
                 *
                 * p[0] = "1"
                 * p[1] = "Ana"
                 * p[2] = "ana@email.com"
                 * p[3] = "600123456"
                 *
                 *
                 * Por eso utilizamos un array:
                 *
                 *      String[] p
                 */
                String[] p =
                        linea.split(";");


                /*
                 * =================================================
                 * VALIDAMOS LA ESTRUCTURA
                 * =================================================
                 *
                 * Nuestro formato dice:
                 *
                 * Cliente =
                 *
                 *      id
                 *      nombre
                 *      email
                 *      teléfono
                 *
                 * Por tanto necesitamos exactamente:
                 *
                 *      4 campos
                 *
                 *
                 * Si tenemos:
                 *
                 *      p.length != 4
                 *
                 * significa que la línea no tiene
                 * la estructura esperada.
                 */
                if (p.length != 4) {


                    /*
                     * continue NO termina el while.
                     *
                     * Lo que hace es:
                     *
                     *      "abandona esta iteración
                     *       y pasa a la siguiente."
                     *
                     *
                     * Ejemplo:
                     *
                     * línea 1 → correcta → importar
                     *
                     * línea 2 → incorrecta → continue
                     *
                     * línea 3 → seguimos procesando
                     */
                    continue;
                }


                /*
                 * =================================================
                 * CONVERTIMOS LOS DATOS
                 * =================================================
                 *
                 * Aunque el fichero contenga:
                 *
                 *      15
                 *
                 * nosotros lo hemos leído como:
                 *
                 *      "15"
                 *
                 * Es decir:
                 *
                 *      String
                 *
                 * No como:
                 *
                 *      int
                 *
                 * Por eso necesitamos convertir determinados
                 * campos a sus tipos Java correspondientes.
                 */
                try {


                    /*
                     * p[0] contiene el id.
                     *
                     * Pero:
                     *
                     *      p[0] es String
                     *
                     * y:
                     *
                     *      Cliente.id es int
                     *
                     *
                     * Integer.parseInt realiza:
                     *
                     *      String → int
                     *
                     *
                     * "123"
                     *    |
                     *    v
                     *   123
                     */
                    int id =
                            Integer.parseInt(p[0]);


                    /*
                     * Estos campos ya tienen el tipo String,
                     * por lo que no necesitan conversión.
                     */
                    String nombre =
                            p[1];

                    String email =
                            p[2];

                    String telefono =
                            p[3];


                    /*
                     * =================================================
                     * RECONSTRUIMOS EL OBJETO
                     * =================================================
                     *
                     * Ya tenemos:
                     *
                     *      int id
                     *      String nombre
                     *      String email
                     *      String telefono
                     *
                     * Ahora podemos crear:
                     *
                     *      new Cliente(...)
                     *
                     *
                     * Estamos haciendo:
                     *
                     *      String
                     *         |
                     *         v
                     *      campos
                     *         |
                     *         v
                     *      tipos Java
                     *         |
                     *         v
                     *      Cliente
                     *
                     *
                     * Esto es DESERIALIZACIÓN manual.
                     */
                    Cliente cliente =
                            new Cliente(
                                    id,
                                    nombre,
                                    email,
                                    telefono
                            );


                    /*
                     * Añadimos el Cliente reconstruido
                     * a nuestra lista de resultados.
                     */
                    resultado.add(cliente);


                    /*
                     * =================================================
                     * NumberFormatException
                     * =================================================
                     *
                     * ¿Qué sucede si encontramos?
                     *
                     *      hola;Ana;ana@email.com;600123456
                     *
                     * Entonces intentaremos:
                     *
                     *      Integer.parseInt("hola")
                     *
                     * Pero "hola" no puede convertirse a int.
                     *
                     * Java lanzará:
                     *
                     *      NumberFormatException
                     *
                     *
                     * A diferencia de IOException,
                     * NumberFormatException es una
                     * UNCHECKED EXCEPTION.
                     *
                     * Java NO nos obliga a capturarla.
                     *
                     * Sin embargo, nosotros decidimos hacerlo porque
                     * sabemos que un fichero externo puede contener
                     * datos incorrectos.
                     */
                } catch (NumberFormatException ex) {


                    /*
                     * System.err
                     *
                     * representa la salida estándar de errores.
                     *
                     * La utilizamos para diferenciar conceptualmente
                     * un mensaje normal de un mensaje de error.
                     */
                    System.err.println(
                            "Cliente incorrecto: "
                                    + linea
                    );
                }
            }
        }


        /*
         * ========================================================
         * DEVOLVEMOS LOS RESULTADOS
         * ========================================================
         *
         * Después de leer todo el fichero tenemos:
         *
         *      resultado
         *
         * con todos los clientes que se han podido
         * reconstruir correctamente.
         *
         *
         * Por ejemplo:
         *
         * clientes.txt
         *
         *      1;Ana;ana@email.com;600111111
         *      ABC;Pedro;pedro@email.com;600222222
         *      3;Lucía;lucia@email.com;600333333
         *
         *
         * Resultado:
         *
         *      Cliente Ana
         *      Cliente Lucía
         *
         * La línea de Pedro se descarta porque:
         *
         *      Integer.parseInt("ABC")
         *
         * produce NumberFormatException.
         */
        return resultado;
    }

}