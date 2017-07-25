package ru.enfester.fx.controllers;

import java.awt.MouseInfo;
import ru.enfester.Config;
import ru.enfester.Vars;
import ru.enfester.Save;
import ru.enfester.utils.EnCoreNetwork;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import ru.enfester.Load;
import ru.enfester.Auth;
import static ru.enfester.Config.ClentDir;
import ru.enfester.MessegeWindow;
import ru.enfester.fx.Start;
import static ru.enfester.fx.Start.utils;
import ru.enfester.logic.Game;
import ru.enfester.logic.Update;

public class MainController implements Initializable {

    public double initX, initY;
    public static IntegerProperty sliderValue;

    @FXML
    public AnchorPane pane;
    public Pane paneMain, paneLogin, paneSkin, paneLoadClient, paneSettings, paralaxer;
    public ImageView imageSkin, imageCloack, imageAvatar, sound;
    public Label labelTitle, labelAuth, labelName, info, ramLabel, labelClientDownloadGlobal, labelClientDownload,
            labelItap, labelSpeed, labelTimeDownoad, labelTimeUn, labelSizeFile, labelSizeDownload, labelStatus;
    public TextField getRam;
    public CheckBox checkBoxFullScreen, checksave;
    public RadioButton radioButtonUltra, radioButtonMax, radioButtonMid, radioButtonMin;
    public Slider slider;
    public ProgressIndicator prograssBarClientDownload, prograssBarClientDownloadGlobal, authload;
    public TextField login, password;
    public Hyperlink linck;
    public Button enter;
    public ChoiceBox clientList;

    public static volatile MainController mainController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mainController = this;

        ToggleGroup radioGroup = new ToggleGroup();
        radioButtonUltra.setToggleGroup(radioGroup);
        radioButtonMax.setToggleGroup(radioGroup);
        radioButtonMid.setToggleGroup(radioGroup);
        radioButtonMin.setToggleGroup(radioGroup);

        if (!utils.getMcDir(Config.ClentDir).exists()) { // Если нет папки с клиентом        
            utils.getMcDir(Config.ClentDir).mkdir(); // Создать папку с клиентом
        }
        if (!new File(utils.getMcDir(ClentDir) + File.separator + "launcher.ini").exists()) {
            new Save(); // Создает файл сохранения
        } else {
            new Load(); // Загружает информацию, логин и пароль в формы            
        }

        utils.send("Connect to networck: " + EnCoreNetwork.isOnline());
        mainController.labelTitle.setText(Config.Title);
        if (!EnCoreNetwork.isOnline()) {

            Vars.ONLINE = false;
            new MessegeWindow("Нет подключения к интернету!", "Лаунчер не смог подключиться к удаленному серверу. Игра будет одиночной.", "Продолжить");
            mainController.password.setVisible(false);
            mainController.labelAuth.setText("Придумай логин для одиночной игры");
            mainController.clientList.setVisible(true);

            List<String> list = new ArrayList<>();
            for (String[] Server : Config.Servers) {
                list.add(Server[0]);
            }

            mainController.clientList.setValue("Выбери сервер");
            mainController.clientList.setItems(FXCollections.observableArrayList(list));
            mainController.clientList.getSelectionModel().select(0);

        }

