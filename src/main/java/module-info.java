module com.shortcutcleaner {
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;

    opens com.nextholiday to javafx.fxml;

    exports com.nextholiday;
}
