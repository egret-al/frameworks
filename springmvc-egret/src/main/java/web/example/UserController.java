package web.example;

import com.alibaba.fastjson.JSON;
import com.spring.annotation.Controller;
import web.annotation.RequestBody;
import web.annotation.RequestMapping;
import web.annotation.RequestMethod;
import web.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author：rkc
 * @date：Created in 2021/5/9 12:01
 * @description：
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @RequestMapping(value = "test1", method = {RequestMethod.GET})
    public User test1(HttpServletRequest request, HttpServletResponse response) {
        User user = new User("张三", 21, "女");
        return user;
    }

    @RequestMapping(value = "test2", method = RequestMethod.GET)
    public User test2(HttpServletRequest request, HttpServletResponse response, @RequestParam("name") String name, @RequestParam("age") String age) {
        User user = new User("张三", 21, "男");
        return user;
    }

    @RequestMapping(value = "test3", method = RequestMethod.POST)
    public User user(@RequestBody User user) {
        System.out.println(user);
        user.setAge(user.getAge() + 2);
        return user;
    }
}
