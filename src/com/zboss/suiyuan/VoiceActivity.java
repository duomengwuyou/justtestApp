package com.zboss.suiyuan;

import com.zboss.suiyuan.voice.AudioRecordButton;
import com.zboss.suiyuan.voice.AudioRecordButton.AudioFinishRecorderListener;
import com.zboss.suiyuan.voice.MediaManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class VoiceActivity extends Activity {
	AudioRecordButton button;
	
	public static final String KEY_VOICE_PATH = "voice_path";
	public static final String KEY_VOICE_SECONDS = "voice_seconds";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.voice_main);

		
		button = (AudioRecordButton) findViewById(R.id.recordButton);
		button.setAudioFinishRecorderListener(new AudioFinishRecorderListener() {
			@Override
			public void onFinished(float seconds, String filePath) {
			    Intent lastIntent = new Intent();
			    lastIntent.putExtra(KEY_VOICE_PATH, filePath);
			    lastIntent.putExtra(KEY_VOICE_SECONDS, seconds);
	            setResult(Activity.RESULT_OK, lastIntent);
	            finish();
			}
		});
		
	}

		
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MediaManager.pause();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MediaManager.resume();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		MediaManager.release();
	}
}
