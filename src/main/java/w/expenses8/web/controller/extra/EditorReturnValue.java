package w.expenses8.web.controller.extra;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class EditorReturnValue<T> {

	private final String event;

	private final T element;
}
