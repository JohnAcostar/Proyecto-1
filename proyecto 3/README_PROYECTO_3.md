# Proyecto 3 - Dulces & Dados

## Resumen

Esta carpeta es una copia evolucionada de `proyecto 2`. La aplicacion conserva la logica de dominio existente y agrega una interfaz grafica en Swing, con entrada principal en:

```text
src/gui/MainSwing.java
```

La consola original se conserva en `src/consola/Main.java` para no romper funcionalidades anteriores.

## Requerimientos cubiertos

- Interfaz grafica implementada con Swing.
- Login y creacion de usuario basico.
- Consulta de catalogo, inventario de prestamo y venta.
- Prestamos, reservas para clientes, devoluciones, compras de juegos y cafeteria.
- Historial de compras y prestamos por usuario.
- Vista administrativa de ventas, solicitudes de turno e inventario.
- Vista de torneos existentes.
- Graficas dinamicas generadas con Java2D:
  - Pastel: copias para venta vs. copias para prestamo por juego.
  - Barras: ventas netas de cafeteria vs. juegos durante los ultimos 5 dias.
  - Lineas: reservas/prestamos registrados durante la semana actual.
- Documentos de la aplicacion guardados en archivos portables:
  - `data/documentos_app/reportes.txt`
  - `data/documentos_app/torneos.txt`

## Diseno de interfaz

La ventana principal usa una estructura por pestanas:

```text
Dulces & Dados
├── Catalogo
│   ├── Tabla de juegos
│   └── Acciones administrativas de inventario
├── Operaciones
│   ├── Prestamos y reservas
│   ├── Compras
│   ├── Solicitudes de turno
│   └── Historial del usuario
├── Reportes
│   └── Tabla de ventas con subtotal, impuesto y total
├── Graficas
│   ├── Diagrama de pastel por juego
│   ├── Diagrama de barras por periodo de 5 dias
│   └── Diagrama de lineas semanal
└── Torneos
    └── Tabla de torneos registrados
```

## Decisiones de diseno

- Se reutiliza `SistemaCafe` como fachada principal para no duplicar reglas de negocio.
- Se conserva `FilePersistence` para cargar y guardar archivos en `data`.
- Los documentos visibles de reportes y torneos se exportan como texto en `data/documentos_app`, usando rutas relativas al proyecto para que funcionen igual en Windows y macOS.
- Se evita JavaFX y librerias externas. Las graficas se dibujan con Java2D dentro de paneles Swing:
  - `PieChartPanel`
  - `BarChartPanel`
  - `LineChartPanel`
- El calculo de ventas de la grafica de barras usa valores netos de impuestos:

```text
venta neta = total - impuesto
```

- La grafica semanal de reservas usa el historial de prestamos como evidencia de reservas confirmadas, porque el Proyecto 2 no persistia objetos `Reserva` independientes.

## Diagrama de clases de alto nivel

```text
MainSwing
  └── Proyecto3Frame
        ├── PieChartPanel
        ├── BarChartPanel
        ├── LineChartPanel
        ├── SistemaCafe
        ├── ServicioTorneos
        └── FilePersistence

SistemaCafe
  ├── Usuario
  │   ├── Cliente
  │   ├── Administrador
  │   └── Empleado
  │       ├── Mesero
  │       └── Cocinero
  ├── JuegoDeMesa
  ├── CopiaJuego
  ├── Prestamo
  ├── Venta
  │   ├── VentaCafe
  │   └── VentaJuegos
  └── SolicitudCambioTurno
```

## Diagrama de alto nivel de interfaz

```text
Proyecto3Frame
  ├── LoginPanel
  ├── CatalogPanel
  ├── OperationsPanel
  ├── ReportsPanel
  ├── ChartsPanel
  └── TournamentsPanel

ChartsPanel
  ├── JComboBox<JuegoDeMesa>
  ├── PieChartPanel
  ├── BarChartPanel
  └── LineChartPanel
```

## Coherencia interfaz-dominio

La GUI no modifica archivos directamente. Todas las operaciones pasan por servicios o modelos existentes:

- Autenticacion: `SistemaCafe.autenticar`.
- Usuarios basicos: `SistemaCafe.crearUsuarioBasico`.
- Prestamos: `SistemaCafe.reservarYPrestarJuego`, `prestarJuegoAUsuarioBasico`, `prestarJuegoAEmpleado`.
- Compras: `comprarJuegoPorMenu`, `comprarCafePorMenu`, canjes con puntos.
- Inventario: `moverJuegoDeVentaAPrestamo`, `marcarJuegoDesaparecido`.
- Reportes y graficas: `getVentas`, `getHistorialPrestamos`, `disponibilidadPrestamo`, `disponibilidadVenta`.

## Anexo de uso de IAG

Uso documentado:

- Interpretacion del enunciado del Proyecto 3.
- Replicacion de la estructura del Proyecto 2 en una nueva carpeta.
- Generacion inicial de plantillas Swing y paneles Java2D.
- Depuracion estatica de integracion entre la interfaz y el modelo existente.

Prompts usados:

```text
i want you to read the md to understand what is needed for the "proyecto 3" so i want you to create a copy of the proyecto 2 and call it 'proyecto 3' and there you must do what the md asks for i you need more information don't doubt on asking me first
```

Como se usaron las respuestas:

- Las respuestas se usaron como apoyo para crear estructura de interfaz, documentacion y conexiones repetitivas con el sistema existente.
- Las decisiones criticas del dominio se conservaron desde el Proyecto 2 y no se reemplazaron por reglas nuevas generadas desde cero.
