package Classes;

public class ProcessManager {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (args.length > 0)
			System.out.println("arg[0] is:" + args[0]);
		
		TestMigratableProcess p = new TestMigratableProcess();
		p.run();
	}

}
