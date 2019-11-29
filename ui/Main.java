package ui;

import game_manager.GameManager;
import game_manager.Player;
import game_map.GameMap;
import game_map.Tile;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import units.Unit;

public class Main extends Application {

	Unit selectedUnit = null;
	
	double zoom = 1; //How much bigger in one dimension are you
	
	final int scalingFactor = 10;
	final int mapRows = 100;
	final int mapCols = 60;
	final int canvasDimensionX = mapRows * scalingFactor;
	final int canvasDimensionY = mapCols * scalingFactor;
	double cameraX = mapRows / 2.0;
	double cameraY = mapCols / 2.0;
	
	double dragStartX, dragStartY;

	Image hammerImage = new Image("file:C:\\Users\\lmqtfx\\eclipse-workspace\\patience\\src\\ui\\hammer_transparent.png");
	
	
	public void render(GraphicsContext gc, GameManager game, Text t) {
		GameMap temp = game.getOmnimap();
		
		Tile[][] terrain = temp.getTerrain();//new Tile[mapRows][mapCols];
		
		//System.out.println("got hammerImage " + hammerImage.getHeight() + ' ' +  hammerImage.getRequestedHeight());
		for (int i = 0; i < mapRows; i++) {
			for (int j = 0; j < mapCols; j++) {
				if (temp.getMobileUnits()[i][j] != null) {
					//terrain = temp.getMobileUnits()[i][j].getKnown().getTerrain();
				}
			}
		}
		//Tile[][] terrain = temp.getTerrain();
		
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, canvasDimensionX, canvasDimensionY);
		
		for (int i = 0; i < mapRows; i++) {
			for (int j = 0; j < mapCols; j++) {
				if (terrain[i][j] == Tile.CLEAR) {
					//Paint one color
					gc.setFill(Color.LIGHTGREEN);
				} else if (terrain[i][j] == Tile.BLOCKED) {
					gc.setFill(Color.DARKGREY);
				} else if (terrain[i][j] == Tile.MINERALS) {
					gc.setFill(Color.RED);
				} else if (terrain[i][j] == Tile.UNKNOWN) {
					gc.setFill(Color.BLACK);
				}
				
				double ulxabs = i; //upper left x, absolute terms
				double ulyabs = j;
				double xrel = ulxabs;
				double yrel = ulyabs;
				
				double totalScaling = scalingFactor * zoom;
				xrel *= totalScaling; yrel *= totalScaling;
				
				
				double topLeft = cameraX * totalScaling - canvasDimensionX / 2;
				double topRight = cameraY * totalScaling - canvasDimensionY / 2;
				
				//System.out.println("before clamp have top left and right as " + topLeft + " " + topRight);
				/*/
				topLeft = Math.max(0, topLeft); topRight = Math.max(0, topRight);
				
				if (topLeft + canvasDimensionX > mapRows * totalScaling) {
					//Clamp it down
					topLeft = mapRows * totalScaling - canvasDimensionX;
				}
				if (topRight + canvasDimensionY > mapCols * totalScaling) {
					topRight = mapCols * totalScaling - canvasDimensionY;
				}*/
				
				//Recalculate cameraX and cameraY to clamp it, essentially, so player never sees anyting
				//that is "out of bounds" of the map
				
				cameraX = (topLeft + canvasDimensionX / 2) / totalScaling;
				cameraY = (topRight + canvasDimensionY / 2) / totalScaling;
				
				//System.out.println("cameraX and cameraY are now " + cameraX + " " + cameraY);
				
				if (temp.getMobileUnits()[i][j] != null) {
					gc.setFill(Color.WHITE);
					//gc.drawImage(hammerImage, scalingFactor * i, scalingFactor * j, scalingFactor, scalingFactor);
				}
				gc.fillRect(xrel - topLeft, yrel - topRight, totalScaling, totalScaling);
				
				if (temp.getMobileUnits()[i][j] != null) {
					//System.out.println("drawing img");
					gc.drawImage(hammerImage, xrel - topLeft, yrel - topRight, totalScaling, totalScaling);
				}
				
			}
		}
		
		//The x, y here are inverted (x is across columns).
		//gc.setFill(Color.WHITE);
		//gc.fillRect(40 * scalingFactor, 20 * scalingFactor, 10 * scalingFactor, 10 * scalingFactor);
		
		Player us = game.getPlayers()[0];
		t.setText("Food: " + us.getFood() + '\n' + "Minerals: " + us.getMinerals() + 
				'\n' + "Wealth: " + us.getWealth());
		
