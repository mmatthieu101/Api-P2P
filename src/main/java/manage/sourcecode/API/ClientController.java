package manage.sourcecode.API;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


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

	@RequestMapping(value = "repository/{name}", method = RequestMethod.GET)
	public @ResponseBody String repository(HttpServletRequest request, Model model, @PathVariable String name) {
		System.out.println("-->" + name);
		
		return name;
	}

}