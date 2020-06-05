import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

//----------------------------------------------------------
// GoGo Shell Client - Groovy script  
//----------------------------------------------------------
//  This Groovy script allows you to connect to a GoGo shell session, send one or more commands and read the result from output.
//  This could be useful to bypass access restrictions to shell sessions from the target machine.
//

String hostname = "localhost";
int port = 11311;
String[] commandsToExecute = [ "lb", "diag" ];

//
// Tested with Liferay 7.0 CE / DXP
// author: Simone Cinti - 05/06/2020
//----------------------------------------------------------

_executeGogoShell(hostname, port, commandsToExecute);


	void _executeGogoShell(String hostname, int port, String[] commands) {
		Socket socket = null;
		OutputStream outStream = null;
		DataOutputStream outToServer = null;
		try {
			out.println("connecting to: " + hostname + " port: " + port);
			socket = new Socket("localhost", port);
			outStream = socket.getOutputStream();
			outToServer = new DataOutputStream(outStream);
			InputStream inFromServer = socket.getInputStream();
			out.print("connection established.\n\n");

			initTelnetSession(inFromServer, outToServer);

			_readAndPrint(inFromServer, out);

			for (String command : commands) {

				_writeCommand(outToServer, command);

				_readAndPrint(inFromServer, out);

			}

			_writeCommand(outToServer, "disconnect");

			_readAndPrint(inFromServer, out);

			_writeCommand(outToServer, "y");

			_readAndPrint(inFromServer, out);

			outToServer.close();
			socket.close();
		} catch (Exception ex) {
			ex.printStackTrace(out);
		}
	}

	void _write(DataOutputStream outputStream, byte[] bytes, boolean flush) throws Exception {
		outputStream.write(bytes, 0, bytes.length);
		if (flush) {
			outputStream.flush();
		}
	}

	void _writeCommand(DataOutputStream outputStream, String command) throws Exception {
		byte[] bytes = command.getBytes();
		for (int i = 0; i < bytes.length; i++) {
			outputStream.write(bytes, i, 1);
			outputStream.flush();
		}
		_write(outputStream, "\r\n".getBytes(), true);
	}

	void _readAndPrint(inputStream, printStream) throws Exception {
		String r = _readWithRetries(inputStream, 5, 20);
		if (!r.isEmpty()) {
			printStream.print(r);
		}
		printStream.flush();
	}

	String _readWithRetries(InputStream inputStream, long delayInMillis, int nretries) throws Exception {
		long cretries = nretries;
		String r = "";
		StringBuffer result = new StringBuffer();
		while (!r.isEmpty() || cretries > 0L) {
			r = _read(inputStream);
			if (!r.isEmpty()) {
				result.append(r);
				cretries = nretries;
			} else {
				Thread.sleep(delayInMillis);
				cretries--;
			}
		}
		return result.toString();
	}

	String _read(InputStream inputStream) throws Exception {
		StringBuffer sb = new StringBuffer();
		int i = -1;
		boolean isTelnetControlCommand = false;
		while (inputStream.available() > 0 && (i = inputStream.read()) != -1) {
			isTelnetControlCommand = isTelnetControlCommand || (i == 255);
			if (!isTelnetControlCommand) {
				sb.append(String.valueOf((char) i));
			}
		}
		return sb.toString();
	}

	void initTelnetSession(InputStream inFromServer, DataOutputStream outToServer) throws Exception {
		byte[] bytes = null;

		bytes = [0xff, 0xfd, 0x01];
		_write(outToServer, bytes, true);
		Thread.sleep(1);

		bytes =[ 0xff, 0xfd, 0x03 ];
		_write(outToServer, bytes, true);
		Thread.sleep(1);

		bytes = [ 0xff, 0xfb, 0x1f ];
		_write(outToServer, bytes, true);
		Thread.sleep(1);

		bytes = [ 0xff, 0xfa, 0x1f, 0x00, 0x78, 0x00, 0x1e, 0xff, 0xf0 ];
		_write(outToServer, bytes, true);
		Thread.sleep(1);

		bytes = [ 0xff, 0xfb, 0x18 ];
		_write(outToServer, bytes, true);
		Thread.sleep(1);

		bytes = [ 0xff, 0xfa, 0x18, 0x00, 0x41, 0x4e, 0x53, 0x49, 0xff, 0xf0 ];
		_write(outToServer, bytes, true);
		Thread.sleep(1);
	}
