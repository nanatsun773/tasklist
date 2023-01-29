package jp.gihyo.projava.tasklist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// コントローラを作るためのアノテーション
// @Controller:HTMLを返す場合
@Controller
public class HomeController {

    public record TaskItem(String id, String task, String deadline, boolean done) {
    }

//    private List<TaskItem> taskItems = new ArrayList<>();
    private final TaskListDao dao;

    @Autowired
    public HomeController(TaskListDao dao) {
        this.dao = dao;
    }

    @RequestMapping("/hello")
    // HTTPレスポンスを直接戻り値で返す場合
//    @ResponseBody
//    public String hello() {
//        return """
//                <html>
//                    <head><title>Hello</title></head>
//                    <body>
//                        <h1>Hello</h1>
//                        It works!<br>
//                        現在時刻は%sです。
//                    </body>
//                </html>
//                """.formatted(LocalDateTime.now());
//    }
    // ビューを表すオブジェクト(HTML文書を作成するオブジェクト)の名称(hello.htmlの"hello")を返すのがデフォルトであるため@ResponseBodyは付けない
    // ModelクラスはJavaプログラムとHTMLテンプレートの間で値を受け渡す役割を担う
    public String hello(Model model) {
        model.addAttribute("time", LocalDateTime.now());
        return "hello";
    }

    @GetMapping("/list")
    public String listItems(Model model) {
        List<TaskItem> taskItems = dao.findAll();
        model.addAttribute("taskList", taskItems);
        return "home";
    }

    @GetMapping("/add")
    public String addItem(@RequestParam("task") String task,
                          @RequestParam("deadline") String deadline) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        TaskItem item = new TaskItem(id, task, deadline, false);
        dao.add(item);

        return "redirect:/list"; // 表示するWebページを指定のパスにリダイレクトする
    }

    @GetMapping("/delete")
    public String deleteItem(@RequestParam("id") String id) {
        dao.delete(id);

        return "redirect:/list";
    }

    @GetMapping("/update")
    public String updateItem(@RequestParam("id") String id,
                             @RequestParam("task") String task,
                             @RequestParam("deadline") String deadline,
                             @RequestParam("done") boolean done) {
        TaskItem taskItem = new TaskItem(id, task, deadline, done);
        dao.update(taskItem);

        return "redirect:/list";
    }
}