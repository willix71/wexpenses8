package w.expenses8.web.converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;

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
@FacesConverter(value = "localDateTimeCustomConverter", managed = true)
public class LocalDateTimeCustomConverter implements Converter<LocalDateTime> {

	@Override
	public LocalDateTime getAsObject(FacesContext context, UIComponent component, String value) {
		log.info("Converting [{}] to LocalDate", value);
		if (value==null || value.length()==0) return null;

		if ("t".equals(value)) {
			// tomorrow start of day
			return LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.DAYS);
		}
		
		int hour = 0;
		int minute = 0;
		int second = 0;
		String[] parts = value.split(" ");
		if (parts.length > 1) {
			try {
				// parsing time
				String fields[] = parts[1].split("[:.]");
				if (fields.length > 0) {
					hour = Integer.parseInt(fields[0]);
					if (fields.length > 1) {
						minute = Integer.parseInt(fields[1]);
						if (fields.length > 2) {
							second = Integer.parseInt(fields[2]);
						}
					}
				}
			} catch (NumberFormatException e) {
				throw new NumberFormatException("invalid time (Can not parse " + parts[1] + ")");
			}
		}
		
		try {
			String fields[] = parts.length == 0 ? new String[] {} : parts[0].split("[/.-]");
			Calendar now = Calendar.getInstance();
			int d = fields.length > 0 && fields[0].length() > 0 ? Integer.parseInt(fields[0]) : now.get(Calendar.DAY_OF_MONTH);
			int m = fields.length > 1 && fields[1].length() > 0 ? Integer.parseInt(fields[1]) : now.get(Calendar.MONTH) + 1;
			int y = fields.length > 2 && fields[2].length() > 0 ? Integer.parseInt(fields[2]) : now.get(Calendar.YEAR);
			LocalDateTime date =  LocalDateTime.of(y, m, d, hour, minute, second);
			log.debug("Converted [{}] to {}", value, date);
			return date;
		} catch(Exception e) {
			throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid date", "Can't parse " + value));
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, LocalDateTime value) {
		log.debug("Converting [{}] from LocalDate", value);
		if (value == null) {
			return null;
		} else if (value.getMinute()==0 && value.getHour()==0) {
			return value.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		} else {
			return value.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
		}
	}
}

