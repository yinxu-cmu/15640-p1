package Classes;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import Exceptions.MasterServiceException;

public class MasterServer {
	
	public MasterServer() {
		
	}
	
	public void startService() throws MasterServiceException, IOException, InterruptedException {
		System.out.println("Master Service Started");
		
		// concurrently listen and accept slave servers' connection
		ListenerService ss = new ListenerService();
		ss.start();

		// setup for read command from standard input
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(System.in));
		String input = "";
		String[] args = null;
		
		// prompt the user to enter instructions
		while(true) {
			System.out.print(":-) ");
			input = stdInput.readLine();
			args = input.split(" ");
			
			// parse command line input
			if (args.length == 0)
				continue;
			else if (args[0].equals("quit"))
				System.exit(1);
			else if (args[0].equals("hosts"))
				this.printSlaveList();
			
			// syntax should be "start slavehostID someProcess inputFile outputFile"
			else if (args[0].equals("start") && args.length > 1) {
				int hostID = -1;
				try {
					hostID = Integer.parseInt(args[1]);
				} catch (Exception e) {
					System.err.println("Wrong slave host ID format");
					continue;
				}
				
				if (hostID > -1 && hostID < this.slaveList.size()) {
					SlaveInfo slave = this.slaveList.get(hostID);
					slave.out.write(input + "\n");
					slave.out.flush();
				} else 
					System.err.println("Wrong slave host ID");
			}
			
			// syntax should be "migrate someProcessID hostSRC hostDES"
			else if (args[0].equals("migrate") && args.length > 1) {
				
				SlaveInfo slaveSrc = null;
				SlaveInfo slaveDes = null;
				
				try {
					slaveSrc = this.slaveList.get(Integer.parseInt(args[2]));
					slaveDes = this.slaveList.get(Integer.parseInt(args[3]));
				} catch (NumberFormatException e) {
					System.err.println("wrong slave ID format");
					continue;
				}
				
				if (slaveSrc == null || slaveDes == null) {
					System.err.println("wrong slave ID");
					continue;
				}
				
				slaveSrc.out.write("suspend " + args[1] + " " + args[2] + " " + args[3] + "\n");
				slaveSrc.out.flush();
				
				try {
					String slavereply = slaveSrc.in.readLine();
					if (slavereply.equals("finish suspending")) {
						slaveDes.out.write("resume " + args[1] + " " + args[2] + " " + args[3] + "\n");
						slaveDes.out.flush();
					} else {
						System.err.println("slave replied" + slavereply);
						System.err.println("dumping object or acknowledge back error");
					}
				} catch (java.net.SocketTimeoutException e) {
					System.err.println("dumping object or acknowledge back timeout");
				}
				
			}
				
		}
	}

	
	private ArrayList<SlaveInfo> slaveList = new ArrayList<SlaveInfo>();

	private void printSlaveList() {
		int listSize = this.slaveList.size();
		for (int i = 0; i < listSize; ++i)
			System.out.println("#" + i + this.slaveList.get(i).toString());
	}
	
	private class ListenerService extends Thread {
		
		private ServerSocket socketListener= null;
		
		public ListenerService () {
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
					socketServing.setSoTimeout(5 * 1000);
					BufferedReader in = new BufferedReader(new InputStreamReader(socketServing.getInputStream()));
					PrintWriter out = new PrintWriter(new OutputStreamWriter(socketServing.getOutputStream()));
					
					SlaveInfo slaveInfo = new SlaveInfo();
					slaveInfo.iaddr = socketServing.getInetAddress();
					slaveInfo.port = socketServing.getPort();
					slaveInfo.in = in;
					slaveInfo.out = out;
					slaveList.add(slaveInfo);
					
					System.out.println("Socket accepted from " + socketServing.getInetAddress() + " " + socketServing.getPort());
				} catch (IOException e) {
					System.err.println("Fail to accept slave server request.");
				}
			}
		}
		
		// do we really need this to listen from slave servers
		private class ServingService extends Thread {
			
			public void run() {
				
			}
		}
	}
}
