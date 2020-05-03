package socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Scanner;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;


public class Phone {
	// 通話用Thread th_talk：発話用、th_hear：受信用
	private Thread th_talk;
	private Thread th_hear;

	// Buffer
	byte[] buf_mic = new byte[800];

	// AudioFormat for Calling
	float sampleRate = 4000.0f; // 2000.0[Hz]
	int sampleSizeInBits = 16; // 16 bits / frame
	int channels = 1; // 1 for mono, 2 for stereo
	int frameSize = 2;
	float frameRate = 4000.0f;
	boolean bigEndian = false;

	private AudioFormat format_mic = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sampleRate, sampleSizeInBits,
			channels, frameSize, frameRate, bigEndian);

	// Info of Microphone and Speaker
	private DataLine.Info info_mic;
	private TargetDataLine line_mic;
	private DataLine.Info info_sp;
	private SourceDataLine line_sp;

	// UDP Socket
	private UdpSend udp_send;
	private DatagramSocket socket;
	
	private void prepareAudio() {
		// マイクの準備
		try {
			info_mic = new DataLine.Info(TargetDataLine.class, format_mic);
			line_mic = (TargetDataLine) AudioSystem.getLine(info_mic);

			info_sp = new DataLine.Info(SourceDataLine.class, format_mic);
			line_sp = (SourceDataLine) AudioSystem.getLine(info_sp);

			line_mic.open();
			line_mic.start();
			
			line_sp.open();
			line_sp.start();
			
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!AudioSystem.isLineSupported(info_mic)) {
			System.err.println("Your AudioSystem doesn't support the format");
		}
	}
	public Phone(String dst_ip) {
		udp_send = new UdpSend(dst_ip, 4445);
	}

	public void call() {
		prepareAudio();
		th_talk = new Thread() {
			public void run() {
				AudioInputStream audioStream = new AudioInputStream(line_mic);
				try {
						while(true) {
							int size = audioStream.read(buf_mic, 0, buf_mic.length);
							if(size <= 0) break;
							udp_send.sendPacket(buf_mic);
						}
				} catch (IOException ioe) {
					// TODO Auto-generated catch block
					ioe.printStackTrace();
				}
			}
		};
		th_hear = new Thread() {
			@Override
			public void run() {
				try {
					socket  = new DatagramSocket(4445);
				} catch (SocketException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				byte[] buf = new byte[900];
				
				while(true) {
					DatagramPacket packet = new DatagramPacket(buf, buf.length);
					try {
						socket.receive(packet);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					line_sp.write(packet.getData(), 0, packet.getData().length);
				}
			}
		};
		
		th_talk.start();
		th_hear.start();
	}

	public void closeAudio() {
		line_mic.stop();
		line_mic.close();
		line_sp.drain();
		line_sp.stop();
		line_sp.close();
	}
	public static void main(String[] args) {
		System.out.println("なんちゃってZOOMへようこそ");
		Scanner scanner = new Scanner(System.in);
		// IPアドレスの入力
		System.out.print("通話相手のIPアドレス>>");
	    String dst_ip = scanner.nextLine();
	    scanner.close();
	    
		System.out.println("Phone Started");
		Phone ph = new Phone(dst_ip);
		ph.call();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ph.closeAudio();
		System.out.println("Phone Ended");
	}
}
