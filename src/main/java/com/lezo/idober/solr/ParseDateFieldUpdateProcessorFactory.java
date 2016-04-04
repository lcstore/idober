package com.lezo.idober.solr;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import lombok.extern.log4j.Log4j;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.SolrCore;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.schema.FieldType;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.TrieDateField;
import org.apache.solr.update.processor.FieldMutatingUpdateProcessor;
import org.apache.solr.update.processor.FieldMutatingUpdateProcessorFactory;
import org.apache.solr.update.processor.UpdateRequestProcessor;
import org.apache.solr.util.TimeZoneUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Log4j
public class ParseDateFieldUpdateProcessorFactory extends FieldMutatingUpdateProcessorFactory {
    private static final String FORMATS_PARAM = "format";
    private static final String DEFAULT_TIME_ZONE_PARAM = "defaultTimeZone";
    private static final String LOCALE_PARAM = "locale";
    private Map<String, DateFormat> formats = Maps.newLinkedHashMap();

    @Override
    public UpdateRequestProcessor getInstance(SolrQueryRequest req, SolrQueryResponse rsp, UpdateRequestProcessor next) {
        return new AllValuesOrNoneFieldMutatingUpdateProcessor(getSelector(), next) {
            @Override
            protected Object mutateValue(Object srcVal) {
                if (srcVal instanceof CharSequence) {
                    String srcStringVal = srcVal.toString();
                    for (Map.Entry<String, DateFormat> format : formats.entrySet()) {
                        DateFormat parser = format.getValue();
                        try {
                            Date dateTime = parser.parse(srcStringVal);
                            return dateTime;
                        } catch (Exception e) {
                            String smsg = String.format("value '%s' is not parseable with format '%s'",
                                    new Object[] { srcStringVal, format.getKey() });
                            log.debug(smsg);
                        }
                    }
                    return SKIP_FIELD_VALUE_LIST_SINGLETON;
                }
                if (srcVal instanceof Date) {
                    return srcVal;
                }
                return SKIP_FIELD_VALUE_LIST_SINGLETON;
            }
        };
    }

    @Override
    public FieldMutatingUpdateProcessor.FieldNameSelector
            getDefaultSelector(final SolrCore core) {
        return new FieldMutatingUpdateProcessor.FieldNameSelector() {
            @Override
            public boolean shouldMutate(final String fieldName) {
                final IndexSchema schema = core.getSchema();
                FieldType type = schema.getFieldTypeNoEx(fieldName);
                return (null == type) || type instanceof TrieDateField;
            }
        };
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init(NamedList args) {

        Locale locale = Locale.ROOT;
        String localeParam = (String) args.remove(LOCALE_PARAM);
        if (null != localeParam) {
            locale = LocaleUtils.toLocale(localeParam);
        }
        Object defaultTimeZoneParam = args.remove(DEFAULT_TIME_ZONE_PARAM);

        TimeZone defaultTimeZone = TimeZoneUtils.getTimeZone("UTC");
        if (null != defaultTimeZoneParam) {
            defaultTimeZone = TimeZoneUtils.getTimeZone(defaultTimeZoneParam.toString());
        }
        Object formatsParam = args.remove(FORMATS_PARAM);
        if (null != formatsParam) {

            Collection<String> formatList = null;
            if (formatsParam instanceof Collection) {
                formatList = (Collection<String>) formatsParam;
            } else {
                formatList = Lists.newArrayList(formatsParam.toString().trim());
            }
            for (String value : formatList) {
                SimpleDateFormat format = new SimpleDateFormat(value, locale);
                format.setTimeZone(defaultTimeZone);
                formats.put(value, format);
            }
        }
        super.init(args);
    }
}
