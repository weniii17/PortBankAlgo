module com.mycompany.portmanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens com.mycompany.portmanagement to javafx.fxml;

    exports com.mycompany.portmanagement;
}
