package Classes;

public class TestMigratableProcess implements MigratableProcess{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1241500530790605595L;
	private volatile boolean suspending = false;
	
	public String toString() {
		return "";
	}
	
	public void run() {
		int i = 0;
		while(!suspending) {
			System.out.println(i++);
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
