package ui;

import game_map.GameMap;
import game_map.Tile;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
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

	@Override
	public void start(Stage stage) throws Exception {
		// TODO Auto-generated method stub
		stage.setTitle("test");
		
		Group root = new Group();
		Scene s = new Scene(root, 400, 400, Color.BLACK);

		final Canvas canvas = new Canvas(400,400);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		
		GameMap temp = new GameMap(100, 100);
		temp.generate();
		
		Tile[][] terrain = temp.getTerrain();
		
		for (int i = 0; i < 100; i++) {
			for (int j = 0; j < 100; j++) {
				if (terrain[i][j] == Tile.CLEAR) {
					//Paint one color
					gc.setFill(Color.LIGHTGREEN);
				} else if (terrain[i][j] == Tile.BLOCKED) {
					gc.setFill(Color.DARKGREY);
				} else if (terrain[i][j] == Tile.MINERALS) {
					gc.setFill(Color.RED);
				}
				
				gc.fillRect(4 * i, 4 * j, 4, 4);
			}
		}
		
		//gc.setFill(Color.BLUE);
		//gc.fillRect(75,75,100,100);
		 
		root.getChildren().add(canvas);
		

		//VBox vBox = new VBox(new Label("A JavaFX Label"));
		//Scene scene = new Scene(vBox);

		
		stage.setScene(s);
		
		stage.show();
		
		
	}

}
