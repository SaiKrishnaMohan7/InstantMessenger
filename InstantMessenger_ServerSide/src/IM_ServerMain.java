
import javax.swing.JFrame;

public class IM_ServerMain {

	public static void main(String[] args) {
		IM_Server msk = new IM_Server();
		msk.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		msk.startRunning();

	}

}
