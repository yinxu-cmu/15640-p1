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
			
			// syntax should be "migrate hostSRC hostDES someProcessID"
			else if (args[0].equals("migrate") && args.length > 1) {
				// this is hard coding
				SlaveInfo slaveSrc = this.slaveList.get(0);
				SlaveInfo slaveDes = this.slaveList.get(1);
				
				slaveSrc.out.write("suspend\n");
				slaveSrc.out.flush();
				
				Thread.sleep(1500);
				
				slaveDes.out.write("resume\n");
				slaveDes.out.flush();
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
					BufferedReader in = new BufferedReader(new InputStreamReader(socketServing.getInputStream()));
					PrintWriter out = new PrintWriter(new OutputStreamWriter(socketServing.getOutputStream()));
					
					SlaveInfo slaveInfo = new SlaveInfo();
					slaveInfo.iaddr = socketServing.getInetAddress();
					slaveInfo.port = socketServing.getPort();
					slaveInfo.in = in;
					slaveInfo.out = out;
					slaveList.add(slaveInfo);
					
					System.out.println("Socket accepted from " + socketServing.getInetAddress() + " " + socketServing.getPort());
//					out.write("TestMigratableProcess in.txt out.txt\n");
//					out.flush();
					
				} catch (IOException e) {
					System.err.println("Fail to accept slave server request.");
				}
			}
		}
		
		// do we really need this to listem from slave servers
		private class ServingService extends Thread {
			
			public void run() {
				
			}
		}
	}
}
