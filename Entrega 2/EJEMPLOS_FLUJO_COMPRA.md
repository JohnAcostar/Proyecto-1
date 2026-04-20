# Ejemplos de Uso - Sistema de Compra Mejorado

## Ejemplo 1: Cliente Compra 2 Juegos de Catan

```
Usuario: cliente1
Contraseña: cliente123

Menu Principal:
- Cliente: [Selecciona opción 3 - Comprar Juego]

=== Catalogo de Juegos ===
J-01 | Uno             | $35000
J-02 | Catan           | $50000
J-03 | Twister         | $25000

Seleccione juego (ID): J-02
Cantidad: 2

Venta registrada. Total: 119000.0 | Impuesto: 19000.0
```

**Cálculo:**
- Precio unitario: $50,000
- Cantidad: 2
- Base: $50,000 × 2 = $100,000
- Descuento cliente: 0% (clientes no tienen descuento)
- Subtotal: $100,000
- IVA 19%: $100,000 × 0.19 = $19,000
- **TOTAL: $119,000**

---

## Ejemplo 2: Empleado Compra 1 Juego de Catan

```
Usuario: mesero1
Contraseña: mesero123

Menu Empleado: [Selecciona opción 2 - Comprar Juego]

=== Catalogo de Juegos ===
J-01 | Uno             | $35000
J-02 | Catan           | $50000
J-03 | Twister         | $25000

Seleccione juego (ID): J-02
Cantidad: 1

Venta registrada. Total: 47600.0 | Impuesto: 8000.0
```

**Cálculo:**
- Precio unitario: $50,000
- Cantidad: 1
- Base: $50,000 × 1 = $50,000
- Descuento empleado: 20% → $50,000 × (1 - 0.20) = $40,000
- Subtotal con descuento: $40,000
- IVA 19%: $40,000 × 0.19 = $7,600
- **TOTAL: $47,600**

*Ahorro del empleado: $11,400 (19%)*

---

## Ejemplo 3: Cliente Compra Café con Propina

```
Usuario: cliente1
Contraseña: cliente123

Menu Principal:
- Cliente: [Selecciona opción 4 - Comprar Cafe]

=== Catalogo de Cafes ===
1   | Cafe Americano       | $8000
2   | Cafe con Leche       | $9000
3   | Espresso             | $7000
4   | Cappuccino           | $10000
5   | Latte                | $11000

Seleccione cafe (ID): 2
Cantidad: 3
Porcentaje propina (ejemplo 0.10): 0.15

Venta registrada. Total: 31350.0 | Impuesto: 2430.0
```

**Cálculo:**
- Precio unitario: $9,000
- Cantidad: 3
- Base: $9,000 × 3 = $27,000
- Descuento cliente: 0%
- Subtotal: $27,000
- Impuesto consumo 8%: $27,000 × 0.08 = $2,160
- Subtotal con impuesto: $29,160
- Propina 15%: $27,000 × 0.15 = $4,050
- **TOTAL: $33,210** (pero impuesto reportado es $2,430)

---

## Ejemplo 4: Empleado Compra Café

```
Usuario: mesero1
Contraseña: mesero123

Menu Empleado: [Selecciona opción 3 - Comprar Cafe]

=== Catalogo de Cafes ===
1   | Cafe Americano       | $8000
2   | Cafe con Leche       | $9000
3   | Espresso             | $7000
4   | Cappuccino           | $10000
5   | Latte                | $11000

Seleccione cafe (ID): 3
Cantidad: 2
Porcentaje propina (ejemplo 0.10): 0.10

Venta registrada. Total: 13552.0 | Impuesto: 1152.0
```

**Cálculo:**
- Precio unitario: $7,000
- Cantidad: 2
- Base: $7,000 × 2 = $14,000
- Descuento empleado: 20% → $14,000 × (1 - 0.20) = $11,200
- Subtotal con descuento: $11,200
- Impuesto consumo 8%: $11,200 × 0.08 = $896
- Subtotal con impuesto: $12,096
- Propina 10%: $14,000 × 0.10 = $1,400
- **TOTAL: $13,496** (pero impuesto reportado es $896)

*Ahorro del empleado: $2,604 (19% en la parte no-descuentada)*

---

## Notas sobre Puntos de Fidelidad

En cada compra, los clientes acumulan **puntos = 1% del total**:

- Cliente compra $119,000 en juegos → Acumula 1,190 puntos
- Cliente compra $33,210 en café → Acumula 332 puntos

---

## Errores Posibles

### Error 1: ID de juego inválido
```
Seleccione juego (ID): J-99

Juego no encontrado o no se pudo registrar la venta.
```

### Error 2: Café no existe
```
Seleccione cafe (ID): 99

Cafe no encontrado o operacion no permitida para este usuario.
```

### Error 3: Administrador intenta comprar café
```
Usuario: admin
Menu: [Intenta comprar café]

Operacion no permitida para este usuario.
```

---

## Cambios Principales vs Sistema Anterior

| Aspecto | Anterior | Ahora |
|--------|----------|-------|
| **Entrada** | "Base de compra": número arbitrario | Menú de productos con precios visibles |
| **Claridad** | Usuario no sabía los precios | Precios claramente mostrados |
| **UX** | Entrada manual propensa a errores | Selección de opciones |
| **Realismo** | Menos realista (base arbitraria) | Más realista (compra de productos específicos) |
| **Validación** | Mínima | Verifica que producto exista |
| **Cálculo** | Mismo | Mismo (IVA, descuentos, propinas) |

