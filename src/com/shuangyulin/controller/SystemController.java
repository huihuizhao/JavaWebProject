package com.shuangyulin.controller;

import java.io.UnsupportedEncodingException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller; 
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated; 
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

 

import com.shuangyulin.dao.AdminDAO;
import com.shuangyulin.domain.Admin;
import com.shuangyulin.utils.UserException;

 
@Controller
@SessionAttributes("username")
public class SystemController { 
	
	@Resource AdminDAO adminDAO;  
	
	@RequestMapping(value="/login",method=RequestMethod.GET)
	public String login(Model model) {
		model.addAttribute(new Admin());
		return "login";
	}

	@RequestMapping(value="/login",method=RequestMethod.POST)
	public String login(@Validated Admin admin,BindingResult br,Model model,HttpServletRequest request,HttpSession session) { 
		if(br.hasErrors()) {
			return "login";
		} 
		if (!adminDAO.CheckLogin(admin)) {
			request.setAttribute("error", java.net.URLEncoder.encode(adminDAO.getErrMessage())); 
			return "error";
		}
		//session.setAttribute("username", admin.getUsername());
		model.addAttribute("username", admin.getUsername());
		return "main";  
		//return "redirect:/user/users";
	}
	
	
	
	@RequestMapping("/logout")
	public String logout(Model model,HttpSession session) {
		model.asMap().remove("username");
		session.invalidate();
		return "redirect:/login";
	}
	
	
	@RequestMapping(value="/changePassword",method=RequestMethod.POST)
	public String ChangePassword(String oldPassword,String newPassword,String newPassword2,HttpServletRequest request,HttpSession session) throws UnsupportedEncodingException { 
		if(oldPassword.equals("")) throw new UserException("请输入旧密码！");
		if(newPassword.equals("")) throw new UserException("请输入新密码！");
		if(!newPassword.equals(newPassword2)) throw new UserException("两次新密码输入不一致"); 
		
		String username = (String)session.getAttribute("username");
		if(username == null) throw new UserException("session会话超时，请重新登录系统!");
		 
		
		Admin admin = adminDAO.GetAdmin(username); 
		if(!admin.getPassword().equals(oldPassword)) throw new UserException("输入的旧密码不正确!");
		
		try { 
			adminDAO.ChangePassword(username,newPassword);
			request.setAttribute("message", java.net.URLEncoder.encode(
					"密码修改成功!", "GBK"));
			return "message";
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("error", java.net.URLEncoder
					.encode("密码修改失败!","GBK"));
			return "error";
		}   
	}
	
	
	
	
}
