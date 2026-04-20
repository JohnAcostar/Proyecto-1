# Cambios Implementados - Sistema de Compra con Menú

## Resumen
Se rediseñó el flujo de compra de juegos y cafés para utilizar catálogos con precios predeterminados en lugar de ingresar un monto arbitrario.

---

## Archivos Modificados

### 1. **modelo/ProductoCafe.java** (NUEVO)
- Clase que representa un producto de café con precio base
- Atributos: `id`, `nombre`, `precioBase`
- Getters para acceso a datos

### 2. **modelo/JuegoDeMesa.java**
- Agregado atributo: `precioVenta` (double)
- Agregados métodos: `getPrecioVenta()`, `setPrecioVenta(double)`
- Cada juego en el catálogo tiene un precio fijo

### 3. **service/SistemaCafe.java**
- Agregado atributo: `cafesCatalogo` (List<ProductoCafe>)
- Agregado import: `modelo.ProductoCafe`
- Modificados métodos:
  - `SistemaCafe()`: Inicializa lista de cafés
  - `inicializarDatosBaseSiVacio()`: Crea catálogos con precios

**Nuevos métodos:**
```java
public List<ProductoCafe> getCafesCatalogo()
public ProductoCafe buscarCafePorId(String id)
public VentaJuegos comprarJuegoPorMenu(Usuario, String idJuego, int cantidad)
public VentaCafe comprarCafePorMenu(Usuario, String idCafe, int cantidad, double propinaPorcentaje)
```

### 4. **consola/Main.java**
- Agregado import: `modelo.ProductoCafe`
- Modificados métodos:

#### flujoCompraJuego()
**Antes:**
```
Base de compra de juegos: [usuario ingresa número]
```

**Ahora:**
```
=== Catalogo de Juegos ===
J-01 | Uno             | $35000
J-02 | Catan           | $50000
J-03 | Twister         | $25000

Seleccione juego (ID): [usuario elige]
Cantidad: [usuario ingresa cantidad]
```

#### flujoCompraCafe()
**Antes:**
```
Base compra cafe: [usuario ingresa número]
Porcentaje propina (ejemplo 0.10): [usuario ingresa propina]
```

**Ahora:**
```
=== Catalogo de Cafes ===
1   | Cafe Americano       | $8000
2   | Cafe con Leche       | $9000
3   | Espresso             | $7000
4   | Cappuccino           | $10000
5   | Latte                | $11000

Seleccione cafe (ID): [usuario elige]
Cantidad: [usuario ingresa cantidad]
Porcentaje propina (ejemplo 0.10): [usuario ingresa propina]
```

---

## Catálogos Predefini​dos

### Juegos (con precios)
| ID | Nombre  | Precio |
|----|---------|--------|
| J-01 | Uno | $35,000 |
| J-02 | Catan | $50,000 |
| J-03 | Twister | $25,000 |

### Cafés (con precios)
| ID | Nombre | Precio |
|----|--------|--------|
| 1 | Cafe Americano | $8,000 |
| 2 | Cafe con Leche | $9,000 |
| 3 | Espresso | $7,000 |
| 4 | Cappuccino | $10,000 |
| 5 | Latte | $11,000 |

---

## Flujo de Cálculo

### Compra de Juego
```
1. Usuario ve catálogo con precios
2. Elige juego (ID: J-02 "Catan" $50,000)
3. Ingresa cantidad: 2
4. Sistema calcula: base = $50,000 × 2 = $100,000
5. Se aplica descuento si es empleado: $100,000 × (1 - 0.20) = $80,000
6. Se suma IVA 19%: $80,000 × 1.19 = $95,200
7. Total con IVA: $95,200
```

### Compra de Café
```
1. Usuario ve catálogo con precios
2. Elige café (ID: "2" "Cafe con Leche" $9,000)
3. Ingresa cantidad: 3
4. Ingresa propina: 0.10 (10%)
5. Sistema calcula: base = $9,000 × 3 = $27,000
6. Se aplica descuento si es empleado: $27,000 × (1 - 0.20) = $21,600
7. Se suma impuesto consumo 8%: $21,600 × 1.08 = $23,328
8. Se suma propina: $23,328 + ($27,000 × 0.10) = $26,028
9. Total: $26,028
```

---

## Validaciones
- Si juego/café no existe: "Juego/Cafe no encontrado..."
- Si usuario no es cliente/empleado para compra: "Operacion no permitida..."
- Sistema sigue aplicando:
  - Descuento 20% para empleados
  - IVA 19% en juegos
  - Impuesto consumo 8% + propina en cafés
  - Puntos de fidelidad (1% del total)

---

## Compatibilidad
✅ Métodos antiguos de compra se mantienen (comprarJuego, comprarCafe) para backwards compatibility
✅ No se eliminó código existente, solo se agregó funcionalidad nueva
✅ Los nuevos métodos son el flujo recomendado desde la consola

