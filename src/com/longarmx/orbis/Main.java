package com.longarmx.orbis;

import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public class Main extends Application
{

	private Stage stage;

	@Override
	public void start(Stage stage) throws Exception
	{
		this.stage = stage;
		stage.initStyle(StageStyle.UNDECORATED);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(75, 50, 50, 75));
		grid.setAlignment(Pos.TOP_LEFT);
		grid.setStyle("-fx-background-color: linear-gradient(#3FB2FF, #266B99);");

		// Title
		grid.add(createText("Orbis Launcher", 40), 0, 0, 2, 1);

		// Error message
		final Text error = createText("", 12, Color.CORAL);
		grid.add(error, 0, 6, 4, 6);

		// Width
		grid.add(createLabel("Width: "), 0, 1);

		ObservableList<Integer> widths = createObservableList(400, 600, 800,
				1024, 1280, 1366, 1600, 1920, 2048, 2560, 4096);

		final ComboBox<Integer> widthOptions = createComboBox(widths, 1280);
		grid.add(widthOptions, 1, 1);

		// Height
		grid.add(createLabel("Height: "), 0, 2);

		ObservableList<Integer> heights = createObservableList(300, 400, 600,
				720, 768, 900, 1080, 1200, 1440, 2160);

		final ComboBox<Integer> heightOptions = createComboBox(heights, 720);
		grid.add(heightOptions, 1, 2);

		// Fullscreen
		grid.add(createLabel("Fullscreen: "), 0, 3);

		final CheckBox fullscreen = new CheckBox();
		fullscreen.setOnAction(getFullscreenAction(widthOptions, heightOptions,
				fullscreen));
		grid.add(fullscreen, 1, 3);

		// VSync
		grid.add(createLabel("VSync: "), 0, 4);

		final CheckBox vsync = new CheckBox();
		vsync.setSelected(true);
		grid.add(vsync, 1, 4);

		// Exit Button
		Button exitButton = createButton("Exit", getExitAction());
		grid.add(exitButton, 0, 5);

		// Start Button
		Button startButton = createButton(
				"Start",
				getStartAction(widthOptions, heightOptions, fullscreen, vsync,
						error));
		grid.add(startButton, 1, 5);

		Scene scene = new Scene(grid, 600, 400);
		stage.setScene(scene);
		stage.setTitle("Orbis Launcher");
		stage.setResizable(false);
		stage.show();
	}

	/**
	 * Creates a combo box with specified values and selects a default value
	 * @param values The values contained in the ComboBox
	 * @param defaultSelection The item to select by default
	 * @return The ComboBox with the specified parameters
	 */
	private ComboBox<Integer> createComboBox(ObservableList<Integer> values,
			Integer defaultSelection)
	{
		ComboBox<Integer> tmp = new ComboBox<Integer>(values);
		if(defaultSelection != null)
			tmp.getSelectionModel().select(values.indexOf(defaultSelection));
		tmp.setVisibleRowCount(5);
		return tmp;
	}

	/**
	 * Creates an observable list for the combo boxes. Just an abstraction layer
	 * 
	 * @param args
	 *            The Integers to add
	 * @return An ObservableList for a combo box
	 */
	private ObservableList<Integer> createObservableList(Integer... args)
	{
		return FXCollections.observableArrayList(args);
	}

	/**
	 * Creates a button object with specified text and with a different width
	 * 
	 * @param text
	 *            The text to display
	 * @param e
	 *            The action the button performs
	 * @return A Button object with the specified parameters;
	 */
	private Button createButton(String text, EventHandler<ActionEvent> e)
	{
		Button tmp = new Button(text);
		if(e != null)
			tmp.setOnAction(e);
		tmp.setPrefWidth(65);
		return tmp;
	}

	/**
	 * Creates a Text object with specified text and font size
	 * 
	 * @param text
	 *            The text to display
	 * @param fontSize
	 *            The size of the font
	 * @return A Text object with specified parameters
	 */
	private Text createText(String text, int fontSize)
	{
		Text tmp = new Text(text);
		tmp.setFont(Font.font("Arial Black", FontWeight.BOLD, fontSize));
		return tmp;
	}

	/**
	 * Creates a Text object with specified text, font size, and color
	 * 
	 * @param text
	 *            The text to display
	 * @param fontSize
	 *            The size of the font
	 * @param color
	 *            The color of the text
	 * @return A Text object with specified parameters
	 */
	private Text createText(String text, int fontSize, Color color)
	{
		Text tmp = createText(text, fontSize);
		tmp.setFill(color);
		return tmp;
	}

	/**
	 * Returns an action for the fullscreen CheckBox that disables dimension
	 * selecting
	 * 
	 * @param widthOptions
	 *            The ComboBox for screen width
	 * @param heightOptions
	 *            The ComboBox for screen height
	 * @param fullscreen
	 *            The CheckBox for fullscreen
	 * @return The EventHandler with the fullscreen action
	 */
	private EventHandler<ActionEvent> getFullscreenAction(
			final ComboBox<Integer> widthOptions,
			final ComboBox<Integer> heightOptions, final CheckBox fullscreen)
	{
		return new EventHandler<ActionEvent>()
		{

			@Override
			public void handle(ActionEvent event)
			{
				widthOptions.setDisable(fullscreen.isSelected());
				heightOptions.setDisable(fullscreen.isSelected());
			}

		};
	}

	/**
	 * Returns an action for the start button
	 * 
	 * @param widthOptions
	 *            The ComboBox for screen width
	 * @param heightOptions
	 *            The ComboBox for screen height
	 * @param fullscreen
	 *            The CheckBox for fullscreen
	 * @param vsync
	 *            The CheckBox for VSync enabled
	 * @param error
	 *            The Text for displaying any errors
	 * @return The EventHandler for the start button
	 */
	private EventHandler<ActionEvent> getStartAction(
			final ComboBox<Integer> widthOptions,
			final ComboBox<Integer> heightOptions, final CheckBox fullscreen,
			final CheckBox vsync, final Text error)
	{
		return new EventHandler<ActionEvent>()
		{

			@Override
			public void handle(ActionEvent event)
			{
				if (checkSystem(error))
				{
					try
					{
						StringBuilder builder = new StringBuilder(
								"java -Djava.library.path=\"natives/\" -jar 3DWorld.jar");
						builder.append(addArg(widthOptions.getSelectionModel()
								.getSelectedItem()));
						builder.append(addArg(heightOptions.getSelectionModel()
								.getSelectedItem()));
						builder.append(addArg(fullscreen.isSelected()));
						builder.append(addArg(vsync.isSelected()));
						Runtime.getRuntime().exec(builder.toString());
						shutdown();
					} catch (Exception e)
					{
						error.setText(e.toString());
						e.printStackTrace();
					}
				}
			}
		};
	}

	/**
	 * Returns an action for the exit button
	 * 
	 * @return The EventHandler for the exit button
	 */
	private EventHandler<ActionEvent> getExitAction()
	{
		return new EventHandler<ActionEvent>()
		{

			@Override
			public void handle(ActionEvent event)
			{
				shutdown();
			}

		};
	}

	/**
	 * Creates a label to be used with another manipulatable element. Basically
	 * a describing tag
	 * 
	 * @param text
	 *            The text to display
	 * @return The created label with a custom font
	 */
	private Label createLabel(String text)
	{
		Label tmp = new Label(text);
		tmp.setFont(Font.font("Arial Black", FontWeight.BOLD, 20));
		return tmp;
	}

	/**
	 * Used for adding a command line argument
	 * 
	 * @param arg
	 *            The argument to add
	 * @return The formatted argument
	 */
	private String addArg(String arg)
	{
		return " " + arg;
	}

	/**
	 * Used for adding a command line integer argument
	 * 
	 * @param arg
	 *            The argument to add
	 * @return The formatted argument turned into a string
	 */
	private String addArg(int arg)
	{
		return addArg(String.valueOf(arg));
	}

	/**
	 * Used for adding a command line boolean argument
	 * 
	 * @param arg
	 *            The argument to add
	 * @return The formatted argument turned into a string (1 : 0)
	 */
	private String addArg(boolean arg)
	{
		return addArg(arg ? 1 : 0);
	}

	/**
	 * Shuts down the application with a fancy drop-down animation
	 * 
	 * @param stage
	 *            The stage to be modified / main stage
	 */
	private void shutdown()
	{
		Timer animTimer = new Timer();
		animTimer.scheduleAtFixedRate(new TimerTask()
		{

			@Override
			public void run()
			{
				if (stage.getHeight() > 10)
				{
					stage.setWidth(stage.getWidth() - 16 * 6);
					stage.setHeight(stage.getHeight() - 16 * 4);
					stage.setX(stage.getX() + 16 * 3);
					stage.setY(stage.getY() + 16 * 8);
				} else
				{
					stop();
				}

			}
		}, 0, 25);
	}

	/**
	 * Abstraction method to stop the launcher
	 */
	public void stop()
	{
		System.exit(0);
	}

	/**
	 * Checks to see whether the system will be able to run the game
	 * 
	 * @param error
	 *            The error text object that will display any errors
	 * @return Whether or not the system passes inspection
	 */
	private boolean checkSystem(Text error)
	{
		try
		{
			// Creates an 'invisible' window to check gl version
			System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
			Display.setDisplayMode(new DisplayMode(0, 0));
			Display.setLocation(0, 0);
			Display.create();

			String version = GL11.glGetString(GL11.GL_VERSION);
			int maj = Integer.parseInt(version.substring(0, 1));
			int min = Integer.parseInt(version.substring(2, 3));

			Display.destroy();

			if (maj < 3 || maj == 3 && min < 3)
			{
				error.setText("Opengl version " + version
						+ " not supported. Requires version 3.3 or higher");
				return false;
			}

			return true;
		} catch (LWJGLException | UnsatisfiedLinkError e)
		{
			error.setText(e.toString());
			e.printStackTrace();
		}

		return false;
	}

	public static void main(String[] args)
	{
		launch(args);
	}

}
