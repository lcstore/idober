package com.lezo.idober.solr;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.update.AddUpdateCommand;
import org.apache.solr.update.processor.UniqFieldsUpdateProcessorFactory;
import org.apache.solr.update.processor.UpdateRequestProcessor;

import com.google.common.collect.Sets;

public class UniqFieldRegexUpdateProcessorFactory extends UniqFieldsUpdateProcessorFactory {
    private String fieldRegex;

    @SuppressWarnings("rawtypes")
    @Override
    public void init(NamedList args) {
        this.fieldRegex = args.get("fieldRegex").toString();
    }

    @Override
    public UpdateRequestProcessor getInstance(SolrQueryRequest req, SolrQueryResponse rsp, UpdateRequestProcessor next) {
        return new UniqFieldsRegexUpdateProcessor(next, this.fieldRegex);
    }

    public class UniqFieldsRegexUpdateProcessor extends UpdateRequestProcessor {
        private final Pattern fieldRegex;

        public UniqFieldsRegexUpdateProcessor(UpdateRequestProcessor next, String fieldRegex) {
            super(next);
            this.fieldRegex = Pattern.compile(fieldRegex);
        }

        @Override
        public void processAdd(AddUpdateCommand cmd) throws IOException {
            if (fieldRegex != null) {
                SolrInputDocument solrInputDocument = cmd.getSolrInputDocument();
                Iterator<String> it = solrInputDocument.getFieldNames().iterator();
                Set<String> fieldSet = Sets.newHashSet();
                while (it.hasNext()) {
                    String field = it.next();
                    Matcher matcher = fieldRegex.matcher(field);
                    if (!matcher.find()) {
                        continue;
                    }
                    fieldSet.add(field);

                }
                for (String field : fieldSet) {
                    Collection<Object> colList = solrInputDocument.getFieldValues(field);
                    Set<Object> colSet = Sets.newHashSet();
                    solrInputDocument.remove(field);
                    for (Object col : colList) {
                        if (colSet.contains(col)) {
                            continue;
                        }
                        solrInputDocument.addField(field, col);
                        colSet.add(col);
                    }
                }
            }
            super.processAdd(cmd);
        }

    }
}
