package web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author：rkc
 * @date：Created in 2021/5/10 8:51
 * @description：
 */
public interface HandlerAdapter {

    Object[] handle(HttpServletRequest request, HttpServletResponse response, Method method);
}
