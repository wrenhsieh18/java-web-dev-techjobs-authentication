package org.launchcode.javawebdevtechjobsauthentication.controllers;

import org.launchcode.javawebdevtechjobsauthentication.models.User;
import org.launchcode.javawebdevtechjobsauthentication.models.UserRepository;
import org.launchcode.javawebdevtechjobsauthentication.models.dto.LoginFormDTO;
import org.launchcode.javawebdevtechjobsauthentication.models.dto.RegisterFromDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Optional;

@Controller
public class AuthenticationController {

    @Autowired
    private UserRepository userRepository;

    private static final String userSessionKey = "user";

    public User getUserFromSession(HttpSession session) {
        Integer userId = (Integer) session.getAttribute(userSessionKey);

        if (userId == null) {
            return null;
        }

        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            return null;
        }

        return user.get();
    }

    public void setUserInSession(HttpSession session, User user) {
        session.setAttribute(userSessionKey,user.getId());
    }

    @GetMapping("/register")
    public String displayRegisterForm(Model model) {
        model.addAttribute("title","Register");
        model.addAttribute(new RegisterFromDTO());
        return "register";
    }

    @PostMapping("/register")
    public String processRegisterForm(@ModelAttribute @Valid RegisterFromDTO registerFromDTO, Errors errors, Model model, HttpServletRequest request) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Register");
            return "register";
        }

        if (userRepository.findByUsername(registerFromDTO.getUsername()) != null) {
            errors.rejectValue("username", "username.alreadyexists","A user with that username already exists.");
            model.addAttribute("title", "Register");
            return "register";
        }

        if (!registerFromDTO.getPassword().equals(registerFromDTO.getVerifyPassword())) {
            errors.rejectValue("password", "passwords.mismatch","Password do not match.");
            model.addAttribute("title", "Register");
            return "register";
        }

        User newUser = new User(registerFromDTO.getUsername(), registerFromDTO.getPassword());
        userRepository.save(newUser);
        setUserInSession(request.getSession(), newUser);
        return "redirect:";
    }

    @GetMapping("/login")
    public String displayLoginForm(Model model) {
        model.addAttribute("title", "Log In");
        model.addAttribute(new LoginFormDTO());
        return "login";
    }

    @PostMapping("/login")
    public String processLoginForm(@ModelAttribute @Valid LoginFormDTO loginFormDTO, Errors errors,HttpServletRequest request, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("title", "Log In");
            return "login";
        }

        User theUser = userRepository.findByUsername(loginFormDTO.getUsername());

        if ( theUser == null ) {
            errors.rejectValue("username", "user.invalid", "The given username does not exist.");
            model.addAttribute("title", "Log In");
            return "/login";
        }

        if (!theUser.isMatchingPassword(loginFormDTO.getPassword())) {
            errors.rejectValue("password", "password.invalid", "Invalid password.");
            model.addAttribute("title", "Log In");
            return "/login";
        }

        setUserInSession(request.getSession(), theUser);
        return "redirect:";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/login";
    }


}
