package ui;

import game_manager.GameManager;
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

	public void render(GraphicsContext gc, GameManager game) {
		GameMap temp = game.getOmnimap();
		
		Tile[][] terrain = new Tile[100][100];
		
		for (int i = 0; i < 100; i++) {
			for (int j = 0; j < 100; j++) {
				if (temp.getMobileUnits()[i][j] != null) {
					terrain = temp.getMobileUnits()[i][j].getKnown().getTerrain();
				}
			}
		}
		//Tile[][] terrain = temp.getTerrain();
		
		for (int i = 0; i < 100; i++) {
			for (int j = 0; j < 100; j++) {
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
				}
				gc.fillRect(8 * i, 8 * j, 8, 8);
				
				
			}
		}
		
		//System.out.println("rerender done");
	}
	public void update(GraphicsContext gc, GameManager game) {
		game.turn();
		render(gc, game);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		// TODO Auto-generated method stub
		stage.setTitle("test");
		
		Group root = new Group();
		Scene s = new Scene(root, 1000, 1000, Color.BLACK);
		
		

		final Canvas canvas = new Canvas(800,800);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		
		GameManager game = new GameManager(100, 100, 2);
		 
		root.getChildren().add(canvas);
		

		Button turnButton = new Button();
		turnButton.setText("Next turn");
		turnButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				for (int i = 0; i < 10; i++) {
					update(gc, game);
				//	System.out.println("updated");
				}
			}
		});
		turnButton.setTranslateX(850);
		turnButton.setTranslateY(850);
		root.getChildren().add(turnButton);

		System.out.println("initial terrain: ");
		render(gc, game);
		
		stage.setScene(s);
		
		stage.show();
		/*
		while (true) {
			Thread.sleep(500);
			
			System.out.println("maikawut");
			update(gc, game);
			
			//stage.show();
		}*/
	}

}
