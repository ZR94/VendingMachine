@echo off
cd target

REM Avvio di DisplayManager in una nuova finestra
start cmd /k "java -cp machine-0.1-SNAPSHOT-shaded.jar displayService.DisplayManager"

REM Avvio di CashboxManager in una nuova finestra
start cmd /k "java -cp machine-0.1-SNAPSHOT-shaded.jar cashboxService.CashboxManager"

REM Avvio di BeverageService in una nuova finestra
start cmd /k "java -cp machine-0.1-SNAPSHOT-shaded.jar beverageService.BeverageManager"

REM Avvio di AssistanceService in una nuova finestra
start cmd /k "java -cp machine-0.1-SNAPSHOT-shaded.jar assistanceService.AssistanceManager"