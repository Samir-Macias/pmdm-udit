
//
// PANTALLA DEL REPRODUCTOR
//
// Reproduce el vídeo asociado al contenido seleccionado utilizando
// el paquete `video_player`.
//
// Incluye:
// - Reproducción y pausa.
// - Barra de progreso interactiva (scrubbing) y tiempos.
// - Control de silencio y reinicio.
// - Modo de pantalla completa.
//

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:video_player/video_player.dart';

import '../models/contenido.dart';

class ReproductorScreen extends StatefulWidget {
  // Contenido que queremos visualizar.
  final Contenido contenido;

  const ReproductorScreen({
    super.key,
    required this.contenido,
  });

  @override
  State<ReproductorScreen> createState() => _ReproductorScreenState();
}

class _ReproductorScreenState extends State<ReproductorScreen> {
  // Vídeo MP4 público de demostración por defecto.
  static const String _defaultDemoVideoUrl =
      'https://flutter.github.io/assets-for-api-docs/assets/videos/butterfly.mp4';

  late VideoPlayerController _controller;
  bool _isInitialized = false;
  bool _hasError = false;
  bool _isMuted = false;
  bool _isFullScreen = false;

  @override
  void initState() {
    super.initState();
    _inicializarReproductor();
  }

  Future<void> _inicializarReproductor() async {
    setState(() {
      _isInitialized = false;
      _hasError = false;
    });

    final String url = widget.contenido.videoUrl ?? _defaultDemoVideoUrl;
    _controller = VideoPlayerController.networkUrl(Uri.parse(url));

    try {
      await _controller.initialize();
      _controller.addListener(_onPlayerUpdate);
      if (!mounted) return;
      setState(() {
        _isInitialized = true;
      });
    } catch (_) {
      if (!mounted) return;
      setState(() {
        _hasError = true;
      });
    }
  }

  void _onPlayerUpdate() {
    if (mounted) {
      setState(() {});
    }
  }

  @override
  void dispose() {
    _controller.removeListener(_onPlayerUpdate);
    _controller.dispose();
    if (_isFullScreen) {
      SystemChrome.setEnabledSystemUIMode(SystemUiMode.edgeToEdge);
    }
    super.dispose();
  }

  void _togglePlayPause() {
    if (!_isInitialized) return;
    setState(() {
      if (_controller.value.isPlaying) {
        _controller.pause();
      } else {
        _controller.play();
      }
    });
  }

  void _toggleMute() {
    if (!_isInitialized) return;
    setState(() {
      _isMuted = !_isMuted;
      _controller.setVolume(_isMuted ? 0.0 : 1.0);
    });
  }

  void _restartVideo() {
    if (!_isInitialized) return;
    _controller.seekTo(Duration.zero);
    _controller.play();
  }

  void _seekRelative(Duration offset) {
    if (!_isInitialized) return;
    final Duration current = _controller.value.position;
    final Duration total = _controller.value.duration;
    Duration target = current + offset;
    if (target < Duration.zero) target = Duration.zero;
    if (target > total) target = total;
    _controller.seekTo(target);
  }

  void _toggleFullScreen() {
    setState(() {
      _isFullScreen = !_isFullScreen;
    });

    if (_isFullScreen) {
      SystemChrome.setEnabledSystemUIMode(SystemUiMode.immersiveSticky);
    } else {
      SystemChrome.setEnabledSystemUIMode(SystemUiMode.edgeToEdge);
    }
  }

  String _formatDuration(Duration duration) {
    final String minutes =
        duration.inMinutes.remainder(60).toString().padLeft(2, '0');
    final String seconds =
        duration.inSeconds.remainder(60).toString().padLeft(2, '0');
    return '$minutes:$seconds';
  }

