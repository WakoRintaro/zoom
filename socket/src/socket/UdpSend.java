package socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UdpSend {
	private DatagramSocket socket;
	private InetAddress dst_address;
	private int dst_port;
	
	public UdpSend(String dst_address_str, int port) {
		dst_port = port;
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			dst_address = InetAddress.getByName(dst_address_str);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void sendPacket(byte[] buf) {
		DatagramPacket packet = new DatagramPacket(buf, buf.length, dst_address, dst_port );
		try {
			socket.send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void close() {
		socket.close();
	}

	public static void main(String[] args) {
		UdpSend udp_send = new UdpSend("localhost", 4445);
		String msg = "rintaro";
		udp_send.sendPacket(msg.getBytes());
		udp_send.close();
		
	}

}
