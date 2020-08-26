package w.expenses8.web.controller;

import java.util.Locale;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;
import w.expenses8.data.domain.model.Tag;

@Named
@ViewScoped
@Getter @Setter
@SuppressWarnings("serial")
public class TagController extends AbstractTypeController<Tag> { }