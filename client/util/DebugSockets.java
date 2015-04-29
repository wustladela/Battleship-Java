package battleship.client.util;

import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class DebugSockets {
	/**
	 * Launch window to track bytes
	 * @param s
	 * @return
	 */
	public static DataInputStream genIn(Socket s, final PrintStreamPanel viz) {
		try {
			final InputStream sockIn = s.getInputStream();
			return new DataInputStream(new InputStream() {

				@Override
				public int read() throws IOException {
					int ans = sockIn.read();
					viz.getPrintStream().println(debugByte(ans));
					return ans;
				}
				
			});
		} catch (IOException e) {
			error("Problem getting input stream " + e);
		}
		return null;  // unreachable
	}
	
	public static DataOutputStream genOut(final Socket s, final PrintStreamPanel viz) {
		try {
			final OutputStream sockOut = s.getOutputStream();
			return new DataOutputStream(new OutputStream() {

				@Override
				public void write(int b) throws IOException {
					viz.getPrintStream().println(debugByte(b));
					sockOut.write(b);
				}
				
			});
		} catch (IOException e) {
			error("Problem getting output stream " + e);
		}
		return null;  // unreachable
	}

	public static void error(String s) {
		throw new Error(s);
	}

	private static String debugByte(int b) {
		String ans = String.format(" %5d", b);
		char ch = (char)b;
		if (isPrintableChar(ch)) {
			ans += " " + ch;
		}
		return ans;
	}
	
	private static boolean isPrintableChar( char c ) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of( c );
        return (!Character.isISOControl(c)) &&
                c != KeyEvent.CHAR_UNDEFINED &&
                block != null &&
                block != Character.UnicodeBlock.SPECIALS;
    }
}
