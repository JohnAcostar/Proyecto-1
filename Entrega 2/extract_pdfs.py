#!/usr/bin/env python3
import os
import sys

# Add to path for pip package
try:
    import PyPDF2
except ImportError:
    print("PyPDF2 not available")

downloads_path = r"C:\Users\juand\Downloads"
target_files = [
    "Proyecto #1 - Entrega 2 (Diseño e Implementación) - DISEÑO Y PROGRAMACIÓN O.O. - Universidad de los Andes.pdf",
    "Proyecto 1_DulcesnDados.pdf",
    "Clase UML.pdf"
]

for fname in target_files:
    fpath = os.path.join(downloads_path, fname)
    if os.path.exists(fpath):
        print(f"✓ Found: {fname}")
        print(f"  Size: {os.path.getsize(fpath)} bytes")
    else:
        print(f"✗ Not found: {fname}")
