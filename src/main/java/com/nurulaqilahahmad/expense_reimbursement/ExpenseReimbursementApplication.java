package com.nurulaqilahahmad.expense_reimbursement;

import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class ExpenseReimbursementApplication {

    private transient Environment environment;

    public ExpenseReimbursementApplication( Environment environment) {
        this.environment = environment;
    }

	public static void main(String[] args) {
		SpringApplication.run(ExpenseReimbursementApplication.class, args);
	}

    @EventListener
    public void printApplicationUrl(final ApplicationStartedEvent event) {
        LoggerFactory.getLogger(ExpenseReimbursementApplication.class).info("Application started at "
                + "http://localhost:"
                + environment.getProperty("local.server.port"));
    }
}
