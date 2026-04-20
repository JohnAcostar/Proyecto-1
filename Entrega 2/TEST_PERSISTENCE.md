# Persistence Testing Guide

## Test Case: Verify Loan Data Persistence

### Steps:
1. **Start App**
   - Run the application

2. **Create Test User & Borrow Game**
   - Login or create a basic user account
   - Go to "Pedir Prestamo" (Request Loan)
   - Select a game
   - Confirm the loan

3. **Verify Loan Shows**
   - Go to "Ver Historial de Prestamos" (View Loan History)
   - Confirm loan appears and is marked as "Activo: true"

4. **Close App**
   - Select option to exit
   - Watch for "Datos guardados" (Data saved) message
   - Verify files exist in `./data/` folder

5. **Restart App**
   - Run the application again
   - Login with the same user

6. **Verify Persistence**
   - Go to "Historial de Prestamos" - loan should still appear
   - Go to "Devolver Juego" - active loan should be listed
   - If not showing → Persistence issue

## Files Being Saved:
- `data/usuarios.txt` - User logins, passwords, IDs, fidelity points
- `data/prestamos.txt` - Loan records with user login references
- `data/ventas.txt` - Purchase history
- `data/juegos.txt` - Game catalog
- Other inventory & state files

## What Was Fixed:
- ✅ Fidelity points type conversion (Integer vs Double)
- ✅ Cliente.getPuntosDeFidelidad() return type
