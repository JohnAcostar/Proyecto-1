# REVISIÓN EXHAUSTIVA DEL PROYECTO BOARDGAMECAFE

## ESTRUCTURA DEL CÓDIGO ANALIZADA

### Paquetes Identificados:
1. **modelo/** - Clases de dominio (33 clases)
2. **service/** - Lógica de negocio (SistemaCafe)
3. **consola/** - Interfaz de usuario (Main)
4. **persistence/** - Persistencia de datos (FilePersistence, AppData)

### Clases de Modelo (33 total):

#### Jerarquía de Usuarios (6):
- Usuario (abstract)
  - Administrador
  - Cliente  
  - Empleado (abstract)
    - Mesero
    - Cocinero

#### Juegos y Categorías (4):
- JuegoDeMesa
- CopiaJuego
- CategoriaJuego (enum: CARTAS, TABLERO, ACCION)
- EstadoJuego (enum: NUEVO, BUENO, FALTA_PIEZA, DESAPARECIDO, EN_REPARACION)
- RestriccionEdad (enum: ADULTOS_SOLO, TODAS_LAS_EDADES, MAYORES_DE_5)

#### Reservas y Préstamos (4):
- Reserva
- Prestamo
- EstadoReserva (enum: PENDIENTE, ACEPTADA, RECHAZADA, FINALIZADA)
- InventarioPrestamos

#### Ventas (5):
- Venta (abstract)
  - VentaJuegos
  - VentaCafe
- InventarioVenta
- RubroVenta (enum: JUEGO, CAFE)
- ReporteVentas

#### Café y Menú (4):
- Cafe
- Mesa
- ProductoMenu (abstract)
  - Bebida (TipoBebida enum: FRIA, CALIENTE, ALCOHOLICA)
  - Pasteleria

#### Empleados y Turnos (3):
- Turno
- Horario
- TipoSolicitudTurno (enum: CAMBIO, INTERCAMBIO)
- SolicitudCambioTurno
- EstadoSolicitudTurno (enum: PENDIENTE, APROBADA, RECHAZADA)

#### Gestión de Menú (1):
- Sugerencia

#### Persistencia (2):
- AppData
- FilePersistence

### Funcionalidades Implementadas por SistemaCafe:

#### ✓ IMPLEMENTADAS:
1. Gestión de Usuarios (login, autenticación, favoritos)
2. Catálogo de Juegos (consulta, búsqueda por ID)
3. Reservas y Préstamos de Juegos (validación de elegibilidad)
4. Restricciones por Edad (ADULTOS_SOLO, MAYORES_DE_5, TODAS_LAS_EDADES)
5. Restricciones por Cantidad de Jugadores
6. Gestión de Inventario (Préstamos y Ventas)
7. Compra de Juegos (con descuento para empleados)
8. Compra de Café (con propina, IVA, descuento empleado)
9. Sistema de Puntos de Fidelidad (Clientes)
10. Advertencias por Juegos Difíciles (si no hay mesero capacitado)
11. Restricción de Bebidas Calientes en Mesas con Juegos de Acción
12. Préstamo de Juegos a Empleados (con validación)
13. Devolución de Préstamos
14. Movimiento de Juegos entre Inventarios
15. Reparación de Juegos
16. Marcado de Juegos Desaparecidos
17. Sugerencias de Menú (registro y aprobación)
18. Solicitudes de Cambio de Turno (registro y aprobación)
19. Validación de Personal Mínimo (1 cocinero, 2 meseros)
20. Reportes de Ventas (por rubro, resumen financiero)
21. Descuentos para Empleados (20% en juegos, 20% en café)

### Enumerados (9):
1. CategoriaJuego
2. EstadoJuego
3. RestriccionEdad
4. TipoBebida
5. RubroVenta
6. EstadoReserva
7. TipoSolicitudTurno
8. EstadoSolicitudTurno
9. TipoSolicitudTurno

---

## HALLAZGOS PRELIMINARES

### LO QUE SÍ SE IMPLEMENTÓ CORRECTAMENTE:

1. ✓ Estructura de usuario con jerarquía completa
2. ✓ Sistema de autenticación
3. ✓ Catálogo de juegos con propiedades completas
4. ✓ Validaciones de elegibilidad complejas (edad, cantidad, restricciones)
5. ✓ Gestión de inventario separada (préstamos vs. ventas)
6. ✓ Sistema de puntos de fidelidad para clientes
7. ✓ Cálculo de impuestos y propinas diferenciados
8. ✓ Manejo de estados para juegos, reservas y préstamos
9. ✓ Restricciones de negocio complejas (bebidas calientes + juegos acción)
10. ✓ Capacitación de meseros por juego específico
11. ✓ Descuentos diferenciados por tipo de usuario

### POSIBLES PROBLEMAS O INCOMPLETOS:

Pendiente: Lectura de los PDFs específicos del proyecto para validar:
- Si se pidió Mesa en la entrega 2
- Si se pidió Cafe como clase de dominio
- Si se pidieron Bebida y Pasteleria
- Si se pidieron ProductoMenu, Turno, Horario
- Si se pidieron Sugerencia y SolicitudCambioTurno
- Si ReporteVentas debe ser solo simple o debe incluir reportes diarios/semanales/mensuales
- Qué métodos específicos se pidieron en cada clase

### CLASES QUE PODRÍAN ESTAR DE MÁS:
- Cafe (parece que no se usa en SistemaCafe)
- Mesa (existe pero podría no ser requerida)
- Turno, Horario (existe validación básica pero puede no ser la que se pide)
- ProductoMenu, Bebida, Pasteleria (sistema de menú completo)

---

## PRÓXIMOS PASOS:

Para completar este análisis necesito:
1. Leer "Proyecto #1 - Entrega 2 (Diseño e Implementación).pdf" - requisitos específicos
2. Leer "Proyecto 1_DulcesnDados.pdf" - especificación general
3. Leer "Clase UML.pdf" - validar estructura vs. diagrama esperado

