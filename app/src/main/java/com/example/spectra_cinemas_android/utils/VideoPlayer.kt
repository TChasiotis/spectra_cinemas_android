package com.example.spectra_cinemas_android.utils

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.VideoView
import com.example.spectra_cinemas_android.activities.FullscreenVideoActivity
import com.example.spectra_cinemas_android.databinding.CustomVideoControlsBinding

object VideoPlayer {

    private var videoView: VideoView? = null
    private val updateHandler = Handler(Looper.getMainLooper())
    private var controlsBinding: CustomVideoControlsBinding? = null
    
    // Global State
    var currentResId: Int = 0
    var currentPosition: Int = 0
    var isPlayingState: Boolean = false
    var currentVolume: Int = -1

    fun attachPlayer(context: Context, container: ViewGroup, trailerResId: Int, inflater: LayoutInflater, forceStop: Boolean = false) {
        // Αν αλλάξει η ταινία ή ζητηθεί forceStop (π.χ. πρώτη φορά από toggle)
        if (currentResId != trailerResId || forceStop) {
            stop()
            currentResId = trailerResId
            currentPosition = 0
            isPlayingState = false
        }
        
        val videoContainer = FrameLayout(context)
        val vView = VideoView(context)
        videoView = vView
        
        val uri = Uri.parse("android.resource://${context.packageName}/$trailerResId")
        vView.setVideoURI(uri)

        controlsBinding = CustomVideoControlsBinding.inflate(inflater, videoContainer, false)
        
        videoContainer.addView(vView)
        videoContainer.addView(controlsBinding?.root)
        
        container.removeAllViews()
        container.addView(videoContainer)

        setupControls(context, vView)
    }

    private fun setupControls(context: Context, vView: VideoView) {
        val binding = controlsBinding ?: return

        binding.btnPlayPause.setOnClickListener {
            if (vView.isPlaying) {
                vView.pause()
                isPlayingState = false
                binding.btnPlayPause.text = "|>"
            } else {
                vView.start()
                isPlayingState = true
                binding.btnPlayPause.text = "||"
                startSeekBarUpdate()
            }
        }

        vView.setOnPreparedListener {
            binding.videoSeekBar.max = vView.duration
            vView.seekTo(currentPosition)
            binding.videoSeekBar.progress = currentPosition
            
            if (isPlayingState) {
                vView.start()
                binding.btnPlayPause.text = "||"
            } else {
                binding.btnPlayPause.text = "|>"
            }
            startSeekBarUpdate()
        }

        binding.videoSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    vView.seekTo(progress)
                    currentPosition = progress
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        binding.volumeSeekBar.max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        
        if (currentVolume == -1) {
            currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        }
        binding.volumeSeekBar.progress = currentVolume
        
        binding.volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    currentVolume = progress
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.btnFullScreen.setOnClickListener {
            // Αποθήκευση πριν την έξοδο
            currentPosition = vView.currentPosition
            isPlayingState = vView.isPlaying
            
            val intent = Intent(context, FullscreenVideoActivity::class.java).apply {
                putExtra("TRAILER_RES_ID", currentResId)
                putExtra("CURRENT_POSITION", currentPosition)
                putExtra("IS_PLAYING", isPlayingState)
                putExtra("VOLUME_LEVEL", currentVolume)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    private fun startSeekBarUpdate() {
        updateHandler.removeCallbacksAndMessages(null)
        val runnable = object : Runnable {
            override fun run() {
                videoView?.let { vView ->
                    if (vView.isPlaying) {
                        currentPosition = vView.currentPosition
                        controlsBinding?.videoSeekBar?.progress = currentPosition
                        updateHandler.postDelayed(this, 1000)
                    }
                }
            }
        }
        updateHandler.post(runnable)
    }

    fun syncFromFullscreen(position: Int, isPlaying: Boolean, volume: Int) {
        currentPosition = position
        isPlayingState = isPlaying
        currentVolume = volume
    }

    fun stop() {
        videoView?.stopPlayback()
        videoView = null
        updateHandler.removeCallbacksAndMessages(null)
        controlsBinding = null
        currentResId = 0
        currentPosition = 0
        isPlayingState = false
        currentVolume = -1
    }
}
