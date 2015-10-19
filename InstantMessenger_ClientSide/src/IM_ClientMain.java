import javax.swing.JFrame;


public class IM_ClientMain {

	public static void main(String[] args) {
		IM_Client msk_c = new IM_Client("127.0.0.1");//the server program is on th SAME machine as the client; its the address of the localhost
		msk_c.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		msk_c.startRunning();
	}

}
