package w.expenses8.data.domain.validation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class ModuloTenValidatorTest {

	ModuloTenValidator validator = new ModuloTenValidator();

	CcpValidator ccpValidator = new CcpValidator();
	
	@Test
	public void simpleTest() {
		assertThat(validator.isValid(null, null)).isTrue();
		assertThat(validator.isValid("", null)).isTrue();
		assertThat(validator.isValid("1", null)).isFalse();
		assertThat(validator.isValid("a", null)).isFalse();
	}
	@Test
	public void simpleTest101586696200565198090000002() {
		assertThat(validator.isValid("10 15866 96200 56519 80600 00002", null)).isTrue();
		assertThat(validator.isValid("12 15866 96200 56519 80600 00002", null)).isFalse();
	}
	@Test
	public void simpleTest152135000001468500014159418() {
		assertThat(validator.isValid("15 21350 00001 46850 00141 59418", null)).isTrue();
		assertThat(validator.isValid("15 21350 00002 46850 00141 59418", null)).isFalse();
		
	}

	@Test
	public void simpleTest70_004152_8() {
		assertThat(validator.isValid("70-004152-8", null)).isTrue();
		assertThat(validator.isValid("80-004152-8", null)).isFalse();	
	}

	@Test
	public void simpleTest01291957() {
		assertThat(validator.isValid("01-029195-7", null)).isTrue();
		assertThat(validator.isValid("01-029194-7", null)).isFalse();
	}
	
	@Test
	public void ccpTest01291957() {
		assertThat(ccpValidator.isValid("01-29195-7", null)).isTrue();
		assertThat(ccpValidator.isValid("01-29194-7", null)).isFalse();
	}
}
