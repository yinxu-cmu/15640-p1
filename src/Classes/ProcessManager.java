package Classes;

import java.io.IOException;

import Exceptions.*;

public class ProcessManager {
	
	public static final int MASTER_PORT = 62742;
	public static final int SLAVE_PORT = 62743;

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// start master server
		if (args.length == 0) {
			MasterServer ms = new MasterServer();
			try {
				ms.startService();
			} catch (MasterServiceException e) {
				System.err.println("Master Serivce Ended with Exception");
			}
			System.out.println("Master Serivce Ended");
//			System.exit(0);
		} 
		
		// start slave server
		else if (args.length == 2 && args[0].equals("-c")) {
			SlaveServer ss = new SlaveServer();
			try {
				ss.startService();
			} catch (SlaveServiceException e) {
				System.err.println("Slave Serivce Ended with Exception");
			}
			System.out.println("Slave Serivce Ended");
		}
		
		else {
			System.out.println("Usage: java ProcessManager [-c <master hostname>]");
		}
	}

}
