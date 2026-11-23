//
// VIDEOJUEGO 2D: UDIT ARCADE (SPACE SHOOTER)
//
// Implementación educativa con el motor Flame.
// Incluye:
// - Nave espacial controlada mediante pulsaciones (y arrastre).
// - Disparos láser automáticos y al pulsar.
// - Obstáculos (asteroides) que descienden desde la parte superior.
// - Detección de colisiones con hitboxes de Flame.
// - Contador de puntuación y estado de Game Over con reinicio.
//

import 'dart:math';

import 'package:flame/collisions.dart';
import 'package:flame/components.dart';
import 'package:flame/events.dart';
import 'package:flame/game.dart';
import 'package:flutter/material.dart';

class UditSpaceGame extends FlameGame
    with HasCollisionDetection, TapCallbacks, DragCallbacks {
  static const String gameOverOverlayId = 'GameOver';

  final ValueNotifier<int> scoreNotifier = ValueNotifier<int>(0);
  final Random _random = Random();

  late PlayerShip _player;
  double _spawnTimer = 0;
  double _shootTimer = 0;
  bool isGameOver = false;

  // Estrellas decorativas de fondo y partículas de impacto.
  final List<_Star> _stars = [];
  final List<_Spark> _sparks = [];
  double _gridOffset = 0;

  @override
  Color backgroundColor() => const Color(0xFF07070C);

  @override
  Future<void> onLoad() async {
    await super.onLoad();

    for (int i = 0; i < 40; i++) {
      _stars.add(
        _Star(
          x: _random.nextDouble(),
          y: _random.nextDouble(),
          radius: 1.0 + _random.nextDouble() * 1.8,
          speed: 0.08 + _random.nextDouble() * 0.18,
        ),
      );
    }

    _player = PlayerShip(
      position: Vector2(size.x / 2, size.y - 80),
    );
    await add(_player);
  }

  @override
  void onGameResize(Vector2 size) {
    super.onGameResize(size);
    if (isLoaded) {
      _player.position.x = _player.position.x.clamp(24, size.x - 24);
      _player.position.y = size.y - 80;
    }
  }

  @override
  void render(Canvas canvas) {
    super.render(canvas);

    // Retícula cyber-espacial sutil inspirada en estética 3D/WebGL.
    final Paint gridPaint = Paint()
      ..color = const Color(0xFF00F0FF).withValues(alpha: 0.06)
      ..strokeWidth = 1;
    const double cellSize = 48;
    for (double x = 0; x < size.x; x += cellSize) {
      canvas.drawLine(Offset(x, 0), Offset(x, size.y), gridPaint);
    }
    for (double y = _gridOffset; y < size.y; y += cellSize) {
      canvas.drawLine(Offset(0, y), Offset(size.x, y), gridPaint);
    }

    // Dibujamos el campo de estrellas en movimiento.
    final Paint starPaint = Paint()..color = const Color(0xFFF4F2EE).withValues(alpha: 0.55);
    for (final _Star star in _stars) {
      canvas.drawCircle(
        Offset(star.x * size.x, star.y * size.y),
        star.radius,
        starPaint,
      );
    }

    // Dibujamos chispas de impacto al destruir obstáculos.
    for (final _Spark spark in _sparks) {
      final Paint sparkPaint = Paint()
        ..color = spark.color.withValues(alpha: spark.life.clamp(0.0, 1.0));
      canvas.drawCircle(Offset(spark.x, spark.y), 2.5, sparkPaint);
    }
  }

  @override
  void update(double dt) {
    super.update(dt);
    if (isGameOver) return;

    _gridOffset = (_gridOffset + 36 * dt) % 48;

    // Actualizamos el desplazamiento de las estrellas.
    for (final _Star star in _stars) {
      star.y += star.speed * dt;
      if (star.y > 1.0) {
        star.y = 0.0;
        star.x = _random.nextDouble();
      }
    }

    // Actualizamos partículas de explosión.
    for (final _Spark spark in _sparks) {
      spark.x += spark.vx * dt;
      spark.y += spark.vy * dt;
      spark.life -= 2.4 * dt;
    }
    _sparks.removeWhere((s) => s.life <= 0);

    // Generación periódica de obstáculos.
    _spawnTimer += dt;
    if (_spawnTimer >= 0.85) {
      _spawnTimer = 0;
      _spawnObstacle();
    }

    // Disparo automático periódico de la nave.
    _shootTimer += dt;
    if (_shootTimer >= 0.35) {
      _shootTimer = 0;
      _fireLaser();
    }
  }

  void _spawnObstacle() {
    if (size.x <= 60) return;
    final double obstacleSize = 32 + _random.nextDouble() * 20;
    final double xPos =
        obstacleSize / 2 + _random.nextDouble() * (size.x - obstacleSize);
    final double speed = 150 + _random.nextDouble() * 130;

    add(
      SpaceObstacle(
        position: Vector2(xPos, -obstacleSize),
        obstacleSize: obstacleSize,
        speed: speed,
      ),
    );
  }

  void _fireLaser() {
    add(
      LaserBolt(
        position: Vector2(_player.position.x, _player.position.y - 28),
      ),
    );
  }

  @override
  void onTapDown(TapDownEvent event) {
    if (isGameOver) return;
    _player.setTargetX(event.localPosition.x);
    _fireLaser();
  }

  @override
  void onDragUpdate(DragUpdateEvent event) {
    if (isGameOver) return;
    _player.setTargetX(event.localEndPosition.x);
  }

  void addScore(int points) {
    scoreNotifier.value += points;
  }

  void spawnExplosion(Vector2 origin) {
    for (int i = 0; i < 10; i++) {
      final double angle = _random.nextDouble() * pi * 2;
      final double speed = 70 + _random.nextDouble() * 140;
      _sparks.add(
        _Spark(
          x: origin.x,
          y: origin.y,
          vx: cos(angle) * speed,
          vy: sin(angle) * speed,
          color: i.isEven ? const Color(0xFFD4FF00) : const Color(0xFF00F0FF),
        ),
      );
    }
  }

  void triggerGameOver() {
    if (isGameOver) return;
    isGameOver = true;
    pauseEngine();
    overlays.add(gameOverOverlayId);
  }

  void resetGame() {
    children.whereType<SpaceObstacle>().toList().forEach((c) => c.removeFromParent());
    children.whereType<LaserBolt>().toList().forEach((c) => c.removeFromParent());

    _sparks.clear();
    scoreNotifier.value = 0;
    _spawnTimer = 0;
    _shootTimer = 0;
    isGameOver = false;

    _player.position = Vector2(size.x / 2, size.y - 80);
    _player.setTargetX(size.x / 2);

    overlays.remove(gameOverOverlayId);
    resumeEngine();
  }

  @override
  void onRemove() {
    scoreNotifier.dispose();
    super.onRemove();
  }
}

