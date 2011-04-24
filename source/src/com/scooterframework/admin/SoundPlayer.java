/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.admin;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.util.Random;

import com.scooterframework.autoloader.CompileEvent;

/**
 * SoundPlayer class plays audio clips.
 * 
 * @author (Fei) John Chen
 *
 */
public class SoundPlayer implements Listener {
	private static SoundPlayer me;
	private static File[] successClips;
	private static File[] failureClips;
	
	static {
		String scooterHome = System.getProperty("scooter.home");
		String soundDir = scooterHome + File.separatorChar + "source" + 
				File.separatorChar + "sound";
		
		File successDir = new File(soundDir + File.separatorChar + "compileSuccess");
		successClips = successDir.listFiles();

		File failureDir = new File(soundDir + File.separatorChar + "compileFailure");
		failureClips = failureDir.listFiles();
	}
	
	private SoundPlayer() {
	}
	
	/**
	 * Returns the singleton instance of the <tt>EventsManager</tt>.
	 * 
	 * @return the singleton instance of the <tt>EventsManager</tt>.
	 */
	public static SoundPlayer getInstance() {
		if (me == null) me = new SoundPlayer();
		return me;
	}
	
	public void handleEvent(Event event) {
		if (event instanceof CompileEvent) {
			CompileEvent ce = (CompileEvent)event;
			if (ce.compileSuccess()) {
				playSoundRandom(successClips);
			}
			else {
				playSoundRandom(failureClips);
			}
		}
	}
	
	private void playSoundRandom(File[] soundClips) {
		if (soundClips == null || soundClips.length == 0) return;
		int index = (new Random()).nextInt(soundClips.length);
		audioPlay(soundClips[index]);
	}
	
	@SuppressWarnings("deprecation")
	private static void audioPlay(File audioFile) {
		try {
			AudioClip clip = Applet.newAudioClip(audioFile.toURL());
			clip.play();
			Thread.sleep(2000);
		} catch (Exception e) {
		}
	}
}
