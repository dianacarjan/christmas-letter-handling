package com.christmas.letter.processor.util.cache;

import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.data.domain.Pageable;

@Slf4j
public class PageableKeyGenerator implements KeyGenerator {
	@Override
	public Object generate(Object target, Method method, Object... params) {
		StringBuilder keyBuilder = new StringBuilder();
		keyBuilder
				.append(target.getClass().getSimpleName())
				.append(".")
				.append(method.getName())
				.append(":");

		for (Object param : params) {
			if (param instanceof Pageable pageable) {
				keyBuilder
						.append("page=")
						.append(pageable.getPageNumber())
						.append("_")
						.append("size=")
						.append(pageable.getPageSize())
						.append("_")
						.append("offset=")
						.append(pageable.getOffset())
						.append("_")
						.append("sorting=")
						.append(pageable.getSort());
			}
		}

		return keyBuilder;
	}
}
