# Proyecto-1

El sistema BoardGameCafe fue desarrollado para que su ejecución inicia desde la clase Main, la cual se encarga de crear la instancia principal del sistema, cargar la información persistida desde archivos y reconstruir el estado operativo antes de mostrar el menú principal al usuario. Si no existen datos previos, el sistema inicializa automáticamente los datos base para permitir su uso inmediato.

Al iniciar la aplicación, se presenta un menú principal con tres opciones: iniciar sesión, crear cuenta básica y salir.

| Usuario (Login) | Password    | Rol           | ID   | Nivel de Acceso                        |
| --------------- | ----------- | ------------- | ---- | -------------------------------------- |
| admin           | admin123    | Administrador | A-01 | Control total, reportes e inventario   |
| cliente1        | cliente123  | Cliente       | C-01 | Préstamos, compras y sistema de puntos |
| mesero1         | mesero123   | Mesero        | M-01 | Ventas, turnos y descuentos (Empleado) |
| cocinero1       | cocinero123 | Cocinero      | K-01 | Ventas, turnos y descuentos (Empleado) |


A partir de este punto, el usuario puede autenticarse con sus credenciales o registrar una cuenta básica nueva. Una vez autenticado, el sistema redirige automáticamente al menú correspondiente según el tipo de usuario: cliente, empleado, administrador o usuario básico.

Para ejecutar correctamente el proyecto, se recomienda contar con un entorno Java configurado y compilar todos los archivos fuente del sistema. Adicionalmente, debe ejecutarse la clase consola.Main, debido a que esta actúa como punto de entrada de toda la aplicación. Durante la ejecución, el programa muestra en consola la ruta de la carpeta de persistencia, lo cual permite verificar que los datos del sistema se están cargando desde una ubicación externa al código fuente, tal como lo exigen las restricciones del proyecto.

Una vez dentro del sistema, cada rol dispone de un conjunto diferente de funcionalidades. Los clientes pueden consultar el catálogo, reservar y solicitar préstamos, comprar juegos o productos del café y gestionar sus puntos. Los empleados pueden realizar compras con descuento, pedir préstamos cuando la operación lo permite y solicitar cambios de turno. El administrador, por su parte, puede consultar ventas, revisar el historial de préstamos, mover juegos entre inventarios, marcar juegos como desaparecidos, aprobar solicitudes de turno y generar reportes financieros.

Finalmente, al seleccionar la opción de salida, el sistema guarda nuevamente toda la información relevante, incluyendo usuarios, juegos, ventas, copias en inventario, historial de préstamos, solicitudes de turno y sugerencias del menú. De esta manera, el estado de la aplicación se conserva entre ejecuciones y puede retomarse posteriormente sin pérdida de información
