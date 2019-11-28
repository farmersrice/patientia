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
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {

	/*
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("ree");
		GameMap temp = new GameMap(100, 100);
		temp.generate();
		
		System.out.println("thonking");
		Tile[][] terrain = temp.getTerrain();
		
		for (int i = 0; i < 100; i++) {
			for (int j = 0; j < 100; j++) {
				if (terrain[i][j] == Tile.CLEAR) {
					//Paint one color
					System.out.print('.');
				} else if (terrain[i][j] == Tile.BLOCKED) {
					System.out.print('X');
				} else if (terrain[i][j] == Tile.MINERALS) {
					System.out.print('*');
				}
			}
			System.out.print('\n');
		}
	}*/
	
	final int scalingFactor = 16;
	final int mapRows = 100;
	final int mapCols = 50;

	public void render(GraphicsContext gc, GameManager game, Text t) {
		GameMap temp = game.getOmnimap();
		
		Tile[][] terrain = new Tile[mapRows][mapCols];
		
		Image hammerImage = new Image("file:C:\\Users\\lmqtfx\\eclipse-workspace\\patience\\src\\ui\\hammer_transparent.png");
		System.out.println("got hammerImage " + hammerImage.getHeight() + ' ' +  hammerImage.getRequestedHeight());
		for (int i = 0; i < mapRows; i++) {
			for (int j = 0; j < mapCols; j++) {
				if (temp.getMobileUnits()[i][j] != null) {
					terrain = temp.getMobileUnits()[i][j].getKnown().getTerrain();
				}
			}
		}
		//Tile[][] terrain = temp.getTerrain();
		
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
				
				if (temp.getMobileUnits()[i][j] != null) {
					gc.setFill(Color.WHITE);
					//gc.drawImage(hammerImage, scalingFactor * i, scalingFactor * j, scalingFactor, scalingFactor);
				}
				gc.fillRect(scalingFactor * i, scalingFactor * j, scalingFactor, scalingFactor);
				
				if (temp.getMobileUnits()[i][j] != null) {
					System.out.println("drawing img");
					gc.drawImage(hammerImage, scalingFactor * i, scalingFactor * j, scalingFactor, scalingFactor);
				}
				
			}
		}
		
		//The x, y here are inverted (x is across columns).
		//gc.setFill(Color.WHITE);
		//gc.fillRect(40 * scalingFactor, 20 * scalingFactor, 10 * scalingFactor, 10 * scalingFactor);
		
		Player us = game.getPlayers()[0];
		t.setText("Food: " + us.getFood() + '\n' + "Minerals: " + us.getMinerals() + '\n' + "Wealth: " + us.getWealth());
		
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
		
		

		final Canvas canvas = new Canvas(mapRows * scalingFactor, mapCols * scalingFactor);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		
		GameManager game = new GameManager(mapRows, mapCols, 2);
		
		root.getChildren().add(canvas);
		
		Text testingText = new Text();
		
		testingText.setTranslateX(mapRows * scalingFactor + 10);
		testingText.setTranslateY(10);
		root.getChildren().add(testingText);
		
		canvas.setOnMouseClicked(
				event -> {
					int clickedRow = (int)(event.getSceneY() / scalingFactor);
					int clickedCol = (int)(event.getSceneX() / scalingFactor);
					testingText.setText(testingText.getText() + "\nclicked " + 
							(int)(event.getSceneX() / scalingFactor) + " " + (int)(event.getSceneY() / scalingFactor));
					
					gc.setFill(Color.WHITE);
					gc.fillRect(clickedCol * scalingFactor, clickedRow * scalingFactor, scalingFactor, scalingFactor);
					//game.getOmnimap().getMobileUnits()[clickedRow][clickedCol] = new Soldier(0, 1, 1, 1, null, 1, 1, 1);
				
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
