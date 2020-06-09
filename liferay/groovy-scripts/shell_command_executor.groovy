import com.liferay.portal.kernel.util.OSDetector;

//----------------------------------------------------------
// Execute shell command - Groovy script  
//----------------------------------------------------------
//

String commandToExecute = "cd";


// Tested with Liferay 7.0 CE / DXP
// author: Simone Cinti - 25/05/2020
//---------------


try {
	_executeCmd(commandToExecute);
} catch (Exception ex) {
	ex.printStackTrace(out);
}


def _executeCmd(commandToExecute) throws Exception {
	boolean isWin = OSDetector.isWindows();
	ProcessBuilder processBuilder = new ProcessBuilder();

	if (isWin) {
		processBuilder.command("cmd.exe", "/c", commandToExecute);
	}
	else {
		processBuilder.command("bash", "-c", commandToExecute);
	}

	Process process = processBuilder.start();

	StringBuilder output = new StringBuilder();

	BufferedReader reader = new BufferedReader(
			new InputStreamReader(process.getInputStream()));

	String line = null;
	while ((line = reader.readLine()) != null) {
		output.append(line + "\n");
	}

	int exitCode = process.waitFor();
	if (exitCode == 0) {
		out.println("Result of command: '" + commandToExecute + "'\n");
		out.println(output);
	} else {
		out.println("Command execution for '" + commandToExecute + "' failed with exit code: " + exitCode);
	}
}