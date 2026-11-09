# Evidencias de entrega

## Pruebas automatizadas

Ejecutar desde `AppBlocker/`:

```bash
./gradlew clean testDebugUnitTest
```

En la ejecución realizada para esta entrega, Gradle terminó con `BUILD SUCCESSFUL`: 16 pruebas unitarias aprobadas, sin fallos ni errores. El código de las pruebas está en `app/src/test/`; el informe HTML se genera en `app/build/reports/tests/testDebugUnitTest/index.html`.

## Capturas de pantalla realizadas en dispositivo / emulador

Se han incorporado las siguientes capturas reales de la aplicación en la carpeta `docs/evidencias/`:

1. **[pantalla_principal.png](pantalla_principal.png)**: Pantalla principal de la aplicación mostrando la regla de bloqueo configurada (`400`) y las opciones de gestión.
2. **[lista_blanca.png](lista_blanca.png)**: Pestaña de gestión de la lista blanca de números exentos.
3. **[registro_actividad.png](registro_actividad.png)**: Ventana de diálogo con el registro de actividad (Call Logs) y estadísticas de llamadas bloqueadas y permitidas.
4. **[filtro_sms.png](filtro_sms.png)**: Pantalla del filtro inteligente de SMS y bandeja clasificada.
