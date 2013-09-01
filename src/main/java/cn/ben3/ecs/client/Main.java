/*
 * Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved.
 */
package cn.ben3.ecs.client;

import cn.ben3.ecs.api.API;
import cn.ben3.ecs.client.controller.LoginController;
import cn.ben3.ecs.client.controller.ProfileController;
import cn.ben3.ecs.client.logic.Context;
import cn.ben3.ecs.client.model.User;
import cn.ben3.ecs.client.security.Authenticator;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main Application. This class handles navigation and user session.
 */
public class Main extends Application {

    private Stage stage;
    private User loggedUser;
    private final double MINIMUM_WINDOW_WIDTH = 289;
    private final double MINIMUM_WINDOW_HEIGHT = 352;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Application.launch(Main.class, (String[])null);
        Context.loadProps();
        Context.storeProps();

    }

    @Override
    public void start(Stage primaryStage) {
        try {
            stage = primaryStage;
            stage.setTitle("阿里云压测部署工具");
            gotoLogin();
            primaryStage.show();
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public User getLoggedUser() {
        return loggedUser;
    }
        
    public boolean userLogging(String userId, String password){
        if (Authenticator.validate(userId, password)) {
            loggedUser = User.of(userId);
            gotoProfile();
            return true;
        } else {
            return false;
        }
    }
    
    public void userLogout(){
        loggedUser = null;
        gotoLogin();
    }
    
    private void gotoProfile() {
        StackPane root = new StackPane();
        final WebView view = new WebView();
        WebEngine engine = view.getEngine();
        engine.setJavaScriptEnabled(true);
        URL localUrl = this.getClass().getResource("view/assets/index.html");
        engine.load(String.valueOf(localUrl));
        root.getChildren().add(view);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void gotoLogin() {
        try {
            LoginController login = (LoginController) replaceSceneContent("Login.fxml");
            login.setApp(this);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Initializable replaceSceneContent(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        InputStream in = Main.class.getResourceAsStream("view/"+fxml);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(Main.class.getResource("view/"+fxml));
        AnchorPane page;
        try {
            page = (AnchorPane) loader.load(in);
        } finally {
            in.close();
        } 
        Scene scene = new Scene(page);
        stage.setScene(scene);
        stage.sizeToScene();
        return (Initializable) loader.getController();
    }
}
