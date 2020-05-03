package socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UdpRecv extends Thread{
	private DatagramSocket socket;
	private byte[] buf = new byte[900];
	
	public UdpRecv(int port) {
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run() {
		System.out.println("[*] Start Waiting");
		while(true) {
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String received = new String(packet.getData(), 0, packet.getLength());
			System.out.println(received);
			if(received.equals("bye")) break;
		}
		socket.close();
		System.out.println("[*] End");
	}
	public static void main(String[] args) {
		UdpRecv udp_recv = new UdpRecv(4445);
		udp_recv.start();
	}

	

}
