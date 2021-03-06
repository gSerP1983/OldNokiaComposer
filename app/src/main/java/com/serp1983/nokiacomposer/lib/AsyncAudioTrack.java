package com.serp1983.nokiacomposer.lib;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class AsyncAudioTrack implements Runnable {
	private static AsyncAudioTrack instance;

	private byte[] _buffer;
	private AudioTrack _audioTrack;
	private Callback _callback;
	private int _bufferSize;
	public interface Callback {
		void onComplete();
	}

	private AsyncAudioTrack(byte[] buffer, Callback callback){
		_buffer = buffer;
		_callback = callback;

		try {
			_bufferSize = AudioTrack.getMinBufferSize(PCMConverter.SAMPLING_FREQUENCY,
					AudioFormat.CHANNEL_OUT_MONO,
					AudioFormat.ENCODING_PCM_16BIT);
		}
		catch(Exception e){
			e.printStackTrace();
			_bufferSize = 3528;
		}

		if (_bufferSize == -1)
			_bufferSize = 3528;
	}
	
	@Override
	public void run() {
		// double try
		try {
			runInner();
		}
		catch(Exception e){
			e.printStackTrace();
			delay(100);
			runInner();
		}

		if (_callback != null)
			_callback.onComplete();
	}

	private void runInner(){
		release();

		_audioTrack = new AudioTrack(
				AudioManager.STREAM_MUSIC, PCMConverter.SAMPLING_FREQUENCY,
				AudioFormat.CHANNEL_OUT_MONO,
				AudioFormat.ENCODING_PCM_16BIT,
				_bufferSize, AudioTrack.MODE_STREAM);

		_audioTrack.play();
		_audioTrack.write(_buffer, 0, _buffer.length);
	}

	private void release(){
		if (_audioTrack != null) {
			_audioTrack.release();
			_audioTrack = null;
			delay(250);
		}
	}
	
	public static void start(byte[] buffer, Callback callback){
		stop();
        ExecutorService executor = Executors.newFixedThreadPool(1);
        instance = new AsyncAudioTrack(buffer, callback);
        executor.execute(instance);
        executor.shutdown();
	}
	
	public static void stop(){
		if (instance != null)
			instance.release();
	}

	private static void delay(long millis){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