        //   paneLogin.getChildren().add(drawSemiRing(520, 320, 100, 170, 90, Color.LIGHTGREEN));
        paralaxInit();
    }

    ArrayList<Image> paralaxImage = new ArrayList<>();
    ArrayList<Pane> paralax = new ArrayList<>();
    int PARALAX_COUNT = 6;

    void paralaxInit() {
        for (int i = 0; i < PARALAX_COUNT; i++) {
            paralaxImage.add(new Image(getClass().getResourceAsStream("/ru/enfester/fx/images/paralax" + i + ".png")));
            System.out.println("/ru/enfester/fx/images/paralax" + i + ".png - Загружен");
            paralax.add(new Pane());
            paralax.get(i).setPrefSize(900, 600);
            mainController.paralaxer.getChildren().add(paralax.get(i));
        }

        /* new Thread(() -> {
            while (true) {
                paralax(MouseInfo.getPointerInfo().getLocation().getX() / 2, MouseInfo.getPointerInfo().getLocation().getY() / 2);
            }
        }).start();*/
    }

    BackgroundSize backgroundSize = new BackgroundSize(990, 660, false, false, false, false);

    void paralax(double x, double y) {

        for (int i = 0; i < PARALAX_COUNT; i++) {
            double posX = -(x / 10 * (i + 1));
            double posY = -(y / 10 * (i + 1));
            BackgroundPosition backgroundPosition = new BackgroundPosition(Side.LEFT, posX, false, Side.TOP, posY, false);
            BackgroundImage backgroundImage = new BackgroundImage(paralaxImage.get(i), BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, backgroundPosition, backgroundSize);
            mainController.paralax.get(i).setBackground(new Background(backgroundImage));
        }
    }

    @FXML
    public void moved(MouseEvent me) {
        paralax(MouseInfo.getPointerInfo().getLocation().getX() / 2, MouseInfo.getPointerInfo().getLocation().getY() / 2);
    }

    @FXML
    public void pressed(MouseEvent me) {
        if (movedMain) {
            initX = me.getScreenX() - Start.stage.getX();
            initY = me.getScreenY() - Start.stage.getY();
        }
    }

    @FXML
    public void handle(MouseEvent me) {
        if (movedMain) {
            Start.stage.setX(me.getScreenX() - initX);
            Start.stage.setY(me.getScreenY() - initY);
        }
    }

    @FXML
    void closeOnMouseClicked(MouseEvent event) {
        utils.PlaySound("click.mp3");
        new Save();
        System.exit(0);
    }

    @FXML
    public void soundController(MouseEvent event) {
        utils.PlaySound("click.mp3");
        if (Vars.CHECK_MUSIC) {
            Start.player.pause();
            mainController.sound.setImage(new Image(getClass().getResourceAsStream("/ru/enfester/fx/images/suOff.png")));
        } else {
            Start.player.play();
            mainController.sound.setImage(new Image(getClass().getResourceAsStream("/ru/enfester/fx/images/suOn.png")));
        }
        Vars.CHECK_MUSIC = !Vars.CHECK_MUSIC;
        new Save();
    }

    /**
     * Кнопка просмотра скина
     *
     * @param event
     */
    @FXML
    void buttonSkinOpenOnMouseClicked(MouseEvent event) {
        utils.PlaySound("menu_open.mp3");

        mainController.paneMain.setEffect(new GaussianBlur());
        mainController.paneMain.setDisable(true);

        mainController.paneSkin.setTranslateX(-mainController.paneSkin.getWidth()); // Устанавливаем новую позицию отличие от размеченой
        mainController.paneSkin.setVisible(true); // Делаем видимой
        TranslateTransition tt = new TranslateTransition(Duration.millis(150), mainController.paneSkin);
        tt.setByX(-mainController.paneSkin.getWidth());
        tt.setToX(0);
        tt.playFromStart(); //

    }

    @FXML
    void buttonSkinCloseOnAction(ActionEvent event) {
        utils.PlaySound("menu_close.mp3");

        mainController.paneMain.setEffect(null);
        mainController.paneMain.setDisable(false);

        TranslateTransition tt = new TranslateTransition(Duration.millis(150), mainController.paneSkin);
        tt.setByX(0);
        tt.setToX(-mainController.paneSkin.getWidth());
        tt.playFromStart(); //      
    }

    /**
     * Кнопка настроек
     *
     * @param event
     */
    @FXML
    void buttonSettingsOnCliecked(MouseEvent event) {
        utils.PlaySound("menu_open.mp3");
        mainController.paneLogin.setEffect(new GaussianBlur());
        mainController.paneLogin.setDisable(true);
        mainController.paneMain.setEffect(new GaussianBlur());
        mainController.paneMain.setDisable(true);
        mainController.paneSettings.setTranslateY(400); // Устанавливаем новую позицию отличие от размеченой
        mainController.paneSettings.setVisible(true); // Делаем видимой
        TranslateTransition tt = new TranslateTransition(Duration.millis(150), mainController.paneSettings);
        tt.setByY(461);
        tt.setToY(0);
        tt.playFromStart(); // Запускаем анимацию от 400 до 0 позиции

    }

    /**
     * Кнопка сохранения настроек
     *
     * @param event
     */
    @FXML
    void buttonSaveSettingsOnAction(ActionEvent event) {
        utils.PlaySound("menu_close.mp3");
        mainController.paneLogin.setEffect(null);
        mainController.paneLogin.setDisable(false);
        mainController.paneMain.setEffect(null);
        mainController.paneMain.setDisable(false);

        TranslateTransition tt = new TranslateTransition(Duration.millis(150), mainController.paneSettings);
        tt.setByY(0);
        tt.setToY(461);
        tt.playFromStart(); // Запускаем анимацию от 0 до 440 позиции
        // paneSettings.setVisible(false); // Скрываем панель

        Vars.DEDICATED_MEMORY = MainController.sliderValue.getValue();
        Vars.FULL_SVCREEN_GAME = mainController.checkBoxFullScreen.isSelected();

        if (mainController.radioButtonUltra.isSelected()) {
            Vars.QUALITY = 1;
        } else if (mainController.radioButtonMax.isSelected()) {
            Vars.QUALITY = 2;
        } else if (mainController.radioButtonMid.isSelected()) {
            Vars.QUALITY = 3;
        } else if (mainController.radioButtonMin.isSelected()) {
            Vars.QUALITY = 4;
        } else {
            Vars.QUALITY = 0;
        }

        new Save();
    }

    @FXML
    void onMouseClikedSkinUpload(MouseEvent event) {
        utils.PlaySound("click.mp3");
        utils.fileUploadToServer("skin");
        setSkin();

    }

    @FXML
    void onMouseClikedCloakUpload(MouseEvent event) {
        utils.PlaySound("click.mp3");
        utils.fileUploadToServer("cape");
        setSkin();

    }

    @FXML
    void linckOnAction(ActionEvent event) {
        utils.PlaySound("click.mp3");
        toURL(Config.RestorePassDir);
    }

    /**
     * Кнопка входа авторизации
     *
     * @param event
     */
    @FXML
    public void enterOnAction(ActionEvent event) {
        utils.PlaySound("click.mp3");

        if (Vars.ONLINE) {
            new Auth();
        } else {
            new Game(mainController.clientList.getSelectionModel().getSelectedIndex());
        }
    }

    /**
     * Кнопка открытия логов
     *
     * @param event
     */
    @FXML
    void openLogOnAction(ActionEvent event) {
        utils.PlaySound("click.mp3");
        utils.openURL("file://" + utils.getMcDir(Config.ClentDir).toString() + File.separator + Config.LogName);
    }

    @FXML
    void openLogClientOnAction(ActionEvent event) {
        utils.PlaySound("click.mp3");
        utils.openURL("file://" + utils.getMcDir(Config.ClentDir).toString() + File.separator + "Industrial" + File.separator + "crash-reports");
    }

    @FXML
    void clientRestoreOnAction(ActionEvent event) {
        utils.PlaySound("click.mp3");
        Update.delete(utils.getMcDir(ClentDir));
    }

    void toURL(String url) {
        try {
            utils.seeURL(url);
        } catch (URISyntaxException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    Image setImage(int i) {
        try {
            return new Image(MainController.class.getResourceAsStream("/ru/enfester/fx/images/" + Config.Servers[i][0] + ".jpg"));
        } catch (Exception e) {
            new MessegeWindow("Ошибка изображения", Config.Servers[i][0] + " - " + e.getMessage() + " Обратись к администратору", "Продолжить");
            return null;
        }
    }

    float lsX = 0F;
    public volatile Pane ls = new Pane();
    volatile boolean movedMain = true;

    public void listing() {

        ls.setTranslateX(-624);
        ls.setTranslateY(138);

        ls.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent me) {
                TranslateTransition tt = new TranslateTransition(Duration.millis(150), ls);
                tt.setByX(ls.getTranslateX());

                if (me.getDeltaY() > 0) {
                    tt.setToX(ls.getTranslateX());
                } else {
                    tt.setToX(ls.getTranslateX());
                }

                if (ls.getTranslateX() % 624 == 0) {
                    tt.playFromStart();
                }

            }
        });
        final TranslateTransition tt = new TranslateTransition(Duration.millis(150), ls);

        ls.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                movedMain = false;

                /*TranslateTransition tt = new TranslateTransition(Duration.millis(150), ls);
                        tt.setByX(ls.getTranslateX());
                        tt.setToX(ls.getTranslateX() - (posawX + ls.getTranslateX()) + 174);
                        tt.playFromStart();
                 */
                tt.setByX(ls.getTranslateX());
                if ((lsX - me.getSceneX() + ls.getTranslateX()) > 0) {
                    tt.setToX(ls.getTranslateX() - 624);
                } else {
                    tt.setToX(ls.getTranslateX() + 624);
                }

                ls.setTranslateX(me.getSceneX() - lsX);
                if ((ls.getTranslateX()) < -(ls.getWidth() - 624)) {
                    ls.setTranslateX(-(ls.getWidth() - 624));
                }
                if (ls.getTranslateX() > 0) {
                    ls.setTranslateX(0);
                }

            }
        });

        ls.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                lsX = (float) (me.getSceneX() - ls.getTranslateX());
                //   paralax(MouseInfo.getPointerInfo().getLocation().getX() / 2, MouseInfo.getPointerInfo().getLocation().getY() / 2);
            }

        });
        ls.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {

                //tt.playFromStart();
                movedMain = true;
            }
        });

        mainController.paneMain.getChildren().add(ls);

        lsRender();
    }

    void lsRender() {
        for (int i = 0; i < Config.Servers.length; i++) {
            Pane aw = new Pane();
            final int posawX = i * 624 + 174;
            aw.setLayoutX(posawX);
            aw.setPrefSize(552, 368);

            aw.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent me) {

                    TranslateTransition tt = new TranslateTransition(Duration.millis(150), ls);
                    tt.setByX(ls.getTranslateX());
                    tt.setToX(ls.getTranslateX() - (posawX + ls.getTranslateX()) + 174);
                    tt.playFromStart();

                }
            });

            aw.setStyle("-fx-background-image: url('/ru/enfester/fx/images/" + Config.Servers[i][0] + ".jpg');");

            Button st = new Button();
            st.setPrefSize(223, 42);
            st.setText("Играть на " + Config.Servers[i][0]);
            st.setLayoutX((aw.getWidth() / 2) - (st.getWidth() / 2) + 174);
            st.setLayoutY(300);
            final int srv = i;
            st.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent me) {
                    utils.PlaySound("click.mp3");

                    if (Vars.QUALITY == 0) {
                        new MessegeWindow("Не выбран режим графики!", "Зайди в настройки и вебери подходящий тебе режим. \n"
                                + "Выдели памяти как можно больше."
                                + "ПОМНИ! Если у тебя крашиться клиент, то возможно слабый компьютер не может загрузить \n"
                                + "все моды. Ставь режим меньше.", "Хорошо");
                    } else {
                        ls.setVisible(false);
                        new Game(srv);
                    }
                }
            });

            Label lin = new Label();
            lin.setWrapText(true);
            lin.setTextFill(Color.WHITE);
            lin.setFont(new Font(18));
            lin.setLayoutX(10);
            lin.setLayoutY(10);
            lin.setPrefWidth(531);
            lin.setText(Config.Servers[i][5]);

            final ProgressBar br = new ProgressBar();
            br.setLayoutY(250);
            br.setLayoutX(10);
            br.setPrefSize(532, 30);

            final Label lb = new Label();
            lb.setTextFill(Color.WHITE);
            lb.setLayoutY(br.getLayoutY());
            lb.setLayoutX(br.getLayoutX());
            lb.setPrefSize(br.getPrefWidth(), br.getPrefHeight());
            lb.setAlignment(Pos.CENTER);

            final int g = i;

            lb.setText("Загрузка..");
            br.setProgress(1);

            new Thread(new Runnable() {
                int[] online;

                @Override
                public void run() //Этот метод будет выполняться в побочном потоке
                {
                    try {
                        online = utils.getOnline(Config.Servers[g][1], Config.Servers[g][2]);

                        // onlineLab.setText((float) online[0] + "/" + (float) online[1]);               
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                br.setProgress((float) (online[0] / 100F));
                                lb.setText((int) online[0] + "/" + (int) online[1]);
                            }
                        });
                    } catch (Exception ex) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                lb.setText("Сервер выключен");
                                br.setProgress(1);
                            }
                        });
                    }
                }
            }).start();

            aw.getChildren().add(br);
            aw.getChildren().add(lin);
            aw.getChildren().add(lb);
            aw.getChildren().add(st);
            ls.getChildren().add(aw);
        }
    }

    public void setSkin() {
        new Thread(()
                -> //Этот метод будет выполняться в побочном потоке
                {
                    Platform.runLater(() -> {
                        if (EnCoreNetwork.isOnline()) {
                            Image skin1 = new Image(Config.SystemDir + "skin2d.php?size=128&name=" + Vars.LOGIN);
                            Image skin2 = new Image(Config.SystemDir + "skin2d.php?size=128&name=" + Vars.LOGIN + "&id=2");
                            Image head = new Image(Config.SystemDir + "skin2d.php?size=128&name=" + Vars.LOGIN + "&id=head");

                            mainController.imageSkin.setImage(skin1);
                            mainController.imageCloack.setImage(skin2);
                            mainController.imageAvatar.setImage(head);
                        }
                    });
                }).start();
    }
}
