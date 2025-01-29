package com.tigeranalytics.pricingfeed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = {"com.tigeranalytics.pricingfeed",
		"com.tigeranalytics.pricingfeed.repository"})
@EnableAsync
public class PricingFeedApplication {

	public static void main(String[] args) {
		SpringApplication.run(PricingFeedApplication.class, args);
	}

}
