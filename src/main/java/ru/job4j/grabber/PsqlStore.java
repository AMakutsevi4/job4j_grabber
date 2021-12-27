package ru.job4j.grabber;


import ru.job4j.grabber.utils.SqlRuDateTimeParser;
import ru.job4j.html.SqlRuParse;
import ru.job4j.post.Post;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {
    private Connection cnn;

    public PsqlStore(Properties cfg) {
        try {
            Class.forName(cfg.getProperty("driver"));
            cnn = DriverManager.getConnection(
                    cfg.getProperty("url"),
                    cfg.getProperty("username"),
                    cfg.getProperty("password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void save(Post post) {
        var sql = "Insert into post(name, text, link, created) values(?, ?, ?, ?)";
        try (var statement = cnn.prepareStatement(sql, Statement
                .RETURN_GENERATED_KEYS)) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getDescription());
            statement.setString(3, post.getLink());
            statement.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            statement.execute();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    post.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        ArrayList<Post> table = new ArrayList<>();
        var sql = "select * from post";
        try (var statement = cnn.prepareStatement(sql)) {
            try (var rslKey = statement.executeQuery()) {
               while (rslKey.next()) {
                    table.add(createPost(rslKey));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return table;
    }

    @Override
    public Post findById(int id) {
        Post post = null;
        var sql = "select * from post where id = ?;";
        try (var statement = cnn.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (var rslKey = statement.executeQuery()) {
                if (rslKey.next()) {
                    post = createPost(rslKey);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return post;
    }

    private Post createPost(ResultSet resultSet) throws SQLException {
        Post post = new Post();
        post.setId(resultSet.getInt("id"));
        post.setTitle(resultSet.getString("name"));
        post.setDescription(resultSet.getString("text"));
        post.setLink(resultSet.getString("link"));
        post.setCreated(resultSet.getTimestamp("created").toLocalDateTime());
        return post;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

    public static void main(String[] args) throws IOException {
        Properties cfg = new Properties();
        InputStream in = PsqlStore.class.getClassLoader()
                .getResourceAsStream("app.properties");
        cfg.load(in);
        SqlRuParse sqlRuParse = new SqlRuParse(new SqlRuDateTimeParser());
        PsqlStore psqlStore = new PsqlStore(cfg);
        String page = "https://www.sql.ru/forum/job-offers/";
        List<Post> list = sqlRuParse.list(page);
        psqlStore.save(list.get(1));
        System.out.println(psqlStore.getAll());
        System.out.println(psqlStore.findById(1));
    }
}