class _Star {
  double x;
  double y;
  final double radius;
  final double speed;

  _Star({
    required this.x,
    required this.y,
    required this.radius,
    required this.speed,
  });
}

class _Spark {
  double x;
  double y;
  final double vx;
  final double vy;
  final Color color;
  double life = 1.0;

  _Spark({
    required this.x,
    required this.y,
    required this.vx,
    required this.vy,
    required this.color,
  });
}

/// Nave espacial controlada por el jugador mediante pulsaciones o arrastre.
class PlayerShip extends PositionComponent
    with HasGameReference<UditSpaceGame>, CollisionCallbacks {
  static const double _moveSpeed = 420;
  double? _targetX;

  PlayerShip({required super.position})
      : super(
          size: Vector2(46, 52),
          anchor: Anchor.center,
        );

  void setTargetX(double x) {
    _targetX = x.clamp(size.x / 2, game.size.x - size.x / 2);
  }

  @override
  Future<void> onLoad() async {
    await super.onLoad();
    await add(
      RectangleHitbox(
        size: Vector2(34, 40),
        position: Vector2(6, 6),
      ),
    );
  }

  @override
  void update(double dt) {
    super.update(dt);
    final double? target = _targetX;
    if (target == null) return;

    final double diff = target - position.x;
    if (diff.abs() <= 4) {
      position.x = target;
      return;
    }

    final double step = _moveSpeed * dt;
    position.x += diff > 0 ? min(step, diff) : max(-step, diff);
  }

  @override
  void render(Canvas canvas) {
    super.render(canvas);

    // Cuerpo principal de la nave (estética cyber-vectorial).
    final Paint bodyPaint = Paint()..color = const Color(0xFFF4F2EE);
    final Path shipPath = Path()
      ..moveTo(size.x / 2, 0)
      ..lineTo(size.x, size.y - 8)
      ..lineTo(size.x * 0.65, size.y - 14)
      ..lineTo(size.x * 0.35, size.y - 14)
      ..lineTo(0, size.y - 8)
      ..close();
    canvas.drawPath(shipPath, bodyPaint);

    // Detalle carmesí en las alas.
    final Paint wingPaint = Paint()
      ..color = const Color(0xFFFF3B30)
      ..strokeWidth = 2.5
      ..style = PaintingStyle.stroke;
    canvas.drawLine(
      Offset(4, size.y - 10),
      Offset(size.x / 2, 8),
      wingPaint,
    );
    canvas.drawLine(
      Offset(size.x - 4, size.y - 10),
      Offset(size.x / 2, 8),
      wingPaint,
    );

    // Cabina iluminada.
    final Paint cockpitPaint = Paint()..color = const Color(0xFF00F0FF);
    canvas.drawOval(
      Rect.fromCenter(
        center: Offset(size.x / 2, size.y * 0.45),
        width: 11,
        height: 17,
      ),
      cockpitPaint,
    );

    // Propulsor inferior.
    final Paint thrusterPaint = Paint()..color = const Color(0xFFD4FF00);
    canvas.drawCircle(
      Offset(size.x / 2, size.y - 6),
      5,
      thrusterPaint,
    );
  }
}

