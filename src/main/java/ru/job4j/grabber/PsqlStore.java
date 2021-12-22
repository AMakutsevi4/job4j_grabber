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
    private final Connection cnn;

    public PsqlStore(Properties cfg) {
        try (InputStream in = PsqlStore.class.getClassLoader()
                .getResourceAsStream("app.properties")) {
            cfg.load(in);
            Class.forName(cfg.getProperty("jdbc.driver"));
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
            statement.setString(4, String.valueOf(post.getCreated()));
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
        var sql = "select * from items where id = ?;";
        try (PreparedStatement statement = cnn.prepareStatement(sql)) {
            try (ResultSet rslKey = statement.executeQuery()) {
                if (rslKey.next()) {
                    Post post = new Post();
                    post.setId(rslKey.getInt("id"));
                    post.setTitle(rslKey.getString("name"));
                    post.setDescription(rslKey.getString("text"));
                    post.setLink(rslKey.getString("link"));
                    post.setCreated(rslKey.getTimestamp("created").toLocalDateTime());
                    table.add(post);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return table;
    }

    @Override
    public Post findById(int id) {
        Post post = new Post();
        var sql = "select * from items where id = ?;";
        try (PreparedStatement statement = cnn.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet rslKey = statement.executeQuery()) {
                if (rslKey.next()) {
                    post.setId(rslKey.getInt("id"));
                    post.setTitle(rslKey.getString("name"));
                    post.setDescription(rslKey.getString("text"));
                    post.setLink(rslKey.getString("link"));
                    post.setCreated(rslKey.getTimestamp("created").toLocalDateTime());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

    public static void main(String[] args) throws IOException {
        String page = "https://www.sql.ru/forum/job-offers/";
        SqlRuParse sqlRuParse = new SqlRuParse(new SqlRuDateTimeParser());
        List<Post> list = sqlRuParse.list(page);
    }
}
