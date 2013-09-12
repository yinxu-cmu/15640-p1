package Classes;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.InetAddress;

public class SlaveInfo {
	
		public InetAddress iaddr;
		public int port;
		public BufferedReader in;
		public PrintWriter out;
		
		public String toString() {
			return "\tInetAddress: " + iaddr +
					"\tport number: " + port;
		}
}
