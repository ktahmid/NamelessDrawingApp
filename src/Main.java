import javafx.application.Application;import javafx.application.Platform;import javafx.geometry.Insets;import javafx.geometry.Pos;import javafx.scene.Cursor;import javafx.scene.Scene;import javafx.scene.control.*;import javafx.scene.image.Image;import javafx.scene.image.ImageView;import javafx.scene.layout.*;import javafx.scene.paint.Color;import javafx.scene.shape.Line;import javafx.scene.text.Font;import javafx.stage.FileChooser;import javafx.stage.Modality;import javafx.stage.Stage;import java.io.File;public class Main extends Application {    // UI components    private MenuBar menuBar = new MenuBar();     // the menu bar    private GridPane toolbar = new GridPane();    // the toolbar    private ToggleGroup toolbarGroup = new ToggleGroup(); // toggle group for the tools, so that at most one tool can be selected at a time    private VBox toolbarHolder = new VBox(5);    private AnchorPane toolbarSection = new AnchorPane(toolbarHolder);  // left pane of the app    public static Pane canvas;    private Pane hintCanvas = new Pane();    public static Pane canvasBackground = new Pane();    private ScrollPane canvasSection = new ScrollPane(canvasBackground);  // middle pane of the app; scrollable    private HBox statusBar = new HBox();  // status bar at the bottom    private AnchorPane optionsSection = new AnchorPane();  // right pane of the app; scrollable    private BorderPane appBody = new BorderPane(canvasSection, null, optionsSection, statusBar, toolbarSection);  // container for everything except the menu bar    private VBox root = new VBox(menuBar, appBody);  // root node; container for everything    // Menus    private Menu menuFile = new Menu("File");    private Menu menuEdit = new Menu("Edit");    private Menu menuHelp = new Menu("Help");    // File menu items    private MenuItem mitemNew = new MenuItem("New…");    private MenuItem mitemOpen = new MenuItem("Open…");    private MenuItem mitemSave = new MenuItem("Save");    private MenuItem mitemSaveAs = new MenuItem("Save As…");    private MenuItem mitemExport = new MenuItem("Export to SVG…");    private MenuItem mitemExit = new MenuItem("Exit");    // Edit menu items    private MenuItem mitemUndo = new MenuItem("Undo");    private MenuItem mitemRedo = new MenuItem("Redo");    private MenuItem mitemClear = new MenuItem("Clear");    // Help menu items    private MenuItem mitemAbout = new MenuItem("About");    // Drawing tools    private class ToolButton extends ToggleButton {        ToolButton(String name, String iconFilename) {            super();            ImageView view = new ImageView(new Image("icons/" + iconFilename));            view.setPreserveRatio(true); view.setFitWidth(25); this.setGraphic(view); this.setPrefWidth(50);            this.setTooltip(new Tooltip(name));  // add a mouse-over tooltip that says the tool's name            setToggleGroup(toolbarGroup);  // add it to the toolbar's toggle group            this.setUserData(name);        }    }    private ToolButton toolbtnLine = new ToolButton("Line", "line.png");    private ToolButton toolbtnArrow = new ToolButton("Arrow", "arrow.png");    private ToolButton toolbtnPolyline = new ToolButton("Polyline", "polyline.png");    private ToolButton toolbtnArc = new ToolButton("Arc", "arc.png");    private ToolButton toolbtnEllipse = new ToolButton("Ellipse", "ellipse.png");    // Helpers    private UIHandlers uihandlers = new UIHandlers(canvas, canvasBackground, 10, 10);    private DrawingHelper d = new DrawingHelper(canvas);    private DrawingHandlers dhandlers = new DrawingHandlers(d, canvas, hintCanvas);    private History hist;    @Override    public void start(Stage primaryStage) throws Exception {        primaryStage.setTitle("JDrawpad");        primaryStage.getIcons().add(new Image("icons/logo.png"));        createCanvas(600,500);        setupMenus(primaryStage);        setupToolbar();        setupOptionsArea();        setupStatusBar();        toolbtnLine.setOnAction(click -> dhandlers.handleLineDrawing());        toolbtnArrow.setOnAction(click -> dhandlers.handleArrowDrawing());        toolbtnPolyline.setOnAction(click -> dhandlers.handlePolylineDrawing());        toolbtnArc.setOnAction(click -> dhandlers.handleArcDrawing());        toolbtnEllipse.setOnAction(click -> dhandlers.handleEllipseDrawing());        primaryStage.setScene(new Scene(root, 1000, 500));        primaryStage.show();    }    private void showNewFileDialog() {        Stage newFileDialog = new Stage();        newFileDialog.getIcons().add(new Image("icons/logo.png"));        newFileDialog.setTitle("Create a new file");        newFileDialog.setResizable(false);        newFileDialog.initModality(Modality.APPLICATION_MODAL);        Label instruction = new Label("Enter the dimensions of the canvas in pixels.");        HBox topPart = new HBox(instruction);        Label lblWidth = new Label("Width: ");        Label multiplicationSign = new Label(" × ");        Label lblHeight = new Label("Height: ");        TextField tfWidth = new TextField();        tfWidth.setPrefColumnCount(5);        TextField tfHeight = new TextField();        tfHeight.setPrefColumnCount(5);        HBox middlePart = new HBox(lblWidth, tfWidth, multiplicationSign, lblHeight, tfHeight);        middlePart.setAlignment(Pos.CENTER);        Button btnCancel = new Button("Cancel");        btnCancel.setCancelButton(true);        btnCancel.setOnAction(e -> newFileDialog.close());        Button btnCreate = new Button("Create");        btnCreate.setDefaultButton(true);        btnCreate.setOnAction(e -> {            String width = tfWidth.getText(), height = tfHeight.getText();            if (width.matches("^[1-9]\\d*") && height.matches("^[1-9]\\d*")) {  // check valid input using regex                createCanvas(Integer.parseInt(width), Integer.parseInt(height));                newFileDialog.close();            }        });        HBox bottomPart = new HBox(10, btnCancel, btnCreate);        bottomPart.setAlignment(Pos.CENTER_RIGHT);        VBox dialogHolder = new VBox(20, topPart, middlePart, bottomPart);        dialogHolder.setPadding(new Insets(10, 10, 10, 10));        newFileDialog.setScene(new Scene(dialogHolder));        newFileDialog.show();        newFileDialog.requestFocus();    }    public void createCanvas(int width, int height) {        hintCanvas.setPrefSize(width, height);        canvasBackground.setPrefSize(width, height);        // Clear everything and add canvas and hintCanvas to canvasBackground        canvasBackground.getChildren().clear();        hintCanvas.getChildren().clear();        canvasBackground.getChildren().add(hintCanvas);        // Make the canvas background white with a gray border        canvasBackground.setStyle("-fx-background-color: #FFFFFF;");        canvasBackground.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));        // Draw the grid lines/marks in canvasBackground        drawGridMarks(width, height);        canvas = new Pane();        canvas.setPrefSize(width, height);        canvas.setCursor(Cursor.CROSSHAIR);        canvasBackground.getChildren().add(canvas);        // The canvasBackground has to be manually centered within the scrollable        // canvasSection, else the canvasBackground (and hence the canvas itself)        // appears at the top-left corner of the scrollable canvasSection.        canvasBackground.translateXProperty().bind(                canvasSection.widthProperty()                        .subtract(canvasBackground.widthProperty())                        .divide(2)        );        canvasBackground.translateYProperty().bind(                canvasSection.heightProperty()                        .subtract(canvasBackground.heightProperty())                        .divide(2)        );    }    private void drawGridMarks(int width, int height) {        // x-axis        Line gridLine = new Line(0,height/2, width,height/2);        gridLine.setStrokeWidth(1); gridLine.setStroke(Color.GRAY);        canvasBackground.getChildren().add(gridLine);        // y-axis        gridLine.setStartX(width/2); gridLine.setStartY(0);        gridLine.setEndX(width/2); gridLine.setEndY(height);        canvasBackground.getChildren().add(gridLine);        gridLine.setStrokeWidth(0.5); gridLine.setStroke(Color.LIGHTGRAY);        // horizontal marks        for (int y = 0; y < height; y += 10) {            gridLine.setStartX(0); gridLine.setStartY(y);            gridLine.setEndX(width); gridLine.setEndY(y);            canvasBackground.getChildren().add(gridLine);        }        // vertical marks        for (int x = 0; x < width; x += 10) {            gridLine.setStartX(x); gridLine.setStartY(0);            gridLine.setEndX(x); gridLine.setEndY(height);            canvasBackground.getChildren().add(gridLine);        }    }    private void setupMenus(Stage primaryStage) {        menuFile.getItems().addAll(                mitemNew,                mitemOpen,                mitemSaveAs,                mitemExport,                mitemExit        );        menuEdit.getItems().addAll(mitemUndo, mitemRedo, mitemClear);        mitemNew.setOnAction(e -> showNewFileDialog());        mitemExit.setOnAction(e -> Platform.exit());        mitemOpen.setOnAction(e -> {            FileChooser csvFileChooser = new FileChooser();            File csvFile = csvFileChooser.showOpenDialog(primaryStage);            if (csvFile != null) {                FileUtils.readFileToCanvas(csvFile, canvas);            }        });        mitemExport.setOnAction(e -> {            FileChooser svgFileChooser = new FileChooser();            File svgFile = svgFileChooser.showSaveDialog(primaryStage);            if (svgFile != null) {                FileUtils.exportToSvgFile(svgFile, canvas);            }        });        mitemSaveAs.setOnAction(e -> {            FileChooser fileChooser = new FileChooser();            File outFile = fileChooser.showSaveDialog(primaryStage);            if (outFile != null) {                FileUtils.saveToFile(outFile, canvas);            }        });        mitemClear.setOnAction(e -> {            canvas.getChildren().clear();        });        menuBar.getMenus().addAll(menuFile, menuEdit, menuHelp);    }    private void setupToolbar() {        toolbar.getColumnConstraints().add(new ColumnConstraints(50)); // for 1st column in the toolbar        toolbar.getColumnConstraints().add(new ColumnConstraints(50)); // for 2nd column        toolbar.addColumn(0, toolbtnLine, toolbtnArrow, toolbtnPolyline);        toolbar.addColumn(1, toolbtnArc, toolbtnEllipse);        Label toolsLabel = new Label("Tools");        toolsLabel.setFont(new Font(18));        toolbarHolder.getChildren().addAll(toolsLabel, toolbar);        AnchorPane.setLeftAnchor(toolbarHolder, 20.0);        AnchorPane.setTopAnchor(toolbarHolder, 20.0);        AnchorPane.setRightAnchor(toolbarHolder, 20.0);    }    private void setupStatusBar() {        Label lblX = new Label("X: ");        Label lblY = new Label("Y: ");        Label lblXCoord = new Label("--");        Label lblYCoord = new Label("--");        statusBar.getChildren().addAll(lblX, lblXCoord, new Label(",  "), lblY, lblYCoord);        statusBar.setPadding(new Insets(5,0,5,0));        statusBar.setAlignment(Pos.CENTER);        uihandlers.showCoordsInStatusBar(lblXCoord, lblYCoord, canvasBackground);    }    private void setupOptionsArea() {        optionsSection.setPrefWidth(90);        optionsSection.prefHeightProperty().bind(appBody.heightProperty());    }    public static void main(String[] args) {        launch(args);    }}