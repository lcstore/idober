package com.lezo.idober.solr;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import lombok.extern.log4j.Log4j;

import org.apache.solr.common.SolrInputField;
import org.apache.solr.update.processor.FieldMutatingUpdateProcessor;
import org.apache.solr.update.processor.UpdateRequestProcessor;

import com.google.common.collect.Lists;

@Log4j
public abstract class AllValuesOrNoneFieldMutatingUpdateProcessor extends FieldMutatingUpdateProcessor {

    public static final Object DELETE_VALUE_SINGLETON = new Object() {
        @Override
        public String toString() {
            return "!!Singleton Object Triggering Value Deletion!!";
        }
    };

    public static final Object SKIP_FIELD_VALUE_LIST_SINGLETON = new Object() {
        @Override
        public String toString() {
            return "!!Singleton Object Triggering Skipping Field Mutation!!";
        }
    };

    public AllValuesOrNoneFieldMutatingUpdateProcessor(FieldNameSelector selector, UpdateRequestProcessor next) {
        super(selector, next);
    }

    /**
     * Mutates individual values of a field as needed, or returns the original value.
     *
     * @param srcVal a value from a matched field which should be mutated
     * @return the value to use as a replacement for src, or <code>DELETE_VALUE_SINGLETON</code> to indicate that the
     *         value should be removed completely, or <code>SKIP_FIELD_VALUE_LIST_SINGLETON</code> to indicate that a
     *         field value is not consistent with
     * @see #DELETE_VALUE_SINGLETON
     * @see #SKIP_FIELD_VALUE_LIST_SINGLETON
     */
    protected abstract Object mutateValue(final Object srcVal);

    protected final SolrInputField mutate(final SolrInputField srcField) {
        Collection<Object> vals = srcField.getValues();
        if (vals == null || vals.isEmpty())
            return srcField;
        List<String> messages = null;
        SolrInputField result = new SolrInputField(srcField.getName());
        for (final Object srcVal : vals) {
            final Object destVal = mutateValue(srcVal);
            if (SKIP_FIELD_VALUE_LIST_SINGLETON == destVal) {
                String msg = String.format("field '%s' %s value '%s' is not mutatable, so no values will be mutated",
                        new Object[] { srcField.getName(), srcVal.getClass().getSimpleName(), srcVal });
                log.debug(msg);
                return srcField;
            }
            if (DELETE_VALUE_SINGLETON == destVal) {
                if (log.isDebugEnabled()) {
                    if (null == messages) {
                        messages = Lists.newArrayList();
                    }
                    messages.add(String.format(Locale.ROOT, "removing value from field '%s': %s '%s'",
                            srcField.getName(), srcVal.getClass().getSimpleName(), srcVal));
                }
            } else {
                if (log.isDebugEnabled()) {
                    if (null == messages) {
                        messages = Lists.newArrayList();
                    }
                    messages.add(String.format(Locale.ROOT, "replace value from field '%s': %s '%s' with %s '%s'",
                            srcField.getName(), srcVal.getClass().getSimpleName(), srcVal,
                            destVal.getClass().getSimpleName(), destVal));
                }
                result.addValue(destVal, 1.0F);
            }
        }
        result.setBoost(srcField.getBoost());

        if (null != messages && log.isDebugEnabled()) {
            for (String message : messages) {
                log.debug(message);
            }
        }
        return 0 == result.getValueCount() ? null : result;
    }
}
