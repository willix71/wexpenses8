package w.expenses8.web.converter;

import javax.faces.convert.FacesConverter;
import javax.inject.Named;

import w.expenses8.data.domain.model.TransactionEntry;

@Named
@FacesConverter(value = "transactionEntryConverter", managed = true)
public class TransactionEntryConverter extends DbableConverter<TransactionEntry> { }
