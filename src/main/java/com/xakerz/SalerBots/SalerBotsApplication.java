package com.xakerz.SalerBots;

import SaveSerFiles.MapFileHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SalerBotsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalerBotsApplication.class, args);

		MapFileHandler.loadMaps();
	}

}
