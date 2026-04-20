#!/usr/bin/env python3
"""
Extract text from PDFs to analyze requirements
"""
import os
import sys

downloads = r"C:\Users\juand\Downloads"

pdfs = {
    "entrega2": "Proyecto #1 - Entrega 2 (Diseño e Implementación) - DISEÑO Y PROGRAMACIÓN O.O. - Universidad de los Andes.pdf",
    "dulces": "Proyecto 1_DulcesnDados.pdf",
    "uml": "Clase UML.pdf"
}

for key, filename in pdfs.items():
    path = os.path.join(downloads, filename)
    exists = os.path.exists(path)
    size = os.path.getsize(path) if exists else 0
    print(f"{key:12} | {exists} | {size:>10} | {filename[:60]}")

# Try to use pdfplumber or PyPDF2
try:
    import pdfplumber
    print("\n✓ pdfplumber available")
except ImportError:
    print("\n✗ pdfplumber not available - trying alternative")

try:
    from PyPDF2 import PdfReader
    print("✓ PyPDF2 available")
except ImportError:
    print("✗ PyPDF2 not available")

try:
    import pdf2image
    print("✓ pdf2image available")
except ImportError:
    print("✗ pdf2image not available")

# Try running pdftotext command
import subprocess
try:
    result = subprocess.run(["pdftotext", "--version"], capture_output=True, text=True)
    print("✓ pdftotext command available")
except FileNotFoundError:
    print("✗ pdftotext command not available")
