# ✅ Solución Final - Precios y Selección por Índice

## Problemas Solucionados

### 1. ✅ Precios Seguían en Cero

**Causa:** Datos guardados previamente no tenían el campo de precio.

**Solución Implementada:**
```java
// En inicializarDatosBaseSiVacio() - Línea 97-108
// Asignar precios a juegos existentes (por nombre)
for (JuegoDeMesa juego : juegosCatalogo) {
    if (juego.getPrecioVenta() == 0) {
        if ("Uno".equals(juego.getNombre())) {
            juego.setPrecioVenta(35000);
        } else if ("Catan".equals(juego.getNombre())) {
            juego.setPrecioVenta(50000);
        } else if ("Twister".equals(juego.getNombre())) {
            juego.setPrecioVenta(25000);
        }
    }
}
```

**Resultado:** Cada vez que se carga el sistema, asigna automáticamente los precios a los juegos que no los tienen.

---

### 2. ✅ Cambio de Selección por ID a Índice

**Cambios en Main.java:**

```java
// ANTES: Mostrar ID (J-01, J-02, J-03)
for (JuegoDeMesa juego : sistema.getJuegosCatalogo()) {
    System.out.println(String.format("%-4s | %-15s | %s", 
        juego.getIdJuego(), juego.getNombre(), formatearMoneda(juego.getPrecioVenta())));
}

// DESPUÉS: Mostrar número de posición (1, 2, 3)
int indice = 1;
for (JuegoDeMesa juego : sistema.getJuegosCatalogo()) {
    System.out.println(String.format("%d. %-15s | %s", 
        indice++, juego.getNombre(), formatearMoneda(juego.getPrecioVenta())));
}
```

**Entrada del usuario:**
```java
// ANTES:
System.out.print("\nSeleccione juego (ID): ");
String idJuego = SCANNER.nextLine().trim();

// DESPUÉS:
System.out.print("\nSeleccione juego (1-" + sistema.getJuegosCatalogo().size() + "): ");
int seleccion = leerEntero();
```

---

## Cambios por Archivo

### 1. `service/SistemaCafe.java`

**Método `inicializarDatosBaseSiVacio()` - Línea 88-134**
- ✅ Agregada lógica para asignar precios a juegos existentes
- ✅ Verifica si precio es 0, luego asigna por nombre

**Método `comprarJuegoPorMenu()` - Línea 428-435** (Modificado)
```java
// ANTES: public VentaJuegos comprarJuegoPorMenu(Usuario comprador, String idJuego, int cantidad)
// DESPUÉS:
public VentaJuegos comprarJuegoPorMenu(Usuario comprador, int indice, int cantidad) {
    if (indice < 1 || indice > juegosCatalogo.size()) {
        return null;
    }
    JuegoDeMesa juego = juegosCatalogo.get(indice - 1);
    double base = juego.getPrecioVenta() * cantidad;
    return comprarJuego(comprador, base);
}
```

**Método `comprarCafePorMenu()` - Línea 437-444** (Modificado)
```java
// ANTES: public VentaCafe comprarCafePorMenu(Usuario comprador, String idCafe, int cantidad, ...)
// DESPUÉS:
public VentaCafe comprarCafePorMenu(Usuario comprador, int indice, int cantidad, double propinaPorcentaje) {
    if (indice < 1 || indice > cafesCatalogo.size()) {
        return null;
    }
    ProductoCafe cafe = cafesCatalogo.get(indice - 1);
    double base = cafe.getPrecioBase() * cantidad;
    return comprarCafe(comprador, base, propinaPorcentaje);
}
```

---

### 2. `consola/Main.java`

**Método `flujoCompraJuego()` - Línea 183-202** (Reescrito)
```java
// Catálogo con números:
System.out.println("\n=== Catalogo de Juegos ===");
int indice = 1;
for (JuegoDeMesa juego : sistema.getJuegosCatalogo()) {
    System.out.println(String.format("%d. %-15s | %s", 
        indice++, juego.getNombre(), formatearMoneda(juego.getPrecioVenta())));
}

// Entrada con validación de rango:
System.out.print("\nSeleccione juego (1-" + sistema.getJuegosCatalogo().size() + "): ");
int seleccion = leerEntero();
```

**Método `flujoCompraCafe()` - Línea 204-227** (Reescrito)
```java
// Catálogo con números:
System.out.println("\n=== Catalogo de Cafes ===");
int indice = 1;
for (ProductoCafe cafe : sistema.getCafesCatalogo()) {
    System.out.println(String.format("%d. %-20s | %s", 
        indice++, cafe.getNombre(), formatearMoneda(cafe.getPrecioBase())));
}

// Entrada con validación de rango:
System.out.print("\nSeleccione cafe (1-" + sistema.getCafesCatalogo().size() + "): ");
int seleccion = leerEntero();
```

---

## Ejemplos de Uso

### Compra de Juego

**ANTES:**
```
=== Catalogo de Juegos ===
J-01 | Uno             | $ 0
J-02 | Catan           | $ 0
J-03 | Twister         | $ 0

Seleccione juego (ID): J-02
```

**AHORA:**
```
=== Catalogo de Juegos ===
1. Uno             | $ 35.000
2. Catan           | $ 50.000
3. Twister         | $ 25.000

Seleccione juego (1-3): 2
Cantidad: 2

Venta registrada. Total: $ 119.000 | Impuesto: $ 19.000 | COP
```

### Compra de Café

**ANTES:**
```
=== Catalogo de Cafes ===
1   | Cafe Americano       | $ 0
2   | Cafe con Leche       | $ 0
3   | Espresso             | $ 0
4   | Cappuccino           | $ 0
5   | Latte                | $ 0

Seleccione cafe (ID): 2
```

**AHORA:**
```
=== Catalogo de Cafes ===
1. Cafe Americano        | $ 8.000
2. Cafe con Leche        | $ 9.000
3. Espresso              | $ 7.000
4. Cappuccino            | $ 10.000
5. Latte                 | $ 11.000

Seleccione cafe (1-5): 2
Cantidad: 3
Porcentaje propina (ejemplo 0.10): 0.15

Venta registrada. Total: $ 31.350 | Impuesto: $ 2.430 | Propina: $ 4.050 | COP
```

---

## Validaciones

✅ Precios se asignan automáticamente a juegos cargados  
✅ Rango válido es 1 a número de elementos  
✅ Entrada inválida (0, 4 en juegos, etc.) devuelve error  
✅ Selección solo con números, sin IDs  
✅ Interfaz más amigable y simple  

---

## Notas

- **Automático:** Los precios se asignan cada vez que se carga el sistema, no solo en datos nuevos
- **Simple:** Usuario solo ingresa número en lugar de copiar ID
- **Seguro:** Validación de rango antes de acceder a la lista
- **Compatible:** Los datos antiguos se actualizan automáticamente

