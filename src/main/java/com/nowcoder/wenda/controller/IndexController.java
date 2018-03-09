package com.nowcoder.wenda.controller;

import com.nowcoder.wenda.model.User;
import com.nowcoder.wenda.service.WendaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller   //入口层
public class IndexController {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    WendaService wendaService;

    /**
     * 指定这两个路径的映射都用下面的函数来处理这个访问
     *
     */
    @RequestMapping(path = {"/" , "/index"}, method = {RequestMethod.GET})

    /**
     * 返回的是字符串而不是模板
     * 如果是复杂的页面 就把ResponseBody去掉 去模板的目录下面找文件
     */
    @ResponseBody
    public String index(HttpSession httpSession) {
        LOGGER.info("visit home");
        return "Hello nowcoder" + httpSession.getAttribute("msg");
    }

    /**
     * userId在路径里面 所以下面用PathVariable解析路径里面的参数userId
     *
     */
    @RequestMapping(path = {"profile/{group}/{userId}"})
    @ResponseBody
    public String profile(@PathVariable("userId") int userId,
                          @PathVariable("group") String group,
                          @RequestParam(value = "type", defaultValue = "1") int type,
                          @RequestParam(value = "key", defaultValue = "zz", required = false) String key) {
        //当访问页面的时候 会把userId通过{}的形式解析到变量里面去 可以在网页处理函数中直接用这个变量 这里userId变量与上面路径映射中的路径变量userId对应
        return String.format("Profile Page of %s / %d, type: %d, key: %s", group, userId, type, key);
    }

    @RequestMapping(value = "/vm", method = RequestMethod.GET)
    public String templateVm(Model model) { //返回的为什么还是String   //Model会把各种各样的数据传递到模板里面去
        //用这个方法把变量传到模板中
        model.addAttribute("value1", "vvvv1");

        List<String> colors = Arrays.asList(new String[] {"RED", "GREEN", "BLUE"});
        model.addAttribute("colors", colors);

        Map<String,String> map = new HashMap<>();
        for(int i=0;i<10;i++) {
            map.put(String.valueOf(i), String.valueOf(i*i));
        }
        model.addAttribute("map", map);

        //也可以add自定义的类
        model.addAttribute("user", new User("Lee"));


        // 当访问URL为/vm的链接地址的时候 返回的内容是叫homeVm的模板 系统会自动加上后缀名  ? 如果有同名但后缀名不同的模板 会找哪一个
        return "homeVm";
    }

    @RequestMapping(value = "/ftl", method = RequestMethod.GET)
    public String templateFtl() {
        return "homeFtl";
    }

    @RequestMapping("/request")
    @ResponseBody
    public String request(Model model, HttpServletResponse response,
                          HttpServletRequest request, HttpSession session,
                          @CookieValue("JSESSIONID") String sessionId) { //把网页请求里面的response request session包装成了对象
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(sessionId + "<br>");

        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            stringBuilder.append(name + ":" + request.getHeader(name) + "<br>");
        }

        if(request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                stringBuilder.append("name:" + cookie.getName() + " value:" + cookie.getValue());
            }
        }

        stringBuilder.append(request.getMethod() + "<br>");
        stringBuilder.append(request.getQueryString() + "<br>");
        stringBuilder.append(request.getPathInfo() + "<br>");
        stringBuilder.append(request.getRequestURI() + "<br>");

        //这样http请求里面的Response Header里面就会有相应的字段
        response.addHeader("nowcoderId", "hello");
        //这样加进去的cookie就会出现在response cookie中
        response.addCookie(new Cookie("username", "nowcoder"));

        return stringBuilder.toString();
    }

    /*
    //302临时跳转
    @RequestMapping(path = {"/redirect/{code}"}, method = RequestMethod.GET)
    public String redirect(@PathVariable("code") int code,
                           HttpSession httpSession) {
        //在跳转的过程中还可以传递消息 用httpSession.getAttribute()得到传到session里面的消息
        httpSession.setAttribute("msg", " jump from redirect");

        //通过redirect前缀重定向 重定向到首页
        return "redirect:/";
    }
    */

    @RequestMapping(path = {"/redirect/{code}"}, method = RequestMethod.GET)
    public RedirectView redirect(@PathVariable("code") int code,
                                 HttpSession httpSession) {
        httpSession.setAttribute("msg", " jump from redirect");

        RedirectView redirectView = new RedirectView("/", true);
        if(code == 301) {
            //301强制性跳转
            redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        }
        return redirectView;
    }

    @RequestMapping(path = {"/admin"})
    @ResponseBody
    public String admin(@RequestParam("key") String key) {
        if("admin".equals(key)) {
            return "hello, admin";
        } else {
            throw new IllegalArgumentException("参数不对");
        }
    }

    /**
     * 自定义一个ExceptionHandler 如果系统出现一些spring没有处理的异常的话就跳到这里来
     * @param e
     * @return
     */
    @ExceptionHandler()
    @ResponseBody
    public String error(Exception e) {
        return "ERROR:" + e.getMessage();
    }

}
