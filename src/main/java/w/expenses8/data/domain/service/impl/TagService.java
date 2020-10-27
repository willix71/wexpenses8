package w.expenses8.data.domain.service.impl;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import w.expenses8.data.core.service.GenericServiceImpl;
import w.expenses8.data.domain.dao.ITagDao;
import w.expenses8.data.domain.model.Payee;
import w.expenses8.data.domain.model.Tag;
import w.expenses8.data.domain.model.enums.TagType;
import w.expenses8.data.domain.service.ITagService;
import w.expenses8.data.utils.CriteriaHelper;

@Service
public class TagService extends GenericServiceImpl<Tag, Long, ITagDao> implements ITagService {

	@Autowired
	public TagService(ITagDao dao) {
		super(Tag.class, dao);
	}

	@Override
	public Tag findByName(String name) {
		return getDao().findByName(name);
	}

	@Override
	public List<Tag> findByText(String text) {
		return getDao().findByText(CriteriaHelper.safeLowerLike(text));
	}
	
	@Override
	public List<Tag> findByType(TagType type) {
		return getDao().findByType(type);
	}

	@Override
	public List<Tag> findByInstitution(Payee institution) {
		return getDao().findByInstitution(institution);
	}

	@Override
	public Tag getConsolidationTag(LocalDate d) {
		String name = MessageFormat.format("{0,number,00}/{1,number,0000}", d.getMonthValue(), d.getYear());
		Tag t = getDao().findByName(name);
		if (t == null) {
			int number = d.getYear() * 100 + d.getMonthValue();
			t = save(Tag.with().type(TagType.CONSOLIDATION).name(name).number(number).build());
		}
		return t;
	}
	
	
}