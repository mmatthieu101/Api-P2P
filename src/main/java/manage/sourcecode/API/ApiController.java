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
	public static final String PATH_SHELL_FILES  = "/root/workspaceJavaEEEclipse/spring_mygithub/shell/";
	
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
		
		String[] cmd = { "/bin/bash", "-c", "cd " + nameScriptFolder + " && /bin/bash " + nameScriptFile + " " + commentaire };
		
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
	 * Create root directory
	 * @return
	 */
	public String _createRootDirectory() {
		//String value = this._shellCommand("mkdir -m 777 "+PATH_ROOT_FOLDER+ nameDirectory);
		String nameRootFolder = "P2P-PROJECT";
		
		String result = "";
		if(!_isFolderExist("/home/device/", "P2P-PROJECT")) {
			result = this._shellCommand("mkdir -m 777 /home/device/" + nameRootFolder);
		}
		
		return result;
	}
	
	// Check if directory exist
	// Display file content
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
	 * Get output of mkdir command
	 * 
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "mkdir", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody String command_mkdir() throws IOException {
		String value = this._shellCommand("mkdir -m 777 /home/device/123456");

		return value;
	}

	@RequestMapping(value = "test", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody String command_test() throws IOException, InterruptedException {
		String s = "";
		String[] cmd = { "/bin/sh", "-c", "cd "+PATH_SHELL_FILES+" && /bin/bash gitadd.sh" };

		Process p = Runtime.getRuntime().exec(cmd);
		System.out.println("FIN");

		String value = "";

		try {
			p = Runtime.getRuntime().exec(cmd);
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((s = br.readLine()) != null) {
				value += s + "\n";
				System.out.println("-->" + s);
			}

			p.waitFor();
			System.out.println("exit: " + p.exitValue());
			p.destroy();
		} catch (Exception e) {
		}

		return value;
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

	/*
	 * String files; JSONObject json = new JSONObject();
	 * 
	 * JSONArray filesArray = new JSONArray(); JSONObject file1 = new
	 * JSONObject(); file1.put("name", 1); filesArray.add(file1);
	 * 
	 * //JSONObject file2 = new JSONObject(); //file2.put("name", 2);
	 * //array.add(file2);
	 * 
	 * json.put("folders", filesArray);
	 * 
	 * files = json.toString();
	 */

}