package by.modsen.driverservice.utils;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Component
public class LocaleUtils {

    private final ResourcePatternResolver resourcePatternResolver;

    public LocaleUtils(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    public Set<Locale> getSupportedLocales() throws IOException {
        Set<Locale> supportedLocales = new HashSet<>();

        Resource[] resources = resourcePatternResolver.getResources(
            "classpath*:/messages/exception/messages_*.properties"
        );

        for (Resource resource : resources) {
            String filename = resource.getFilename();
            if (filename != null && filename.startsWith("messages_")) {
                String localeCode = filename.substring("messages_".length(), filename.lastIndexOf('.'));
                supportedLocales.add(Locale.forLanguageTag(localeCode));
            }
        }

        return supportedLocales;
    }

}