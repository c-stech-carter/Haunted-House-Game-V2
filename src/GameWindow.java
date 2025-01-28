import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class GameWindow extends Application {

    // Room data storage
    private Map<String, Room> rooms;
    private Room currentRoom;

    // UI components
    private ImageView backgroundView;
    private TextArea descriptionArea;
    private Menu exitsMenu;
    private FlowPane itemIconsPane;

    @Override
    public void start(Stage primaryStage) {
        // Root layout
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: dimgray;");

        // Game display area (Top)
        StackPane gameArea = new StackPane();
        gameArea.setPrefSize(1300, 743);

        backgroundView = new ImageView();
        backgroundView.setPreserveRatio(true);
        backgroundView.setFitWidth(1300);
        backgroundView.setFitHeight(743);
        gameArea.getChildren().add(backgroundView);
        gameArea.setAlignment(Pos.CENTER);
        root.setCenter(gameArea);

        // MenuBar
        MenuBar menuBar = new MenuBar();
        Menu actionsMenu = new Menu("Actions");
        MenuItem lookItem = new MenuItem("Look");
        lookItem.setOnAction(e -> handleLookAction());
        actionsMenu.getItems().add(lookItem);

        exitsMenu = new Menu("Exits");
        menuBar.getMenus().addAll(actionsMenu, exitsMenu);
        root.setTop(menuBar);

        // Description area (Bottom)
        // Description area
        descriptionArea = new TextArea();
        descriptionArea.setWrapText(true);
        descriptionArea.setEditable(false);
        descriptionArea.setPrefHeight(150);
        descriptionArea.setPrefWidth(600);
        descriptionArea.setStyle("-fx-control-inner-background: black; -fx-text-fill: yellow;");

        ScrollPane textScrollPane = new ScrollPane(descriptionArea);
        textScrollPane.setFitToWidth(true);
        textScrollPane.setFitToHeight(false);

        // Item Icons
        itemIconsPane = new FlowPane();
        itemIconsPane.setPadding(new Insets(10));
        itemIconsPane.setHgap(10);
        itemIconsPane.setVgap(10);
        itemIconsPane.setStyle("-fx-background-color: #333333; -fx-border-color: black; -fx-border-width: 2;");
        itemIconsPane.setPrefHeight(150);
        itemIconsPane.setPrefWidth(400);

        ScrollPane itemScrollPane = new ScrollPane(itemIconsPane);
        itemScrollPane.setFitToWidth(true);
        itemScrollPane.setFitToHeight(false);

        // Combine description and item panes in HBox
        HBox bottomBox = new HBox();
        bottomBox.setSpacing(10);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(10));
        bottomBox.getChildren().addAll(textScrollPane, itemScrollPane);

        root.setBottom(bottomBox);

        // Initialize Rooms
        initializeRooms();
        updateRoom("Front Yard");

        // Scene and Stage setup
        Scene scene = new Scene(root, 1320, 950, Color.DIMGRAY);
        primaryStage.setTitle("Haunted House Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Initialize the rooms with descriptions, exits, and items.
     */
    private void initializeRooms() {
        rooms = new HashMap<>();

        Room frontYard = new Room(
                "Front Yard",
                "\tThe last thing you remember is an intense pain to the back of your head, " +
                        "and after that, darkness for a time, where you were barely aware of yourself.   " +
                        "Beyond that, there are no memories, but echoes still dance in your mind, unable " +
                        "to be fully retrieved.    Now as your vision returns you find yourself standing " +
                        "in the yard of a very large foreboding house.   A very strange realization is forced " +
                        "on your mind, that this place *isn't real*...   and yet, you can feel the cold biting at your skin.  " +
                        "Looking at the home, it seems many lights are on, but no movement can be seen through any of them. " +
                        "Whether or not this place is situated in reality, something tells you are trapped here" +
                        "by a real force, and the only way to wake from this dream is to enter the house. The way " +
                        "out of this realm is hidden inside, somewhere.",
                "\tThe path up to the house is well-trodden, and you see many footprints leading " +
                        "up to the porch and the front door.   Could it be that many a person has stood where you do now?   " +
                        "All of the footprints start around where you are standing, and there are " +
                        "none leading away from the house.   Something tells you much worse things " +
                        "could be waiting if you were to walk into the woods.",
                "file:src/resources/image/FrontYard.png",
                new String[]{"Front Hall"},
                false,
                new String[]{}
        );

        Room frontHall = new Room(
                "Front Hall",
                "\tYou step into the grand front hall of the mansion. Dust swirls in the dim light..." +
                        "  something is very wrong here, but you can't quite tell what exactly.",
                "\tYou stand in the Front Hall of the house.  Calling out does nothing and no human " +
                        "presence  arrives to the room. The door you entered this place with, has somehow locked " +
                        "itself, and no force you can muster can open it. Many lights are on but there is silence, " +
                        "a suffocating silence that you can almost feel in your throat.  The room itself is " +
                        "quite beautiful,  a grand staircase goes to the upper floor, a chandelier hangs from " +
                        "the ceiling.  A door on the left goes to a parlor, and a door behind you goes to what " +
                        "seems to be a dining room.   Two paintings hang here on the walls, one of a sad looking " +
                        "young woman, and the other of a woman who seems to be in severe distress.  " +
                        "Her hair is shorn, and her clothing is gray.",
                "file:src/resources/image/FrontHall.png",
                new String[]{"Parlor", "Dining Room"},
                true,
                new String[]{}
        );

        rooms.put("Front Yard", frontYard);
        rooms.put("Front Hall", frontHall);
        // Add more rooms here...
    }

    /**
     * Updates the current room display and UI elements.
     * @param roomName Name of the room to switch to.
     */
    private void updateRoom(String roomName) {
        Room room = rooms.get(roomName);
        if (room == null) return;

        currentRoom = room;
        backgroundView.setImage(new Image(room.getImagePath()));
        descriptionArea.setText(room.getInitialDescription());

        // Update exits menu
        exitsMenu.getItems().clear();
        for (String exit : room.getExits()) {
            MenuItem exitItem = new MenuItem(exit);
            exitItem.setOnAction(e -> updateRoom(exit));
            exitsMenu.getItems().add(exitItem);
        }

        // Update items
        itemIconsPane.getChildren().clear();
        for (String item : room.getItems()) {
            Label itemLabel = new Label(item);
            itemLabel.setStyle("-fx-text-fill: white; -fx-background-color: black;");
            itemIconsPane.getChildren().add(itemLabel);
        }
    }

    /**
     * Handles the "Look" action to display the room's secondary description.
     */
    private void handleLookAction() {
        if (currentRoom != null) {
            descriptionArea.setText(currentRoom.getLookDescription());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
