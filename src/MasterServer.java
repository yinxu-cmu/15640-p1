import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MasterServer {

	public MasterServer() {

	}

	public void startService() throws MasterServiceException, IOException,
			InterruptedException {
		System.out.println("Master Service Started");

		/*
		 * create a new thread, concurrently listen and accept slave servers'
		 * connection
		 */
		ListenerService ss = new ListenerService();
		ss.start();

		/* setup for read command from standard input */
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(
				System.in));
		String input = "";
		String[] args = null;

		/* prompt the user to enter instructions */
		while (true) {
			System.out.print(":-) ");
			input = stdInput.readLine();
			args = input.split(" ");

			// parse command line input
			if (args.length == 0)
				continue;
			else if (args[0].equals("quit"))
				System.exit(1);
			else if (args[0].equals("help"))
				System.out.println(helpMessage);
			else if (args[0].equals("hosts"))
				this.printSlaveList();
			else if (args[0].equals("process"))
				this.printProcessList();

			/*
			 * to start a new process running on a specific slave host, the
			 * syntax should be
			 * "start slavehostID someProcess inputFile outputFile"
			 */
			else if (args[0].equals("start") && args.length > 1) {

				/*
				 * check if the host ID number is valid
				 */
				int hostID = -1;
				try {
					hostID = Integer.parseInt(args[1]);
				} catch (Exception e) {
					System.err.println("Wrong slave host ID format");
					continue;
				}

				/* send the whole command from stdIN to slave server */
				if (hostID > -1 && hostID < this.slaveList.size()) {
					SlaveInfo slave = this.slaveList.get(hostID);
					slave.out.write(input + "\n");
					slave.out.flush();
				} else
					System.err.println("Wrong slave host ID");
			}

			/*
			 * to migrate a running process from one host to another, the syntax
			 * should be "migrate someProcessID hostSRC hostDES"
			 */
			else if (args[0].equals("migrate") && args.length > 1) {

				SlaveInfo slaveSrc = null;
				SlaveInfo slaveDes = null;

				/*
				 * check if the host ID number is valid and get slave host
				 * information
				 */
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

				/* send the whole command from stdIN to slave server */
				slaveSrc.out.write("suspend " + args[1] + " " + args[2] + " "
						+ args[3] + "\n");
				slaveSrc.out.flush();

				try {
					/*
					 * get the acknowledge back from slave host and send another
					 * command to another slave host
					 */
					String slaveReply = slaveSrc.in.readLine();
					if (slaveReply.equals("finish suspending")) {
						slaveDes.out.write("resume " + args[1] + " " + args[2]
								+ " " + args[3] + "\n");
						slaveDes.out.flush();
					} else {
						System.err.println("slave replied" + slaveReply);
						System.err
								.println("dumping object or acknowledge back error");
					}
				} catch (java.net.SocketTimeoutException e) {
					System.err
							.println("dumping object or acknowledge back timeout");
				}

			}

		}
	}

	private ArrayList<SlaveInfo> slaveList = new ArrayList<SlaveInfo>();
	private String helpMessage = "usage:\n"
			+ "\thelp: print this message\n"
			+ "\thosts: print slave hosts list\n"
			+ "\tprocess: print process list on every slave hsot\n"
			+ "\tstart <slavehostID> <someProcessName> <inputFile> <outputFile>\n"
			+ "\tmigrate <someProcessID> <hostSRC> <hostDES>\n";

	/*
	 * print out ip address and port number of the slave hosts
	 */
	private void printSlaveList() {
		synchronized (slaveList) {
			int numSlaves = this.slaveList.size();
			for (int i = 0; i < numSlaves; ++i)
				System.out.println("#" + i + this.slaveList.get(i).toString());
		}
	}

	/*
	 * print out all the processes along with their name and status from all
	 * slave hosts
	 */
	private void printProcessList() throws IOException {
		synchronized (slaveList) {
			int numSlaves = this.slaveList.size();
			for (int i = 0; i < numSlaves; ++i) {
				System.out.println("#" + i + this.slaveList.get(i).toString());

				/* tell the slave to send back its process list */
				PrintWriter out = this.slaveList.get(i).out;
				out.write("processlist\n");
				out.flush();

				/* read from slave and print out the process list */
				BufferedReader in = this.slaveList.get(i).in;
				String slaveReply = null;
				while ((slaveReply = in.readLine()) != null) {
					if (slaveReply.equals("process list finish"))
						break;
					System.out.println("\t" + slaveReply);
				}

			}
		}
	}

	private class ListenerService extends Thread {

		private ServerSocket socketListener = null;

		public ListenerService() {
			try {
				socketListener = new ServerSocket(ProcessManager.MASTER_PORT);
			} catch (IOException e) {
				System.err
						.println("Fail to open socket during master server init.");
			}
		}

		/*
		 * keep accept connections from slave servers by listening to the
		 * listening socket
		 */
		public void run() {
			while (true) {
				try {
					Socket socketServing = socketListener.accept();
					socketServing.setSoTimeout(5 * 1000);
					BufferedReader in = new BufferedReader(
							new InputStreamReader(
									socketServing.getInputStream()));
					PrintWriter out = new PrintWriter(new OutputStreamWriter(
							socketServing.getOutputStream()));

					/* save all the information into the list for future use */
					SlaveInfo slaveInfo = new SlaveInfo();
					slaveInfo.iaddr = socketServing.getInetAddress();
					slaveInfo.port = socketServing.getPort();
					slaveInfo.in = in;
					slaveInfo.out = out;
					synchronized (slaveList) {
						slaveList.add(slaveInfo);
					}

					System.out.println("Socket accepted from "
							+ socketServing.getInetAddress() + " "
							+ socketServing.getPort());
				} catch (IOException e) {
					System.err.println("Fail to accept slave server request.");
				}
			}
		}
	}
}
