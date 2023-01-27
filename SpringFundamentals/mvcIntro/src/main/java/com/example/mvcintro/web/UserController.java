package com.example.mvcintro.web;


import com.example.mvcintro.model.UserDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/user")
public class UserController {



    @GetMapping
    public String newUser(){
        return "newuser";
    }

    @PostMapping
    public String createUser(UserDto userDto){

        System.out.println("Creating new user..." + userDto);

        return "usercreated";

    }

    /*@GetMapping("/hello")
    public String hello(Model model, @RequestParam("num") Integer num){

        model.addAttribute("num", num);
        return "newuser";
    }*/



    /*@GetMapping("/hello")
    public String hello(ModelMap modelMap){

        modelMap.addAttribute("num", 3);
        //modelMap.put("num", 5);
        return "helloworld";
    }*/

    /*@GetMapping("/hello")
    public ModelAndView hello(ModelAndView modelAndView){

        modelAndView.addObject("num", 5);
        modelAndView.setViewName("helloworld");
        return modelAndView;
    }*/


}
