package com.yabloko.filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/home")
public class AuthFilter implements Filter{
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    // на вход фильтр получает запрос, ответ, а также цепочку фильтров, которым следует отдать запрос далее.
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        // выполняем преобразование Servlet-запросов-ответов в HTTP-запросы-ответы

        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;

        // смотрим, есть ли сессия для данного запроса (проверяется наличие Cookie с названием JSESSIONID
        HttpSession session = request.getSession(false);

        // если сессии не было, или у сессии отсутствует атрибут user, перенаправляем пользователя на страницу с логином
        if (session == null || session.getAttribute("user") == null) {
            servletRequest.getServletContext().getRequestDispatcher("/login").forward(request, response);
//            response.sendRedirect(request.getContextPath() + "/login"); // вообще не работает - сразу двойной редирект !!!

        }
        // отдаем запрос дальше в цепочку фильтров
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
