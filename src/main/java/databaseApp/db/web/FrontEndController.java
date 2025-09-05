package databaseApp.db.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontEndController {

    // изброяваме React маршрутите ти; можеш да добавяш още
    @GetMapping({
            "/login",
            "/dashboard",
            "/task-status",
            "/generator",
            "/reports",
            "/manage-users",
            "/manage-tasks"
    })
    public String forwardToIndex() {
        return "forward:/index.html";
    }
}
