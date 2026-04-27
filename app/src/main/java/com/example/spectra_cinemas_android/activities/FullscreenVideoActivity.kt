package com.example.spectra_cinemas_android.activities

import android.content.Context
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.example.spectra_cinemas_android.databinding.ActivityFullscreenVideoBinding
import com.example.spectra_cinemas_android.utils.VideoPlayer

class FullscreenVideoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullscreenVideoBinding
    private val updateHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullscreenVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupVideoView()
        setupControls()
    }

    private fun setupVideoView() {
        val uri = Uri.parse("android.resource://$packageName/${VideoPlayer.currentResId}")
        binding.fullscreenVideoView.setVideoURI(uri)
        
        binding.fullscreenVideoView.setOnPreparedListener { mp ->
            binding.videoControls.videoSeekBar.max = binding.fullscreenVideoView.duration
            binding.fullscreenVideoView.seekTo(VideoPlayer.currentPosition)
            
            if (VideoPlayer.isPlayingState) {
                binding.fullscreenVideoView.start()
                binding.videoControls.btnPlayPause.text = "||"
            } else {
                binding.videoControls.btnPlayPause.text = "|>"
            }
            startSeekBarUpdate()
        }
    }

    private fun setupControls() {
        binding.videoControls.btnPlayPause.setOnClickListener {
            if (binding.fullscreenVideoView.isPlaying) {
                binding.fullscreenVideoView.pause()
                binding.videoControls.btnPlayPause.text = "|>"
                VideoPlayer.isPlayingState = false
            } else {
                binding.fullscreenVideoView.start()
                binding.videoControls.btnPlayPause.text = "||"
                VideoPlayer.isPlayingState = true
                startSeekBarUpdate()
            }
        }

        binding.videoControls.videoSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    binding.fullscreenVideoView.seekTo(progress)
                    VideoPlayer.currentPosition = progress
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        binding.videoControls.volumeSeekBar.max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        binding.videoControls.volumeSeekBar.progress = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        
        binding.videoControls.volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)
                    VideoPlayer.currentVolume = progress
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.videoControls.btnFullScreen.text = "⛶"
        binding.videoControls.btnFullScreen.setOnClickListener {
            syncAndExit()
        }
    }

    private fun startSeekBarUpdate() {
        val runnable = object : Runnable {
            override fun run() {
                if (binding.fullscreenVideoView.isPlaying) {
                    val pos = binding.fullscreenVideoView.currentPosition
                    binding.videoControls.videoSeekBar.progress = pos
                    VideoPlayer.currentPosition = pos
                    updateHandler.postDelayed(this, 1000)
                }
            }
        }
        updateHandler.post(runnable)
    }

    private fun syncAndExit() {
        VideoPlayer.currentPosition = binding.fullscreenVideoView.currentPosition
        VideoPlayer.isPlayingState = binding.fullscreenVideoView.isPlaying
        finish()
    }

    override fun onBackPressed() {
        syncAndExit()
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        updateHandler.removeCallbacksAndMessages(null)
    }
}
