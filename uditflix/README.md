# UDITflix

UDITflix es una aplicación de demostración para explorar un catálogo de
contenidos educativos y de entretenimiento: vídeos, cómics y juegos. Su
finalidad es mostrar una interfaz personalizada y practicar el desarrollo
multiplataforma con Flutter.

## Funcionalidades

- Catálogo generado con `ListView.builder`, con portadas, tipo, duración,
  año y valoración.
- Búsqueda por título o descripción y filtros por categoría.
- Fichas con descripción y acceso a un reproductor de vídeo de demostración.
- Botón «Mi lista» con estado temporal en la ficha.
- UDIT Arcade: minijuego de naves con marcador.
- Interfaz oscura con colores, iconos y estilos personalizados.

Los contenidos están definidos localmente en
[`lib/data/contenidos.dart`](lib/data/contenidos.dart); no hay servidor, base
de datos ni cuentas de usuario. «Mi lista» y el récord del juego no son
persistentes. Las portadas y vídeos de ejemplo se cargan desde Internet. Los
contenidos sin vídeo asociado utilizan un vídeo de demostración; el catálogo
no incluye un lector de cómics independiente.

## Tecnologías

- **Flutter y Dart**: interfaz, navegación y lógica de la aplicación.
- **Material 3**: componentes y tema visual.
- **`video_player`**: reproducción de vídeo en plataformas compatibles.
- **Flame**: motor del minijuego UDIT Arcade.
- **`flutter_test` y `flutter_lints`**: pruebas y análisis estático.

Las versiones y dependencias están en [`pubspec.yaml`](pubspec.yaml) y
[`pubspec.lock`](pubspec.lock). El proyecto requiere Dart `^3.13.3` (desde
3.13.3 hasta antes de 4.0.0). Se ha utilizado Flutter 3.47.5 con Dart 3.13.4.

## Requisitos

1. Instalar el [SDK de Flutter](https://docs.flutter.dev/get-started/install),
   añadirlo al `PATH` y comprobar la instalación con `flutter doctor`.
2. Tener acceso a Internet para descargar dependencias y visualizar los
   recursos remotos.
3. Preparar las herramientas de la plataforma elegida:
   - **Android**: Android Studio, Android SDK, un emulador o dispositivo y JDK
     17. El proyecto configura Java/Kotlin para la versión 17.
   - **Web**: Chrome para desarrollo y un servidor de archivos estáticos
     para publicar la compilación.
   - **iOS**: macOS, Xcode, CocoaPods y un simulador o dispositivo. Para
     distribuir en dispositivos se necesita configurar la firma de Apple.
   - **Escritorio**: las herramientas nativas correspondientes indicadas en
     la [documentación de Flutter](https://docs.flutter.dev/platform-integration/desktop).

## Instalación y ejecución

Clona el repositorio y entra en `uditflix`, o descomprime el ZIP y abre la
carpeta que contiene `pubspec.yaml`. Desde esa carpeta:

```bash
flutter doctor
flutter pub get
flutter devices
flutter run
```

Selecciona el dispositivo cuando Flutter lo solicite. También puedes
especificarlo, por ejemplo, con `flutter run -d chrome` o
`flutter run -d <id-del-dispositivo>`. El ZIP no incluye cachés ni archivos
generados: Flutter los vuelve a crear al instalar dependencias y compilar.

## Compilación y despliegue

Ejecuta estos comandos desde la carpeta que contiene `pubspec.yaml`, después de
instalar las dependencias:

### Web

```bash
flutter build web --release
```

Publica el contenido de `build/web/` en un servidor de archivos estáticos.
Si se sirve desde una subruta, por ejemplo `https://ejemplo.com/uditflix/`,
compila con `flutter build web --release --base-href /uditflix/`. Los vídeos
remotos dependen de la compatibilidad del navegador y de las políticas de
acceso del servidor que los aloja.

### Android

```bash
flutter build apk --release
```

El APK se genera en `build/app/outputs/flutter-apk/app-release.apk`. Para
Google Play se puede generar un App Bundle con `flutter build appbundle
--release`, cuyo resultado se encuentra en
`build/app/outputs/bundle/release/app-release.aab`.

**Antes de publicar**, cambia el identificador de ejemplo
`com.example.uditflix` y configura una clave de firma de producción: la
configuración actual firma las compilaciones de release con la clave de
depuración. No incluyas claves ni contraseñas en el repositorio.
Consulta la [guía de publicación de Android](https://docs.flutter.dev/deployment/android).

### iOS y escritorio

En macOS, `flutter build ios --release` compila para iOS; configura el equipo,
identificador y firma en Xcode antes de distribuir. Para escritorio, utiliza
`flutter build macos --release`, `flutter build windows --release` o
`flutter build linux --release` en el sistema operativo correspondiente y
consulta las guías de [despliegue de Flutter](https://docs.flutter.dev/deployment).

El reproductor incluido tiene implementaciones para Android, iOS, macOS y web;
el proyecto no incorpora una implementación de `video_player` para Linux o
Windows. En macOS, el sandbox actual no incluye el permiso de conexiones de
salida (`com.apple.security.network.client`); hay que configurarlo en los
entitlements de depuración y release para cargar portadas y vídeos remotos.
Comprueba siempre el funcionamiento en la plataforma de destino antes de
publicar.

## Pruebas y análisis

```bash
flutter analyze
flutter test
```

Las pruebas de widgets comprueban el catálogo, sus filtros y búsquedas, y la
navegación a las fichas, el reproductor y Arcade.

## Estructura principal

```text
lib/
  main.dart                 Punto de entrada y tema.
  data/contenidos.dart      Datos locales del catálogo.
  models/contenido.dart     Modelo de contenido.
  screens/                  Catálogo, ficha, reproductor y Arcade.
  game/                     Lógica del minijuego.
test/                       Pruebas de widgets.
android/, ios/, web/        Configuración y recursos de plataforma.
linux/, macos/, windows/    Proyectos de escritorio.
```
