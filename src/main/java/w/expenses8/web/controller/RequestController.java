package w.expenses8.web.controller;

import java.time.LocalDateTime;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import lombok.Getter;

@Named
@RequestScoped
@Getter
public class RequestController {

	public LocalDateTime time = LocalDateTime.now();
	
}
