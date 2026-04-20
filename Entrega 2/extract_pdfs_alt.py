#!/usr/bin/env python3
"""
Extract PDF content using available tools
"""
import os
import subprocess
import sys

downloads = r"C:\Users\juand\Downloads"

pdfs = {
    "entrega2": "Proyecto #1 - Entrega 2 (Diseño e Implementación) - DISEÑO Y PROGRAMACIÓN O.O. - Universidad de los Andes.pdf",
    "dulces": "Proyecto 1_DulcesnDados.pdf",
    "uml": "Clase UML.pdf"
}

# Try pdftotext (from Xpdf or Poppler)
print("Attempting to extract PDFs...")

for key, filename in pdfs.items():
    pdf_path = os.path.join(downloads, filename)
    if not os.path.exists(pdf_path):
        print(f"✗ Not found: {filename}")
        continue
    
    txt_path = pdf_path.replace(".pdf", "_extracted.txt")
    
    print(f"\nProcessing: {key}")
    print(f"  Input: {pdf_path}")
    print(f"  Output: {txt_path}")
    
    # Try pdftotext
    try:
        cmd = ["pdftotext", pdf_path, txt_path]
        result = subprocess.run(cmd, capture_output=True, text=True, timeout=10)
        if result.returncode == 0 and os.path.exists(txt_path):
            size = os.path.getsize(txt_path)
            print(f"  ✓ Extracted with pdftotext ({size} bytes)")
        else:
            print(f"  ✗ pdftotext failed: {result.stderr[:100]}")
    except FileNotFoundError:
        print(f"  ✗ pdftotext not available")
    except Exception as e:
        print(f"  ✗ Error: {e}")

print("\nDone.")