/// Disparo láser emitido por la nave.
class LaserBolt extends PositionComponent
    with HasGameReference<UditSpaceGame>, CollisionCallbacks {
  static const double _speed = 520;

  LaserBolt({required super.position})
      : super(
          size: Vector2(6, 18),
          anchor: Anchor.center,
        );

  @override
  Future<void> onLoad() async {
    await super.onLoad();
    await add(RectangleHitbox());
  }

  @override
  void update(double dt) {
    super.update(dt);
    position.y -= _speed * dt;
    if (position.y < -20) {
      removeFromParent();
    }
  }

  @override
  void render(Canvas canvas) {
    super.render(canvas);
    final Paint paint = Paint()..color = Colors.cyanAccent;
    canvas.drawRRect(
      RRect.fromRectAndRadius(
        Offset.zero & size.toSize(),
        const Radius.circular(3),
      ),
      paint,
    );
  }
}

/// Obstáculo espacial que desciende y colisiona con la nave o los disparos.
class SpaceObstacle extends PositionComponent
    with HasGameReference<UditSpaceGame>, CollisionCallbacks {
  final double speed;

  SpaceObstacle({
    required super.position,
    required double obstacleSize,
    required this.speed,
  }) : super(
          size: Vector2.all(obstacleSize),
          anchor: Anchor.center,
        );

  @override
  Future<void> onLoad() async {
    await super.onLoad();
    await add(
      CircleHitbox(
        radius: size.x * 0.42,
        position: Vector2(size.x * 0.08, size.y * 0.08),
      ),
    );
  }

  @override
  void update(double dt) {
    super.update(dt);
    position.y += speed * dt;
    angle += 1.4 * dt;

    if (position.y > game.size.y + size.y) {
      removeFromParent();
      game.addScore(1);
    }
  }

  @override
  void render(Canvas canvas) {
    super.render(canvas);
    final double r = size.x / 2;
    final Paint asteroidPaint = Paint()..color = const Color(0xFFFF3B30);
    final Paint borderPaint = Paint()
      ..color = const Color(0xFFD4FF00).withValues(alpha: 0.75)
      ..style = PaintingStyle.stroke
      ..strokeWidth = 1.8;
    final Paint corePaint = Paint()..color = const Color(0xFF1A0D12);

    canvas.drawCircle(Offset(r, r), r * 0.88, asteroidPaint);
    canvas.drawCircle(Offset(r, r), r * 0.88, borderPaint);
    canvas.drawCircle(Offset(r * 0.72, r * 0.78), r * 0.2, corePaint);
    canvas.drawCircle(Offset(r * 1.25, r * 1.1), r * 0.14, corePaint);
  }

  @override
  void onCollisionStart(
    Set<Vector2> intersectionPoints,
    PositionComponent other,
  ) {
    super.onCollisionStart(intersectionPoints, other);

    if (other is LaserBolt) {
      other.removeFromParent();
      game.spawnExplosion(position.clone());
      removeFromParent();
      game.addScore(10);
    } else if (other is PlayerShip) {
      game.triggerGameOver();
    }
  }
}
