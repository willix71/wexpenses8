package w.expenses8.web.converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Named;

import org.hibernate.annotations.common.util.StringHelper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Named
@FacesConverter(value = "localDateCustomConverter", managed = true)
public class LocalDateCustomConverter implements Converter<LocalDate> {

	private final LocalDate reference;
	
	public LocalDateCustomConverter() {this.reference = LocalDate.now();}
	public LocalDateCustomConverter(LocalDate reference) { this.reference = reference;};
		
	@Override
	public LocalDate getAsObject(FacesContext context, UIComponent component, String value) {
		log.info("Converting [{}] to LocalDate", value);
		if (StringHelper.isEmpty(value)) return null;

		if ("t".equals(value)) {
			// tomorrow start of day
			return LocalDate.now().plusDays(1);
		}
		
		try {
			String fields[] = value.split("[/.-]");

			int d = fields.length > 0 && fields[0].length() > 0 ? Integer.parseInt(fields[0]) : reference.getDayOfMonth();
			int m = fields.length > 1 && fields[1].length() > 0 ? Integer.parseInt(fields[1]) : reference.getMonthValue();
			int y = fields.length > 2 && fields[2].length() > 0 ? Integer.parseInt(fields[2]) : reference.getYear();
			LocalDate date =  LocalDate.of(y, m, d);
			log.debug("Converted [{}] to {}", value, date);
			return date;
		} catch(Exception e) {
			throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid date", "Can't parse " + value));
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, LocalDate value) {
		log.debug("Converting [{}] from LocalDate", value);
		return value==null?null:value.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	}
	
	public String getReference() {
		return reference.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	}
}

