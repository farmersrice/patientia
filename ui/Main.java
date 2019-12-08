package ui;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;

import javax.imageio.ImageIO;

import game_manager.GameManager;
import game_manager.OutstandingOrder;
import game_manager.Player;
import game_manager.ResourceDelta;
import game_map.GameMap;
import game_map.Tile;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;
import orders.AttackOrder;
import orders.BuildCityOrder;
import orders.BuildFarmOrder;
import orders.BuildMineOrder;
import orders.CreateSoldierOrder;
import orders.CreateWorkerOrder;
import orders.MoveOrder;
import orders.Order;
import orders.TogglePopulationControlsOrder;
import units.City;
import units.Farm;
import units.Mine;
import units.MobileUnit;
import units.Soldier;
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
	final int numPlayers = 400;
	final int canvasDimensionX = mapRows * scalingFactor;
	final int canvasDimensionY = mapCols * scalingFactor;
	double cameraX = mapRows / 2.0;
	double cameraY = mapCols / 2.0;
	
	double dragStartX, dragStartY;

	Image hammerImage = null, swordImage = null, cityImage = null, farmImage = null, mineImage = null;
	
	boolean shiftPressed = false;
	
	ObservableList<String> orderList;
	
	GameManager game;
	

	final Color[] PLAYER_COLORS = {Color.rgb(255, 255, 255, 0.4), Color.rgb(255, 0, 0, 0.4), 
			Color.rgb(0, 255, 0, 0.4), Color.rgb(0, 0, 255, 0.4)};

	
	void loadImages() {
		try {
			hammerImage = SwingFXUtils.toFXImage(ImageIO.read(getClass().getClassLoader().getResource("assets/hammer_transparent.png")), null);
			swordImage = SwingFXUtils.toFXImage(ImageIO.read(getClass().getClassLoader().getResource("assets/sword_transparent.png")), null);
			cityImage = SwingFXUtils.toFXImage(ImageIO.read(getClass().getClassLoader().getResource("assets/city_transparent.png")), null);
			farmImage = SwingFXUtils.toFXImage(ImageIO.read(getClass().getClassLoader().getResource("assets/farm_transparent.png")), null);
			mineImage = SwingFXUtils.toFXImage(ImageIO.read(getClass().getClassLoader().getResource("assets/mine_transparent.png")), null);
			
		} catch (Exception e) {
			System.out.println("Failed to load images");
			e.printStackTrace();
		}
		
	}
	//copied from https://stackoverflow.com/questions/35751576/javafx-draw-line-with-arrow-canvas
	final int arrowSize = 8;
	
		
	void drawArrow(GraphicsContext gc, double x1, double y1, double x2, double y2, Color c) {
	    gc.setFill(c);
	    gc.setStroke(c);

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
		//GameMap temp = game.getOmnimap();//game.getPlayers()[0].getKnown(); //game.getOmnimap();
		GameMap temp = game.getPlayers()[0].getKnown();
		
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
					
					updateOrderList();
				}
				
				//System.out.println("drawing img");
				if (curStaticUnit instanceof City) {
					gc.drawImage(cityImage, screenPos[0], screenPos[1], totalScaling, totalScaling);
				}
				
				if (curStaticUnit instanceof Farm) {
					gc.drawImage(farmImage, screenPos[0], screenPos[1], totalScaling, totalScaling);
				}
				
				if (curStaticUnit instanceof Mine) {
					gc.drawImage(mineImage, screenPos[0], screenPos[1], totalScaling, totalScaling);
				}
				
				if (curStaticUnit != selectedUnit) {
					gc.setFill(PLAYER_COLORS[curStaticUnit.getTeam() % PLAYER_COLORS.length]);
					gc.fillRect(screenPos[0], screenPos[1], totalScaling, totalScaling);
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
					
					updateOrderList();
				}
				
				
				if (curMobileUnit instanceof Worker) {
					if (!(curMobileUnit != selectedUnit && selectedUnit != null && curMobileUnit.getX() == selectedUnit.getX() && 
							curMobileUnit.getY() == selectedUnit.getY())) 
						gc.drawImage(hammerImage, screenPos[0], screenPos[1], totalScaling, totalScaling);
				}
				
				if (curMobileUnit instanceof Soldier) {
					if (!(curMobileUnit != selectedUnit && selectedUnit != null && curMobileUnit.getX() == selectedUnit.getX() && 
							curMobileUnit.getY() == selectedUnit.getY())) 
						gc.drawImage(swordImage, screenPos[0], screenPos[1], totalScaling, totalScaling);
				}
				
				
				if (curMobileUnit == selectedUnit) {
					//Draw orders!
					
					Queue<Order> orders = curMobileUnit.getOrders();
					
					int lastX = curMobileUnit.getX();
					int lastY = curMobileUnit.getY();
					
					for (Order o : orders) {
						double[] lastScreenPos = cellToScreen(lastX, lastY);
						double bigX = lastScreenPos[0] - totalScaling * 0.5;
						double bigY = lastScreenPos[1] - totalScaling * 0.5;
						
						if (o instanceof MoveOrder || o instanceof AttackOrder) {
							double curCenterX = lastScreenPos[0] + totalScaling / 2;
							double curCenterY = lastScreenPos[1] + totalScaling / 2;
							
							int nextX = -1;
							int nextY = -1;
							
							if (o instanceof MoveOrder) { 
								MoveOrder o2 = (MoveOrder) o;
								nextX = o2.getTx();
								nextY = o2.getTy();
							} else {
								AttackOrder o2 = (AttackOrder) o;
								int[] coords = o2.findTarget(selectedUnit);
								nextX = coords[0];
								nextY = coords[1];
							}
							
							double[] nextPos = cellToScreen(nextX, nextY);
							double nextCenterX = nextPos[0] + totalScaling / 2;
							double nextCenterY = nextPos[1] + totalScaling / 2;
							
							drawArrow(gc, curCenterX, curCenterY, nextCenterX, nextCenterY, 
									(o instanceof MoveOrder ? Color.WHITE : Color.RED));
							
							lastX = nextX;
							lastY = nextY;
						}
						
						gc.setFill(PLAYER_COLORS[0]);
						
						if (o instanceof BuildCityOrder) {
							gc.fillRect(bigX, bigY, 2 * totalScaling, 2 * totalScaling);
							gc.drawImage(cityImage, bigX, bigY, 2 * totalScaling, 2 * totalScaling);
						}
						
						if (o instanceof BuildFarmOrder) {
							gc.fillRect(bigX, bigY, 2 * totalScaling, 2 * totalScaling);
							gc.drawImage(farmImage, bigX, bigY, 2 * totalScaling, 2 * totalScaling);
						}
						
						if (o instanceof BuildMineOrder) {
							gc.fillRect(bigX, bigY, 2 * totalScaling, 2 * totalScaling);
							gc.drawImage(mineImage, bigX, bigY, 2 * totalScaling, 2 * totalScaling);
						}
						
						if (o instanceof CreateSoldierOrder) {
							gc.fillRect(bigX, bigY, 2 * totalScaling, 2 * totalScaling);
							gc.drawImage(swordImage, bigX, bigY, 2 * totalScaling, 2 * totalScaling);
						}
						
						if (o instanceof CreateWorkerOrder) {
							gc.fillRect(bigX, bigY, 2 * totalScaling, 2 * totalScaling);
							gc.drawImage(hammerImage, bigX, bigY, 2 * totalScaling, 2 * totalScaling);
						}
					}
				}
				
				if (curMobileUnit != selectedUnit) {
					gc.setFill(PLAYER_COLORS[curMobileUnit.getTeam() % PLAYER_COLORS.length]);
					gc.fillRect(screenPos[0], screenPos[1], totalScaling, totalScaling);
				}
			}
		}
		
		//Calculate the expected deltas now
		ResourceDelta thisTurnDelta = game.getPlayers()[0].getExpectedDelta();
		
		double delf = thisTurnDelta.getFood();
		double delm = thisTurnDelta.getMinerals();
		double delw = thisTurnDelta.getWealth();
		
		Player us = game.getPlayers()[0];
		DecimalFormat df = new DecimalFormat("#.##");
		t.setText("Food: " + df.format(us.getFood()) + " (" + (delf > 0 ? "+" : "") + df.format(delf) + ")" + '\n' 
				+ "Minerals: " + df.format(us.getMinerals()) + " (" + (delm > 0 ? "+" : "") + df.format(delm) + ")" + '\n' 
				+ "Wealth: " + df.format(us.getWealth()) + " (" + (delw > 0 ? "+" : "") + df.format(delw) + ")" + '\n' + 
				"Selected unit: " + (selectedUnit != null ? selectedUnit.toString() + "\nTeam: " + selectedUnit.getTeam()  + ", id: " + selectedUnit.getId(): ""));
		
		//System.out.println("rerender done");
	}
	public void update(GraphicsContext gc, GameManager game, Text t) {
		game.turn();
		render(gc, game, t);
	}
	
	public static class TimedOrder implements Comparable<TimedOrder> {
		int time;
		Order o;
		boolean set;
		boolean receiving;
		
		TimedOrder(int t, Order or, boolean s, boolean r) {
			time = t; o = or; set = s; receiving = r;
		}
		
		public int compareTo(TimedOrder other) {
			return time - other.time;
		}
	}
	
	private ArrayList<TimedOrder> getFutureOrderState() {
		ArrayList<TimedOrder> futureOrderStateUnfiltered = new ArrayList<TimedOrder>();
		
		for (OutstandingOrder o : game.getOutstandingOrders()) {
			if (o.getTarget().getId() != selectedUnit.getId()) continue;
			
			futureOrderStateUnfiltered.add(new TimedOrder(o.getTimeIssued() + game.getLag(selectedUnit) - game.getTurnCounter(),
					o.getOrder(), o.isSet(), true));
		}
		
		int lastX = selectedUnit.getX();
		int lastY = selectedUnit.getY();
		int currentCompletionTime = 0;
		for (Order o : selectedUnit.getOrders()) {
			if (o instanceof MoveOrder) {
				currentCompletionTime += (Algorithms.kingBFS(selectedUnit.getKnown(), ((MoveOrder) o).getTx(), 
						((MoveOrder) o).getTy(), selectedUnit)).getDist()[lastX][lastY];
				
				lastX = ((MoveOrder) o).getTx();
				lastY = ((MoveOrder) o).getTy();
			} else {
				currentCompletionTime += o.expectedCompletionTime(selectedUnit);
			}
			
			futureOrderStateUnfiltered.add(new TimedOrder(currentCompletionTime, o, false, false));
		}
		
		Collections.sort(futureOrderStateUnfiltered);
		
		ArrayList<TimedOrder> futureOrderState = new ArrayList<TimedOrder>();
		
		
		boolean erased = false;
		for (TimedOrder next : futureOrderStateUnfiltered) {
			if (next.set) erased = true;
			
			if (!next.receiving && erased) {
				//Remove it and do nothing
			} else {
				futureOrderState.add(next);
			}
			
			
		}
		
		
		return futureOrderState;
	}
	
	private void updateOrderList() {
		if (selectedUnit == null) return;
		
		orderList.clear();
		
		ArrayList<TimedOrder> futureOrderState = getFutureOrderState();

		for (TimedOrder t : futureOrderState) {
			if (t.receiving) {
				//Will come in the future, put  est. time
				orderList.add(t.o.toString() + " (est. arrival: " + t.time + ", " + (t.set ? "set" : "add") + ")" );
			} else {
				//We already have this, also put est. time
				orderList.add(t.o.toString() + " (est. completion: " + t.time + ")");
			}
		}
		
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		loadImages();
		
		stage.setTitle("test");
		
		//Group root = new Group();
		//Scene s = new Scene(root, mapRows * scalingFactor + 100, mapCols * scalingFactor + 100, Color.WHITE);
		
		BorderPane bPane = new BorderPane();
		
		Group root = new Group();
		bPane.setCenter(root);
		
		BorderPane infoPane = new BorderPane();
		
		orderList = FXCollections.observableArrayList("hi", "hi2", "yeet", "hi2", "yeet", "hi2", "yeet", "hi2", "yeet", "hi2", "yeet", "hi2", "yeet", "hi2", "yeet", "hi2", "yeet", "hi2", "yeet", "hi2", "yeet", "hi2", "yeet", "hi2", "yeet", "hi2", "yeet");
		ListView<String> orderListView = new ListView<String>(orderList);
		
		VBox orderBox = new VBox(5);
		
		Text selectedUnitOrdersText = new Text("Selected unit orders: ");
		orderBox.getChildren().add(selectedUnitOrdersText);
		selectedUnitOrdersText.setVisible(false);
		
		final int LIST_CELL_HEIGHT = 24;
		orderListView.setMaxHeight(LIST_CELL_HEIGHT * 15);
		orderListView.prefHeightProperty().bind(orderListView.maxHeightProperty().multiply(LIST_CELL_HEIGHT));
		orderBox.getChildren().add(orderListView);
		orderListView.setVisible(false);
		
		infoPane.setCenter(orderBox);
		
		VBox info = new VBox(25);
		
		infoPane.setTop(info);
		
		bPane.setRight(infoPane);
		
		BorderPane.setMargin(infoPane, new Insets(10)); //add border of 10 around our info
		BorderPane.setMargin(orderBox, new Insets(5));
		
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
		
		game = new GameManager(mapRows, mapCols, numPlayers);
		
		root.getChildren().add(canvas);
		
		Text testingText = new Text();
		//testingText.wrappingWidthProperty().bind(info.widthProperty().subtract(15));
		
		info.getChildren().add(testingText);
		
		Button createSoldierButton = new Button("Create soldier");
		createSoldierButton.setOnMouseClicked(e -> {
			game.addOutstandingOrder(new OutstandingOrder(selectedUnit, game.getTurnCounter(), new CreateSoldierOrder(1), !shiftPressed));
			updateOrderList();
		});
		Button createWorkerButton = new Button("Create worker");
		createWorkerButton.setOnMouseClicked(e -> {
			game.addOutstandingOrder(new OutstandingOrder(selectedUnit, game.getTurnCounter(), new CreateWorkerOrder(), !shiftPressed));
			updateOrderList();
		});
		
		Button buildFarmButton = new Button("Build farm");
		buildFarmButton.setOnMouseClicked(e -> {
			game.addOutstandingOrder(new OutstandingOrder(selectedUnit, game.getTurnCounter(), new BuildFarmOrder(), !shiftPressed));
			updateOrderList();
		});
		Button buildCityButton = new Button("Build city");
		buildCityButton.setOnMouseClicked(e -> {
			game.addOutstandingOrder(new OutstandingOrder(selectedUnit, game.getTurnCounter(), new BuildCityOrder(), !shiftPressed));
			updateOrderList();
		});
		Button buildMineButton = new Button("Build mine");
		buildMineButton.setOnMouseClicked(e -> {
			game.addOutstandingOrder(new OutstandingOrder(selectedUnit, game.getTurnCounter(), new BuildMineOrder(), !shiftPressed));
			updateOrderList();
		});
		Button togglePopulationControlsButton = new Button("Toggle pop controls");
		togglePopulationControlsButton.setOnMouseClicked(e -> {
			game.addOutstandingOrder(new OutstandingOrder(selectedUnit, game.getTurnCounter(), new TogglePopulationControlsOrder(), !shiftPressed));
			updateOrderList();
		});
		
		
		canvas.setOnMouseClicked(
				event -> {
					
					if (!event.isStillSincePress()) return;
					
					int[] scrpos = screenToCell(event.getX(), event.getY());
					int clickedRow = scrpos[0];
					int clickedCol = scrpos[1];
					
					//Try to select the mobile unit first
					
					if (Algorithms.isValidCoordinate(clickedRow, clickedCol, mapRows, mapCols)) {
						
						if (event.getButton() != MouseButton.SECONDARY) {
							//Selecting a unit
							Unit mobileOnClick = game.getPlayers()[0].getKnown().getMobileUnits()[clickedRow][clickedCol];
							Unit staticOnClick = game.getPlayers()[0].getKnown().getStaticUnits()[clickedRow][clickedCol];
							
							//Omnipresent selection for debug purposes
							//Unit mobileOnClick = game.getOmnimap().getMobileUnits()[clickedRow][clickedCol];
							//Unit staticOnClick = game.getOmnimap().getStaticUnits()[clickedRow][clickedCol];

							
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
							if (selectedUnit != null && selectedUnit.getTeam() == 0) {
								GameMap known = game.getPlayers()[0].getKnown();
								
								MobileUnit occupant = known.getMobileUnits()[clickedRow][clickedCol];

								Order o = new MoveOrder(clickedRow, clickedCol);
								
								if (occupant != null && occupant.isValid() && occupant.getTeam() != 0) {
									//Attack!!!!!!
									o = new AttackOrder(occupant);
								}
								
								game.addOutstandingOrder(new OutstandingOrder(selectedUnit, game.getTurnCounter(), o, !shiftPressed));
							}
						}
						
						//Remove all buttons from the system
						orderBox.getChildren().remove(createWorkerButton);
						orderBox.getChildren().remove(createSoldierButton);
						orderBox.getChildren().remove(buildFarmButton);
						orderBox.getChildren().remove(buildMineButton);
						orderBox.getChildren().remove(buildCityButton);
						orderBox.getChildren().remove(togglePopulationControlsButton);
						
						if (selectedUnit != null && selectedUnit.getTeam() == 0) {
							selectedUnitOrdersText.setVisible(true);
							orderListView.setVisible(true);
							
							//Modify the orderList to give us the right names that we want
							
							updateOrderList();
							
							
							if (selectedUnit instanceof City) {
								//Add creation buttons
								orderBox.getChildren().add(createWorkerButton);
								orderBox.getChildren().add(createSoldierButton);
								orderBox.getChildren().add(togglePopulationControlsButton);
							} else if (selectedUnit instanceof Worker) {
								orderBox.getChildren().add(buildFarmButton);
								orderBox.getChildren().add(buildMineButton);
								orderBox.getChildren().add(buildCityButton);
							}
						} else {
							selectedUnitOrdersText.setVisible(false);
							orderListView.setVisible(false);
						}
						render(gc, game, testingText);
					}
					
					//testingText.setText(testingText.getText() + (selectedUnit != null ? "\nselected unit " + selectedUnit.getId() : ""));
					//game.getOmnimap().getMobileUnits()[clickedRow][clickedCol] = new Soldier(0, 1, 1, 1, null, 1, 1, 1);
					
				});
		
		canvas.setOnScroll(e -> {
			
			boolean neg = e.getDeltaY() < 0;
			if (!neg) {
				//System.out.println("val is " + (1 + (0.01 * e.getDeltaY() / 2)));
				zoom *= (1 + (0.01 * e.getDeltaY() / 2));
			} else {
				zoom *= 1 / ((1 - (0.01 * e.getDeltaY() / 2)));
			}
			//zoom += e.getDeltaY() / 10;
			
			zoom = Math.max(1, Math.min(10, zoom));
			
			
			render(gc, game, testingText);
		});
		
		canvas.setOnMousePressed(e -> {
			if (e.getButton() == MouseButton.SECONDARY) {
				//Dragging the map around
				dragStartX = e.getX();
				dragStartY = e.getY();

				System.out.println("found start pos as " + dragStartX + ' ' + dragStartY);
				return;
			}
		});
		canvas.setOnMouseDragged(e -> {
			
			//System.out.println("got a mouse drag");
			//if (selectedUnit != null) return;
			
			if (e.getButton() != MouseButton.SECONDARY) return;
			
			double dragDeltaX = e.getX() - dragStartX;
			double dragDeltaY = e.getY() - dragStartY;
			
			cameraX += -dragDeltaX / (scalingFactor * zoom);
			cameraY += -dragDeltaY / (scalingFactor * zoom);
			
			dragStartX = e.getX();
			dragStartY = e.getY(); //So that we adjust relative to prev pos
			
			render(gc, game, testingText);
		});
		
		Button turnButton = new Button();
		turnButton.setText("Next turn");
		turnButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				//for (int i = 0; i < 10; i++) {
					update(gc, game, testingText);
				//}
			}
		});
		
		VBox turnButtonBox = new VBox();
		turnButtonBox.setAlignment(Pos.BOTTOM_CENTER);
		turnButtonBox.getChildren().add(turnButton);
		infoPane.setBottom(turnButtonBox);
		

		System.out.println("initial terrain: ");
		render(gc, game, testingText);
		
		stage.setScene(s);
		
		stage.show();
	}
	
	public static void main(String[] args) {
		Application.launch();
	}

}
