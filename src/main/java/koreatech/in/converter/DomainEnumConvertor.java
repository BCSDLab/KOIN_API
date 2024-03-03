package koreatech.in.converter;

import org.springframework.core.convert.converter.Converter;

import koreatech.in.domain.Upload.DomainEnum;

public class DomainEnumConvertor implements Converter<String, DomainEnum> {
    @Override
    public DomainEnum convert(String domainPathVariable) {
        return DomainEnum.mappingFor(domainPathVariable.toUpperCase());
    }
}
