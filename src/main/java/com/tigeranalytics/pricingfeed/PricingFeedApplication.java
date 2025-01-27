package com.tigeranalytics.pricingfeed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.tigeranalytics.pricingfeed",
		"com.tigeranalytics.pricingfeed.repository"})
public class PricingFeedApplication {

	public static void main(String[] args) {
		SpringApplication.run(PricingFeedApplication.class, args);
	}

}
