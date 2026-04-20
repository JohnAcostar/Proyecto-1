#!/usr/bin/env python3
"""
Comprehensive analysis of BoardGameCafe project structure
Based on code inspection and DPOO standard project structure
"""

import os
from pathlib import Path

PROJECT_ROOT = Path(r"C:\Users\juand\eclipse-workspace\BoardGameCafe")
SRC_ROOT = PROJECT_ROOT / "src" / "modelo"

# Count classes
java_files = list(SRC_ROOT.glob("*.java"))
print(f"Total classes found: {len(java_files)}")
print(f"\nClasses:")

classes = {}
for f in sorted(java_files):
    name = f.stem
    classes[name] = f
    
# Categorize
categories = {
    "Core Users": ["Usuario", "Administrador", "Cliente", "Empleado", "Mesero", "Cocinero"],
    "Games": ["JuegoDeMesa", "CopiaJuego", "CategoriaJuego", "EstadoJuego", "RestriccionEdad"],
    "Reservations & Loans": ["Reserva", "Prestamo", "EstadoReserva", "InventarioPrestamos"],
    "Sales": ["Venta", "VentaJuegos", "VentaCafe", "InventarioVenta", "ReporteVentas", "RubroVenta"],
    "Cafe & Menu": ["Cafe", "Mesa", "ProductoMenu", "Bebida", "Pasteleria", "TipoBebida"],
    "Shifts & Turns": ["Turno", "Horario", "SolicitudCambioTurno", "TipoSolicitudTurno", "EstadoSolicitudTurno"],
    "Suggestions": ["Sugerencia"]
}

for cat, names in categories.items():
    found = [n for n in names if n in classes]
    status = "✓" if len(found) == len(names) else "⚠"
    print(f"\n{status} {cat}:")
    for name in found:
        print(f"    {name}")
    if len(found) < len(names):
        missing = [n for n in names if n not in classes]
        print(f"    ✗ Missing: {missing}")

print(f"\n{'='*60}")
print("TYPICAL REQUIREMENTS FOR ENTREGA 2:")
print(f"{'='*60}")

required = [
    ("MUST", ["Usuario", "Administrador", "Cliente", "Empleado", "Mesero", "Cocinero",
              "JuegoDeMesa", "Reserva", "Prestamo", "Venta", "VentaJuegos", "VentaCafe"]),
    ("LIKELY", ["Cafe", "Mesa", "InventarioPrestamos", "InventarioVenta", "ReporteVentas",
                "SolicitudCambioTurno", "Sugerencia"]),
    ("MAYBE", ["Bebida", "Pasteleria", "ProductoMenu", "Turno", "Horario"])
]

for level, items in required:
    found = sum(1 for item in items if item in classes)
    print(f"\n{level}: {found}/{len(items)} classes found")
    missing = [item for item in items if item not in classes]
    if missing:
        print(f"  Missing: {missing}")
    extra = [item for item in items if item in classes]
    print(f"  Found: {', '.join(extra[:3])}{'...' if len(extra) > 3 else ''}")

print(f"\n{'='*60}")
print("CLASSES THAT MAY BE UNNECESSARY:")
print(f"{'='*60}")

unnecessary_candidates = [
    ("Cafe", "Not used in SistemaCafe - should be integrated or removed"),
    ("Mesa", "Exists but reservation logic is in Reserva class"),
    ("ProductoMenu", "Bebida/Pasteleria not integrated in sales"),
    ("Bebida", "Menu system not integrated in coffee sales"),
    ("Pasteleria", "Menu system not integrated"),
]

for classname, reason in unnecessary_candidates:
    if classname in classes:
        print(f"⚠ {classname}: {reason}")

print(f"\n{'='*60}")
print("RECOMMENDATIONS:")
print(f"{'='*60}")

print("""
1. VERIFY IN PDFs:
   - Which classes are actually required by the assignment?
   - Is the UML diagram available?
   - What are the specific requirements?

2. IF CLASSES ARE UNNECESSARY:
   - Remove Cafe (not integrated)
   - Remove Mesa (logic duplicated in Reserva)
   - Remove ProductoMenu, Bebida, Pasteleria (system not used)
   - Clean imports in FilePersistence

3. IF CLASSES ARE NECESSARY:
   - Integrate Cafe with SistemaCafe
   - Integrate Mesa with Reserva
   - Integrate Bebida/Pasteleria in coffee sales
   - Document with Javadoc

4. GENERAL IMPROVEMENTS:
   - Add Javadoc documentation
   - Refactor Main (too long, ~300 lines)
   - Add proper exception handling
   - Use UUID instead of nanoTime for IDs
   - Consider JSON persistence instead of text files

5. VERIFICATION:
   - Compile project: javac
   - Check for unused classes: grep -r "unused"
   - Verify UML compliance
""")
