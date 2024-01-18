package mementee.mementee.converter;

import mementee.mementee.vo.SocialLoginType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToSocialLoginConverter implements Converter<String, SocialLoginType> {

    @Override
    public SocialLoginType convert(String source) {
        return SocialLoginType.valueOf(source.toUpperCase());
    }
}
