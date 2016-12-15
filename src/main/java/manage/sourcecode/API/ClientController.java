package manage.sourcecode.API;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Handles jsp pages
 * 
 * @author Matthieu MERRHEIM
 */
@Controller
public class ClientController {

	public static final String PATH_ROOT_FOLDER = "/opt/gitrepo/";
	public static final String PATH_SHELL_FILES  = "/root/workspaceJavaEEEclipse/spring_mygithub/shell/";
	
	@RequestMapping("/homepage")
	String homePage() {
		return "homepage";
	}

	/**
	 * Create a repository
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "create/repository", method = RequestMethod.POST)
	public String command_createrepository3(HttpServletRequest request) {
		String repositoryName = request.getParameter("repositoryName");

		System.out.println("ENTRE:" + repositoryName);
		String value = "";
		String s = "";
		Process p;
		
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();

		try {
			p = Runtime.getRuntime().exec("mkdir -m 777 "+ PATH_ROOT_FOLDER + repositoryName);
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((s = br.readLine()) != null) {
				System.out.println(s);
				value += s + "\n";
			}

			p.waitFor();
			System.out.println("exit: " + p.exitValue());
			p.destroy();
		} catch (Exception e) {
			System.out.println("Erreur lors de la création du repository");
		}

		// return JSON
		jsonObject.put("message", "Repository créé");
		
		jsonArray.add(jsonObject);
		
		return jsonArray.toJSONString();
		//return "redirect:/homepage";
	}

	/**
	 * Show repositories list
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "show/repositories", method = RequestMethod.GET)
	public String repositories(HttpServletRequest request, Model model) {

		String repositoryName = "";

		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();

		try {
			Process p = Runtime.getRuntime().exec("ls "+PATH_ROOT_FOLDER);
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((repositoryName = br.readLine()) != null) {
				jsonObject = new JSONObject();
				jsonObject.put("name", repositoryName);
				jsonObject.put("path", PATH_ROOT_FOLDER + repositoryName);
				jsonArray.add(jsonObject);
			}

			p.waitFor();
			System.out.println("exit: " + p.exitValue());
			p.destroy();
		} catch (Exception e) {
			System.out.println("Error in repositories function");
		}

		model.addAttribute("repositories", jsonArray);
		return "repositories";
	}

	/**
	 * Show repository
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "show/repository", method = RequestMethod.POST)
	public String repository(HttpServletRequest request, Model model) {
		System.out.println("Entre dans show/repository");

		String path = request.getParameter("path");
		String repositoryName = request.getParameter("name");

		String value = "";
		String nameFileOrFolder = "";
		Process p;

		JSONArray files;
		JSONObject jsonObject = new JSONObject();

		JSONArray jsonArray = new JSONArray();

		try {
			p = Runtime.getRuntime().exec("ls " + path);
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((nameFileOrFolder = br.readLine()) != null) {
				jsonObject = new JSONObject();
				jsonObject.put("name", nameFileOrFolder);
				String pathFileOrFolder = path + "/" + nameFileOrFolder;
				jsonObject.put("path", pathFileOrFolder);

				// check if is folder or is file
				String type = this._isFileOrFolder(pathFileOrFolder);

				// if file display
				if (type.equals("folder")) {
					jsonObject.put("type", "folder");

				} else if (type.equals("file")) {
					String extension = this._getExtension(nameFileOrFolder);
					System.out.println("EXT:"+extension);
					jsonObject.put("type", extension);
				}

				jsonArray.add(jsonObject);
			}

			p.waitFor();
			System.out.println("exit: " + p.exitValue());
			p.destroy();
		} catch (Exception e) {
			System.out.println("Erreur dans repository function");
		}

		//return jsonArray.toJSONString();
		
		model.addAttribute("repository", jsonArray);

		return "repository";
	}

	/**
	 * Show file
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/show/file", method = RequestMethod.POST)
	public String showfile(HttpServletRequest request, Model model) {

		String path = request.getParameter("path");
		String name = request.getParameter("name");
		String content = "";
		
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();

		content = this._catFile(path);

		//jsonObject.put("content", content);

		//jsonArray.add(jsonObject);
		
		//return jsonArray.toJSONString(
		
		model.addAttribute("contentFile", content);

		return "contentfile";
	}
	
	public String _getExtension(String fileName) {
		String extension = "";

		int i = fileName.lastIndexOf('.');
		if (i >= 0) {
		    extension = fileName.substring(i+1);
		}
		
		return extension;
	}

	/**
	 * Check if path to file or folder 
	 * @param path
	 * @return
	 */
	public @ResponseBody String _isFileOrFolder(String path) {

		String nameScriptFolder = PATH_SHELL_FILES;
		String nameScriptFile = "isFileOrFolder.sh";
		String pathParam = path;

		String[] cmd = { "/bin/bash", "-c",
				"cd " + nameScriptFolder + " && /bin/bash " + nameScriptFile + " " + pathParam };

		String result = this._displayOutput(cmd);

		return result;
	}

	/**
	 * Cat file
	 * @param path
	 * @return
	 */
	public @ResponseBody String _catFile(String path) {
		String nameScriptFolder = PATH_SHELL_FILES;
		String nameScriptFile = "cat.sh";
		String pathParam = path;

		String[] cmd = { "/bin/bash", "-c",
				"cd " + nameScriptFolder + " && /bin/bash " + nameScriptFile + " " + pathParam };

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
				System.out.println("Entre");
				System.out.println(line);
				result += line;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	//PART2
	


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
	
	@RequestMapping(value = "/editFile")
	public String editFile(@RequestParam(value="fileSelected", required = false) String fileSelected,
			HttpServletRequest request,
			Model model) {
		if(fileSelected == null || fileSelected.isEmpty()){
			//il faut selectionner un fichier
			
			List<File> files = new ArrayList<File>();
			files.add(new File("C:\\Users\\Jordane\\AppData\\Local\\Temp\\DebianInstallation.txt"));
			
			model.addAttribute("allFiles", files);
			
			return "editFile";
		}else{
			//fichier selectionner, on peut editer le contenu
			
			request.setAttribute("fileSelected", fileSelected);
			return "forward:/editContent";
		}
	}

	@RequestMapping(value = "/editContent")
	public String editContent(@RequestParam(value="content", required = false) String content,
			@RequestParam(value="fileSelected", required = false) String fileSelected,
			HttpServletRequest request,
			Model model) {
		
		if(fileSelected == null || fileSelected.isEmpty()){
			//pas de fichier selectionné
			return "redirect:/editFile";
		}
		
		//TODO: lire le contenu reel du fichier (syntaxe : cat fichier.txt)
		String contenu = "azertyuiop";
		model.addAttribute("content", contenu);
		//request.setAttribute("content", contenu);
		
		//fichier selectionne mais pas de contenu maj pour le moment
		request.setAttribute("fileSelected", fileSelected); //on garde le fichier precedemment choisi			
		
		
		return "editContent";
	}
	


	@RequestMapping(value = "/edit")
	public String edit(@RequestParam(value="content", required = false) String content,
			@RequestParam(value="fileSelected", required = false) String fileSelected,
			HttpServletRequest request,
			Model model) {
		
		if(fileSelected == null || fileSelected.isEmpty()){
			//pas de fichier selectionné
			return "redirect:/editFile";
		}
		
		if(content == null || content.isEmpty()){
			//pas de contenu
			return "redirect:/editContent";
		}
		
		// TODO : mettre le contenu dans le fichier + git add + git commit + git push
			
		
		
		return "editContent";
}
	
	
}
	