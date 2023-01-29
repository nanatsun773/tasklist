package jp.gihyo.projava.tasklist;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

// コントローラを作るためのアノテーション
// @RestController:HTTPレスポンスとして文字列をそのまま返す場合
@RestController
public class HomeRestController {

    public record TaskItem(String id, String task, String deadline, boolean done) {}
    private List<TaskItem> taskItems = new ArrayList<>();

    // クライアントからのリクエストを処理するメソッドであることを表すアノテーション
    // @RequestMapping:HTTPのGETメソッドとPOSTメソッドに対応する
    // クライアントとの窓口になるメソッドをエンドポイントという
    @RequestMapping("/resthello") // 省略前は@RequestMapping(value="/resthello")
    public String hello() {
        return """
                Hello.
                It works!
                現在時刻は%sです。
                """.formatted(LocalDateTime.now());
    }

    // クライアントからのリクエストを処理するメソッドであることを表すアノテーション
    // @GetMapping:HTTPのGETメソッドのみに対応する
    // @RequestParam:HTTPリクエストのパラメータと関連づける
    @GetMapping("/restadd") // 省略前は@GetMapping(value="/restadd")
    public String addItem(@RequestParam("task") String task,
                          @RequestParam("deadline") String deadline) {
        String id = UUID.randomUUID().toString().substring(0,8); // randomUUID:32文字 subString:8文字を切り出す
        TaskItem item = new TaskItem(id, task, deadline, false);
        taskItems.add(item);

        return "タスクを追加しました。";
    }

    @GetMapping("/restlist")
    public String listItems() {
        String result = taskItems.stream()
                .map(TaskItem::toString)
                .collect(Collectors.joining(", ")); // ,で結合する
        return result;
    }
}
