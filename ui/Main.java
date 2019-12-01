package ui;

import java.util.Queue;

import game_manager.GameManager;
import game_manager.Player;
import game_map.GameMap;
import game_map.Tile;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;
import orders.MoveOrder;
import orders.Order;
import units.City;
import units.Unit;
import units.Worker;
import utilities.Algorithms;



/*
 * 
 * 
 * 
 * 
 * READ THESE
 * 
 * 
 * 
 * https://docs.oracle.com/javafx/2/layout/builtin_layouts.htm
 * 
 * 
 * 
 * 
 * 
 */


public class Main extends Application {

	Unit selectedUnit = null;
	
	double zoom = 1; //How much bigger in one dimension are you
	
	//you may be wondering: why are these final ints not capitalized? well, it's because they won't be final later, they will
	//be changeable.
	
	final int scalingFactor = 10;
	final int mapRows = 100;
	final int mapCols = 60;
	final int numPlayers = 3000;
	final int canvasDimensionX = mapRows * scalingFactor;
	final int canvasDimensionY = mapCols * scalingFactor;
	double cameraX = mapRows / 2.0;
	double cameraY = mapCols / 2.0;
	
	double dragStartX, dragStartY;

	Image hammerImage = new Image("file:C:\\Users\\lmqtfx\\eclipse-workspace\\patience\\src\\ui\\hammer_transparent.png");
	Image sbeve = new Image("file:C:\\Users\\lmqtfx\\eclipse-workspace\\patience\\src\\ui\\sbeve.png");
	
	boolean shiftPressed = false;
	
	//copied from https://stackoverflow.com/questions/35751576/javafx-draw-line-with-arrow-canvas
	final int arrowSize = 8;
	
	void drawArrow(GraphicsContext gc, double x1, double y1, double x2, double y2) {
	    gc.setFill(Color.WHITE);
	    gc.setStroke(Color.WHITE);

	    double dx = x2 - x1, dy = y2 - y1;
	    double angle = Math.atan2(dy, dx);
	    int len = (int) Math.sqrt(dx * dx + dy * dy);

	    Transform transform = Transform.translate(x1, y1);
	    transform = transform.createConcatenation(Transform.rotate(Math.toDegrees(angle), 0, 0));
	    gc.setTransform(new Affine(transform));

	    gc.strokeLine(0, 0, len, 0);
	    gc.fillPolygon(new double[]{len, len - arrowSize, len - arrowSize, len}, new double[]{0, -arrowSize, arrowSize, 0},
	            4);
	    
	    gc.setTransform(new Affine());
	}
	//end copied code
	
	
	double[] cellToScreen(double i, double j) {
		double totalScaling = scalingFactor * zoom;
		i *= totalScaling; j *= totalScaling;
		
		
		double topLeftX = cameraX * totalScaling - canvasDimensionX / 2;
		double topLeftY = cameraY * totalScaling - canvasDimensionY / 2;
		
		//System.out.println("before clamp have top left and right as " + topLeftX + " " + topLeftY);
		/*/
		topLeftX = Math.max(0, topLeftX); topLeftY = Math.max(0, topLeftY);
		
		if (topLeftX + canvasDimensionX > mapRows * totalScaling) {
			//Clamp it down
			topLeftX = mapRows * totalScaling - canvasDimensionX;
		}
		if (topLeftY + canvasDimensionY > mapCols * totalScaling) {
			topLeftY = mapCols * totalScaling - canvasDimensionY;
		}*/
		
		//Recalculate cameraX and cameraY to clamp it, essentially, so player never sees anyting
		//that is "out of bounds" of the map
		
		cameraX = (topLeftX + canvasDimensionX / 2) / totalScaling;
		cameraY = (topLeftY + canvasDimensionY / 2) / totalScaling;
		
		return new double[]{i - topLeftX, j - topLeftY};
	}
	
