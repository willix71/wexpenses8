package w.expenses8.web.converter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Named
@FacesConverter(value = "localTimeCustomConverter", managed = true)
public class LocalTimeCustomConverter implements Converter<LocalTime> {

	@Override
	public LocalTime getAsObject(FacesContext context, UIComponent component, String value) {
		log.info("Converting [{}] to LocalDate", value);
		if (value==null) return null;

		if ("n".equals(value)) {
			return LocalTime.now();
		} else if (value.trim().length()==0) {
			return LocalTime.MIDNIGHT;
		}
		
		int hour = 0;
		int minute = 0;
		int second = 0;
		try {
			// parsing time
			String fields[] = value.split("[:.]");
			if (fields.length > 0) {
				hour = Integer.parseInt(fields[0]);
				if (fields.length > 1) {
					minute = Integer.parseInt(fields[1]);
					if (fields.length > 2) {
						second = Integer.parseInt(fields[2]);
					}
				}
			}
			return LocalTime.of(hour, minute, second);
		} catch (NumberFormatException e) {
			throw new ConverterException(
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid time", "Can't parse " + value));
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, LocalTime value) {
		log.debug("Converting [{}] from LocalTime", value);
		return value==null?null:value.format(DateTimeFormatter.ofPattern("HH:mm"));
	}
}

