package w.expenses8.web.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.hibernate.annotations.common.util.StringHelper;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import w.expenses8.web.converter.LocalDateCustomConverter;

@Slf4j
@Named
@ViewScoped
@Getter @Setter
public class LocalDateCustomConverterController {

	public static final String referenceDateParam = "referenceDate";
	
	public static final DateTimeFormatter referenceDateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
	
	public static LocalDateCustomConverter getLocalDateCustomConverter() {
		String reference = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("referenceDate");
		if (!StringHelper.isEmpty(reference)) {
			try {
				return new LocalDateCustomConverter(LocalDate.parse(reference, referenceDateFormat));
			} catch(DateTimeParseException e) {
				log.error("Can't parse {} as reference date", reference);
			}
		}
		return new LocalDateCustomConverter();
	}

	
	private LocalDateCustomConverter converter = getLocalDateCustomConverter();
}
