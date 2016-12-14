package manage.sourcecode.API;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


/**
 * Handles jsp pages
 * 
 * @author Matthieu MERRHEIM
 */
@Controller
public class ClientController {

	@RequestMapping("/homepage")
	String homePage() {
		return "homepage";
	}

	@RequestMapping(value = "createrepository3", method = RequestMethod.POST)
	public String command_createrepository3(HttpServletRequest request) {
		String name = request.getParameter("name");
		System.out.println("ENTRE:" + name);
		String value = "";
		String s = "";
		Process p;

		try {
			p = Runtime.getRuntime().exec("mkdir -m 777 /home/device/P2P-PROJECT/" + name);
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
		return "redirect:/homepage";
	}

	@RequestMapping(value = "repositories", method = RequestMethod.GET)
	public String repositories(HttpServletRequest request, Model model) {

		String value = "";
		String s = "";
		Process p;

		JSONArray files;
		JSONObject json = new JSONObject();

		JSONArray filesArray = new JSONArray();
		JSONObject file1 = new JSONObject();

		try {
			p = Runtime.getRuntime().exec("ls /home/device/P2P-PROJECT/");
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((s = br.readLine()) != null) {
				file1 = new JSONObject();
				file1.put("name", s);
				filesArray.add(file1);
				System.out.println(s);
				value += s + "\n";
			}

			p.waitFor();
			System.out.println("exit: " + p.exitValue());
			p.destroy();
		} catch (Exception e) {
			System.out.println("ERROR!");
		}

		files = filesArray;

		System.out.println("--->" + files.toString());

		model.addAttribute("repositories", files);
		return "repositories";
	}

	@RequestMapping(value = "show/{name}", method = RequestMethod.GET)
	public String repository(HttpServletRequest request, Model model, @PathVariable String name) {
		System.out.println("-->" + name);
		
		String value = "";
		String s = "";
		Process p;

		JSONArray files;
		JSONObject json = new JSONObject();

		JSONArray filesArray = new JSONArray();
		JSONObject file1 = new JSONObject();

		try {
			p = Runtime.getRuntime().exec("ls /home/device/P2P-PROJECT/"+name);
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((s = br.readLine()) != null) {
				file1 = new JSONObject();
				file1.put("name", s);
				filesArray.add(file1);
				System.out.println(s);
				value += s + "\n";
			}

			p.waitFor();
			System.out.println("exit: " + p.exitValue());
			p.destroy();
		} catch (Exception e) {
			System.out.println("ERROR!");
		}
		System.out.println("-1->"+value);
		
		files = filesArray;

		System.out.println("--->" + files.toString());

		model.addAttribute("repository", files);
		return "repository";
	}
	
	public @ResponseBody String isFolder() {
		
		String nameScriptFolder = "/home/device/p2p";
		String nameScriptFile = "isFileOrFolder.sh";
		String path = "commentaire";
		
		String[] cmd = { "/bin/bash", "-c", "cd " + nameScriptFolder + " && /bin/bash " + nameScriptFile + " " + path };
		
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

	public boolean isFile() {
		return false;
	}
	

	@RequestMapping(value = "/upload", method = RequestMethod.GET)
	public String upload(Model model) {
		return "upload";
	}
	
	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	public String uploadFile(@RequestParam("file") MultipartFile file,
			Model model) {
		System.out.println("OriginalFilename : "+ file.getOriginalFilename());

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("filename", file.getOriginalFilename());
		
		String tmpDir = System.getProperty("java.io.tmpdir");
		File fichierLocal = new File(tmpDir + file.getOriginalFilename());
		try {
			//on met le fichier uploader dans le dossier temp
			file.transferTo(fichierLocal);
			
			System.out.println(fichierLocal.getAbsolutePath() +" ? "+ fichierLocal.exists());
			
			model.addAttribute("pathLocal", fichierLocal.getAbsolutePath());
			//TODO: git add + git commit + git push
			
		} catch (Exception e) {
			System.err.println("Impossible de sauvegarder le fichier. "+ e.getMessage());
		}
		
		return "upload";
	}
}