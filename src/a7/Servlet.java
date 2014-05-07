package a7;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import parse.ParserImpl;
import a5.Constants;
import a5.Critter;
import a5.CritterWorld;
import ast.Program;


/**
 * Servlet implementation class Servlet
 */
@WebServlet("/*")
public class Servlet extends HttpServlet {
	private static CritterWorld cw;
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Servlet() {
        super();
        Constants.read("src/constants.txt");
        try {
        	if (cw == null) {
        		cw = new CritterWorld("src/world.txt");
        		cw.step();
        		cw.step();
        		cw.step();
        		cw.step();
        		cw.step();
        	}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String url = request.getRequestURI();
		String[] str = url.split("/");
		if (str.length==4 && str[2].equals("CritterWorld") && str[3].equals("critters")){
			getCritters(response);
		} else if (str.length==5 && str[2].equals("CritterWorld") &&
				str[3].equals("critters")){
			getCrittersID(response, str[4]);
		} else if (str.length==4 && str[2].equals("CritterWorld") && str[3].substring(0,5).equals("world")){
			if(request.getParameter("update_since")!=null&&request.getParameter("from_row")!=null){
				getWorldParams(response,request.getParameter("update_since"),
						request.getParameter("from_row"),request.getParameter("to_row"),
						request.getParameter("from_col"),request.getParameter("to_col"));
			} else if (request.getParameter("from_row")!=null){
				getWorldParams(response, request.getParameter("from_row"),request.getParameter("to_row"),
						request.getParameter("from_col"),request.getParameter("to_col"));
			} else if(request.getParameter("update_since")!=null){
				getWorld(response, request.getParameter("update_since"));
			} else {
				getWorld(response);
			}
		}
	}

	protected void getCritters(HttpServletResponse response) throws IOException{
		response.addHeader("Content-Type", "application/json");
		response.setStatus(200);
		JSONArray json = new JSONArray();
		try {
			for (int i = 0; i < cw.critters.size(); i++){
				Critter c = cw.critters.get(i);
				JSONObject temp = c.getJson();
				json.put(temp);
			}
		} catch (JSONException e) {
			System.out.println("Unable to make JSON Object");
		}
		PrintWriter pw = response.getWriter();
		pw.println(json);
	}
	
	protected void getCrittersID(HttpServletResponse response, String str) throws IOException {
		response.addHeader("Content-Type", "application/json");
		response.setStatus(200);
		try {
			int id = Integer.parseInt(str);
			Critter c = null;
			for (int i = 0; i < cw.critters.size(); i++){
				Critter t = cw.critters.get(i);
				if (t.id == id){
					c = t;
					break;
				}
			}
			if (c == null) {
				response.getWriter().println("No such critter");
			} else {
				JSONObject json = c.getJson();
				response.getWriter().println(json);
			}
		} catch (Exception e){ 
			response.getWriter().println("Incorrect URL");
		}
	}
	
	protected void getWorld(HttpServletResponse response) throws IOException{
		response.addHeader("Content-Type", "application/json");
		response.setStatus(200);
		try{
			JSONObject json = cw.getJson(0,Constants.MAX_ARRAY_ROW,0,Constants.MAX_COLUMN);
			response.getWriter().println(json);
		} catch (Exception e){
			response.getWriter().println("Incorrect URL");
		}
	}
	
	protected void getWorld(HttpServletResponse response, String update) throws IOException{
		response.addHeader("Content-Type", "application/json");
		response.setStatus(200);
		try{
			int updatesince = Integer.parseInt(update);
			if (updatesince < 0 || updatesince > cw.steps){
				response.getWriter().println("This step has not occurred yet");
			} else {
				JSONObject json = cw.getJson(updatesince,0,Constants.MAX_ARRAY_ROW,0,Constants.MAX_COLUMN);
				response.getWriter().println(json);
			}
		} catch (Exception e){
			response.getWriter().println("Incorrect URL");
		}
	}
	
	protected void getWorldParams(HttpServletResponse response, String a, String b, String c, String d) throws IOException{
		response.addHeader("Content-Type", "application/json");
		response.setStatus(200);
		try{
			int from_row = Integer.parseInt(a);
			int to_row = Integer.parseInt(b);
			int from_col = Integer.parseInt(c);
			int to_col = Integer.parseInt(d);
			JSONObject json = cw.getJson(from_row,to_row,from_col,to_col);
			response.getWriter().println(json);
		} catch (Exception e){
			response.getWriter().println("Incorrect URL");
		}
	}
	
	protected void getWorldParams(HttpServletResponse response, String update, String a, String b, String c, String d) throws IOException{
		response.addHeader("Content-Type", "application/json");
		response.setStatus(200);
		try{
			int from_row = Integer.parseInt(a);
			int to_row = Integer.parseInt(b);
			int from_col = Integer.parseInt(c);
			int to_col = Integer.parseInt(d);
			int updatesince = Integer.parseInt(update);
			if (updatesince < 0 || updatesince > cw.steps){
				response.getWriter().println("This step has not occurred yet");
			} else {
				JSONObject json = cw.getJson(updatesince,from_row,to_row,from_col,to_col);
				response.getWriter().println(json);
			}
		} catch (Exception e){
			response.getWriter().println("Incorrect URL");
		}
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		StringBuffer jb = new StringBuffer();
		String line = null;
		BufferedReader reader = request.getReader();
		while ((line = reader.readLine()) != null) {
		 	jb.append(line + "\n");
		}
		try{
			if (request.getRequestURI().equals("/Assignment-7/CritterWorld/critters")){
				createCritter(jb, response);
			} else if(request.getRequestURI().equals("/Assignment-7/CritterWorld/world")){
				createWorld(jb, response);
			} else if(request.getRequestURI().contains("/Assignment-7/CritterWorld/step")){
				step(request, response);
			} else if(request.getRequestURI().contains("/Assignment-7/CritterWorld/run")){
				rate(request, response);
			}
		} catch(JSONException E){
			E.printStackTrace();
		}
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(request.getRequestURI().contains("/Assignment-7/CritterWorld/critter")){
			response.addHeader("Content-Type", "text/json");
			response.setStatus(204);
			int a = Integer.parseInt(request.getRequestURI().substring(request.getRequestURI().indexOf("/", request.getRequestURI().indexOf("critters"))+1));
			for(Critter c: cw.critters){
				if(c.id == a){
					cw.kill(c);
				}
			}
		}
	}
			
	void createCritter(StringBuffer jb, HttpServletResponse response) throws IOException, JSONException{
		response.addHeader("Content-Type", "text/json");
		response.setStatus(201);
		JSONObject o = new JSONObject(jb.toString());
		StringReader str = new StringReader(o.getString("program"));
		ArrayList<Integer> ids = new ArrayList<Integer>();
		ParserImpl p = new ParserImpl();
		Program critterProgram = p.parse(str);
		JSONArray ja = o.getJSONArray("mem");
		int[] mem = new int[ja.length()];
		for(int i = 0; i<ja.length();i++){
			mem[i] = ja.getInt(i);
		}
		String species_id = o.getString("species_id");
		if(o.has("num")){
			for(int i=0;i<o.getInt("num");i++){
				ids.add(cw.addRandomCritter(critterProgram, mem, species_id));
			}
		}
		else{
			JSONArray pos = o.getJSONArray("positions");
			for(int i=0;i<pos.length();i++){
				JSONObject jo = pos.getJSONObject(i);
				int column = jo.getInt("col");
				int row = jo.getInt("row");
				ids.add(cw.addCritterHere(critterProgram, mem, column, row, species_id));
			}
		
		}
		
		JSONObject res = new JSONObject();
		JSONArray jsonIDS = new JSONArray(ids);
		res.put("species_id", species_id);
		res.put("ids", jsonIDS);
		response.getWriter().println(res.toString());
		
		
	}
	
	void createWorld(StringBuffer jb, HttpServletResponse response) throws IOException, JSONException{
		response.addHeader("Content-Type", "text/json");
		response.setStatus(201);
		cw = new CritterWorld(jb);
		response.getWriter().println("OK");
	}
	
	void step(HttpServletRequest request, HttpServletResponse response) throws IOException{
		response.addHeader("Content-Type", "text/json");
		if(cw.rate != 0){
			response.setStatus(406);
			response.getWriter().println("NOT OK");
			return;
		}
		response.setStatus(200);
		int count = Integer.parseInt(request.getParameter("count"));
		for(int i=0;i<count;i++) cw.step();
		response.getWriter().println("OK");
	}
	
	void rate(HttpServletRequest request, HttpServletResponse response) throws IOException{
		response.addHeader("Content-Type", "text/json");
		double r = Double.parseDouble(request.getParameter("rate"));
		if(r < 0){
			response.setStatus(406);
			response.getWriter().println("NOT OK");
			return;
		}
		response.setStatus(200);
		cw.rate = r;
		response.getWriter().println("OK");
	}
}