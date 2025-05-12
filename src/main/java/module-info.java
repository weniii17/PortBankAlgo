module com.mycompany.portmanagement {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.mycompany.portmanagement to javafx.fxml;
    exports com.mycompany.portmanagement;
}
