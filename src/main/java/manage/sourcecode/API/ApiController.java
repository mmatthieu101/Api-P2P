package manage.sourcecode.API;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Handles API requests
 * 
 * @author Matthieu MERRHEIM
 */
@Controller
@RequestMapping("/*")
public class ApiController {

	public static final String PATH_ROOT_FOLDER = "/opt/gitrepo/";
	public static final String PATH_SHELL_FILES  = "/root/workspaceJavaEEEclipse/Api-P2P-matth/shell/";
	
	/**
	 * Exec commit command
	 * 
	 * @return String
	 * @throws IOException
	 */
	@RequestMapping(value = "commit", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody String command_commit() throws IOException {
		String nameScriptFolder = PATH_SHELL_FILES;
		String nameScriptFile = "gitadd.sh";
		String commentaire = "commentaire";
		
		String[] cmd = { "/bin/bash", "-c", "cd " + nameScriptFolder + " && /bin/bash " + nameScriptFile + " " + commentaire +" 2> /opt/gitrepo/error.txt" };
		
		System.out.println("-->"+Arrays.toString(cmd));
		
		String result = this._displayOutput(cmd);
		
		return result;
	}

	public String _displayOutput(String[] cmd) {
		String result = "";
		Runtime runtime = Runtime.getRuntime();
		try {
			Process process = runtime.exec(cmd);

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				System.out.println(line);
				result += line;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	//HELPERS
	
	/**
	 * Check if directory exist
	 * @param nameFile
	 * @return
	 */
	@RequestMapping(value = "fileexist/{nameFile}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody String command_fileIsExist(@PathVariable String nameFile) {
		
		System.out.println("ENTRE FILEISEXIST");
		
		String nameScriptFolder = PATH_SHELL_FILES;
		String nameScriptFile = "fileexist.sh";
		String param = "/home/device/"+nameFile; // name of the folder
		
		String value = this._shellCommand("if test -d /home/device/123456; then echo 'exist'; fi");

		String[] cmd = { "/bin/bash", "-c", "cd " + nameScriptFolder + " && /bin/bash " + nameScriptFile + " " + param };
		
		String result = "";
		Runtime runtime = Runtime.getRuntime();
		try {
			Process process = runtime.exec(cmd);

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				System.out.println(line);
				result += line;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public boolean _isFolderExist(String pathFolder, String nameFile) {
		
		String nameScriptFolder = PATH_SHELL_FILES;
		String nameScriptFile = "fileexist.sh";
		String param = pathFolder + nameFile; // name of the folder
		
		String value = this._shellCommand("if test -d /home/device/123456; then echo 'exist'; fi");

		String[] cmd = { "/bin/bash", "-c", "cd " + nameScriptFolder + " && /bin/bash " + nameScriptFile + " " + param };
		
		String result = "";
		Runtime runtime = Runtime.getRuntime();
		try {
			Process process = runtime.exec(cmd);

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				System.out.println(line);
				result += line;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(result.equals("exist")) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * exec shell command line
	 * 
	 * @param command
	 * @return String
	 */
	public String _shellCommand(String command) {
		String s = "";
		Process p;
		String value = "";

		try {
			p = Runtime.getRuntime().exec(command);
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((s = br.readLine()) != null) {
				System.out.println(s);
				value += s + "\n";
			}

			p.waitFor();
			System.out.println("exit: " + p.exitValue());
			p.destroy();
		} catch (Exception e) {
			System.out.println("ERROR!");
		}

		return value;
	}
}