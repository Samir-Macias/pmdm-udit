//
// PANTALLA UDIT ARCADE
//
// Integra el videojuego 2D Space Shooter desarrollado con Flame
// dentro de la aplicación UDITflix.
//
// Incluye:
// - Marcador de puntuación en tiempo real.
// - Área de juego interactiva con controles por pulsación.
// - Overlay de Game Over con botón para reiniciar la partida.
//

import 'package:flame/game.dart';
import 'package:flutter/material.dart';

import '../game/udit_space_game.dart';

class UditArcadeScreen extends StatefulWidget {
  const UditArcadeScreen({super.key});

  @override
  State<UditArcadeScreen> createState() => _UditArcadeScreenState();
}

class _UditArcadeScreenState extends State<UditArcadeScreen> {
  late final UditSpaceGame _game;
  int _bestScore = 0;

  @override
  void initState() {
    super.initState();
    _game = UditSpaceGame();
    _game.scoreNotifier.addListener(_onScoreChanged);
  }

  void _onScoreChanged() {
    final int current = _game.scoreNotifier.value;
    if (current > _bestScore && mounted) {
      setState(() {
        _bestScore = current;
      });
    }
  }

  @override
  void dispose() {
    _game.scoreNotifier.removeListener(_onScoreChanged);
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Row(
          children: [
            Icon(Icons.rocket_launch_rounded, color: Color(0xFFD4FF00), size: 20),
            SizedBox(width: 8),
            Text(
              'UDIT Arcade',
              style: TextStyle(fontWeight: FontWeight.w800),
            ),
          ],
        ),
        actions: [
          IconButton(
            tooltip: 'Reiniciar partida',
            icon: const Icon(Icons.refresh),
            onPressed: _game.resetGame,
          ),
        ],
      ),
      body: Stack(
        children: [
          GameWidget<UditSpaceGame>(
            game: _game,
            overlayBuilderMap: {
              UditSpaceGame.gameOverOverlayId: (context, game) {
                return Container(
                  color: Colors.black.withValues(alpha: 0.72),
                  child: Center(
                    child: Card(
                      color: const Color(0xFF111116),
                      margin: const EdgeInsets.symmetric(horizontal: 32),
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(18),
                        side: const BorderSide(color: Color(0xFFD4FF00)),
                      ),
                      child: Padding(
                        padding: const EdgeInsets.all(24),
                        child: Column(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            const Icon(
                              Icons.rocket_launch,
                              size: 56,
                              color: Color(0xFFD4FF00),
                            ),
                            const SizedBox(height: 12),
                            const Text(
                              'GAME OVER',
                              style: TextStyle(
                                fontSize: 26,
                                fontWeight: FontWeight.w900,
                                letterSpacing: 1.5,
                                color: Color(0xFFF4F2EE),
                              ),
                            ),
                            const SizedBox(height: 8),
                            ValueListenableBuilder<int>(
                              valueListenable: game.scoreNotifier,
                              builder: (context, score, _) {
                                return Text(
                                  'Puntuación final: $score · Récord: $_bestScore',
                                  style: const TextStyle(
                                    fontSize: 15,
                                    color: Color(0xFF9E9BA6),
                                  ),
                                );
                              },
                            ),
                            const SizedBox(height: 20),
                            ElevatedButton.icon(
                              onPressed: game.resetGame,
                              icon: const Icon(Icons.refresh),
                              label: const Text('Reiniciar partida'),
                              style: ElevatedButton.styleFrom(
                                backgroundColor: const Color(0xFFD4FF00),
                                foregroundColor: const Color(0xFF070709),
                                padding: const EdgeInsets.symmetric(
                                  horizontal: 24,
                                  vertical: 12,
                                ),
                              ),
                            ),
                          ],
                        ),
                      ),
                    ),
                  ),
                );
              },
            },
          ),
          // HUD superior con puntuación y guía rápida de control.
          SafeArea(
            child: Padding(
              padding: const EdgeInsets.all(12),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Container(
                    padding: const EdgeInsets.symmetric(
                      horizontal: 14,
                      vertical: 8,
                    ),
                    decoration: BoxDecoration(
                      color: const Color(0xCC07070C),
                      borderRadius: BorderRadius.circular(8),
                      border: Border.all(color: const Color(0xFFD4FF00)),
                    ),
                    child: ValueListenableBuilder<int>(
                      valueListenable: _game.scoreNotifier,
                      builder: (context, score, _) {
                        return Text(
                          'Puntos: $score  ·  Récord: $_bestScore',
                          style: const TextStyle(
                            color: Color(0xFFF4F2EE),
                            fontWeight: FontWeight.w700,
                            fontSize: 14,
                          ),
                        );
                      },
                    ),
                  ),
                  Container(
                    padding: const EdgeInsets.symmetric(
                      horizontal: 12,
                      vertical: 8,
                    ),
                    decoration: BoxDecoration(
                      color: const Color(0xCC07070C),
                      borderRadius: BorderRadius.circular(8),
                      border: Border.all(color: const Color(0x33FFFFFF)),
                    ),
                    child: const Text(
                      'Pulsa para mover y disparar',
                      style: TextStyle(
                        color: Color(0xFF00F0FF),
                        fontSize: 12,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}