	int[] screenToCell(double i, double j) {
		double totalScaling = scalingFactor * zoom;
		
		double topLeftX = cameraX * totalScaling - canvasDimensionX / 2;
		double topLeftY = cameraY * totalScaling - canvasDimensionY / 2;
		
		double absX = i + topLeftX;
		double absY = j + topLeftY;
		
		int clickedRow = (int)(absX / totalScaling);
		int clickedCol = (int)(absY / totalScaling);
		
		return new int[] {clickedRow, clickedCol};
	}
	
	
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
				double totalScaling = zoom * scalingFactor;
				double[] screenPos = cellToScreen(i, j);
				gc.fillRect(screenPos[0], screenPos[1], totalScaling, totalScaling);
				
			}
		}
		
		//Draw static units
		for (int i = 0; i < mapRows; i++) {
			for (int j = 0; j < mapCols; j++) {
				Unit curStaticUnit = temp.getStaticUnits()[i][j];
				
				if (curStaticUnit == null) continue;
				
				//gc.setFill(Color.WHITE);

				double totalScaling = zoom * scalingFactor;
				double[] screenPos = cellToScreen(i, j);
				
				if (curStaticUnit == selectedUnit) {
					gc.setFill(Color.GOLD);
					gc.fillRect(screenPos[0], screenPos[1], totalScaling, totalScaling);
					//Highlight selected unit gold
				}
				
				//System.out.println("drawing img");
				if (curStaticUnit instanceof City) {
					gc.drawImage(sbeve, screenPos[0], screenPos[1], totalScaling, totalScaling);
				}
				
				
				if (curStaticUnit == selectedUnit) {
					if (curStaticUnit instanceof City) {
						//Add some cute buttons to spawn units and whatnot
						
					}
				}
			}
		}
		
		//Draw mobile units
		for (int i = 0; i < mapRows; i++) {
			for (int j = 0; j < mapCols; j++) {
				Unit curMobileUnit = temp.getMobileUnits()[i][j];
				
				if (curMobileUnit == null) continue;
				
				//gc.setFill(Color.WHITE);
				
				double totalScaling = zoom * scalingFactor;
				
				double[] screenPos = cellToScreen(i, j);
				if (curMobileUnit == selectedUnit) {
					gc.setFill(Color.GOLD);
					//Highlight selected unit gold
					gc.fillRect(screenPos[0], screenPos[1], totalScaling, totalScaling);
				}
				
				
				


				if (curMobileUnit instanceof Worker) {
					gc.drawImage(hammerImage, screenPos[0], screenPos[1], totalScaling, totalScaling);
				}
				
				
				if (curMobileUnit == selectedUnit) {
					//Draw orders!
					
					Queue<Order> orders = curMobileUnit.getOrders();
					
					int lastX = curMobileUnit.getX();
					int lastY = curMobileUnit.getY();
					
					for (Order o : orders) {
						if (o instanceof MoveOrder) {
							double[] lastScreenPos = cellToScreen(lastX, lastY);
							double curCenterX = lastScreenPos[0] + totalScaling / 2;
							double curCenterY = lastScreenPos[1] + totalScaling / 2;
							
							double[] nextPos = cellToScreen(((MoveOrder) o).getTx(), ((MoveOrder) o).getTy());
							double nextCenterX = nextPos[0] + totalScaling / 2;
							double nextCenterY = nextPos[1] + totalScaling / 2;
							
							drawArrow(gc, curCenterX, curCenterY, nextCenterX, nextCenterY);
							
							lastX = ((MoveOrder) o).getTx();
							lastY = ((MoveOrder) o).getTy();
						}
					}
				}
			}
		}
		
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
		stage.setTitle("test");
		
		//Group root = new Group();
		//Scene s = new Scene(root, mapRows * scalingFactor + 100, mapCols * scalingFactor + 100, Color.WHITE);
		
		BorderPane bPane = new BorderPane();
		
		Group root = new Group();
		bPane.setCenter(root);
		
		BorderPane infoPane = new BorderPane();
		
		VBox info = new VBox(25);
		
		infoPane.setTop(info);
		
		bPane.setRight(infoPane);
		
		BorderPane.setMargin(infoPane, new Insets(10)); //add border of 10 around our info
		
		Scene s = new Scene(bPane);
		
		s.setOnKeyPressed(e -> {
			if (e.isShiftDown()) {
				shiftPressed = true;
			}
		});
		
		s.setOnKeyReleased(e -> {
			if (!e.isShiftDown()) {
				shiftPressed = false;
			}
		});

		final Canvas canvas = new Canvas(canvasDimensionX, canvasDimensionY);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		
		GameManager game = new GameManager(mapRows, mapCols, numPlayers);
		
		root.getChildren().add(canvas);
		
		Text testingText = new Text();
		//testingText.wrappingWidthProperty().bind(info.widthProperty().subtract(15));
		
		info.getChildren().add(testingText);
		
		canvas.setOnMouseClicked(
				event -> {
					
					if (!event.isStillSincePress()) return;
					
					
					int[] scrpos = screenToCell(event.getSceneX(), event.getSceneY());
					int clickedRow = scrpos[0];
					int clickedCol = scrpos[1];
					
					//Try to select the mobile unit first
					
					if (Algorithms.isValidCoordinate(clickedRow, clickedCol, mapRows, mapCols)) {
						
						if (event.getButton() != MouseButton.SECONDARY) {
							//Selecting a unit
							Unit mobileOnClick = game.getOmnimap().getMobileUnits()[clickedRow][clickedCol];
							Unit staticOnClick = game.getOmnimap().getStaticUnits()[clickedRow][clickedCol];

							
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
						} else {
							//Move order!
							if (selectedUnit != null) {
								if (shiftPressed) {
									selectedUnit.addOrder(new MoveOrder(clickedRow, clickedCol));
								} else {
									selectedUnit.setOrder(new MoveOrder(clickedRow, clickedCol));
								}
							}
						}
						
						
						render(gc, game, testingText);
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
			if (e.getButton() == MouseButton.SECONDARY) {
				//Dragging the map around
				dragStartX = e.getSceneX();
				dragStartY = e.getSceneY();

				System.out.println("found start pos as " + dragStartX + ' ' + dragStartY);
				return;
			}
		});
		canvas.setOnMouseDragged(e -> {
			
			//System.out.println("got a mouse drag");
			//if (selectedUnit != null) return;
			
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
				}
			}
		});
		
		VBox turnButtonBox = new VBox();
		turnButtonBox.setAlignment(Pos.BOTTOM_CENTER);
		turnButtonBox.getChildren().add(turnButton);
		infoPane.setBottom(turnButtonBox);
		
		
		//turnButton.setTranslateX(mapRows * scalingFactor + 10);
		//turnButton.setTranslateY(mapCols * scalingFactor + 10);
		//root.getChildren().add(turnButton);
		
		
		

		System.out.println("initial terrain: ");
		render(gc, game, testingText);
		
		stage.setScene(s);
		
		stage.show();
	}

}
