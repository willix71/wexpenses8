package w.expenses8.data.domain.model.enums;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class TransactionFactorConverter implements AttributeConverter<TransactionFactor, Integer>{

	@Override
	public Integer convertToDatabaseColumn(TransactionFactor attribute) {
		if (attribute == null)
			return null;
		else 
			return attribute.getFactor();
	}

	@Override
	public TransactionFactor convertToEntityAttribute(Integer dbData) {
		if (dbData == null) 
			return null;
		if (dbData > 0)
			return TransactionFactor.IN;
		if (dbData < 0)
			return TransactionFactor.OUT;
		return TransactionFactor.SUM;
	}
}
