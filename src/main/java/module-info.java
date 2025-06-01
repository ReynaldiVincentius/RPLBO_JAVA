module org.example.java_todolist {
    requires javafx.controls;
    requires javafx.fxml;
    requires  java.sql;
    requires spring.security.crypto;
    requires java.desktop;

//    requires spring.security.crypto;

    opens org.example.java_todolist to javafx.fxml;
    opens org.example.java_todolist.controller to javafx.fxml;

    exports org.example.java_todolist;
}