package com.mycompany.portmanagement;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.List;

public class PortUI extends Application {

    // Resource tracking
    private int totalDocks;
    private int totalCranes;
    private int totalForklifts;
    private int allocatedDocks = 0;
    private int allocatedCranes = 0;
    private int allocatedForklifts = 0;

    // Banker's Algorithm implementation
    private BankerAlgorithm banker;

    // Ship management
    private ObservableList<Ship> ships = FXCollections.observableArrayList();
    private TextField selectedShipField;

    // UI elements to update
    private ProgressBar dockProgress, craneProgress, forkliftProgress;
    private Label dockLabel, craneLabel, forkliftLabel;
    private Label statusLabel;
    private Label availableResourcesLabel;
    private TableView<Ship> shipsTable; // Add this line to make shipsTable a class field

    @Override
    public void start(Stage primaryStage) {
        // Show resource setup dialog first
        if (!showResourceSetupDialog()) {
            // User cancelled setup or provided invalid input
            return;
        }

        // Initialize Banker's Algorithm with user-provided resources
        banker = new BankerAlgorithm(totalDocks, totalCranes, totalForklifts);

        primaryStage.setTitle("Port Resource Management System - Banker's Algorithm");

        // --- Top Info & Status ---
        Label title = new Label("Port Resource Manager");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        statusLabel = new Label("System State: SAFE");
        statusLabel.setStyle("-fx-text-fill: green;");

        // --- Resource Usage Visualization ---
        dockProgress = new ProgressBar(0);
        craneProgress = new ProgressBar(0);
        forkliftProgress = new ProgressBar(0);

        dockLabel = new Label("Docks: 0 / " + totalDocks);
        craneLabel = new Label("Cranes: 0 / " + totalCranes);
        forkliftLabel = new Label("Forklifts: 0 / " + totalForklifts);

        availableResourcesLabel = new Label("Available Resources: D:" + totalDocks +
                ", C:" + totalCranes + ", F:" + totalForklifts);
        availableResourcesLabel.setStyle("-fx-font-weight: bold;");

        VBox resourceUsageBox = new VBox(5,
                new Label("Resource Usage:"),
                dockLabel, dockProgress,
                craneLabel, craneProgress,
                forkliftLabel, forkliftProgress,
                availableResourcesLabel);

        // Input Fields
        TextField shipId = new TextField();
        shipId.setPromptText("Ship ID");
        TextField maxDocks = new TextField();
        maxDocks.setPromptText("Max Docks");
        TextField maxCranes = new TextField();
        maxCranes.setPromptText("Max Cranes");
        TextField maxForklifts = new TextField();
        maxForklifts.setPromptText("Max Forklifts");

        TextField reqDocks = new TextField();
        reqDocks.setPromptText("Request Docks");
        TextField reqCranes = new TextField();
        reqCranes.setPromptText("Request Cranes");
        TextField reqForklifts = new TextField();
        reqForklifts.setPromptText("Request Forklifts");

        Button addShipBtn = new Button("Add Ship");
        Button requestBtn = new Button("Process Request");
        Button releaseBtn = new Button("Release Resources");

        // Control Panel // Selected ship
        selectedShipField = new TextField();
        selectedShipField.setPromptText("Selected Ship ID");
        selectedShipField.setEditable(false);
        Button removeShipBtn = new Button("Remove Ship");

        HBox selectionBox = new HBox(10, new Label("Selected:"), selectedShipField, removeShipBtn);

        VBox controlPanel = new VBox(10,
                title, statusLabel,
                resourceUsageBox,
                new Label("New Ship Details:"),
                shipId, maxDocks, maxCranes, maxForklifts,
                new Label("Resource Request:"),
                reqDocks, reqCranes, reqForklifts,
                selectionBox,
                addShipBtn, requestBtn, releaseBtn);
        controlPanel.setPadding(new Insets(15));
        controlPanel.setPrefWidth(300);

        // Setup ships table
        shipsTable = new TableView<>(); // Change this line to use the class field instead of local variable
        shipsTable.setPlaceholder(new Label("No ships in port"));
        shipsTable.setItems(ships);

        // Add styling for rows
        shipsTable.setRowFactory(tv -> new TableRow<Ship>() {
            @Override
            protected void updateItem(Ship ship, boolean empty) {
                super.updateItem(ship, empty);

                // Clear any existing style
                setStyle("");

                if (ship != null) {
                    // Apply green background for fully allocated ships
                    if (ship.isFullyAllocated()) {
                        setStyle("-fx-background-color: #90EE90;"); // Light green
                        setTooltip(new Tooltip("All resource requirements met - Ready to release"));
                    }
                }
            }
        });

        TableColumn<Ship, String> idColumn = new TableColumn<>("Ship ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(80);

        TableColumn<Ship, Integer> allocatedDocksColumn = new TableColumn<>("D Alloc");
        allocatedDocksColumn.setCellValueFactory(new PropertyValueFactory<>("allocatedDocks"));
        allocatedDocksColumn.setPrefWidth(60);

        TableColumn<Ship, Integer> maxDocksColumn = new TableColumn<>("D Max");
        maxDocksColumn.setCellValueFactory(new PropertyValueFactory<>("maxDocks"));
        maxDocksColumn.setPrefWidth(60);

        TableColumn<Ship, Integer> neededDocksColumn = new TableColumn<>("D Need");
        neededDocksColumn.setCellValueFactory(new PropertyValueFactory<>("neededDocks"));
        neededDocksColumn.setPrefWidth(60);

        TableColumn<Ship, Integer> allocatedCranesColumn = new TableColumn<>("C Alloc");
        allocatedCranesColumn.setCellValueFactory(new PropertyValueFactory<>("allocatedCranes"));
        allocatedCranesColumn.setPrefWidth(60);

        TableColumn<Ship, Integer> maxCranesColumn = new TableColumn<>("C Max");
        maxCranesColumn.setCellValueFactory(new PropertyValueFactory<>("maxCranes"));
        maxCranesColumn.setPrefWidth(60);

        TableColumn<Ship, Integer> neededCranesColumn = new TableColumn<>("C Need");
        neededCranesColumn.setCellValueFactory(new PropertyValueFactory<>("neededCranes"));
        neededCranesColumn.setPrefWidth(60);

        TableColumn<Ship, Integer> allocatedForkliftsColumn = new TableColumn<>("F Alloc");
        allocatedForkliftsColumn.setCellValueFactory(new PropertyValueFactory<>("allocatedForklifts"));
        allocatedForkliftsColumn.setPrefWidth(60);

        TableColumn<Ship, Integer> maxForkliftsColumn = new TableColumn<>("F Max");
        maxForkliftsColumn.setCellValueFactory(new PropertyValueFactory<>("maxForklifts"));
        maxForkliftsColumn.setPrefWidth(60);

        TableColumn<Ship, Integer> neededForkliftsColumn = new TableColumn<>("F Need");
        neededForkliftsColumn.setCellValueFactory(new PropertyValueFactory<>("neededForklifts"));
        neededForkliftsColumn.setPrefWidth(60);

        // Fix for type safety warning - add columns individually instead of using
        // varargs
        shipsTable.getColumns().add(idColumn);
        shipsTable.getColumns().add(allocatedDocksColumn);
        shipsTable.getColumns().add(maxDocksColumn);
        shipsTable.getColumns().add(neededDocksColumn);
        shipsTable.getColumns().add(allocatedCranesColumn);
        shipsTable.getColumns().add(maxCranesColumn);
        shipsTable.getColumns().add(neededCranesColumn);
        shipsTable.getColumns().add(allocatedForkliftsColumn);
        shipsTable.getColumns().add(maxForkliftsColumn);
        shipsTable.getColumns().add(neededForkliftsColumn);

        shipsTable.setOnMouseClicked(event -> {
            Ship selectedShip = shipsTable.getSelectionModel().getSelectedItem();
            if (selectedShip != null) {
                selectedShipField.setText(selectedShip.getId());
            }
        });

        // Safe sequence display
        Label safeSequenceLabel = new Label("Safe Sequence:");
        TextArea safeSequenceArea = new TextArea();
        safeSequenceArea.setEditable(false);
        safeSequenceArea.setPrefRowCount(2);
        safeSequenceArea.setWrapText(true);

        Label logLabel = new Label("Activity Log:");
        TextArea logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefRowCount(6);
        logArea.setWrapText(true);
        VBox tableArea = new VBox(10,
                new Label("Ships in Port:"), shipsTable,
                safeSequenceLabel, safeSequenceArea,
                logLabel, logArea);
        tableArea.setPadding(new Insets(15));

        // Add Ship
        addShipBtn.setOnAction(e -> {
            try {
                String id = shipId.getText().trim();
                int d = Integer.parseInt(maxDocks.getText());
                int c = Integer.parseInt(maxCranes.getText());
                int f = Integer.parseInt(maxForklifts.getText());

                // Validate inputs
                if (id.isEmpty()) {
                    logArea.appendText("Error: Ship ID cannot be empty.\n");
                    return;
                }

                if (d < 0 || c < 0 || f < 0) {
                    logArea.appendText("Error: Resources cannot be negative.\n");
                    return;
                }

                if (banker.shipExists(id)) {
                    logArea.appendText("Error: Ship with ID " + id + " already exists.\n");
                    return;
                }

                // Check if maximum resource requirements exceed total resources
                if (d > totalDocks || c > totalCranes || f > totalForklifts) {
                    logArea.appendText("Error: Ship's maximum resource requirements exceed port capacity.\n");
                    return;
                }

                // Add ship to Banker's Algorithm
                banker.addShip(id, d, c, f);

                // Add ship to UI list
                Ship newShip = new Ship(id, d, c, f);
                ships.add(newShip);

                // Update UI
                logArea.appendText("Ship " + id + " added with max resources (D:" + d +
                        ", C:" + c + ", F:" + f + ").\n");

                // Clear inputs
                shipId.clear();
                maxDocks.clear();
                maxCranes.clear();
                maxForklifts.clear();

                // Check if system is still in safe state
                updateSafeState(safeSequenceArea);
            } catch (NumberFormatException ex) {
                logArea.appendText("Invalid input. Please enter numeric values.\n");
            }
        });

        // Process Resource Request
        requestBtn.setOnAction(e -> {
            try {
                String id = selectedShipField.getText().trim();
                int d = Integer.parseInt(reqDocks.getText());
                int c = Integer.parseInt(reqCranes.getText());
                int f = Integer.parseInt(reqForklifts.getText());

                // Validate inputs
                if (id.isEmpty()) {
                    logArea.appendText("Error: No ship selected.\n");
                    return;
                }

                if (d < 0 || c < 0 || f < 0) {
                    logArea.appendText("Error: Request cannot be negative.\n");
                    return;
                }

                // Request resources using Banker's Algorithm
                boolean result = banker.requestResources(id, d, c, f);

                if (result) {
                    // Update allocated resources
                    for (Ship ship : ships) {
                        if (ship.getId().equals(id)) {
                            ship.updateAllocated(d, c, f);
                            break;
                        }
                    }

                    // Refresh table to show updated allocations and apply styling
                    shipsTable.refresh();

                    // Update total resources display
                    allocatedDocks += d;
                    allocatedCranes += c;
                    allocatedForklifts += f;
                    updateResourceBars();

                    logArea.appendText("Resource request granted for Ship " + id +
                            ": D:" + d + ", C:" + c + ", F:" + f + "\n");

                    // Show current available resources
                    int[] available = banker.getAvailable();
                    logArea.appendText("Current available resources - D:" + available[0] +
                            ", C:" + available[1] + ", F:" + available[2] + "\n");
                } else {
                    logArea.appendText("Resource request DENIED for Ship " + id +
                            ": Would cause unsafe state (potential deadlock).\n");
                }

                // Clear request inputs
                reqDocks.clear();
                reqCranes.clear();
                reqForklifts.clear();

                // Update available resources label
                updateAvailableResourcesLabel();

                // Update safe state
                updateSafeState(safeSequenceArea);

                // Show algorithm state for debugging
                logArea.appendText("\nAlgorithm State:\n" + banker.getStateInfo() + "\n");
            } catch (NumberFormatException ex) {
                logArea.appendText("Invalid input. Please enter numeric values.\n");
            }
        });

        // Release Resources
        releaseBtn.setOnAction(e -> {
            try {
                String id = selectedShipField.getText().trim();
                int d = Integer.parseInt(reqDocks.getText());
                int c = Integer.parseInt(reqCranes.getText());
                int f = Integer.parseInt(reqForklifts.getText());

                // Validate inputs
                if (id.isEmpty()) {
                    logArea.appendText("Error: No ship selected.\n");
                    return;
                }

                if (d < 0 || c < 0 || f < 0) {
                    logArea.appendText("Error: Released resources cannot be negative.\n");
                    return;
                }

                // Release resources
                boolean result = banker.releaseResources(id, d, c, f);

                if (result) {
                    // Update allocated resources in UI
                    for (Ship ship : ships) {
                        if (ship.getId().equals(id)) {
                            ship.updateAllocated(-d, -c, -f);
                            break;
                        }
                    }

                    // Refresh table to show updated allocations
                    shipsTable.refresh();

                    // Update total resources display
                    allocatedDocks -= d;
                    allocatedCranes -= c;
                    allocatedForklifts -= f;
                    updateResourceBars();

                    logArea.appendText("Resources released from Ship " + id +
                            ": D:" + d + ", C:" + c + ", F:" + f + "\n");
                } else {
                    logArea.appendText(
                            "Error: Could not release resources. Check that ship exists and has sufficient allocated resources.\n");
                }

                // Clear request inputs
                reqDocks.clear();
                reqCranes.clear();
                reqForklifts.clear();

                // Update safe state (always safe after release)
                updateSafeState(safeSequenceArea);

                // Update available resources label
                updateAvailableResourcesLabel();

                // Show algorithm state for debugging
                logArea.appendText("\nAlgorithm State:\n" + banker.getStateInfo() + "\n");
            } catch (NumberFormatException ex) {
                logArea.appendText("Invalid input. Please enter numeric values.\n");
            }
        });

        // Remove Ship
        removeShipBtn.setOnAction(e -> {
            String id = selectedShipField.getText().trim();

            if (id.isEmpty()) {
                logArea.appendText("Error: No ship selected.\n");
                return;
            }

            // Remove ship from banker's algorithm
            boolean result = banker.removeShip(id);

            if (result) {
                // Find ship in UI list
                Ship shipToRemove = null;
                for (Ship ship : ships) {
                    if (ship.getId().equals(id)) {
                        shipToRemove = ship;

                        // Update total resources
                        allocatedDocks -= ship.getAllocatedDocks();
                        allocatedCranes -= ship.getAllocatedCranes();
                        allocatedForklifts -= ship.getAllocatedForklifts();
                        break;
                    }
                }

                if (shipToRemove != null) {
                    ships.remove(shipToRemove);
                }

                updateResourceBars();
                selectedShipField.clear();
                logArea.appendText("Ship " + id + " removed from port.\n");
            } else {
                logArea.appendText("Error: Could not remove ship. Ship ID not found.\n");
            }

            // Update safe state (always safe after removal)
            updateSafeState(safeSequenceArea);
        });

        // SplitPane Layout
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.getItems().addAll(controlPanel, tableArea);
        splitPane.setDividerPositions(0.3);

        Scene scene = new Scene(splitPane, 1300, 800);
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

        // Update available resources
        updateAvailableResourcesLabel();

        // Update color based on resource utilization
        if ((double) allocatedDocks / totalDocks > 0.8 ||
                (double) allocatedCranes / totalCranes > 0.8 ||
                (double) allocatedForklifts / totalForklifts > 0.8) {
            dockProgress.setStyle("-fx-accent: orange;");
            craneProgress.setStyle("-fx-accent: orange;");
            forkliftProgress.setStyle("-fx-accent: orange;");
        } else {
            dockProgress.setStyle("-fx-accent: green;");
            craneProgress.setStyle("-fx-accent: green;");
            forkliftProgress.setStyle("-fx-accent: green;");
        }
    }

    /**
     * Updates the available resources label with current values from the banker
     * algorithm
     */
    private void updateAvailableResourcesLabel() {
        if (banker != null) {
            int[] available = banker.getAvailable();
            availableResourcesLabel.setText("Available Resources: D:" + available[0] +
                    ", C:" + available[1] + ", F:" + available[2]);
        }
    }

    /**
     * Updates the safe state display and checks if the system is in a safe state
     * 
     * @param safeSequenceArea TextArea to display the safe sequence
     */
    private void updateSafeState(TextArea safeSequenceArea) {
        boolean isSafe = banker.isSafe();

        if (isSafe) {
            statusLabel.setText("System State: SAFE");
            statusLabel.setTextFill(Color.GREEN);

            // Generate a safe sequence display
            StringBuilder sequence = new StringBuilder("Safe Sequence: ");
            List<String> safeShips = banker.getSafeSequence();
            boolean first = true;

            for (String shipId : safeShips) {
                if (!first) {
                    sequence.append(" → ");
                }
                sequence.append(shipId);
                first = false;

                // Add needed resources information for each ship in the sequence
                int[] needed = banker.getNeeded(shipId);
                if (needed != null) {
                    sequence.append(" (Needs: D:" + needed[0] +
                            ", C:" + needed[1] +
                            ", F:" + needed[2] + ")");
                }
            }

            safeSequenceArea.setText(sequence.toString());
        } else {
            statusLabel.setText("System State: UNSAFE");
            statusLabel.setTextFill(Color.RED);
            safeSequenceArea.setText("No safe sequence exists. System at risk of deadlock!");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Shows a dialog for setting up the total resources
     * 
     * @return true if setup completed successfully, false otherwise
     */
    private boolean showResourceSetupDialog() {
        // Create a dialog
        Stage dialog = new Stage();
        dialog.setTitle("Port Resource Setup");
        dialog.setResizable(false);

        // Create input fields for resources
        Label titleLabel = new Label("Enter total port resources:");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TextField docksField = new TextField("");
        docksField.setPromptText("Total Docks");

        TextField cranesField = new TextField("");
        cranesField.setPromptText("Total Cranes");

        TextField forkliftsField = new TextField("");
        forkliftsField.setPromptText("Total Forklifts");

        Button confirmButton = new Button("Confirm");
        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);

        // Layout
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(
                titleLabel,
                new HBox(10, new Label("Docks:     "), docksField),
                new HBox(10, new Label("Cranes:    "), cranesField),
                new HBox(10, new Label("Forklifts: "), forkliftsField),
                errorLabel,
                confirmButton);

        // Button action
        confirmButton.setOnAction(e -> {
            try {
                int docks = Integer.parseInt(docksField.getText().trim());
                int cranes = Integer.parseInt(cranesField.getText().trim());
                int forklifts = Integer.parseInt(forkliftsField.getText().trim());

                // Validate input
                if (docks <= 0 || cranes <= 0 || forklifts <= 0) {
                    errorLabel.setText("All resources must be positive numbers.");
                    return;
                }

                // Set the resource values
                totalDocks = docks;
                totalCranes = cranes;
                totalForklifts = forklifts;

                // Close the dialog
                dialog.close();
            } catch (NumberFormatException ex) {
                errorLabel.setText("Please enter valid numbers.");
            }
        });

        // Show dialog and wait for it to close
        Scene scene = new Scene(layout);
        dialog.setScene(scene);
        dialog.showAndWait();

        // Return true if resources have been set (values > 0)
        return totalDocks > 0 && totalCranes > 0 && totalForklifts > 0;
    }
}
