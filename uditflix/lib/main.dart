
//
// ARCHIVO PRINCIPAL DE UDITFLIX
//
// Este archivo inicia nuestra aplicación Flutter.
//
// Desde aquí indicamos cuál será la primera pantalla
// que verá el usuario.
//

import 'package:flutter/material.dart';

import 'screens/catalogo_screen.dart';

// Función principal.
// En Dart, main() es el punto de entrada del programa.

void main() {

  // Ejecutamos la aplicación Flutter.
  runApp(const UDITflixApp());

}

// Widget principal de la aplicación.
// MaterialApp proporciona los componentes visuales
// y la configuración general de nuestra aplicación.

class UDITflixApp extends StatelessWidget {

  const UDITflixApp({super.key});

  @override
  Widget build(BuildContext context) {

    return MaterialApp(

      // Quitamos la etiqueta de depuración.
      debugShowCheckedModeBanner: false,

      // Nombre de la aplicación.
      title: 'UDITflix',

      // Configuración visual inspirada en diseño editorial de estudio (AUGE / Arsh)
      // y estética cyber-arcade de alto contraste (Lyo).
      theme: ThemeData(
        useMaterial3: true,
        brightness: Brightness.dark,
        scaffoldBackgroundColor: const Color(0xFF070709),
        colorScheme: const ColorScheme.dark(
          primary: Color(0xFFFF3B30),
          secondary: Color(0xFFD4FF00),
          tertiary: Color(0xFF00F0FF),
          surface: Color(0xFF111116),
          onPrimary: Colors.white,
          onSecondary: Color(0xFF070709),
          onSurface: Color(0xFFF4F2EE),
        ),
        appBarTheme: const AppBarTheme(
          backgroundColor: Color(0xFF070709),
          foregroundColor: Color(0xFFF4F2EE),
          elevation: 0,
          scrolledUnderElevation: 0,
          centerTitle: false,
        ),
        cardTheme: CardThemeData(
          color: const Color(0xFF111116),
          elevation: 0,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(14),
            side: const BorderSide(color: Color(0x1AFFFFFF)),
          ),
        ),
        elevatedButtonTheme: ElevatedButtonThemeData(
          style: ElevatedButton.styleFrom(
            backgroundColor: const Color(0xFFFF3B30),
            foregroundColor: Colors.white,
            elevation: 0,
            padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 14),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(10),
            ),
            textStyle: const TextStyle(
              fontWeight: FontWeight.w700,
              letterSpacing: 0.3,
            ),
          ),
        ),
      ),

      // Primera pantalla que se muestra.
      home: const CatalogoScreen(),

    );
  }
}
