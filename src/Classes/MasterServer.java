package Classes;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import Exceptions.MasterServiceException;

public class MasterServer {
	
	public MasterServer() {
		
	}
	
	public void startService() throws MasterServiceException, IOException {
		System.out.println("Master Service Started");
		
		ServerService ss = new ServerService();
		ss.start();

		// setup for read command from standard input
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(System.in));
		String input = "";
		String[] args = null;
		
		while(true) {
			System.out.print(":-) ");
			input = stdInput.readLine();
			args = input.split(" ");
			if (args.length == 0)
				continue;
			else if (args[0].equals("quit"))
				System.exit(1);
		}
	}


	private class ServerService extends Thread {
		
		private ServerSocket socketListener= null;
		
		public ServerService () {
			try {
				socketListener = new ServerSocket(ProcessManager.MASTER_PORT);
			} catch (IOException e) {
				System.err.println("Fail to open socket during master server init.");
			}
		}
		
		public void run() {
			while (true) {
				try {
					Socket socketServing = socketListener.accept();
					System.out.println("Socket accepted from " + socketServing.getInetAddress() + " " + socketServing.getPort());
					BufferedReader in = new BufferedReader(new InputStreamReader(socketServing.getInputStream()));
					PrintWriter out = new PrintWriter(new OutputStreamWriter(socketServing.getOutputStream()));
					
					out.write("TestMigratableProcess in.txt out.txt\n");
					out.flush();
					
				} catch (IOException e) {
					System.err.println("Fail to accept slave server request.");
				}
			}
		}
	}
}
