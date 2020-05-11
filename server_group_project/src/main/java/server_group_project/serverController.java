//
// server for PUT@UiO Spring 2020
//

package server_group_project;

import static java.lang.System.out;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import server_group_project.Application;

@RestController
public class serverController {
	
	// ================================================================================	
	// this methodPostRemoteLogin is mapped to hostname:port/methodPostRemoteLogin
	// @RequestMapping(value="/methodPostRemoteLogin", method=RequestMethod.POST) 
	// it receives as arguments the email and the password hash corresponding to 
	// the authentication data that was inserted in the login activity in the client
	//@RequestMapping(value="/methodPostRemoteLogin", method=RequestMethod.POST) 
	@PostMapping(value="/methodPostRemoteLogin")
	@ResponseBody
	public Person methodPostRemoteLogin(@RequestParam String em, @RequestParam String ph) { 
		Person p;
	   	if (em.equals(null)  || ph.equals(null)) {
	   		System.out.println("serverController.methodPostRemoteLogin FATAL ERROR with at least one of the arguments null");
	   		return null;
	   	}
	   	p = Application.s.methodPostRemoteLogin(em, ph); 
	   	if (p == null) {System.out.println("serverController.methodPostRemoteLogin FATAL ERROR with p=null");
	   	}
	   	return p;
	}
	
	// ================================================================================		
	//this getMethod is mapped to hostname:port/getMethodMyClaims
	//@RequestMapping(value="/getMethodMyClaims", method=RequestMethod.GET) 
	@GetMapping(value="/getMethodMyClaims")
	public String  getMethodMyClaims(@RequestParam String id) {
		Claims cl;
		if (id.equals(null)) {
			out.println("serverController.getMethodMyClaims> FATAL ERROR with null id");
			return null;
		}
	    cl = Application.s.getMyClaims(id);		    
	    Gson gson = new Gson(); 
		String clJson = gson.toJson(cl);
	    return clJson;
	}			
	
	// ================================================================================	
	//this postInsertNewClaim is mapped to hostname:port/postInsertNewClaim
	//@RequestMapping(value="/postInsertNewClaim", method=RequestMethod.POST) 
	@PostMapping(value="/postInsertNewClaim")
	@ResponseBody
	public String postInsertNewClaim(@RequestParam String userId, @RequestParam String indexUpdateClaim, @RequestParam String newClaimDes, @RequestParam String newClaimPho, @RequestParam String newClaimLoc) {
		if (userId.equals(null) || indexUpdateClaim.equals(null)  || newClaimDes.equals(null) || newClaimPho.equals(null) || newClaimLoc.equals(null)) {
	   		System.out.println("serverController.postInsertNewClaim FATAL ERROR with at least one null argument");
	   		return null;
	   	}
		Application.s.insertNewClaim(userId, indexUpdateClaim, newClaimDes, newClaimPho, newClaimLoc);
		return "OK";
	}
	
	// ================================================================================	
	//this postUpdateClaim is mapped to hostname:port/postUpdateClaim
	//@RequestMapping(value="/postUpdateClaim", method=RequestMethod.POST) 
	@PostMapping(value="/postUpdateClaim")
	@ResponseBody
	public String postUpdateClaim(@RequestParam String userId, @RequestParam String indexUpdateClaim, @RequestParam String updateClaimDes, @RequestParam String updateClaimPho, @RequestParam String updateClaimLoc) {
		if (userId.equals(null) || indexUpdateClaim.equals(null)) {
	   		System.out.println("serverController.postUpdateClaim FATAL ERROR with either userId or indexUpdateClaim null");
	   		return null;
	   	}
		Application.s.updateClaim(userId, indexUpdateClaim, updateClaimDes, updateClaimPho, updateClaimLoc);
		return "OK";
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////
	// The methods below are just for testing/debug purposes
	////////////////////////////////////////////////////////////////////////////////
	
	// TESTING***********************************************************************
	//this getMethodTesting is mapped to hostname:port/getMethodTesting
	//@RequestMapping(value="/getMethodTesting", method=RequestMethod.GET) 
	@GetMapping(value="/getMethodTesting")
	public String getMethodTesting() {
		out.println("serverController.getMethodTesting> start ");
		Application.s.getMethodTesting();
		return "OK";
	}
			
	// TESTING***********************************************************************
	//this postMethodTesting is mapped to hostname:port/postMethodTesting
	//@RequestMapping(value="/postMethodTesting", method=RequestMethod.POST) 
	@PostMapping(value="/postMethodTesting")
	public String postMethodTesting() {
		out.println("serverController.postMethodTesting> start ");
		Application.s.postMethodTesting();
		return "OK";
	}

}
