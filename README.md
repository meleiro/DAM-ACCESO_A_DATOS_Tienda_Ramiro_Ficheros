# TiendaFicheros — TXT, CSV, JSON y XML

Proyecto didáctico Java 21 + Swing + Maven para Acceso a Datos.

## Incluye
- CRUD de clientes y productos.
- Interfaz Swing con `JTable`.
- Selector de formato al importar y exportar.
- TXT manual con `BufferedReader` / `BufferedWriter`.
- CSV con cabecera y tratamiento básico de comillas.
- JSON mediante Jackson Databind.
- XML mediante Jackson Dataformat XML (`XmlMapper`).
- Ficheros de ejemplo en `datos/`.

## Abrir en IntelliJ IDEA
1. Descomprime el ZIP.
2. File > Open y selecciona esta carpeta o `pom.xml`.
3. Usa JDK 21.
4. Espera a que Maven descargue las dependencias Jackson.
5. Ejecuta `app.Main`.

## Dependencias añadidas
- `jackson-databind`
- `jackson-dataformat-xml`

La práctica permite comparar serialización manual (TXT/CSV) con serialización estructurada mediante librería (JSON/XML).
