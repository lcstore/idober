package com.lezo.idober.solr;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.solr.update.processor.FieldValueSubsetUpdateProcessorFactory;

import com.google.common.collect.Sets;

public class UniqFieldRegexUpdateProcessorFactory extends FieldValueSubsetUpdateProcessorFactory {

	@SuppressWarnings("unchecked")
	@Override
	protected Collection<Object> pickSubset(Collection<Object> values) {
		Collection<Object> setCollection = Sets.newLinkedHashSet();
		for (Object col : values) {
			if (col instanceof Map) {
				Map<Object, Object> colMap = (Map<Object, Object>) col;
				for (Entry<Object, Object> entry : colMap.entrySet()) {
					if (entry.getValue() instanceof Collection) {
						Collection<Object> colCollection = (Collection<Object>) entry.getValue();
						setCollection.addAll(colCollection);
					}
					break;
				}
			} else if (col instanceof Collection) {
				Collection<?> colCollection = (Collection<?>) col;
				setCollection.addAll(colCollection);
			} else {
				setCollection.add(col);
			}
		}
		return setCollection;
	}


}
