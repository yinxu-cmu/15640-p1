package Classes;

import java.io.*;

public class TestMigratableProcess implements MigratableProcess{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1241500530790605595L;
	private String inputFile;
	private String outputFile = "out.txt";
	private volatile boolean suspending = false;
	
	public TestMigratableProcess() {
		
	}
	
	public TestMigratableProcess(String[] args) {
		this.inputFile = args[0];
		this.outputFile = args[1];
	}
	
	public String toString() {
		return "";
	}
	
	public void run() {
		System.out.println("running process migratable test");
		DataOutputStream out = null;
	    try {
			 out = new DataOutputStream(new FileOutputStream(outputFile));
	    } catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    
		int i = 0;
		while(!suspending) {
			
//			try {
//				out.writeBytes("" + ++i + "\n");
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			System.out.println(++i);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	public void suspend() {
		suspending = true;
	}

}
