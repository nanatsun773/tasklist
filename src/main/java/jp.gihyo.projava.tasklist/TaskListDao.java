package jp.gihyo.projava.tasklist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

// DBにアクセスするための窓口となるオブジェクト：DAO(Data Access Object)=この場合TaskListDaoクラス
@Service
public class TaskListDao {
    private final JdbcTemplate jdbcTemplate;

    // @Autowired：TaskListDaoクラスのコンストラクタを呼び出す際に引数として必要なオブジェクトを作成して渡してくれる(自動でJdbcTemplateの初期化や保持を行う)
    // Springが持つこの仕組みをDI(依存性の注入：依存性は「TaskListDaoはJdbcTemplateに依存している」)、仕組みを持つフレームワークをDIコンテナという
    @Autowired
    public TaskListDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void add(HomeController.TaskItem taskItem) {
        SqlParameterSource param = new BeanPropertySqlParameterSource(taskItem);
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate).withTableName("tasklist"); // SimpleJdbcInsert：Spring JDBCに用意されている,テーブルへのデータ追加を行うクラス
        insert.execute(param);
    }

    public List<HomeController.TaskItem> findAll() {
        String query = "select * from tasklist";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(query); // queryForList：JdbcTemplateクラスにあるSELECT文を組み立てるのに使用できるメソッド,SQL文をStringとして引数に渡すとその結果をListで返してくれる
        List<HomeController.TaskItem> taskItems = result.stream()
                .map((Map<String, Object> row) -> new HomeController.TaskItem(
                        row.get("id").toString(),
                        row.get("task").toString(),
                        row.get("deadline").toString(),
                        (Boolean) row.get("done")))
                .toList();

        return taskItems;
    }

    public int delete(String id) {
        var sql = """
                delete from
                tasklist
                where id = ?
                """;
        var n = jdbcTemplate.update(sql, id);
        return n;
    }

    public int update(HomeController.TaskItem taskItem) {
        var sql = """
                update tasklist
                set task = ?, deadline = ?, done = ?
                where id = ?
                """;
        var n = jdbcTemplate.update(sql, taskItem.task(), taskItem.deadline(), taskItem.done(), taskItem.id());
        return n;
    }
}
