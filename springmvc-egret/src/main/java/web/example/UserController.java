package web.example;

import com.spring.annotation.Controller;
import web.annotation.RequestMapping;
import web.annotation.RequestMethod;
import web.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author：rkc
 * @date：Created in 2021/5/9 12:01
 * @description：
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @RequestMapping(value = "test1", method = RequestMethod.GET)
    public Object test1(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("test1");
        return "test1";
    }

    @RequestMapping(value = "test2", method = RequestMethod.GET)
    public Object test2(HttpServletRequest request, HttpServletResponse response, @RequestParam("name") String name, @RequestParam("age") String age) {
        System.out.println("test2");
        System.out.println(name + "  " + age);
        return "test2";
    }
}