  Widget _buildVideoArea() {
    if (_hasError) {
      return Container(
        height: 230,
        width: double.infinity,
        color: Colors.black87,
        padding: const EdgeInsets.all(20),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(Icons.error_outline, color: Colors.white70, size: 48),
            const SizedBox(height: 12),
            const Text(
              'No se pudo cargar el vídeo de demostración.',
              style: TextStyle(color: Colors.white),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 12),
            ElevatedButton.icon(
              onPressed: () {
                _controller.dispose();
                _inicializarReproductor();
              },
              icon: const Icon(Icons.refresh),
              label: const Text('Reintentar'),
            ),
          ],
        ),
      );
    }

    if (!_isInitialized) {
      return Container(
        height: _isFullScreen ? double.infinity : 230,
        width: double.infinity,
        color: Colors.black,
        child: const Center(
          child: CircularProgressIndicator(color: Color(0xFFFF3B30)),
        ),
      );
    }

    final Duration position = _controller.value.position;
    final Duration total = _controller.value.duration;
    final bool isPlaying = _controller.value.isPlaying;

    return Container(
      color: Colors.black,
      child: Column(
        mainAxisSize: _isFullScreen ? MainAxisSize.max : MainAxisSize.min,
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Flexible(
            fit: _isFullScreen ? FlexFit.tight : FlexFit.loose,
            child: GestureDetector(
              onTap: _togglePlayPause,
              child: Stack(
                alignment: Alignment.center,
                children: [
                  Center(
                    child: AspectRatio(
                      aspectRatio: _controller.value.aspectRatio > 0
                          ? _controller.value.aspectRatio
                          : 16 / 9,
                      child: VideoPlayer(_controller),
                    ),
                  ),
                  if (!isPlaying)
                    Container(
                      decoration: const BoxDecoration(
                        color: Colors.black45,
                        shape: BoxShape.circle,
                      ),
                      padding: const EdgeInsets.all(12),
                      child: const Icon(
                        Icons.play_arrow,
                        size: 56,
                        color: Colors.white,
                      ),
                    ),
                ],
              ),
            ),
          ),
          // Barra de progreso interactiva.
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 12),
            child: VideoProgressIndicator(
              _controller,
              allowScrubbing: true,
              colors: const VideoProgressColors(
                playedColor: Color(0xFFFF3B30),
                bufferedColor: Colors.white38,
                backgroundColor: Colors.white24,
              ),
            ),
          ),
          // Controles básicos del reproductor.
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
            child: Row(
              children: [
                IconButton(
                  tooltip: 'Retroceder 10 s',
                  icon: const Icon(Icons.replay_10, color: Colors.white),
                  onPressed: () => _seekRelative(const Duration(seconds: -10)),
                ),
                IconButton(
                  tooltip: isPlaying ? 'Pausar' : 'Reproducir',
                  icon: Icon(
                    isPlaying ? Icons.pause : Icons.play_arrow,
                    color: Colors.white,
                  ),
                  onPressed: _togglePlayPause,
                ),
                IconButton(
                  tooltip: 'Avanzar 10 s',
                  icon: const Icon(Icons.forward_10, color: Colors.white),
                  onPressed: () => _seekRelative(const Duration(seconds: 10)),
                ),
                IconButton(
                  tooltip: 'Reiniciar',
                  icon: const Icon(Icons.replay, color: Colors.white),
                  onPressed: _restartVideo,
                ),
                Text(
                  '${_formatDuration(position)} / ${_formatDuration(total)}',
                  style: const TextStyle(color: Colors.white, fontSize: 13),
                ),
                const Spacer(),
                IconButton(
                  tooltip: _isMuted ? 'Activar sonido' : 'Silenciar',
                  icon: Icon(
                    _isMuted ? Icons.volume_off : Icons.volume_up,
                    color: Colors.white,
                  ),
                  onPressed: _toggleMute,
                ),
                IconButton(
                  tooltip: _isFullScreen
                      ? 'Salir de pantalla completa'
                      : 'Pantalla completa',
                  icon: Icon(
                    _isFullScreen ? Icons.fullscreen_exit : Icons.fullscreen,
                    color: Colors.white,
                  ),
                  onPressed: _toggleFullScreen,
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: _isFullScreen
          ? null
          : AppBar(
              title: const Text('Reproductor UDITflix'),
            ),
      backgroundColor: _isFullScreen ? Colors.black : null,
      body: _isFullScreen
          ? SafeArea(child: _buildVideoArea())
          : SingleChildScrollView(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  _buildVideoArea(),
                  Padding(
                    padding: const EdgeInsets.all(20),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          widget.contenido.titulo,
                          style: const TextStyle(
                            fontSize: 22,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                        const SizedBox(height: 8),
                        Row(
                          children: [
                            Chip(
                              label: Text(widget.contenido.tipo),
                              backgroundColor: const Color(0xFF18162B),
                            ),
                            const SizedBox(width: 8),
                            Text(
                              '${widget.contenido.anio} · ${widget.contenido.duracion}',
                              style: const TextStyle(color: Colors.white60),
                            ),
                            const Spacer(),
                            const Icon(
                              Icons.star_rounded,
                              size: 18,
                              color: Colors.amber,
                            ),
                            const SizedBox(width: 4),
                            Text(
                              widget.contenido.valoracion,
                              style: const TextStyle(
                                color: Colors.white70,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                          ],
                        ),
                        const SizedBox(height: 16),
                        Text(
                          widget.contenido.descripcion,
                          style: const TextStyle(fontSize: 16),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ),
    );
  }
}
