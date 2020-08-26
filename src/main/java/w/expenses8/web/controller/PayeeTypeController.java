package w.expenses8.web.controller;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;
import w.expenses8.data.domain.model.PayeeType;

@Named
@ViewScoped
@Getter @Setter
@SuppressWarnings("serial")
public class PayeeTypeController extends AbstractTypeController<PayeeType> { }