		//System.out.println("rerender done");
	}
	public void update(GraphicsContext gc, GameManager game, Text t) {
		game.turn();
		render(gc, game, t);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		// TODO Auto-generated method stub
		stage.setTitle("test");
		
		Group root = new Group();
		Scene s = new Scene(root, mapRows * scalingFactor + 100, mapCols * scalingFactor + 100, Color.WHITE);
		
		

		final Canvas canvas = new Canvas(canvasDimensionX, canvasDimensionY);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		
		GameManager game = new GameManager(mapRows, mapCols, 2);
		
		root.getChildren().add(canvas);
		
		Text testingText = new Text();
		
		testingText.setTranslateX(mapRows * scalingFactor + 10);
		testingText.setTranslateY(10);
		root.getChildren().add(testingText);
		
		canvas.setOnMouseClicked(
				event -> {
					/*
					if (selectedUnit == null && event.getButton() == MouseButton.SECONDARY) {
						//Dragging the map around
						dragStartX = event.getSceneX();
						dragStartY = event.getSceneY();

						System.out.println("found start pos as " + dragStartX + ' ' + dragStartY);
						return;
					}*/
					
					
					int clickedRow = (int)(event.getSceneY() / scalingFactor);
					int clickedCol = (int)(event.getSceneX() / scalingFactor);
					testingText.setText(testingText.getText() + "\nclicked " + 
							(int)(event.getSceneX() / scalingFactor) + " " + (int)(event.getSceneY() / scalingFactor));
					
					//gc.setFill(Color.WHITE);
					//gc.fillRect(clickedCol * scalingFactor, clickedRow * scalingFactor, scalingFactor, scalingFactor);
					
					//Try to select the mobile unit first
					Unit mobileOnClick = game.getOmnimap().getMobileUnits()[clickedCol][clickedRow];
					Unit staticOnClick = game.getOmnimap().getStaticUnits()[clickedCol][clickedRow];

					if (selectedUnit == null) {
						if (mobileOnClick == null) {
							selectedUnit = staticOnClick;
						} else {
							selectedUnit = mobileOnClick;
						}
					} else {
						if (mobileOnClick == null) {
							//Check if it's the same unit, if yes then deselect
							selectedUnit = staticOnClick;
						} else {
							if (selectedUnit == mobileOnClick && staticOnClick != null) {
								//Shift to the static unit
								selectedUnit = staticOnClick;
							} else {
								selectedUnit = mobileOnClick;
							}
						}
					}
					testingText.setText(testingText.getText() + (selectedUnit != null ? "\nselected unit " + selectedUnit.getId() : ""));
					//game.getOmnimap().getMobileUnits()[clickedRow][clickedCol] = new Soldier(0, 1, 1, 1, null, 1, 1, 1);
					
				});
		
		canvas.setOnScroll(e -> {
			System.out.println("detected scroll " + e.getDeltaY());
			
			boolean neg = e.getDeltaY() < 0;
			if (!neg) {
				//System.out.println("val is " + (1 + (0.01 * e.getDeltaY() / 2)));
				zoom *= (1 + (0.01 * e.getDeltaY() / 2));
			} else {
				zoom *= 1 / ((1 - (0.01 * e.getDeltaY() / 2)));
			}
			//zoom += e.getDeltaY() / 10;
			
			zoom = Math.max(1, Math.min(10, zoom));
			
			System.out.println("zoom is now " + zoom);
			
			render(gc, game, testingText);
		});
		
		canvas.setOnMousePressed(e -> {
			if (selectedUnit == null && e.getButton() == MouseButton.SECONDARY) {
				//Dragging the map around
				dragStartX = e.getSceneX();
				dragStartY = e.getSceneY();

				System.out.println("found start pos as " + dragStartX + ' ' + dragStartY);
				return;
			}
		});
		canvas.setOnMouseDragged(e -> {
			
			//System.out.println("got a mouse drag");
			if (selectedUnit != null) return;
			if (e.getButton() != MouseButton.SECONDARY) return;
			
			double dragDeltaX = e.getSceneX() - dragStartX;
			double dragDeltaY = e.getSceneY() - dragStartY;
			
			cameraX += -dragDeltaX / (scalingFactor * zoom);
			cameraY += -dragDeltaY / (scalingFactor * zoom);
			
			dragStartX = e.getSceneX();
			dragStartY = e.getSceneY(); //So that we adjust relative to prev pos
			
			render(gc, game, testingText);
		});
		
		Button turnButton = new Button();
		turnButton.setText("Next turn");
		turnButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				for (int i = 0; i < 10; i++) {
					update(gc, game, testingText);
				//	System.out.println("updated");
				}
			}
		});
		turnButton.setTranslateX(mapRows * scalingFactor + 10);
		turnButton.setTranslateY(mapCols * scalingFactor + 10);
		root.getChildren().add(turnButton);
		
		
		

		System.out.println("initial terrain: ");
		render(gc, game, testingText);
		
		stage.setScene(s);
		
		stage.show();
	}

}
