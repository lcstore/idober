package com.lezo.idober.solr;

import java.util.Locale;
import java.util.UUID;

import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.update.processor.UUIDUpdateProcessorFactory;
import org.apache.solr.update.processor.UpdateRequestProcessor;

public class HashIDUpdateProcessorFactory extends UUIDUpdateProcessorFactory {

	@Override
	public UpdateRequestProcessor getInstance(SolrQueryRequest req, SolrQueryResponse rsp, UpdateRequestProcessor next) {
		return new DefaultValueUpdateProcessor(fieldName, next) {
			@Override
			public Object getDefaultValue() {
				String sVal = UUID.randomUUID().toString().toLowerCase(Locale.ROOT);
				sVal = "" + sVal.hashCode();
				sVal = sVal.replace("-", "0");
				return sVal;
			}
		};
	}

}
