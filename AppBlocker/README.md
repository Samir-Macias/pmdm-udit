# AppBlocker

Aplicación Android para gestionar reglas locales de filtrado de llamadas y clasificar SMS potencialmente sospechosos. El proyecto forma parte del repositorio académico [pmdm-udit](https://github.com/Samir-Macias/pmdm-udit); el código de esta aplicación está en [`AppBlocker/`](https://github.com/Samir-Macias/pmdm-udit/tree/main/AppBlocker).

## Funcionalidades

- Crear, editar, activar y eliminar reglas de bloqueo por prefijo o número exacto.
- Asignar prioridad a las reglas; si coinciden varias, prevalece la de mayor prioridad y después la coincidencia más específica.
- Mantener una lista blanca que tiene preferencia sobre las reglas de bloqueo.
- Solicitar al sistema el rol de filtrado de llamadas y, una vez concedido, rechazar llamadas que coincidan con reglas activas.
- Simular el resultado para un número sin realizar ni bloquear una llamada real.
- Consultar y borrar el historial local de decisiones de llamadas.
- Importar y exportar reglas mediante CSV.
- Clasificar SMS recibidos usando remitentes incluidos en las reglas y señales sencillas en el texto. Consultar, filtrar y eliminar los mensajes guardados dentro de la aplicación.

> **Alcance del filtro SMS:** la aplicación clasifica y conserva una copia local de SMS recibidos; no los bloquea ni los elimina de la aplicación de mensajería del teléfono. No sustituye a la aplicación SMS predeterminada.

## Decisiones técnicas y funcionamiento

- **Android nativo con Kotlin.** La interfaz usa vistas XML y `RecyclerView`.
- **Room/SQLite local.** Reglas, historial de llamadas y mensajes clasificados se guardan en la base de datos privada de la aplicación. No se necesita un servidor: la evaluación de reglas y la clasificación se realizan en el dispositivo.
- **Lógica separada de Android.** `RuleEngine` evalúa lista blanca, estado, prioridad, tipo de coincidencia y normalización de números. `SmsClassifier` aplica reglas al remitente y busca señales simples en el contenido.
- **Integración de llamadas del sistema.** `SpamCallScreeningService` consulta las reglas y responde a Telecom mediante `respondToCall()`. La aplicación debe tener el rol de filtro de llamadas para que Android invoque el servicio.
- **Procesamiento local de SMS.** El receptor necesita el permiso `RECEIVE_SMS`; los mensajes se guardan con su clasificación y motivo. Se limita la retención a 500 mensajes y 30 días.
- **Importación/exportación interoperable.** `RuleCsvCodec` valida los CSV y mantiene compatibilidad con el formato anterior sin la columna `match_type`.
- **Sin dependencias de red para el filtrado.** No se requiere conectividad para aplicar las reglas configuradas localmente.

### Flujo de llamada

1. El usuario configura reglas y excepciones en la aplicación.
2. Con el rol concedido, Android entrega al servicio una llamada susceptible de filtrado.
3. La aplicación normaliza el número y evalúa las reglas locales.
4. Se registra la decisión y el motivo; el servicio responde a Telecom para permitir o rechazar la llamada.

La simulación de la pantalla principal solo ejecuta los pasos de evaluación: no invoca Telecom ni cambia las estadísticas de llamadas reales.

### Flujo de SMS

Con el permiso de recepción concedido, Android envía los SMS entrantes al receptor registrado. La aplicación clasifica el remitente y el texto, guarda el resultado localmente y lo muestra en su bandeja propia. La recepción/clasificación no equivale a bloquear el SMS.

## Requisitos y ejecución

- Android Studio y Android SDK Platform 37.
- JDK 21 recomendado (JDK completo, con `jlink`).
- Para usar el filtro de llamadas en el dispositivo: Android 10 (API 29) o posterior y rol concedido por el usuario.
- Para ejecutar la aplicación: Android 7.0 (API 24) o posterior. El dispositivo debe admitir telefonía para recibir llamadas/SMS.

Desde la carpeta `AppBlocker/`:

```bash
./gradlew clean testDebugUnitTest
./gradlew assembleDebug
```

En Windows se puede usar `gradlew.bat` con los mismos argumentos. Para probar el bloqueo real se debe instalar la aplicación en un teléfono compatible, conceder el rol desde la aplicación y realizar una llamada de prueba controlada. La simulación numérica no sustituye esa prueba de integración.

## Reflexión crítica

### 1. ¿Qué es `CallScreeningService` y qué función cumple en Android?

Es una API de Android que permite a la aplicación seleccionada por el usuario recibir información de llamadas antes de que se presenten y responder al sistema si deben permitirse, silenciarse o rechazarse. En este proyecto, `SpamCallScreeningService` usa las reglas locales y comunica la decisión a Telecom. Android exige que el servicio responda dentro del plazo establecido por el sistema.

### 2. ¿Por qué necesitamos solicitar el rol `ROLE_CALL_SCREENING`?

El rol identifica qué aplicación ha elegido el usuario para realizar el filtrado de llamadas. Declarar el servicio en el manifiesto no basta: sin el rol, Android no lo selecciona ni le entrega las llamadas para evaluar. La aplicación solicita el rol mediante la interfaz de sistema de `RoleManager`; el usuario puede aceptarlo o denegarlo. El rol está disponible desde Android 10 (API 29).

### 3. ¿Por qué no necesitamos un servidor externo para realizar el filtrado?

Las reglas, la lista blanca y el motor de evaluación están en el dispositivo. Telecom invoca el servicio local y este consulta la base de datos Room, por lo que la decisión puede tomarse sin enviar el número a un servidor ni depender de Internet. Un servicio externo podría ser una mejora opcional para listas compartidas, pero no es necesario para la funcionalidad implementada y tendría implicaciones de privacidad y disponibilidad.

### 4. ¿Qué ocurre si el número telefónico está guardado en los contactos?

Android documenta que `CallScreeningService` se invoca para llamadas de números que **no** están en la lista de contactos del usuario. Por ello, una llamada de un contacto normalmente no llega a este filtro y no se evalúa con las reglas de la aplicación. AppBlocker tampoco consulta los contactos: no solicita `READ_CONTACTS`. Este comportamiento lo determina la plataforma, no la lista blanca de AppBlocker.

### 5. ¿Qué diferencias existen entre comprobar un número mediante una función y bloquear realmente una llamada?

`RuleEngine.evaluate()` solo calcula y devuelve una decisión (`FilterDecision`); la simulación muestra ese resultado sin afectar una llamada. El bloqueo real requiere que Android invoque el servicio con el rol concedido y que este responda a Telecom mediante `respondToCall()` con una `CallResponse` que deniegue/rechace la llamada. Probar la función verifica la lógica de reglas, pero no demuestra por sí solo la integración real con telefonía, permisos y comportamiento del dispositivo.

### 6. ¿Qué mejoras incorporarías en una futura versión?

- Añadir pruebas instrumentadas en dispositivos y versiones de Android distintas, incluyendo llamadas reales controladas y casos de números privados/no disponibles.
- Mejorar la clasificación de SMS con análisis más robusto, explicación de falsos positivos y opciones claras de privacidad; cualquier bloqueo de SMS requeriría una integración compatible con las restricciones y roles de Android.
- Usar una biblioteca especializada para normalizar números internacionales y permitir tipos de coincidencia más flexibles para la lista blanca.
- Incorporar pruebas de migración de Room, accesibilidad, localización y una interfaz más clara para indicar el estado del rol y los permisos.
- Ofrecer una política configurable de retención y protección de los datos locales, especialmente del contenido de SMS y del historial de llamadas.
- Permitir actualizar listas de spam opcionales, con consentimiento explícito y sin hacerlas requisito para el filtrado local.

## Pruebas y evidencias

Pruebas unitarias ejecutadas con:

```bash
./gradlew clean testDebugUnitTest
```

**Resultado verificado en este entorno:** `BUILD SUCCESSFUL`; 16 tests aprobados, 0 fallos y 0 errores. Los tests cubren el motor de reglas (lista blanca, prioridad, prefijos, coincidencia exacta y normalización), la clasificación de SMS y la codificación/validación CSV. Los fuentes están en [`app/src/test/`](app/src/test/).

Las pruebas instrumentadas de Android y una llamada real no se ejecutaron en este entorno: no había un dispositivo/emulador Android conectado. Tampoco se generaron capturas de pantalla de la aplicación; no se incluyen imágenes simuladas como si fueran evidencia de ejecución. Antes de entregar, añade capturas reales en [`docs/evidencias/`](docs/evidencias/README.md) siguiendo la lista indicada allí. Para obtener el informe local de Gradle, ejecuta las pruebas y abre `app/build/reports/tests/testDebugUnitTest/index.html`.

## Referencias

- [Android: CallScreeningService](https://developer.android.com/reference/android/telecom/CallScreeningService)
- [Android: filtrar llamadas](https://developer.android.com/develop/connectivity/telecom/dialer-app/screen-calls)
- [Repositorio GitHub](https://github.com/Samir-Macias/pmdm-udit)
