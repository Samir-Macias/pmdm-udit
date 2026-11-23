
//
// PRUEBAS BÁSICAS DE LA APLICACIÓN UDITFLIX
//
// Comprueba:
// - Que la aplicación muestra el catálogo y sus contenidos.
// - Que se puede navegar a UDIT Arcade.
// - Que se puede navegar desde la ficha de un contenido al reproductor.
//

import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:uditflix/data/contenidos.dart';
import 'package:uditflix/main.dart';

void main() {
  testWidgets('El catálogo construye todos los contenidos almacenados',
      (WidgetTester tester) async {
    await tester.pumpWidget(const UDITflixApp());
    await tester.tap(find.byTooltip('Buscar'));
    await tester.pumpAndSettle();

    final Scaffold scaffold = tester.widget(find.byType(Scaffold).first);
    final ListView lista = scaffold.body! as ListView;
    expect(lista.childrenDelegate, isA<SliverChildBuilderDelegate>());
    expect(find.text('CATÁLOGO (${contenidos.length})'), findsOneWidget);

    for (final contenido in contenidos) {
      await tester.scrollUntilVisible(
        find.text(contenido.titulo),
        150,
        scrollable: find.byType(Scrollable).first,
      );
      final Finder tarjeta = find.ancestor(
        of: find.text(contenido.titulo),
        matching: find.byType(Card),
      );
      expect(tarjeta, findsOneWidget);
      expect(
        find.descendant(
          of: tarjeta,
          matching: find.text(contenido.tipo.toUpperCase()),
        ),
        findsOneWidget,
      );
      expect(
        find.descendant(
          of: tarjeta,
          matching: find.text('${contenido.anio} · ${contenido.duracion}'),
        ),
        findsOneWidget,
      );
      expect(
        find.descendant(of: tarjeta, matching: find.text(contenido.valoracion)),
        findsOneWidget,
      );
    }
  });

  testWidgets('La búsqueda filtra el catálogo y muestra resultados vacíos',
      (WidgetTester tester) async {
    await tester.pumpWidget(const UDITflixApp());
    await tester.tap(find.byTooltip('Buscar'));
    await tester.pumpAndSettle();

    await tester.enterText(find.byType(TextField), 'mysql');
    await tester.pumpAndSettle();
    expect(find.text('CATÁLOGO (1)'), findsOneWidget);
    expect(find.text('Bases de datos MySQL'), findsOneWidget);

    await tester.enterText(find.byType(TextField), 'sin resultados');
    await tester.pumpAndSettle();
    expect(find.text('CATÁLOGO (0)'), findsOneWidget);
    expect(
      find.text('No se encontraron contenidos con ese filtro.'),
      findsOneWidget,
    );
  });

  testWidgets('UDITflix muestra su catálogo y filtros', (WidgetTester tester) async {
    await tester.pumpWidget(const UDITflixApp());

    expect(find.text('UDITflix'), findsOneWidget);
    expect(find.text('Introducción a Flutter'), findsAtLeastNWidgets(1));
    expect(find.text('UDIT Arcade'), findsOneWidget);

    // Probamos el filtro por categoría "Cómic".
    await tester.tap(find.text('Cómic'));
    await tester.pumpAndSettle();
    expect(find.text('El mundo de los cómics'), findsOneWidget);
  });

  testWidgets('Navegación a UDIT Arcade muestra el marcador de puntos',
      (WidgetTester tester) async {
    await tester.pumpWidget(const UDITflixApp());

    await tester.tap(find.text('UDIT Arcade'));
    await tester.pump();
    await tester.pump(const Duration(milliseconds: 300));

    expect(find.text('Puntos: 0  ·  Récord: 0'), findsOneWidget);
    expect(find.byIcon(Icons.refresh), findsOneWidget);
  });

  testWidgets('Navegación desde ficha al reproductor y Mi lista',
      (WidgetTester tester) async {
    await tester.pumpWidget(const UDITflixApp());

    await tester.scrollUntilVisible(
      find.text('Desarrollo con Java'),
      150,
      scrollable: find.byType(Scrollable).first,
    );
    await tester.pumpAndSettle();
    await tester.tap(find.text('Desarrollo con Java'));
    await tester.pumpAndSettle();

    expect(find.text('Ver contenido'), findsOneWidget);
    expect(find.text('Mi lista'), findsOneWidget);

    await tester.tap(find.text('Mi lista'));
    await tester.pump();
    expect(find.text('En mi lista'), findsOneWidget);

    await tester.tap(find.text('Ver contenido'));
    await tester.pump();
    await tester.pump(const Duration(milliseconds: 200));

    expect(find.text('Reproductor UDITflix'), findsOneWidget);
  });
}
