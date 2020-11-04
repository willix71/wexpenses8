package w.expenses8.web.controller;

import java.time.LocalDateTime;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import lombok.Getter;

@Named
@Getter
@RequestScoped
public class RequestController {
	
	public LocalDateTime time = LocalDateTime.now();
}
