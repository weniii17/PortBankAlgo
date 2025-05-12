import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class PortUI extends Application {

    // Resource tracking
    private final int totalDocks = 10;
    private final int totalCranes = 15;
    private final int totalForklifts = 20;
    private int allocatedDocks = 0;
    private int allocatedCranes = 0;
    private int allocatedForklifts = 0;

    // UI elements to update
    private ProgressBar dockProgress, craneProgress, forkliftProgress;
    private Label dockLabel, craneLabel, forkliftLabel;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Port Resource Management System - Split View");

        // --- Top Info & Status ---
        Label title = new Label("Port Resource Manager");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Label status = new Label("System State: SAFE");
        status.setStyle("-fx-text-fill: green;");

        // --- Resource Usage Visualization ---
        dockProgress = new ProgressBar(0);
        craneProgress = new ProgressBar(0);
        forkliftProgress = new ProgressBar(0);

        dockLabel = new Label("Docks: 0 / " + totalDocks);
        craneLabel = new Label("Cranes: 0 / " + totalCranes);
        forkliftLabel = new Label("Forklifts: 0 / " + totalForklifts);

        VBox resourceUsageBox = new VBox(5,
                new Label("Resource Usage:"),
                dockLabel, dockProgress,
                craneLabel, craneProgress,
                forkliftLabel, forkliftProgress
        );

        //Input Fields
        TextField shipId = new TextField(); shipId.setPromptText("Ship ID");
        TextField maxDocks = new TextField(); maxDocks.setPromptText("Max Docks");
        TextField maxCranes = new TextField(); maxCranes.setPromptText("Max Cranes");
        TextField maxForklifts = new TextField(); maxForklifts.setPromptText("Max Forklifts");

        TextField reqDocks = new TextField(); reqDocks.setPromptText("Request Docks");
        TextField reqCranes = new TextField(); reqCranes.setPromptText("Request Cranes");
        TextField reqForklifts = new TextField(); reqForklifts.setPromptText("Request Forklifts");

        Button addShipBtn = new Button("Add Ship");
        Button requestBtn = new Button("Process Request");
        Button releaseBtn = new Button("Release Resources");

        //Control Panel
        VBox controlPanel = new VBox(10,
                title, status,
                resourceUsageBox,
                new Label("New Ship Details:"),
                shipId, maxDocks, maxCranes, maxForklifts,
                new Label("Resource Request:"),
                reqDocks, reqCranes, reqForklifts,
                addShipBtn, requestBtn, releaseBtn
        );
        controlPanel.setPadding(new Insets(15));
        controlPanel.setPrefWidth(300);

        //Tables and Logs
        TableView<String> shipsTable = new TableView<>();
        shipsTable.setPlaceholder(new Label("No ships in port"));

        TableView<String> requestTable = new TableView<>();
        requestTable.setPlaceholder(new Label("No resource requests"));

        Label logLabel = new Label("Activity Log:");
        TextArea logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefRowCount(6);
        logArea.setWrapText(true);

        VBox tableArea = new VBox(10,
                new Label("Ships in Port:"), shipsTable,
                new Label("Resource Requests:"), requestTable,
                logLabel, logArea
        );
        tableArea.setPadding(new Insets(15));

        //Add Ship
        addShipBtn.setOnAction(e -> {
            try {
                int d = Integer.parseInt(maxDocks.getText());
                int c = Integer.parseInt(maxCranes.getText());
                int f = Integer.parseInt(maxForklifts.getText());

                allocatedDocks += d;
                allocatedCranes += c;
                allocatedForklifts += f;

                updateResourceBars();
                logArea.appendText("Ship " + shipId.getText() + " added. Resources allocated.\n");

                // Clear inputs
                shipId.clear(); maxDocks.clear(); maxCranes.clear(); maxForklifts.clear();
            } catch (NumberFormatException ex) {
                logArea.appendText("Invalid input. Please enter numeric values.\n");
            }
        });

        //SplitPane Layout
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.getItems().addAll(controlPanel, tableArea);
        splitPane.setDividerPositions(0.3);

        Scene scene = new Scene(splitPane, 1000, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateResourceBars() {
        dockProgress.setProgress((double) allocatedDocks / totalDocks);
        craneProgress.setProgress((double) allocatedCranes / totalCranes);
        forkliftProgress.setProgress((double) allocatedForklifts / totalForklifts);

        dockLabel.setText("Docks: " + allocatedDocks + " / " + totalDocks);
        craneLabel.setText("Cranes: " + allocatedCranes + " / " + totalCranes);
        forkliftLabel.setText("Forklifts: " + allocatedForklifts + " / " + totalForklifts);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
