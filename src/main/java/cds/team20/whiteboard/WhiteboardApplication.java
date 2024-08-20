package cds.team20.whiteboard;

import cds.team20.whiteboard.controller.WebSocketHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;

@SpringBootApplication
public class WhiteboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(WhiteboardApplication.class, args);
	}

}
